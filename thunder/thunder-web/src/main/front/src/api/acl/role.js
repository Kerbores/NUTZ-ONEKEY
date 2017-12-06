import http from '@/http'

export default {
    /**
     * 角色列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('role/list', {
            page: page
        }, success);
    },
    /**
     * 角色检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('role/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加角色
     * @param {Object} role 角色
     * @param {Function} success 回调
     */
    save(role, success) {
        http.postBody('role/add', role, success)
    },
    /**
     * 更新角色
     * @param {Object} role 角色
     * @param {Function} success 回调
     */
    update(role, success) {
        http.postBody('role/edit', role, success)
    },
    /**
     * 角色授权信息
     * @param {number} id 角色id
     * @param {Function} success 回调
     */
    roleGrantInfo(id, success) {
        http.get('role/permission/' + id, success);
    },
    /**
     * 角色授权
     * @param {number} id 角色id
     * @param {Array} powers 权限
     * @param {Function} success 回调
     */
    grant(id, powers, success) {
        http.postBody('role/grant/', {
            id: id,
            grantIds: powers
        }, success)
    },
    /**
     * 删除角色
     * @param {number} id 角色id
     * @param {*} success 回调
     */
    delete(id, success) {
        http.get('role/delete/' + id, success);
    }
}