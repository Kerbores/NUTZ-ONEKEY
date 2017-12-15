import http from '@/http'
export default {
  /**
   *
   */
  list (page, limit, tab, tag, search, success) {
    http.get(
      'qa/topic',
      {
        page: page,
        limit: limit,
        tab: tab,
        tag: tag,
        search: search
      },
      success
    )
  },
  /**
   *
   */
  detail (id, success) {
    http.get('qa/detail/' + id, success)
  },
  /**
   *
   */
  nutzer (openid, success) {
    http.get('qa/nutzer/' + openid, success)
  },
  /**
   *
   */
  add (topic, token, success) {
    topic.token = token
    http.post('qa/add', topic, success)
  },
  bind (openid, token, success) {
    http.get('qa/bind', { openid: openid, token: token }, success)
  }
}
