<template>
  <div class="sidebar">
    <el-menu :default-active="onRoutes" class="el-menu-vertical-demo el-menu--dark"  unique-opened router background-color="#324157">
      <el-menu-item index="/dashboard">
          <i class="el-icon-fa-tachometer"></i> Dashboard</el-menu-item>
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-fa-cogs"></i> 配置管理</template>
        <el-menu-item index="/test" v-if="hasPermission('aaa')">
          <i class="el-icon-fa-exchange"></i> 渠道管理</el-menu-item>
        <el-menu-item index="/process">
          <i class="el-icon-fa-leaf"></i> 应用管理</el-menu-item>
      </el-submenu>
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-fa-building"></i>商户管理</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-list-ul"></i> 商户列表</el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-fa-coffee"></i>员工管理</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-list-ol"></i> 员工列表</el-menu-item>
      </el-submenu>
      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-fa-shopping-bag"></i>客户管理</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-list-alt"></i> 客户列表</el-menu-item>
      </el-submenu>
      <el-submenu index="5">
        <template slot="title">
          <i class="el-icon-fa-line-chart"></i>运营报表</template>
        <el-menu-item index="/execution">
          <i class="el-icon-fa-bar-chart-o"></i> 报表列表</el-menu-item>
      </el-submenu>
    </el-menu>
    <div class="logo">
      © 2017 SYL, Inc. YC. CQ
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
      'hasRole',
      // ...
    ])
  },
  mounted() {
    console.log(this.hasPermission('aaa'))
    console.log(this.hasRole('aaa'))
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
