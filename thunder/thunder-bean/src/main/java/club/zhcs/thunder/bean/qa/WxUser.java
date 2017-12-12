package club.zhcs.thunder.bean.qa;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class WxUser extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("c_openid")
	@Comment("微信用户的openid")
	@ColDefine(width = 32)
	private String openid;

	@Column("c_nick_name")
	@Comment("微信昵称")
	private String nickName;

	@Column("c_city")
	@Comment("微信资料城市")
	private String city;

	@Column("c_province")
	@Comment("微信资料省份")
	private String province;

	@Column("c_country")
	@Comment("微信资料国家")
	private String country;

	@Column("c_head_image_url")
	@Comment("头像地址")
	@ColDefine(width=150)
	private String headImgUrl;

	/**
	 * @return the headImgUrl
	 */
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	/**
	 * @param headImgUrl
	 *            the headImgUrl to set
	 */
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
