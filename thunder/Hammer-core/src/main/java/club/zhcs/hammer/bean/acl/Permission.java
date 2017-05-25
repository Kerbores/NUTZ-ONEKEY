package club.zhcs.hammer.bean.acl;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.titans.utils.db.po.Entity;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description 权限实体类
 * 
 * @copyright 内部代码,禁止转发
 *
 *
 * @time 2016年1月26日 下午2:16:16
 */
@Table("t_permission")
@Comment("权限表")
public class Permission extends Entity {

	@Column("p_name")
	@Name
	@Comment("权限名称")
	private String name;

	@Column("p_desc")
	@Comment("描述")
	private String description;

	@Column("installed")
	@Comment("内置标识")
	private boolean installed;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

}