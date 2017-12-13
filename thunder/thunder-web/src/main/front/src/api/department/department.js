import http from "@/http";

export default {
  /**
   * 部门列表
   * @param {number} page  页码
   * @param {Function} success 回调
   */
  list(page, success) {
    http.get(
      "dept/list",
      {
        page: page
      },
      success
    );
  },
  /**
   * 部门检索
   * @param {number} page 页码
   * @param {number} branchId 机构id
   * @param {string} key 关键词
   * @param {Function} success 回调
   */
  search(page, branchId, key, success) {
    http.get(
      "dept/search",
      {
        page: page,
        branchId: branchId,
        key: key
      },
      success
    );
  },
  /**
   * 添加部门
   * @param {Object} dept 部门
   * @param {Function} success 回调
   */
  save(dept, success) {
    http.postBody("dept/add", dept, success);
  },
  /**
   * 更新部门
   * @param {Object} dept 部门
   * @param {Function} success 回调
   */
  update(dept, success) {
    http.postBody("dept/edit", dept, success);
  },
  /**
   * 删除部门
   * @param {number} id 部门id
   * @param {*} success 回调
   */
  delete(id, success) {
    http.get("dept/delete/" + id, success);
  }
};
