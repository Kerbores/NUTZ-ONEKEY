package club.zhcs.thunder.dto;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class GrantDTO {
	private long id;

	private long[] grantIds;

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
