package cn.sdpjw.export.stub.provider;

import cn.sdpjw.common.base.page.PageData;
import cn.sdpjw.export.stub.dto.ExportProgressVO;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportSyncResult;
import cn.sdpjw.common.base.response.CommonResponse;

/**
 * 公共导出 Dubbo 接口
 * 提供统一的导出任务创建、进度查询、记录查询、下载、取消和重试功能
 */
public interface ExportProvider {

    /**
     * 创建导出任务
     * 根据请求参数创建异步导出任务，返回任务ID用于后续操作
     *
     * @param request 导出记录查询参数，包含员工ID、菜单编码、状态、时间范围等
     * @return 导出任务ID
     */
    CommonResponse<String> createExport(ExportRequest request);

    /**
     * 同步导出（带行数分流）
     * <p>
     * 先统计导出行数：超过 50000 强制降级为异步并返回 taskId；
     * 未超过阈值则在当前请求内同步执行，返回终态结果（含 exportUrl）。
     *
     * @param request 导出请求
     * @return 同步/降级异步结果
     */
    CommonResponse<ExportSyncResult> createExportSync(ExportRequest request);

    /**
     * 查询导出进度
     * 根据任务ID获取当前导出任务的执行进度和状态信息
     *
     * @param taskId 导出任务ID
     * @return 导出进度信息，包含状态、进度百分比、已处理行数等
     */
    ExportProgressVO getProgress(String taskId);

    /**
     * 查询导出记录列表
     * 根据查询条件获取用户的导出记录列表（不分页）
     *
     * @param query 查询条件，包含员工ID、菜单编码、状态、时间范围等
     * @return 导出记录列表
     */
    CommonResponse<PageData<ExportRecordVO>> listRecords(ExportRecordQuery query);

    /**
     * 获取导出文件下载URL
     * 根据任务ID获取已完成导出任务的下载链接
     *
     * @param taskId 导出任务ID
     * @return 文件下载URL
     */
    String getDownloadUrl(String taskId);

    /**
     * 取消导出任务
     * 取消正在执行中的导出任务，仅运行中的任务可取消
     *
     * @param taskId 导出任务ID
     */
    void cancelExport(String taskId);

    /**
     * 重试导出任务
     * 重新执行失败状态的导出任务
     *
     * @param taskId 导出任务ID
     */
    void retryExport(String taskId);
}