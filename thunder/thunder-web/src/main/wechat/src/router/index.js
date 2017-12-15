import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import List from '@/views/List.vue'
import Detail from '@/views/Detail.vue'
import Add from '@/views/Add.vue'
import Me from '@/views/Me.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'List',
      component: List
    },
    {
      path: '/detail/:id',
      name: 'Detail',
      component: Detail
    },
    {
      path: '/add',
      name: 'Add',
      component: Add
    },
    {
      path: '/me',
      name: 'Me',
      component: Me
    }
  ]
})
