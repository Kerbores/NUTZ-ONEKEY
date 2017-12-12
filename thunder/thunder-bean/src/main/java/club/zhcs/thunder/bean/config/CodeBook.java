package club.zhcs.thunder.bean.config;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.thunder.bean.ThunderEntity;
/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_codebook")
@Comment("码表")
public class CodeBook extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Name
	@Column("c_name")
	private String name;

	@Column("c_group_id")
	private int groupId;

	@Column("c_value")
	private String value;

	@Column("c_index")
	private int index;

	@Column("c_parent_id")
	private int parentId;

	@Column("c_is_active")
	private boolean active = true;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
