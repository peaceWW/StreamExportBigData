package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.DemoRecord;
import org.springframework.stereotype.Component;

/**
 * 易联账户流水CSV字段映射器
 * @author 吴
 * @version 1.0
 */
@Component
public class DemoCsvFieldMapper implements CsvFieldMapper<DemoRecord> {
    @Override
    public String[] getHeaders() {
        return new String[]{"ID", "订单号", "订单状态"};
    }

    @Override
    public Object[] mapToRow(DemoRecord record) {
        String statusDesc;
        switch (record.getOrderStatus()) {
            case 1:
                statusDesc = "充值";
                break;
            case 2:
                statusDesc = "提现";
                break;
            case 3:
                statusDesc = "转账";
                break;
            default:
                statusDesc = "其他";
        }
        return new Object[]{
                record.getId(),
                record.getOrderNo(),
                statusDesc
        };
    }
}