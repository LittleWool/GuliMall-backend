package com.angenao.gulimall.product.entity;

import com.angenao.common.valid.group.AddGroup;
import com.angenao.common.valid.group.UpdateGroup;
import com.angenao.common.valid.group.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.angenao.common.valid.annotaion.ListValue;
import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改品牌id不能为空",groups = {UpdateGroup.class})
	@Null(message = "新增id需要为空",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotEmpty(message = "不能为空", groups = {AddGroup.class})
	@NotBlank(message = "品牌名不能为空",groups = {AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "不能为空", groups = {AddGroup.class})
	@URL(message = "URL地址不合法",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class,UpdateStatusGroup.class})
	@ListValue(vals = {1,2},groups = {AddGroup.class,UpdateGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "不能为空", groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]",message = "检索首字母必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于等于0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
