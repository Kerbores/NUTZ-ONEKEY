<template>
<div>
    <div class="header">
        <div class="logo">
          <img src="../../../static/img/logo.jpg" height="40">
          Nutz-onekey</div>
        <div class="user-info">
            <el-dropdown trigger="click" @command="handleCommand">
                <span class="el-dropdown-link">
                        <img class="user-logo" :src="logo">
                        {{loginUser.nickName}} <i class="el-icon-arrow-down"></i>
                    </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  data() {
    return {
      subjectInfo: {
        user: {
          nickName: ""
        }
      }
    };
  },
  computed: mapState({
    loginUser: state => state.loginUser,
    logo: function() {
      return this.loginUser.headKey
        ? "sufix" + this.loginUser.headKey
        : "../../../static/img/logo.png";
    }
  }),
  watch: {
    loginUser(newVal) {
      if (!newVal.id) {
        this.$router.push("/");
      }
    }
  },
  methods: {
    handleCommand(command) {
      if (command == "loginout") {
        const self = this;
        self.$http.get("/user/logout", {}, data => {
          self.$store.commit("remove");
        });
      }
      if (command == "avatar") {
        this.avatarShow = true;
      }
    }
  }
};
</script>

<style>
.avatar-uploader {
  width: 180px;
}

.el-upload--text {
  width: 180px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
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

.header {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  height: 70px;
  font-size: 22px;
  line-height: 70px;
  color: #fff;
  background-color: #242f42;
}

.header .logo {
  float: left;
  width: 350px;
  text-align: center;
}

.user-info {
  float: right;
  padding-right: 50px;
  font-size: 16px;
  color: #fff;
}

.user-info .el-dropdown-link {
  position: relative;
  display: inline-block;
  padding-left: 50px;
  color: #fff;
  cursor: pointer;
  vertical-align: middle;
}

.user-info .user-logo {
  position: absolute;
  left: 0;
  top: 15px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.el-dropdown-menu__item {
  text-align: center;
}
</style>
