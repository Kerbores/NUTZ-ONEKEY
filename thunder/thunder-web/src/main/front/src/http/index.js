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
//请求前拦截
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
//设置response统一处理
axios.interceptors.response.use(response => {
    if (loadinginstace) {
        loadinginstace.close();
    }
    if (response.data.operationState == 'SUCCESS') { //数据成功
        return Promise.resolve(response.data.data)
    } else { //数据失败直接reject
        return Promise.reject(response.data.errors[0]);
    }
}, error => { //http失败
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
    /**
     * 发送post请求(form表单)
     * @param {string} url 地址
     * @param {object} data 请求数据
     * @param {Function} done 成功回调
     * @param {Function} fail 失败回调(可选)
     */
    post(url, data, done, fail) {
        return axios({
            method: 'post',
            baseURL: baseUrl,
            url,
            data: qs.stringify(data),
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
    /**
     * 发送post请求(body流)
     * @param {string} url 地址
     * @param {object} data 请求数据
     * @param {Function} done 成功回调
     * @param {Function} fail 失败回调(可选)
     */
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
    /**
     * 发送get请求
     * @param {string} url 请求地址
     * @param {object} data 请求数据(可选)
     * @param {Function} done 成功回调
     * @param {Function} fail 失败回调(可选)
     */
    get(url, ...options) {
        let params, done, fail;
        if (typeof options[0] === 'object') {
            params = options[0];
            done = options[1];
            fail = options[2];
        } else {
            params = {};
            done = options[0];
            fail = options[1];
        }
        return axios({
            method: 'get',
            baseURL: baseUrl,
            url,
            params, // get 请求时带的参数
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