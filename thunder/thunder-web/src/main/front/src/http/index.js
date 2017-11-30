'use strict'

import axios from 'axios'
import qs from 'qs'
import {
  Loading,
  Message
} from 'element-ui'
var loadinginstace;
axios.defaults.timeout = 5000
axios.defaults.baseURL = process.env.NODE_ENV == "development" ? 'http://localhost:8888/api' : '';
axios.interceptors.request.use(config => {
  if (loadinginstace) {
    loadinginstace.visible = false;
    loadinginstace = Loading.service({
      fullscreen: true,
      lock: true,
      text: "接口请求中..."
    });
  } else {
    loadinginstace = Loading.service({
      fullscreen: true,
      lock: true,
      text: "接口请求中..."
    });
  }

  return config
}, error => {
  if (loadinginstace) {
    loadinginstace.close();
    loadinginstace = null;
  }
  return Promise.reject('加载超时')
})

axios.interceptors.response.use(response => {
  if (loadinginstace) {
    loadinginstace.close();
  }
  if (response.data.operationState == 'SUCCESS') {
    return Promise.resolve(response.data.data)
  } else {
    return Promise.reject(response.data.errors[0]);
  }
}, error => {
  if (loadinginstace) {
    loadinginstace.close();
  }
  switch (error.response.status) {
    case 403:
      location.href = '/';
      //return Promise.reject("用户没有登录");
    case 404:
      return Promise.reject("接口不存在");
    case 500:
      return Promise.reject("接口发送了异常")
    case 504:
      return Promise.reject("代理接口服务不可用")
    case 502:
      return Promise.reject("接口代理出错")
    default:
      return Promise.reject(error.response.data.msg[0]);
  }
})


export default {
  post(url, data, done, fail) {
    return axios({
      method: 'post',
      baseURL: baseUrl,
      url,
      data: qs.stringify(data),
      timeout: 10000,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
      }
    }).then(
      data => done(data)
    ).catch(error => {
      if (fail) {
        fail(error)
      } else {
        Message.error({
          message: error
        });
      }
    })
  },
  postBody(url, data, done, fail) {
    return axios.post(url, data)
      .then(data => done(data))
      .catch(error => {
        if (fail) {
          fail(error)
        } else {
          Message.error({
            message: error
          });
        }
      });
  },
  get(url, params, done, fail) {
    return axios({
      method: 'get',
      baseURL: baseUrl,
      url,
      params, // get 请求时带的参数
      timeout: 10000,
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      }
    }).then(
      data => done(data)
    ).catch(error => {
      if (fail) {
        fail(error)
      } else {
        Message.error({
          message: error
        });
      }
    })
  }
}
