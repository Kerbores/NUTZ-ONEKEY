import Vue from 'vue';
import App from './App';
import router from './router';
import ElementUI from 'element-ui';
import Mint from 'mint-ui';
import BaiduMap from 'vue-baidu-map'
import 'mint-ui/lib/style.css';
import 'element-ui/lib/theme-default/index.css'; // 默认主题
// import '../static/css/theme-green/index.css';       // 浅绿色主题
import "babel-polyfill";
import axios from 'axios';
import ElTreeGrid from 'element-tree-grid';
Vue.component(ElTreeGrid.name, ElTreeGrid);
import ElTableTreeColumn from 'element-tree-grid/src/index.js';
Vue.component(ElTreeGrid.name, ElTreeGrid)

global.baseUrl = process.env.NODE_ENV == "development" ? 'Hammer-rest' : '';

Vue.prototype.requestFail = function(error, message) {
    if (error.response && error.response.status == 403) {
        router.push('/login');
        return;
    }
    if (error.response) {
        message({
            showClose: true,
            message: '请求错误,状态码: ' + error.response.status,
            type: 'error'
        });
    } else {
        console.log('Error', error.message);
    }
    console.log(error.config);
}
Vue.prototype.bizFail = function(reason) {
    this.$message({
        showClose: true,
        message: '业务错误,错误原因:' + reason,
        type: 'error'
    });
}
Vue.prototype.get = function(url, success, bizFail) { //全局get请求函数
    const message = this.$message;
    const error = this.requestFail;
    axios.get(baseUrl + url).then(resp => {
        if (resp.status == 200 && resp.data.operationState == 'SUCCESS') {
            success(resp.data);
        } else {
            bizFail ? bizFail(resp) : this.bizFail(resp.data.data.reason);
        }
    }).catch(e => {
        error(e, message);
    });
}
Vue.prototype.postBody = function(url, body, success, bizFail) { //全局post请求函数
    const message = this.$message;
    const error = this.requestFail;
    axios.post(baseUrl + url, body)
        .then(resp => {
            if (resp.status == 200 && resp.data.operationState == 'SUCCESS') {
                success(resp.data);
            } else {
                bizFail ? bizFail(resp) : this.bizFail(resp.data.data.reason);
            }
        })
        .catch(e => {
            error(e, message);
        });
}
Vue.prototype.postForm = function(url, body, success, bizFail) { //全局post请求函数
    const message = this.$message;
    const error = this.requestFail;
    var params = new URLSearchParams();
    for (var key in body) {
        params.append(key, body[key]);
    }
    axios.post(baseUrl + url, params)
        .then(resp => {
            if (resp.status == 200 && resp.data.operationState == 'SUCCESS') {
                success(resp.data);
            } else {
                bizFail ? bizFail(resp) : this.bizFail(resp.data.data.reason);
            }
        })
        .catch(e => {
            error(e, message);
        });
}
Vue.prototype.upload = function(url, body, success, bizFail) { //全局文件上传请求函数
    const message = this.$message;
    const error = this.requestFail;
    var params = new FormData();
    for (var key in body) {
        params.append(key, body[key]);
    }
    axios.post(baseUrl + url, params, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
        .then(resp => {
            if (resp.status == 200 && resp.data.operationState == 'SUCCESS') {
                success(resp.data);
            } else {
                bizFail ? bizFail(resp) : this.bizFail(resp.data.data.reason);
            }
        })
        .catch(e => {
            error(e, message);
        });
}
Vue.use(ElementUI);
Vue.use(BaiduMap, {
    // ak 是在百度地图开发者平台申请的密钥 详见 http://lbsyun.baidu.com/apiconsole/key */
    ak: 'CRHkMGE7Db1USNSyFXqVDmdv'
})
Vue.use(Mint);
new Vue({
    router,
    render: h => h(App)
}).$mount('#app');