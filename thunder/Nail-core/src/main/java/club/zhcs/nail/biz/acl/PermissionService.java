package club.zhcs.nail.biz.acl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.nutz.dao.sql.Sql;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.springframework.stereotype.Service;

import club.zhcs.nail.bean.acl.Permission;

/**
 * 
 * @author guiyuan.wang-N
 *
 */
@Service
public class PermissionService extends BaseService<Permission> {

	/**
	 * 用户的全部权限
	 * 
	 * @author 王贵源
	 * @param id
	 *            用户id
	 * @return
	 */
	public List<Permission> getAllPermissionsByUserId(long id) {
		List<Permission> target = listDirectPermissionsByUserId(id);
		target.addAll(listIndirectPermissionsByUserId(id));
		return new ArrayList(new HashSet(target));
	}

	/**
	 * 获取用户的直接权限
	 * 
	 * @author 王贵源
	 * @param id
	 *            用户id
	 * @return 角色列表
	 */
	public List<Permission> listDirectPermissionsByUserId(long id) {
		Sql sql = dao().sqls().create("list.direct.permission.by.user.id");
		sql.params().set("userId", id);
		return searchObj(sql);
	}

	/**
	 * 获取用户的间接权限
	 * 
	 * @author 王贵源
	 * @param id
	 *            用户id
	 * @return 角色列表
	 */
	public List<Permission> listIndirectPermissionsByUserId(long id) {
		Sql sql = dao().sqls().create("list.indirect.permission.by.user.id");
		sql.params().set("userId", id);
		return searchObj(sql);
	}

}
