package cn.sdpjw.export.enums.margin;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值方式
 *
 * @author 吴
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum DictPayTypeEnum {

    OFFLINE(0, "线下转账"),
    GATEWAY(2, "微信支付"),
    SCAN(3, "支付宝支付");

    private final Integer code;
    private final String desc;

    public static DictPayTypeEnum byCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DictPayTypeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
