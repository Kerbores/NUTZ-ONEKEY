package club.zhcs.nail.bean.struts;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

@Table("t_department")
@Comment("部门")
public class Department extends DataBaseEntity {
	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column("d_name")
	@Comment("部门名称")
	private String name;

	@Column("d_descr")
	@Comment("部门描述")
	private String description;

	@Column("d_branch_id")
	@Comment("机构id")
	private String branchId;

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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

}
