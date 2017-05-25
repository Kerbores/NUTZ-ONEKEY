package club.zhcs.nail.bean.acl;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

/**
 * 
 * @author guiyuan.wang-N
 *
 */
@Table("t_permission")
@Comment("权限表")
public class Permission extends DataBaseEntity {

	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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