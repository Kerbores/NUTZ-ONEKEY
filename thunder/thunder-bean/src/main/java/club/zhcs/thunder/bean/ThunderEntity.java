package club.zhcs.thunder.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

/**
 * @author kerbores@gmail.com
 *
 */
public class ThunderEntity extends DataBaseEntity {

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
