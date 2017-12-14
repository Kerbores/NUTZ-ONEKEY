// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import App from "./App";
import router from "./router";
import store from "./store";
import FastClick from "fastclick";
import { WechatPlugin,ToastPlugin } from "vux";
import http from "./http";
import api from "./api";

Vue.use(ToastPlugin)
Vue.use(WechatPlugin);

Vue.prototype.$api = api;

router.beforeEach(function(to, from, next) {
  store.commit("updateLoadingStatus", { isLoading: true });
  next();
});

router.afterEach(function(to) {
  store.commit("updateLoadingStatus", { isLoading: false });
});

Vue.config.productionTip = false;
FastClick.attach(document.body);

global.baseUrl = process.env.NODE_ENV == "development" ? "api/" : "";
/* eslint-disable no-new */
new Vue({
  el: "#app",
  store,
  router,
  template: "<App/>",
  components: { App }
});
