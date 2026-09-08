package cn.sdpjw.export.enums.electronic_account;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道账号开通状态
 * @author 曾令辉LinghuiZeng
 * @date 2021-11-13 11:18
 */
@Getter
@AllArgsConstructor
public enum PaymentAccountStatusEnum {
    /**
     * 支付渠道账号开通状态
     */
    NOT_OPEN(0,"未开通", "未提交"),
    OPENING(1,"开通中","待审核"),
    OPEN_SUCCESS(2,"开通成功","审核通过"),
    OPEN_FAIL(3,"开通失败","审核驳回"),
    LOGGED_OUT(4,"已注销","已解约");

    private final Integer status;
    private final String desc;
    /**
     * 票企通开通状态描述
     */
    private final String pabDesc;

    public static PaymentAccountStatusEnum byCode(Integer status) {
        for (PaymentAccountStatusEnum item : PaymentAccountStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    public static boolean isSuccess(Integer status){
        return OPEN_SUCCESS.status.equals(status);
    }

    public static boolean isNotOpen(Integer status){
        return NOT_OPEN.status.equals(status);
    }

    public static boolean isOpenFail(Integer status){
        return OPEN_FAIL.status.equals(status);
    }

    public static boolean isOpening(Integer status){
        return OPENING.status.equals(status);
    }
}
