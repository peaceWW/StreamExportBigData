package cn.sdpjw.export.enums.electronic_account;

import lombok.Getter;

/**
 * @Auther: liuyuebai
 * @Date: 2023/11/3 19:13
 * @Description:
 */
@Getter
public enum AccountSignStatusEnum {
    UNKNOWN(-1,"UNKNOWN","系统未知"),
    NO_SIGN(0,"NO_SIGN","未签约"),
    SIGNING(1,"PROC","签约中"),
    SIGN_SUCCESS(2,"SUCCESS","签约成功"),
    SIGN_FAIL(3,"FAIL","签约失败"),
    ;

    private Integer status;

    private String jdStatus;

    private String desc;

    AccountSignStatusEnum(Integer status, String jdStatus, String desc) {
        this.status = status;
        this.desc = desc;
        this.jdStatus = jdStatus;
    }

    public static boolean isUnknown(String jdStatus){
        return UNKNOWN.jdStatus.equals(jdStatus);
    }

    public static boolean isNoSign(Integer status){
        return NO_SIGN.status.equals(status);
    }

    public static boolean isSigning(Integer status){
        return SIGNING.status.equals(status);
    }

    public static boolean isSignSuccess(Integer status){
        return SIGN_SUCCESS.status.equals(status);
    }

    public static boolean isSignFail(Integer status){
        return SIGN_FAIL.status.equals(status);
    }

    public static boolean isFinalState(Integer status){
        return isSignSuccess(status) || isSignFail(status);
    }

    public static boolean isJdResultSuccess(String jdStatus){
        return SIGN_SUCCESS.jdStatus.equals(jdStatus);
    }

    public static AccountSignStatusEnum fromBy(Integer status){
        AccountSignStatusEnum[] values = values();
        int length = values.length;
        for (int i=0;i<length;i++){
            if (values[i].status.equals(status)) {
                return values[i];
            }
        }
        return null;
    }

    public static AccountSignStatusEnum fromJdBy(String jdStatus){
        AccountSignStatusEnum[] values = values();
        int length = values.length;
        for (int i=0;i<length;i++){
            if (values[i].jdStatus.equals(jdStatus)) {
                return values[i];
            }
        }
        return null;
    }

}
