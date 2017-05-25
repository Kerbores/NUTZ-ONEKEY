package club.zhcs.nail.bean.struts;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

@Table("t_branch")
@Comment("分支机构")
public class Branch extends DataBaseEntity {
	@Id
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column("b_name")
	@Comment("机构名称")
	private String name;

	@Column("b_descr")
	@Comment("机构描述")
	private String description;

	@Column("b_parent_id")
	@Comment("机构父级id")
	private int parentId;

	@Column("b_address")
	@Comment("机构地址")
	private String address;

	@Column("b_longitude")
	@Comment("机构经度")
	private double longitude;

	@Column("b_latitude")
	@Comment("机构纬度")
	private double latitude;

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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
