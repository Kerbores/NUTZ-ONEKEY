import http from '@/http'

export default {
    /**
     * 码本分组列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('group/list', {
            page: page
        }, success);
    },
    /**
     * 码本分组检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('group/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加码本分组
     * @param {Object} group 码本分组
     * @param {Function} success 回调
     */
    save(group, success) {
        http.postBody('group/add', group, success)
    },
    /**
     * 更新码本分组
     * @param {Object} group 码本分组
     * @param {Function} success 回调
     */
    update(group, success) {
        http.postBody('group/edit', group, success)
    },
    /**
     * 删除码本分组
     * @param {number} id 码本分组id
     * @param {*} success 回调
     */
    delete(id, status, success) {
        http.get('group/toggle/' + id + '/' + status, success);
    },
    /**
     * 全部分组
     * @param {Function} success 回调
     */
    all(success) {
        http.get('group/all', success);
    }
}