package club.zhcs.thunder.bean.struts;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_departmanet")
@Comment("部门表")
public class Department extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("d_name")
	@Comment("部门名称")
	String name;

	@Column("d_descr")
	@Comment("部门描述")
	@ColDefine(width = 500)
	String description;

	@Column("d_branch_id")
	@Comment("归属机构")
	long branchId;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the branchId
	 */
	public long getBranchId() {
		return branchId;
	}

	/**
	 * @param branchId
	 *            the branchId to set
	 */
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}

}
