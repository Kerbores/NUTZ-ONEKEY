<template>
  <div class="sidebar">
    <el-menu :default-active="onRoutes" class="el-menu-vertical-demo el-menu--dark"  unique-opened router background-color="#324157">
      <el-menu-item index="/dashboard">
          <i class="el-icon-fa-tachometer"></i> Dashboard</el-menu-item>
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-fa-users"></i> 访问控制</template>
        <el-menu-item index="/test" v-if="hasPermission('user.list')">
          <i class="el-icon-fa-user"></i> 用户管理</el-menu-item>
        <el-menu-item index="/role" v-if="hasPermission('role.list')">
          <i class="el-icon-fa-lock"></i> 角色管理</el-menu-item>
        <el-menu-item index="/permission" v-if="hasPermission('permission.list')">
          <i class="el-icon-fa-eye"></i> 权限管理</el-menu-item>
      </el-submenu>
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-fa-book"></i>码本管理</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-object-group"></i> 码本分组</el-menu-item>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-cubes"></i> 码本数据</el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-fa-cogs"></i>配置管理</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-cog"></i> 配置列表</el-menu-item>
      </el-submenu>
      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-fa-building"></i>组织架构</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-bank"></i> 机构管理</el-menu-item>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-chrome"></i> 部门管理</el-menu-item>
      </el-submenu>
      <el-submenu index="5">
        <template slot="title">
          <i class="el-icon-fa-camera"></i>监控面板</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-database"></i> Druid监控</el-menu-item>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-server"></i> 运行环境</el-menu-item>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-history"></i> 登录日志</el-menu-item>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-tags"></i> 操作日志</el-menu-item>
      </el-submenu>
    </el-menu>
    <div class="logo">
      © 2018 Kerbores SINO CO.
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
    };
  },
  methods: {
  },
  computed: {
    onRoutes() {
      return this.$route.path;
    },
    ...mapGetters([
      'hasPermission',
      'hasRole'
    ])
  },
  mounted() {
  }
};
</script>

<style scoped>
.sidebar {
  display: block;
  position: absolute;
  width: 200px;
  left: 0;
  top: 70px;
  bottom: 0;
  background: #2e363f;
}

.sidebar > ul {
  height: 100%;
}

.logo {
  position: absolute;
  font-size: 12px;
  left: 10px;
  top: 90%;
  color:#bfcbd9;
  transform: translateY(-60%);
}
</style>
