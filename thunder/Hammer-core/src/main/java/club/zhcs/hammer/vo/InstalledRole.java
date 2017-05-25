package club.zhcs.hammer.vo;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description 内置角色
 * 
 * @copyright copyright©2016 zhcs.club
 *
 * @createTime 2016年8月23日 上午8:54:06
 */
public enum InstalledRole {
	/**
	 * 平台管理员
	 */
	SU("admin", "超级管理员");

	private String name;

	private String description;

	/**
	 * @param name
	 * @param description
	 */
	private InstalledRole(String name, String description) {
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
