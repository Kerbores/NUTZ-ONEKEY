import Topic from './topic.js'
import http from '@/http'
export default {
  Topic: Topic,
  wechat: {
    config: (url, success) => {
      http.post('config', { url: url }, success)
    }
  }
}
