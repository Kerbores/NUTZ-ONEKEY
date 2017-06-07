import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

export default new Router({
    routes: [{
            path: '/',
            redirect: '/login'
        },
        {
            path: '/readme',
            component: resolve => require(['../components/common/Home.vue'], resolve),
            children: [{
                    path: '/',
                    component: resolve => require(['../components/page/Readme.vue'], resolve)
                },
                {
                    path: '/user',
                    component: resolve => require(['../components/page/User.vue'], resolve)
                },
                {
                    path: '/role',
                    component: resolve => require(['../components/page/Role.vue'], resolve)
                },
                {
                    path: '/permission',
                    component: resolve => require(['../components/page/Permission.vue'], resolve)
                },
                {
                    path: '/group',
                    component: resolve => require(['../components/page/Group.vue'], resolve)
                },
                {
                    path: '/codebook',
                    component: resolve => require(['../components/page/Codebook.vue'], resolve)
                },
                {
                    path: '/settings',
                    component: resolve => require(['../components/page/Settings.vue'], resolve)
                },
                {
                    path: '/datasource',
                    component: resolve => require(['../components/page/DataSource.vue'], resolve)
                },
                {
                    path: '/runtime',
                    component: resolve => require(['../components/page/Runtime.vue'], resolve)
                },
                {
                    path: '/trace',
                    component: resolve => require(['../components/page/Trace.vue'], resolve)
                },
                {
                    path: '/log',
                    component: resolve => require(['../components/page/Log.vue'], resolve)
                },
                {
                    path: '/organization',
                    component: resolve => require(['../components/page/Branch.vue'], resolve)
                },
                {
                    path: '/map',
                    component: resolve => require(['../components/page/Map.vue'], resolve)
                },
                {
                    path: '/basetable',
                    component: resolve => require(['../components/page/BaseTable.vue'], resolve)
                },
                {
                    path: '/vuetable',
                    component: resolve => require(['../components/page/VueTable.vue'], resolve) // vue-datasource组件
                },
                {
                    path: '/baseform',
                    component: resolve => require(['../components/page/BaseForm.vue'], resolve)
                },
                {
                    path: '/vueeditor',
                    component: resolve => require(['../components/page/VueEditor.vue'], resolve) // Vue-Quill-Editor组件
                },
                {
                    path: '/markdown',
                    component: resolve => require(['../components/page/Markdown.vue'], resolve) // Vue-Quill-Editor组件
                },
                {
                    path: '/upload',
                    component: resolve => require(['../components/page/Upload.vue'], resolve) // Vue-Core-Image-Upload组件
                },
                {
                    path: '/basecharts',
                    component: resolve => require(['../components/page/BaseCharts.vue'], resolve) // vue-echarts-v3组件
                },
                {
                    path: '/mixcharts',
                    component: resolve => require(['../components/page/MixCharts.vue'], resolve) // vue-echarts-v3组件
                }
            ]
        },
        {
            path: '/mobile',
            component: resolve => require(['../components/page/Mobile.vue'], resolve)
        },
        {
            path: '/login',
            component: resolve => require(['../components/page/Login.vue'], resolve)
        },
    ]
})