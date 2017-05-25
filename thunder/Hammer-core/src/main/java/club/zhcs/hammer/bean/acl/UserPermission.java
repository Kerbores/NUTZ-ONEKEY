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
 * @description 用户权限关系实体
 * 
 * @copyright 内部代码,禁止转发
 *
 *
 * @time 2016年1月26日 下午2:19:54
 */
@Table("t_user_permission")
@Comment("用户权限关系表")
public class UserPermission extends Entity {
	/**
	 * 用户id
	 */
	@Column("u_id")
	@Comment("用户id")
	private int userId;
	/**
	 * 权限id
	 */
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
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param permissionId
	 *            the permissionId to set
	 */
	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

}