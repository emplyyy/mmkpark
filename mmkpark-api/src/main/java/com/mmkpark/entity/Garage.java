package com.mmkpark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("m_garage")
public class Garage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "garage_id", type = IdType.AUTO)
    private Long garageId;

    private String garageName;

    private String garageInfo;

    private Integer parkTotal;

    private Integer parkRemain;

    private String garagePic;

    private String garageVideo;

    private String keeper;

    private String address;

    private String type;

    private String offset;


}
