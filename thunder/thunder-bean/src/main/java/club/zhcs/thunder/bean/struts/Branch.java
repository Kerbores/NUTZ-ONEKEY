package club.zhcs.thunder.bean.struts;

import org.nutz.dao.entity.annotation.ColDefine;
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
@Table("t_branch")
@Comment("分支机构")
public class Branch extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Name
	@Column("sub_name")
	@Comment("分支机构名称")
	@ColDefine(width = 200)
	private String name;

	@Column("sub_code")
	@Comment("分支机构编码")
	private String code;

	@Column("sub_des")
	@Comment("分支机构描述")
	@ColDefine(width = 500)
	private String description;

	@Column("sub_city")
	@Comment("所在城市")
	private String city;

	@Column("sub_province")
	@Comment("所在省份")
	private String province;

	@Column("sub_longitude")
	@Comment("经度")
	private double longitude;

	@Column("sub_latitude")
	@Comment("纬度")
	private double latitude;

	@Column("sub_district")
	@Comment("所在地区")
	private String district;

	@Column("sub_address")
	@Comment("分支机构地址")
	@ColDefine(width = 500)
	private String address;

	@Column("sub_phone")
	@Comment("分支机构电话")
	@ColDefine(width = 500)
	private String phone;

	@Column("sub_principal_id")
	@Comment("分支机构负责人id")
	private int principalId;

	@Column("sub_parent_id")
	@Comment("上级机构编号")
	private int parentId;

	private boolean hasSub;

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @return the principalId
	 */
	public int getPrincipalId() {
		return principalId;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @return the hasSub
	 */
	public boolean isHasSub() {
		return hasSub;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param district
	 *            the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * @param hasSub
	 *            the hasSub to set
	 */
	public void setHasSub(boolean hasSub) {
		this.hasSub = hasSub;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @param principalId
	 *            the principalId to set
	 */
	public void setPrincipalId(int principalId) {
		this.principalId = principalId;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

}
