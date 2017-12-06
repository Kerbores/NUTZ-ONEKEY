import http from '@/http'

export default {
    /**
     * 操作日志列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('log/list', {
            page: page
        }, success);
    },
    /**
     * 操作日志检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('log/search', {
            page: page,
            key: key
        }, success);
    }
}