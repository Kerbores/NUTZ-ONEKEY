import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
  mode:'history',
  routes: [{
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      component: resolve => require(['../components/pages/Login.vue'], resolve)
    },
    {
      path: '/dashboard',
      component: resolve => require(['../components/common/Home.vue'], resolve),
      children: [{
          path: '/',
          component: resolve => require(['../components/pages/Dashboard.vue'], resolve)
        },
        {
          path: '/test',
          component: resolve => require(['../components/pages/Test.vue'], resolve)
        }
      ]
    }
  ]
})
