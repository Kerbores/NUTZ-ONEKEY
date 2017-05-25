package club.zhcs.hammer.vo;

/**
 * 
 * @author kerbores(kerbores@gmail.com)
 *
 */
public enum InstallPermission {
	/**
	 * ++++++++++++++++++++++访问控制++++++++++++++++++++++++++++++++
	 */

	/**
	 * 用户管理
	 */
	USER_LIST("user.list", "用户管理"),
	/**
	 * 
	 */
	USER_ADD("user.add", "用户添加"),
	/**
	 * 
	 */
	USER_DETAIL("user.detail", "用户详情"),
	/**
	 * 
	 */
	USER_ROLE("user.role", "用户设置角色"),
	/**
	 * 
	 */
	USER_GRANT("user.grant", "用户设置权限"),
	/**
	 * 
	 */
	USER_REGION("user.region", "用户设置区域"),
	/**
	 * 
	 */
	USER_EDIT("user.edit", "用户编辑"),
	/**
	 * 
	 */
	USER_DELETE("user.delete", "用户状态"),
	/**
	 * 角色管理
	 */
	ROLE_LIST("role.list", "角色管理"),
	/**
	 * 
	 */
	ROLE_ADD("role.add", "角色添加"),
	/**
	 * 
	 */
	ROLE_GRANT("role.grant", "角色设置权限"),
	/**
	 * 
	 */
	ROLE_EDIT("role.edit", "角色编辑"),
	/**
	 * 
	 */
	ROLE_DELETE("role.delete", "角色状态"),
	/**
	 * 权限管理
	 */
	PERMISSION_LIST("permission.list", "权限管理"),
	/*
	 * 
	 */
	PERMISSION_ADD("permission.add", "权限添加"),
	/**
	 * ++++++++++++++++++++++访问控制++++++++++++++++++++++++++++++++
	 */

	/**
	 * ++++++++++++++++++++++配置管理++++++++++++++++++++++++++++++++
	 */
	CONFIG_LIST("config.list", "配置管理"),
	/**
	 * 
	 */
	CONFIG_ADD("config.add", "配置添加"),
	/**
	 * 
	 */
	CONFIG_EDIT("config.edit", "配置编辑"),
	/**
	 * 
	 */
	CONFIG_DELETE("config.delete", "配置状态"),
	/**
	 * ++++++++++++++++++++++配置管理++++++++++++++++++++++++++++++++
	 */

	/**
	 * ++++++++++++++++++++++码本管理++++++++++++++++++++++++++++++++
	 */
	GROUP_LIST("group.list", "码本分组列表"),
	/**
	 * 
	 */
	GROUP_ADD("group.add", "码本分组添加"),
	/**
	 * 
	 */
	GROUP_EDIT("group.edit", "码本分组编辑"),
	/**
	 * 
	 */
	GROUP_DELETE("group.delete", "码本分组状态"),
	/**
	 * 
	 */
	CODEBOOK_LIST("codebook.list", "数据列表"),
	/**
	 * 
	 */
	CODEBOOK_ADD("codebook.add", "数据添加"),
	/**
	 * 
	 */
	CODEBOOK_EDIT("codebook.edit", "数据编辑"),
	/**
	 * 
	 */
	CODEBOOK_DELETE("codebook.delete", "数据状态"),
	/**
	 * 
	 */
	PERMISSION_EDIT("permission.edit", "编辑权限"),
	/**
	 * 
	 */
	PERMISSION_DELETE("permission.delete", "删除权限"),
	/**
	 * 
	 */
	;

	private String name;

	private String description;

	/**
	 * @param name
	 * @param description
	 */
	private InstallPermission(String name, String description) {
		this.name = name;
		this.description = description;
	}

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
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
