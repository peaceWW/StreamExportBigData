package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 导出承接贝账户明细列表查询参数
 *
 * @author 吴
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class MarginDetailRequestRequest extends ExportRequest implements Serializable {

    private static final long serialVersionUID = 770783008117685893L;


    /**
     * 承接贝账户id
     */
    private Long marginAccountId;
    /**
     * 企业id
     */
    private String corpId;
    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 订单号
     */
    private String bizSerialId;

    /**
     * 收支类型1收入2支出
     */
    private Integer balanceType;

    /**
     * 1  充值  2  积分兑换  3  平台赔付  4  承接贝解冻  5  兑换红包返还  6  兑换红包  7  平台扣除  8  承接贝扣除  9  服务费冻结
     * 10划转转入11划转转出
     */
    private Integer detailType;

    /**
     * 操作人id
     */
    private Integer operatorEmployeeId;

    /***
     * 是否查询历史记录
     */
    private boolean hasHistory = false;

    /**
     * 账号类型 1.充值豆 2.赠送豆 3.活动奖励 4.VIP奖励 5.通道奖励
     */
    private Integer marginType;

    /**
     * 备注
     */
    private String memo;

    /**
     * 内部备注
     */
    private String innerMemo;

    /**
     * 环境是dev还是prd
     */
    private String env;


}
