package club.zhcs.hammer.bean.config;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.titans.utils.db.po.Entity;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project bean
 *
 * @file Config.java
 *
 * @description Config
 *
 * @time 2016年3月15日 下午1:20:36
 *
 */
@Table("t_sys_config")
public class Config extends Entity {

	@Name
	@Column("cfg_key")
	@Comment("配置键")
	private String name;

	@Column("cfg_value")
	@Comment("配置值")
	@ColDefine(type = ColType.TEXT)
	private String value;

	@Column("cfg_description")
	@Comment("配置说明")
	@ColDefine(width = 250)
	private String description;

	@Column("cfg_installed")
	@Comment("是否内置标识")
	private boolean installed = true;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the installed
	 */
	public boolean isInstalled() {
		return installed;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param installed
	 *            the installed to set
	 */
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
