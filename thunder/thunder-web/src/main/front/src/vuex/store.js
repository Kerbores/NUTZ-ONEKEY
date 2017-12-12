import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
export default new Vuex.Store({
    mutations: {
        save(state, user) {
            state.loginUser = user;
        },
        remove(state) {
            state.loginUser = {
                name: '',
                id: 0,
                mobile: '',
                roles: [],
                permissions: []
            }
        },
        updateAvatar(state, key) {
            state.loginUser.headKey = key;
        }
    },
    getters: {
        hasRole: (state, getters) => (role) => {
            return state.loginUser.roles.filter(r => r === role).length > 0;
        },
        hasPermission: (state, getters) => (permission) => {
            return state.loginUser.permissions.filter(p => p === permission).length > 0;
        }
    },
    state: {
        loginUser: {
            name: '',
            id: 0,
            mobile: '',
            roles: [],
            permissions: []
        }
    },
    strict: process.env.NODE_ENV !== 'production',
    plugins: [createPersistedState()]
})
