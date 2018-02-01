package club.zhcs.thunder.dto;

import org.nutz.json.Json;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class GrantDTO {
	private long id;

	private long[] grantIds;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this);
	}

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

	public long[] getGrantIds() {
		return grantIds;
	}

	public void setGrantIds(long[] grantIds) {
		this.grantIds = grantIds;
	}
}
