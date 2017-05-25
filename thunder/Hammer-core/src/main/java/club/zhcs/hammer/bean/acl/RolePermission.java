package club.zhcs.hammer.bean.acl;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.titans.utils.db.po.Entity;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description 角色权限关系实体
 * 
 * @copyright 内部代码,禁止转发
 *
 *
 * @time 2016年1月26日 下午2:18:24
 */
@Table("t_role_permission")
@Comment("角色权限关系表")
public class RolePermission extends Entity {

	@Column("r_id")
	@Comment("角色id")
	private int roleId;

	@Column("p_id")
	@Comment("权限id")
	private int permissionId;

	/**
	 * @return the permissionId
	 */
	public int getPermissionId() {
		return permissionId;
	}

	/**
	 * @return the roleId
	 */
	public long getRoleId() {
		return roleId;
	}

	/**
	 * @param permissionId
	 *            the permissionId to set
	 */
	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

}
