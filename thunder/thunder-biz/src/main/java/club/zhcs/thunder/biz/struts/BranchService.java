package club.zhcs.thunder.biz.struts;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import club.zhcs.thunder.bean.struts.Branch;
import club.zhcs.titans.utils.biz.BaseService;
import club.zhcs.titans.utils.db.Pager;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-biz
 *
 * @file BranchService.java
 *
 * @description 分支机构
 *
 * @time 2016年5月11日 上午10:40:41
 *
 */
public class BranchService extends BaseService<Branch> {

	/**
	 * @param page
	 * @param where
	 * @return
	 */
	public Pager<Branch> listByPage(int page, Cnd cnd) {

		// 组装sql
		Sql sql = dao().sqls().create("list.top.branchs.by.page");
		sql.params().set("pageStart", (page - 1) * PAGESIZE);
		sql.params().set("pageSize", PAGESIZE);

		// 设置回调
		sql.setCallback(Sqls.callback.records());

		// 执行sql
		dao().execute(sql);

		// 组装对象
		final List<Branch> branchs = new ArrayList<Branch>();
		Lang.each(sql.getList(Record.class), new Each<Record>() {

			@Override
			public void invoke(int arg0, Record record, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				// TODO Auto-generated method stub
				Branch branch = record.toEntity(dao().getEntity(Branch.class));
				branch.setHasSub(record.getInt("has_sub") == 1);
				branchs.add(branch);
			}
		});

		// 组装pager
		Pager<Branch> pager = new Pager<Branch>(PAGESIZE, page);
		pager.setEntities(branchs);
		pager.setCount(count(cnd));
		return pager;
	}

	/**
	 * @return
	 */
	public List<Record> loadTop() {
		Sql sql = dao().sqls().create("load.top.branchs");
		return search(sql);
	}

	/**
	 * @param id
	 * @return
	 */
	public List<Record> nodes(int id) {
		Sql sql = dao().sqls().create("load.branchs.by.id");
		sql.params().set("id", id);
		return search(sql);
	}
}
