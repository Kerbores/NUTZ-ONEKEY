import http from '@/http'

export default {
    /**
     * 码本数据列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('codebook/list', {
            page: page
        }, success);
    },
    /**
     * 码本数据检索
     * @param {number} page 页码
     * @param {number} groupId 分组id
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, groupId, key, success) {
        http.get('codebook/search', {
            page: page,
            key: key,
            group: groupId
        }, success);
    },
    /**
     * 添加码本数据
     * @param {Object} codebook 码本数据
     * @param {Function} success 回调
     */
    save(codebook, success) {
        http.postBody('codebook/add', codebook, success)
    },
    /**
     * 更新码本数据
     * @param {Object} codebook 码本数据
     * @param {Function} success 回调
     */
    update(codebook, success) {
        http.postBody('codebook/edit', codebook, success)
    },
    /**
     * 删除码本数据
     * @param {number} id 码本数据id
     * @param {*} success 回调
     */
    delete(id, success) {
        http.get('codebook/delete/' + id, success);
    },
    /**
     * 加载子节点
     * @param {number} id 父级id
     * @param {Function} success 回调
     */
    sub(id, success) {
        http.get('codebook/sub/' + id, success);
    },
    /**
     * 加载顶级
     * @param {number} id 分组id
     * @param {Function} success 回调
     */
    top(id, success) {
        http.get("codebook/top/" + id, success)
    }
}