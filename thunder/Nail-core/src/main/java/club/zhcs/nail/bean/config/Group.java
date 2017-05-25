package club.zhcs.nail.bean.config;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

@Table("t_code_book_group")
@Comment("码表分组")
public class Group extends DataBaseEntity {
	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Name
	@Column("g_name")
	@Comment("分组名称")
	private String name;

	@Column("g_descr")
	@Comment("分组描述")
	private String description;

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
