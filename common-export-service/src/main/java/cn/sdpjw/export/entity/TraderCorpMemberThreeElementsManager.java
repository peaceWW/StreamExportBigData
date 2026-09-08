package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: liuyuebai
 * @date: 2026/7/16 18:20
 * @description:
 */
@Data
@ToString
@TableName("trader_corp_member_three_elements_manager")
public class TraderCorpMemberThreeElementsManager implements Serializable {
    private static final long serialVersionUID = -4856978394264489078L;

    /**
     * 主键,唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 企业id  --非必须 ,因为首次实名时,企业id为空
     */
    private Integer traderCorpId;

    /**
     * 企业名称 --必须字段
     */
    private String corpName;

    /**
     * 统一社会信用代码 --必须字段
     */
    private String companyCreditCode;

    /**
     * 校验项 0-法人三要素验证，1-经办人三要素验证
     */
    private Integer checkItem;

    /**
     * 当前状态 0-开启，1-关闭
     */
    private Integer openStatus;

    /**
     * 操作人
     */
    private Integer operatorId;
    private String operatorName;

    /**
     * 最近操作时间
     */
    private LocalDateTime lastOperateTime;

    /**
     * 删除状态 0-正常，1-删除
     */
    private Integer deletedFlag;


    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
