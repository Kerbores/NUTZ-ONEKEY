import http from '@/http'

export default {
    /**
     * 权限列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('permission/list', {
            page: page
        }, success);
    },
    /**
     * 权限检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('permission/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加权限
     * @param {Object} permission 权限
     * @param {Function} success 回调
     */
    save(permission, success) {
        http.postBody('permission/add', permission, success)
    },
    /**
     * 更新权限
     * @param {Object} permission 权限
     * @param {Function} success 回调
     */
    update(permission, success) {
        http.postBody('permission/edit', permission, success)
    },
    /**
     * 删除权限
     * @param {number} id 权限id
     * @param {*} success 回调
     */
    delete(id, success) {
        http.get('permission/delete/' + id, success);
    }
}