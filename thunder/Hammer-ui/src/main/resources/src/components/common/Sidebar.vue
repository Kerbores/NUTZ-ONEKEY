<template>
    <div class="sidebar">
        <el-menu :default-active="onRoutes" class="el-menu-vertical-demo" theme="dark" unique-opened router>
            <el-menu-item index="readme">
                <i class="el-icon- fa fa-tachometer"></i>项目自述
            </el-menu-item>
            <el-submenu index="2" v-show="hasPermission('user.list')||hasPermission('role.list')||hasPermission('permission.list')">
                <template slot="title">
                    <i class="el-icon- fa fa-users"></i> 访问控制</template>
                <el-menu-item index="/user" v-show="hasPermission('user.list')">
                    <i class="fa fa-user"></i> 用户管理</el-menu-item>
                <el-menu-item index="/role" v-show="hasPermission('role.list')">
                    <i class="fa fa-lock"></i> 角色管理</el-menu-item>
                <el-menu-item index="/permission" v-show="hasPermission('permission.list')">
                    <i class="fa fa-eye"></i> 权限管理</el-menu-item>
            </el-submenu>
            <el-submenu index="3" v-show="hasPermission('group.list') || hasPermission('codebook.list')">
                <template slot="title">
                    <i class="el-icon- fa fa-book"></i>码本管理</template>
                <el-menu-item index="/group" v-show="hasPermission('group.list')">
                    <i class="fa fa-object-group"></i> 码本分组</el-menu-item>
                <el-menu-item index="/codebook" v-show="hasPermission('codebook.list')">
                    <i class="fa fa-cubes"></i> 码本数据</el-menu-item>
            </el-submenu>
            <el-submenu index="4">
                <template slot="title">
                    <i class="el-icon- fa fa-cogs"></i>配置管理</template>
                <el-menu-item index="/settings">
                    <i class="fa fa-cog"></i> 配置列表</el-menu-item>
            </el-submenu>
            <el-submenu index="5">
                <template slot="title">
                    <i class="el-icon- fa fa-building-o"></i>组织结构</template>
                <el-menu-item index="/organization">
                    <i class="fa fa-bank"></i> 机构管理</el-menu-item>
                <el-menu-item index="/dept">
                    <i class="fa fa-chrome"></i> 部门管理</el-menu-item>
            </el-submenu>
            <el-submenu index="6">
                <template slot="title">
                    <i class="el-icon- fa fa-camera"></i>监控面板</template>
                <el-menu-item index="/datasource">
                    <i class="fa fa-database"></i> Druid监控</el-menu-item>
                <el-menu-item index="/runtime">
                    <i class="fa fa-server"></i> 运行环境</el-menu-item>
                <el-menu-item index="/trace">
                    <i class="fa fa-history"></i> 登录日志</el-menu-item>
                <el-menu-item index="/log">
                    <i class="fa fa-tags"></i> 操作日志</el-menu-item>
            </el-submenu>
        </el-menu>
    </div>
</template>

<script>
export default {
    data() {
        return {
            roles: [],
            permissions: []
        }
    },
    methods:{
        hasPermission(p){
            for(var index in this.permissions){
                if(this.permissions[index] == p)
                    return true;
            }
            return false;
        }
    },
    computed: {
        onRoutes() {
            return this.$route.path;
        }
    },
    mounted() {
        this.roles =   localStorage.getItem('roles') ? JSON.parse(localStorage.getItem('roles')):[];
        this.permissions =   localStorage.getItem('permissions') ? JSON.parse(localStorage.getItem('permissions')):[];
    }
}
</script>

<style scoped>
.sidebar {
    display: block;
    position: absolute;
    width: 250px;
    left: 0;
    top: 70px;
    bottom: 0;
    background: #2E363F;
}

.sidebar>ul {
    height: 100%;
}
</style>
