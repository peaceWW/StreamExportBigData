package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.coin.stub.domain.request.GetMarginAccountByIdsRequest;
import cn.sdpjw.coin.stub.domain.resposne.MarginAccountPOJO;
import cn.sdpjw.coin.stub.enums.DetailTypeEnum;
import cn.sdpjw.coin.stub.enums.MarginTypeEnum;
import cn.sdpjw.coin.stub.provider.CoinProvider;
import cn.sdpjw.common.base.basic.CommonResponseUtil;
import cn.sdpjw.corp.stub.domain.dto.TraderCorpSimplifiedDTO;
import cn.sdpjw.corp.stub.provider.TraderCorpProvider;
import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.MarginDetail;
import cn.sdpjw.export.enums.margin.BalanceTypeEnum;
import cn.sdpjw.redis.common.CacheManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 离线承接贝账户明细 CSV 字段映射器。
 * 离线表不关联企业信息，需按 marginAccountId 回查企业名称与手机号。
 *
 * @author 吴
 * @version 1.0
 */
@Slf4j
@Component
public class MarginDetailOfflineCsvFieldMapper implements CsvFieldMapper<MarginDetail> {

    private static final String CORP_NAME = "corpName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String CACHE_KEY_PREFIX = "shendu:common_export_service:margin_detail_list:";
    private static final int REDIS_TTL_SECONDS = 30 * 60;
    private static final Map<String, String> EMPTY_CORP_INFO;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        Map<String, String> empty = new HashMap<>(2);
        empty.put(CORP_NAME, "");
        empty.put(PHONE_NUMBER, "");
        EMPTY_CORP_INFO = Collections.unmodifiableMap(empty);
    }

    /**
     * 导出流式场景下同一账户会重复出现，本地缓存避免逐行打 Dubbo。
     */
    private final Cache<Long, Map<String, String>> localCache = CacheBuilder.newBuilder()
            .maximumSize(20_000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    @DubboReference
    private CoinProvider coinProvider;

    @DubboReference
    private TraderCorpProvider traderCorpProvider;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public String[] getHeaders() {
        return new String[]{
                "企业名称", "手机号码", "收支类型", "类型", "账户类型",
                "金额(贝)", "关联订单号", "对客备注", "对内备注",
                "时间", "操作人"
        };
    }

    @Override
    public Object[] mapToRow(MarginDetail record) {
        Map<String, String> corpInfo = getCorpInfo(record.getMarginAccountId());
        return new Object[]{
                corpInfo.get(CORP_NAME),
                corpInfo.get(PHONE_NUMBER),
                BalanceTypeEnum.translate(record.getBalanceType()),
                DetailTypeEnum.fromIndex(record.getDetailType()).getDesc(),
                MarginTypeEnum.fromIndex(record.getMarginType()).getText(),
                record.getChangeAmt(),
                sanitizeCsvField(record.getBizSerialId()),
                sanitizeCsvField(record.getMemo()),
                sanitizeCsvField(record.getInnerMemo()),
                formatDateTime(record.getCreateTime()),
                sanitizeCsvField(record.getOperationName())
        };
    }

    private Map<String, String> getCorpInfo(Long marginAccountId) {
        if (marginAccountId == null) {
            return EMPTY_CORP_INFO;
        }
        Map<String, String> localHit = localCache.getIfPresent(marginAccountId);
        if (localHit != null) {
            return localHit;
        }
        String redisKey = CACHE_KEY_PREFIX + marginAccountId;
        Map<String, String> redisHit = readRedisCache(redisKey);
        if (redisHit != null) {
            localCache.put(marginAccountId, redisHit);
            return redisHit;
        }
        try {
            Map<String, String> loaded = loadCorpInfo(marginAccountId);
            localCache.put(marginAccountId, loaded);
            writeRedisCache(redisKey, loaded);
            return loaded;
        } catch (Exception e) {
            log.warn("查询企业信息失败, marginAccountId={}", marginAccountId, e);
            return EMPTY_CORP_INFO;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> readRedisCache(String key) {
        try {
            Map<String, String> corpMap = cacheManager.get(key, Map.class);
            if (corpMap == null || corpMap.isEmpty()) {
                return null;
            }
            return corpMap;
        } catch (Exception e) {
            log.warn("读取企业信息缓存失败, key={}", key, e);
            return null;
        }
    }

    private void writeRedisCache(String key, Map<String, String> corpMap) {
        try {
            cacheManager.set(key, corpMap, REDIS_TTL_SECONDS);
        } catch (Exception e) {
            log.warn("写入企业信息缓存失败, key={}", key, e);
        }
    }

    private Map<String, String> loadCorpInfo(Long marginAccountId) {
        Map<String, String> corpMap = new HashMap<>(4);
        corpMap.put(CORP_NAME, "");
        corpMap.put(PHONE_NUMBER, "");

        GetMarginAccountByIdsRequest request = new GetMarginAccountByIdsRequest();
        List<Long> marginAccountIds = new ArrayList<>(1);
        marginAccountIds.add(marginAccountId);
        request.setMarginAccountIds(marginAccountIds);

        List<MarginAccountPOJO> marginAccountList = CommonResponseUtil.getCommonReturn(
                coinProvider.getMarginAccountByIds(request));
        if (marginAccountList == null || marginAccountList.isEmpty()) {
            return corpMap;
        }

        List<Integer> corpIds = new ArrayList<>(marginAccountList.size());
        for (MarginAccountPOJO accountModel : marginAccountList) {
            Integer corpId = parsePositiveCorpId(accountModel);
            if (corpId != null) {
                corpIds.add(corpId);
            }
        }
        if (corpIds.isEmpty()) {
            return corpMap;
        }

        List<TraderCorpSimplifiedDTO> corpSimplifiedDTOs = CommonResponseUtil.getCommonReturn(
                traderCorpProvider.listSimplifiedInfoByIds(corpIds, 1, 1));
        if (corpSimplifiedDTOs == null || corpSimplifiedDTOs.isEmpty()) {
            return corpMap;
        }

        TraderCorpSimplifiedDTO corpSimplifiedDTO = corpSimplifiedDTOs.get(0);
        if (corpSimplifiedDTO != null) {
            corpMap.put(CORP_NAME, StringUtils.defaultString(corpSimplifiedDTO.getCorpName()));
            corpMap.put(PHONE_NUMBER, maskMobile(corpSimplifiedDTO.getMasterMobile()));
        }
        return corpMap;
    }

    private Integer parsePositiveCorpId(MarginAccountPOJO accountModel) {
        if (accountModel == null) {
            return null;
        }
        String corpId = accountModel.getCorpId();
        if (!StringUtils.isNumeric(corpId)) {
            return null;
        }
        try {
            int id = Integer.parseInt(corpId);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            log.warn("企业ID超出Integer范围, corpId={}", corpId);
            return null;
        }
    }

    /**
     * 手机号脱敏：正则不匹配时不得回写明文。
     */
    private String maskMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return "";
        }
        String value = mobile.trim();
        if (value.contains("*")) {
            return value;
        }
        if (value.matches("\\d{11}")) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.length() >= 7) {
            return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
        }
        return "****";
    }

    /**
     * 防止 Excel 将备注等字段解析为公式（CSV/Excel 注入）。
     */
    private String sanitizeCsvField(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        char first = value.charAt(0);
        if (first == '=' || first == '+' || first == '-' || first == '@' || first == '\t' || first == '\r') {
            return "'" + value;
        }
        return value;
    }

    private String formatDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.of("Asia/Shanghai") // 强制使用东八区
        );
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
