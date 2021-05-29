package com.mmkpark.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author mumukang
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "m_parkcoor",autoResultMap = true)


public class Parkcoor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String garName;

    private String parkName;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private CoorBase center;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private CoorBase border1;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private CoorBase border2;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private CoorBase border3;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private CoorBase border4;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String status;


}
