import Login from './views/Login.vue'

import Dashboard from './views/dashbord/DashBoard.vue'

import User from './views/acl/User.vue'
import Role from './views/acl/Role.vue'
import Permission from './views/acl/Permission.vue'

import NotFound from './views/404.vue'
import Home from './views/Home.vue'
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
    {
        path: '/',
        component: Home,
        name: '主页',
        leaf: true,
        iconCls: 'el-icon-fa-dashboard', //图标样式class
        children: [{
            path: '/dashboard',
            iconCls: 'el-icon-fa-dashboard',
            component: Dashboard,
            name: 'Dashboard'
        }]
    },
    {
        path: '/',
        component: Home,
        name: '访问控制',
        iconCls: 'el-icon-fa-users', //图标样式class
        children: [{
                path: '/user',
                iconCls: 'el-icon-fa-user',
                component: User,
                meta: {
                    p: 'user.list'
                },
                name: '用户管理'
            },
            {
                path: '/role',
                iconCls: 'el-icon-fa-lock',
                component: Role,
                meta: {
                    p: 'role.list'
                },
                name: '角色管理'
            },
            {
                path: '/permission',
                iconCls: 'el-icon-fa-eye',
                component: Permission,
                meta: {
                    p: 'permission.list'
                },
                name: '权限管理'
            }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '码本管理',
        iconCls: 'el-icon-fa-book',
        children: [{
                path: '/page4',
                iconCls: 'el-icon-fa-object-group',
                component: Page4,
                name: '码本分组'
            },
            {
                path: '/page5',
                component: Page5,
                iconCls: 'el-icon-fa-cubes',
                name: '码本数据'
            }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'el-icon-fa-cogs',
        name: '配置管理',
        children: [{
            path: '/page6',
            component: Page6,
            iconCls: 'el-icon-fa-cog',
            name: '配置列表'
        }]
    },
    {
        path: '/',
        component: Home,
        name: '组织架构',
        iconCls: 'el-icon-fa-building',
        children: [{
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-bank',
            name: '机构管理'
        }, {
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-chrome',
            name: '部门管理'
        }]
    },
    {
        path: '/',
        component: Home,
        name: '监控面板',
        iconCls: 'el-icon-fa-camera',
        children: [{
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-database',
            name: 'Druid监控'
        }, {
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-server',
            name: '运行环境'
        }, {
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-history',
            name: '登录日志'
        }, {
            path: '/echarts',
            component: echarts,
            iconCls: 'el-icon-fa-tags',
            name: '操作日志'
        }]
    }
];

export default routes;