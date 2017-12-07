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

        <el-dialog title="设置头像" :visible.sync="setAvatarShow" width="400px">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-dialog>
    </el-row>
</template>

<script>
import { mapState, mapGetters, mapMutations } from "vuex";

export default {
  data() {
    return {
      sysName: "NUTZ-ONEKEY",
      collapsed: true,
      setAvatarShow: false,
      copyright: "Copyright © 2018 - Kerbores. All Rights Reserved",
      imageUrl: "",
      uploadAction: baseUrl + "/image/avatar"
    };
  },
  computed: {
    ...mapState({
      loginUser: state => state.loginUser,
      logo: function() {
        return this.loginUser.headKey
          ? "http://7xlb6l.com1.z0.glb.clouddn.com/" + this.loginUser.headKey
          : baseUrl + "/image/avatar";
      }
    }),
    ...mapGetters(["hasRole", "hasPermission"])
  },
  methods: {
    ...mapMutations(["save", "remove", "updateAvatar"]),
    handleAvatarSuccess(res, file) {
      this.imageUrl = URL.createObjectURL(file.raw);
      if (res.operationState === "SUCCESS") {
        this.setAvatarShow = false;
        this.updateAvatar(res.data.r.key);
      }
    },
    beforeAvatarUpload(file) {
      const isPic = file.type.indexOf("image") >= 0;
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isPic) {
        this.$message.error("上传头像图片只能选择图片格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }
      return isPic && isLt2M;
    },
    setAvatar() {
      this.imageUrl = this.logo;
      this.setAvatarShow = true;
    },
    resetPassword() {
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
        permissions.filter(permission => this.hasPermission(permission))
          .length > 0
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

<style lang="scss">
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

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader {
  text-align: center;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>