// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './router'
import http from './http'
import rules from './rules'
import Cookie from 'js-cookie'
import './assets/style.less'
import store from './store'

Vue.config.productionTip = false

Vue.prototype.$http = http;
Vue.prototype.$rules = rules;
Vue.prototype.$cookie = Cookie;

Vue.use(ElementUI)
global.baseUrl = process.env.NODE_ENV == "development" ? 'api' : '';
/* eslint-disable no-new */
new Vue({
  el: '#app',
  store,
  router,
  template: '<App/>',
  components: {
    App
  }
})
