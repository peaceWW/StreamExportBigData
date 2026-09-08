package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.TraderCorpMemberThreeElementsManager;
import cn.sdpjw.export.utils.DateUtil;
import org.springframework.stereotype.Component;

/**
 * @author: liuyuebai
 * @date: 2026/7/20 16:37
 * @description:
 */
@Component
public class TraderCorpMemberThreeElementsManagerCsvFieldMapper  implements CsvFieldMapper<TraderCorpMemberThreeElementsManager> {
    @Override
    public String[] getHeaders() {
        return new String[]{
                "企业名称", "校验项", "当前状态", "操作人", "最近操作时间"
        };
    }

    @Override
    public Object[] mapToRow(TraderCorpMemberThreeElementsManager manager) {
        return new Object[]{
                manager.getCorpName(),
                getCheckItemName(manager.getCheckItem()),
                getOpenStatusName(manager.getOpenStatus()),
                manager.getOperatorName(),
                DateUtil.formatDateTime(manager.getLastOperateTime()),
        };
    }

    private String getCheckItemName(Integer checkItem) {
        if (checkItem == null) return "";
        return checkItem == 0 ? "法人三要素验证" : "经办人三要素验证";
    }

    private String getOpenStatusName(Integer openStatus) {
        if (openStatus == null) return "";
        return openStatus == 0 ? "开启" : "关闭";
    }
}
