package com.mmkpark.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 代码生成业务表字段
 * </p>
 *
 * @author mumukang
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "column_id", type = IdType.AUTO)
    private Long columnId;

    /**
     * 归属表编号
     */
    private String deviceId;

    /**
     * 列名称
     */
    private String deviceName;

    /**
     * 列描述
     */
    private String monitorValue;

    /**
     * 列类型
     */
    private String status;



}
