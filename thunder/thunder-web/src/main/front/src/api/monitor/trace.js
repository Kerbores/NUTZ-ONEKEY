import http from '@/http'

export default {
    /**
     * 登录日志列表
     * @param {number} page  页码
     * @param {Function} success 回调
     */
    list(page, success) {
        http.get('trace/list', {
            page: page
        }, success);
    },
    /**
     * 登录日志检索
     * @param {number} page 页码
     * @param {string} key 关键词
     * @param {Function} success 回调
     */
    search(page, key, success) {
        http.get('trace/search', {
            page: page,
            key: key
        }, success);
    }
}