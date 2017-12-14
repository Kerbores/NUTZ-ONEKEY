import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  // 名字自己定义
  state: {
    isLoading: false
  },
  mutations: {
    updateLoadingStatus(state, payload) {
      state.isLoading = payload.isLoading;
    }
  },
  strict: process.env.NODE_ENV !== "production",
  plugins: [createPersistedState()]
});
