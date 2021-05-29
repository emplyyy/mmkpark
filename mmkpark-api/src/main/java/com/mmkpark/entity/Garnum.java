package com.mmkpark.entity;

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
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_garnum")
public class Garnum implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer adminTotal;

    private Integer garTotal;

    private Integer cgarTotal;

    private Integer cgarRenum;


}
