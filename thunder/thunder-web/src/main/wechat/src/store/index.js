import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    loading: {
      state: {
        isLoading: false
      },
      mutations: {
        updateLoadingStatus(state, payload) {
          state.isLoading = payload.isLoading;
        }
      }
    },
    user: {
      state: { user: {} },
      mutations: {
        save(state, user) {
          state.user = user;
        },
        remove(state) {
          state.user = {};
        }
      },
      getters: {
        logined: (state, getters) => () => {
          return !!state.user.nutzer.data.loginname;
        }
      }
    }
  },
  strict: process.env.NODE_ENV !== "production",
  plugins: [createPersistedState()]
});
