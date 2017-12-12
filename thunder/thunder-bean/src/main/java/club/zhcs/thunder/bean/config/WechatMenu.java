package club.zhcs.thunder.bean.config;

import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

import com.google.common.collect.Lists;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_wechat_menu")
@Comment("微信菜单")
public class WechatMenu extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Type {
		CLICK("click", "点击推事件"),
		VIEW("view", "跳转URL"),
		SCANCODE_PUSH("scancode_push", "扫码推事件"),
		SCANCODE_WAITMSG("scancode_waitmsg", "扫码推事件且弹出“消息接收中”提示框"),
		PIC_SYSPHOTO("pic_sysphoto", "弹出系统拍照发图"),
		PIC_PHOTO_OR_ALBUM("pic_photo_or_album", "弹出拍照或者相册发图"),
		PIC_WEIXIN("pic_weixin", "弹出微信相册发图器"),
		LOCATION_SELECT("location_select", "弹出地理位置选择器"),
		MEDIA_ID("media_id", "下发消息（除文本消息）"),
		VIEW_LIMITED("view_limited", "跳转图文消息URL");
		private String name;
		private String action;

		private Type(String action, String name) {
			this.name = name;
			this.action = action;
		}

		public String getAction() {
			return action;
		}

		public String getName() {
			return name;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@Column("m_parent_id")
	@Comment("父菜单 id")
	private int parentId;

	@Column("m_name")
	@Comment("菜单名称")
	private String name;

	@Column("m_description")
	@Comment("菜单描述")
	private String description;

	@Column("m_index")
	@Comment("菜单序号")
	private int index;

	@Column("m_type")
	@Comment("菜单类型")
	private Type type = Type.VIEW;

	@Column("m_action")
	@Comment("菜单动作")
	private String action;

	@JsonField
	private String typeName;

	private List<WechatMenu> subMenus = Lists.newArrayList();

	public String getAction() {
		return action;
	}

	public String getDescription() {
		return description;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getParentId() {
		return parentId;
	}

	public List<WechatMenu> getSubMenus() {
		return subMenus;
	}

	public Type getType() {
		return type;
	}

	public String getTypeName() {
		return this.type == null ? null : this.type.getName();
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setSubMenus(List<WechatMenu> subMenus) {
		this.subMenus = subMenus;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
