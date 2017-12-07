import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'

import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import store from './vuex/store'
import Vuex from 'vuex'
import 'nprogress/nprogress.css'
import NProgress from 'nprogress'
import routes from './routes'
import 'font-awesome/css/font-awesome.min.css'
import './assets/style.less'

import api from "./api";
import http from './http'
import rules from './rules'

import BgaBackTop from 'bga-back-top-vue'
Vue.use(BgaBackTop)

import ElTreeGrid from 'element-tree-grid';
Vue.component(ElTreeGrid.name, ElTreeGrid);

import utils from './common/js/util'


Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(Vuex)

Vue.prototype.$http = http;
Vue.prototype.$rules = rules;
Vue.prototype.$api = api;
Vue.prototype.$utils = utils;

global.baseUrl = process.env.NODE_ENV == "development" ? 'api' : '';
// NProgress.configure({ showSpinner: false });

const router = new VueRouter({
    mode: 'history',
    routes: routes
})

router.beforeEach((to, from, next) => {
    NProgress.start();
    if (to.path === '/') {
        store.commit('remove');
    }
    next()
})

router.afterEach(transition => {
    NProgress.done();
});

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')