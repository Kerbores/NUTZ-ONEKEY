import http from '../http'

export default {
    /**
     * 登录
     * @param {Object} loginForm 登录信息
     * @param {*} success 回调
     */
    login(loginForm, success) {
        http.postBody('user/login', loginForm, success);
    },
    /**
     * 用户列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('user/list', {
            page: page
        }, success);
    },
    /**
     * 用户检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('user/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加用户
     * @param {Object} user 用户
     * @param {Function} success 回调
     */
    save(user, success) {
        http.postBody('user/add', user, success)
    },
    /**
     * 更新用户
     * @param {Object} user 用户
     * @param {Function} success 回调
     */
    update(user, success) {
        http.postBody('user/edit', user, success)
    },
    /**
     * 删除用户
     * @param {number} id 用户id
     * @param {Function} success 回调
     */
    delete(id, success) {
        http.get('user/delete/' + id, {}, success);
    },
    /**
     * 重置密码
     * @param {number} id 用户id
     * @param {string} name 用户名
     * @param {string} password 密码
     * @param {Function} success 回调
     */
    resetPassword(id, name, password, success) {
        http.postBody("/user/resetPassword", {
            id: id,
            name: name,
            password: password
        }, success);
    },
    /**
     * 授权信息
     * @param {string} type 类型(role/permission)
     * @param {number} id 用户id
     * @param {Function} success 回调
     */
    userGrantInfo(type, id, success) {
        let url = "/user/" + type + "/" + id;
        http.get(url, {}, success);
    },
    /**
     * 用户授权
     * @param {number} id 用户id
     * @param {string} type 类型(role/permission)
     * @param {Array} powers 权限/角色
     * @param {Function} success 回调
     */
    grant(id, type, powers, success) {
        http.postBody('user/grant/' + type, {
            id: id,
            grantIds: powers
        }, success)
    }
}