package cn.sdpjw.export.enums.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 兑换方式
 *
 * @author 吴
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ExchangeTypeEnum {

    CORPORATE(1, "对公兑换"),
    PERSONAL(2, "对私兑换");

    private final Integer code;
    private final String desc;

    public static ExchangeTypeEnum byCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExchangeTypeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
