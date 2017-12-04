package club.zhcs.thunder.bean;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

/**
 * @author kerbores@gmail.com
 *
 */
public class ThunderEntity extends DataBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	long id;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
