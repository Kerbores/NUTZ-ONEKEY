import Login from './views/Login.vue'
import NotFound from './views/404.vue'
import Home from './views/Home.vue'
import Form from './views/acl/Form.vue'
import User from './views/acl/User.vue'
import Page4 from './views/nav2/Page4.vue'
import Page5 from './views/nav2/Page5.vue'
import Page6 from './views/nav3/Page6.vue'
import echarts from './views/charts/echarts.vue'

let routes = [{
    path: '/',
    component: Login,
    name: '',
    hidden: true
},
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    //{ path: '/main', component: Main },
    {
        path: '/',
        component: Home,
        name: '访问控制',
        iconCls: 'el-icon-fa-users', //图标样式class
        children: [{
            path: '/user',
            iconCls: 'el-icon-fa-user',
            component: User,
            name: '用户管理'
        },
            {
                path: '/role',
                iconCls: 'el-icon-fa-lock',
                component: Form,
                name: '角色管理'
            },
            {
                path: '/permission',
                iconCls: 'el-icon-fa-eye',
                component: User,
                name: '权限管理'
            },
        ]
    },
    {
        path: '/',
        component: Home,
        name: '导航二',
        iconCls: 'el-icon-fa-id-card-o',
        children: [{
            path: '/page4',
            component: Page4,
            name: '页面4'
        },
            {
                path: '/page5',
                component: Page5,
                name: '页面5'
            }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'el-icon-fa-address-card',
        leaf: true, //只有一个节点
        children: [{
            path: '/page6',
            component: Page6,
            name: '导航三'
        }]
    },
    {
        path: '/',
        component: Home,
        name: 'Charts',
        iconCls: 'el-icon-fa-bar-chart',
        children: [{
            path: '/echarts',
            component: echarts,
            name: 'echarts'
        }]
    }
];

export default routes;