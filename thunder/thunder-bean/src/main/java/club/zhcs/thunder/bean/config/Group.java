package club.zhcs.thunder.bean.config;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.thunder.bean.ThunderEntity;

@Table("t_code_book_group")
@Comment("码表分组")
public class Group extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Name
	@Column("g_name")
	@Comment("分组名称")
	private String name;

	@Column("g_descr")
	@Comment("分组描述")
	private String description;

	@Column("g_del")
	@Comment("分组可用状态")
	private boolean delete;

	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @param delete
	 *            the delete to set
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

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

}
