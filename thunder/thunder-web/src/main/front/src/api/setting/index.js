import http from '@/http'

export default {
    /**
     * 配置列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('config/list', {
            page: page
        }, success);
    },
    /**
     * 配置检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('config/search', {
            page: page,
            key: key
        }, success);
    },
    /**
     * 添加配置
     * @param {Object} config 配置
     * @param {Function} success 回调
     */
    save(config, success) {
        http.postBody('config/add', config, success)
    },
    /**
     * 更新配置
     * @param {Object} config 配置
     * @param {Function} success 回调
     */
    update(config, success) {
        http.postBody('config/edit', config, success)
    },
    /**
     * 删除配置
     * @param {number} id 配置id
     * @param {*} success 回调
     */
    delete(id, success) {
        http.get('config/delete/' + id, success);
    }
}