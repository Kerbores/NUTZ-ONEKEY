import http from '@/http'

export default {
    /**
     * 机构列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('branch/list', {
            page: page
        }, success);
    },
    /**
     * 机构检索
     * @param {number} page 页码
     * @param {number} groupId 分组id
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('branch/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加机构
     * @param {Object} branch 机构
     * @param {Function} success 回调
     */
    save(branch, success) {
        http.postBody('branch/add', branch, success)
    },
    /**
     * 更新机构
     * @param {Object} branch 机构
     * @param {Function} success 回调
     */
    update(branch, success) {
        http.postBody('branch/edit', branch, success)
    },
    /**
     * 删除机构
     * @param {number} id 机构id
     * @param {*} success 回调
     */
    delete(id, success) {
        http.get('branch/delete/' + id, success);
    },
    /**
     * 加载子节点
     * @param {number} id 父级id
     * @param {Function} success 回调
     */
    sub(id, success) {
        http.get('branch/sub/' + id, success);
    },
    /**
     * 加载顶级
     * @param {number} id 分组id
     * @param {Function} success 回调
     */
    top(success) {
        http.get("branch/top", success)
    }
}