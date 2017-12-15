import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    loading: {
      state: {
        isLoading: false
      },
      mutations: {
        updateLoadingStatus (state, payload) {
          state.isLoading = payload.isLoading
        }
      }
    },
    user: {
      state: { user: {} },
      mutations: {
        save (state, user) {
          state.user = user
        },
        remove (state) {
          state.user = {}
        }
      },
      getters: {
        token: (state, getters) => () => {
          if (getters.logined()) {
            return state.user.nutzer.wechatUser.accessToken
          }
          return null
        },
        logined: (state, getters) => () => {
          return !!(
            state.user &&
            state.user.nutzer &&
            state.user.nutzer.wechatUser &&
            state.user.nutzer.wechatUser.accessToken
          )
        }
      }
    }
  },
  strict: process.env.NODE_ENV !== 'production',
  plugins: [createPersistedState()]
})
