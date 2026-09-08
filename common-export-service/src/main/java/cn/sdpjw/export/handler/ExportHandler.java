package cn.sdpjw.export.handler;


import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.stub.dto.ExportRequest;
import org.apache.ibatis.session.ResultHandler;

import java.util.function.Consumer;

/**
 * 导出 Handler 协议
 * 定义导出任务的核心行为规范，每个具体的导出业务需实现此接口
 * 用于提供数据查询、文件命名、字段映射等导出所需的核心能力
 *
 * @param <T> 导出请求DTO类型，需继承ExportRequestDTO
 */
public interface ExportHandler<T extends ExportRequest> {

    /**
     * 获取菜单编码
     * 用于标识该Handler对应的导出业务模块，需与MenuEnum中的menuCode对应
     *
     * @return 菜单编码
     */
    String menuCode();

    /**
     * 获取查询参数类型
     * 用于反序列化请求中的查询参数，将Map转换为具体的查询DTO类型
     *
     * @return 查询参数的Class类型
     */
    Class<T> queryType();

    /**
     * 获取导出文件名前缀
     * 用于生成导出文件的名称，通常包含业务标识和时间戳
     *
     * @param request 导出请求
     * @return 文件名前缀
     */
    String fileNamePrefix(T request);

    /**
     * 获取远程存储路径
     * 指定导出文件上传到OSS等存储服务的目录路径
     *
     * @param request 导出请求
     * @return 远程存储路径
     */
    String remotePath(T request);

    /**
     * 获取数据总数统计提供者
     * 用于统计需要导出的数据总条数，以便计算导出进度
     *
     * @param request 导出请求
     * @param checkpoint 检查点信息，用于断点续传场景
     * @return 数据总数统计提供者
     */
    CountProvider countProvider(T request, ExportCheckpoint checkpoint);

    /**
     * 获取流式数据查询提供者
     * 用于分批流式查询导出数据，避免一次性加载大量数据到内存
     *
     * @param request 导出请求
     * @param checkpoint 检查点信息，用于断点续传场景
     * @return 流式数据查询提供者
     */
    Consumer<ResultHandler<?>> streamProvider(T request, ExportCheckpoint checkpoint);

    /**
     * 获取CSV字段映射器
     * 用于将数据实体对象映射为CSV文件的列数据
     *
     * @return CSV字段映射器
     */
    CsvFieldMapper<?> fieldMapper();
}