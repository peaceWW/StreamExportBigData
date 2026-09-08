package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.DemoCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.DemoMapper;
import cn.sdpjw.export.entity.DemoRecord;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.dto.query.DemoRequestDTO;
import cn.sdpjw.export.stub.enums.MenuEnum;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 易联账户流水导出服务
 * @author 吴
 * @version 1.0
 */
@Service
public class DemoExportService implements ExportHandler<DemoRequestDTO> {


    @Autowired
    private DemoMapper demoMapper;

    @Autowired
    private DemoCsvFieldMapper fieldMapper;



    @Override
    public String menuCode() {
        return MenuEnum.DEMO.getMenuCode();
    }

    @Override
    public Class<DemoRequestDTO> queryType() {
        return DemoRequestDTO.class;
    }

    @Override
    public String fileNamePrefix(DemoRequestDTO request) {
        return "demo_export";
    }

    @Override
    public String remotePath(DemoRequestDTO request) {
        return "exports/demo";
    }

    @Override
    public CountProvider countProvider(DemoRequestDTO request, ExportCheckpoint checkpoint) {

        return () -> demoMapper.countByCorpAndTime(
                request.getTraderCorpId()
        );
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(DemoRequestDTO request, ExportCheckpoint checkpoint) {
        return handler -> demoMapper.selectByCorpAndTimeRange(
                request.getTraderCorpId(),
                (ResultHandler<DemoRecord>) handler
        );
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}