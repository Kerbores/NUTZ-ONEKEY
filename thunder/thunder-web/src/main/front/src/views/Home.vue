<template>
    <el-row class="container">
        <el-col :span="24" class="header">
            <el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'">
                {{collapsed?sysName.substring(0,1):sysName}}
            </el-col>
            <el-col :span="10">
                <div class="tools" @click.prevent="collapse">
                    <i class="fa fa-align-justify"></i>
                </div>
            </el-col>
            <el-col :span="4" class="userinfo">
                <el-dropdown trigger="hover">
                    <span class="el-dropdown-link userinfo-inner"><img
                            :src="logo"/> {{loginUser.nickName}}</span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item @click.native="setAvatar"><i class="el-icon-fa-upload"></i> 设置头像</el-dropdown-item>
                        <el-dropdown-item @click.native="resetPassword"><i class="el-icon-fa-unlock"></i> 重置密码</el-dropdown-item>
                        <el-dropdown-item divided @click.native="logout"><i class="el-icon-fa-sign-out"></i> 退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </el-col>
        </el-col>
        <el-col :span="24" class="main">
            <aside :class="collapsed?'menu-collapsed':'menu-expanded'">
                <el-menu background-color="#545c64"
                         text-color="#fff"
                         active-text-color="#20a0ff" :default-active="$route.path" class="el-menu-vertical-demo"
                         unique-opened router
                         :collapse="collapsed">
                    <template v-for="(item,index) in $router.options.routes" v-if="!item.hidden">
                        <el-menu-item v-if="item.leaf" v-show="checkPermission(item)" :index="item.children[0].path"
                                      :key="index">
                            <i :class="item.iconCls || item.children[0].iconCls"></i>
                            <span slot="title">{{item.children[0].name}}</span>
                        </el-menu-item>
                        <el-submenu v-else :index="index + ''" :key="index" v-show="checkPermission(item)">
                            <template slot="title">
                                <i :class="item.iconCls"></i>
                                <span slot="title">{{item.name}}</span>
                            </template>
                            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path"
                                          v-show="checkPermission(child)">
                                <template slot="title">
                                    <i :class="child.iconCls"></i>
                                    <span slot="title">{{child.name}}</span>
                                </template>
                            </el-menu-item>
                        </el-submenu>
                    </template>
                </el-menu>
            </aside>
            <section class="content-container bga-back-top">
                <div class="grid-content bg-purple-light">
                    <el-col :span="24" class="breadcrumb-container">
                        <strong class="title">{{$route.name}}</strong>
                        <el-breadcrumb separator="/" class="breadcrumb-inner">
                            <el-breadcrumb-item v-for="item in $route.matched" :to="item.path" :key="item.path">
                                {{ item.name }}
                            </el-breadcrumb-item>
                        </el-breadcrumb>
                    </el-col>
                    <el-col :span="24" class="content-wrapper">
                        <hr style="height:2px;border:none;border-top:1px dashed #0066CC;">
                        <transition name="fade" mode="out-in">
                            <router-view></router-view>
                        </transition>
                    </el-col>
                </div>
            </section>
        </el-col>
        <el-col :span="24">
            <el-footer>{{copyright}}
                <bga-back-top :threshold="101" :right="40" :bottom="10" :width="40" :height="40"
                              :svgMajorColor="'#7b79e5'"
                              :svgMinorColor="'#ba6fda'" :svgType="'rocket_smoke'"/>
            </el-footer>
        </el-col>
    </el-row>
</template>

<script>
import { mapState, mapGetters, mapMutations } from "vuex";

export default {
  data() {
    return {
      sysName: "NUTZ-ONEKEY",
      collapsed: true,
      copyright: "Copyright © 2018 - Kerbores. All Rights Reserved"
    };
  },
  computed: {
    ...mapState({
      loginUser: state => state.loginUser,
      logo: function() {
        return this.loginUser.headKey
          ? "sufix" + this.loginUser.headKey
          : "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512546256940&di=f6395296325116fca0ddcc7c9a8c0ed6&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D1740356695%2C2259268870%26fm%3D214%26gp%3D0.jpg";
      }
    }),
    ...mapGetters(["hasRole", "hasPermission"])
  },
  methods: {
    ...mapMutations(["save", "remove"]),
    setAvatar(){
      console.log(1)
    },
    resetPassword(){
      console.log(2);
    },
    checkPermission(item) {
      let permissions = [];
      if (item.meta) {
        permissions.push(item.meta.p);
      } else if (item.children) {
        item.children.forEach(citem => {
          if (citem.meta) {
            permissions.push(citem.meta.p);
          }
        });
      }
      if (permissions.length == 0) return true;
      if (permissions.length == 1) return this.hasPermission(permissions[0]);
      return (
        permissions.filter(permission => this.hasPermission(permission)).length > 0
      );
    },
    logout: function() {
      this.$confirm("确认退出吗?", "提示", {})
        .then(() => {
          this.$api.User.logout(result => {
            this.remove();
            this.$router.push("/");
          });
        })
        .catch(() => {});
    },
    //折叠导航栏
    collapse: function() {
      this.collapsed = !this.collapsed;
    }
  }
};
</script>

<style scoped lang="scss">
@import "~scss_vars";

.el-footer {
  position: fixed;
  width: 100%;
  bottom: 0;
  background-color: $color-primary;
  border-color: rgba(238, 241, 146, 0.3);
  border-top-width: 1px;
  border-top-style: solid;
  color: #fff;
  text-align: center;
  line-height: 60px;
  z-index: 10;
}

.container {
  position: absolute;
  top: 0px;
  bottom: 0px;
  background-color: #f9f9f9;
  width: 100%;
  .header {
    height: 60px;
    line-height: 60px;
    background: $color-primary;
    color: #fff;
    .userinfo {
      text-align: right;
      padding-right: 35px;
      float: right;
      .userinfo-inner {
        cursor: pointer;
        color: #fff;
        img {
          width: 40px;
          height: 40px;
          border-radius: 20px;
          margin: 10px 0px 10px 10px;
          float: right;
        }
      }
    }
    .logo {
      font-family: -webkit-pictograph;
      height: 60px;
      font-size: 22px;
      padding-left: 20px;
      padding-right: 20px;
      border-color: rgba(238, 241, 146, 0.3);
      border-right-width: 1px;
      border-bottom-width: 1px;
      border-right-style: solid;
      border-bottom-style: solid;
      img {
        width: 40px;
        float: left;
        margin: 10px 10px 10px 18px;
      }
      .txt {
        color: #fff;
      }
    }
    .logo-width {
      width: 230px;
    }
    .logo-collapse-width {
      width: 65px;
    }
    .tools {
      padding: 0px 23px;
      width: 14px;
      height: 60px;
      line-height: 60px;
      cursor: pointer;
    }
  }
  .main {
    display: flex;
    // background: #324057;
    position: absolute;
    top: 60px;
    bottom: 0px;
    overflow: hidden;
    aside {
      flex: 0 0 230px;
      width: 230px;
      // position: absolute;
      // top: 0px;
      // bottom: 0px;
      .el-menu {
        height: 100%;
      }
      .collapsed {
        width: 60px;
        .item {
          position: relative;
        }
        .submenu {
          position: absolute;
          top: 0px;
          left: 60px;
          z-index: 99999;
          height: auto;
          display: none;
        }
      }
    }
    .menu-collapsed {
      flex: 0 0 60px;
      width: 60px;
    }
    .menu-expanded {
      flex: 0 0 230px;
      width: 230px;
    }
    .content-container {
      flex: 1;
      overflow-y: scroll;
      padding: 20px;
      margin-bottom: 60px;
      .breadcrumb-container {
        //margin-bottom: 15px;
        .title {
          width: 200px;
          float: left;
          color: #475669;
        }
        .breadcrumb-inner {
          float: right;
        }
      }
      .content-wrapper {
        background-color: #fff;
        box-sizing: border-box;
      }
    }
  }
}
</style>