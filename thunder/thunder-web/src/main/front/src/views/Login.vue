<template>
    <div class="login-wrap">
        <div class="ms-login">
            <el-form :model="loginForm" status-icon :rules="$rules"  ref="loginForm" label-width="0px" class="demo-ruleForm">
                <div class="ms-title"> NUTZ-ONEKEY</div>
                <el-form-item prop="userName">
                    <el-input v-model="loginForm.userName" placeholder="请输入用户名" suffix-icon="el-icon-fa-user" @keyup.enter.native="submitForm('loginForm')">
                        <template slot="prepend">用户</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="请输入密码" v-model="loginForm.password" suffix-icon="el-icon-fa-lock" @keyup.enter.native="submitForm('loginForm')">
                        <template slot="prepend">密码</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="captcha">
                    <el-input placeholder="请输入验证码" prefix-icon="el-icon-fa-picture-o"  v-model="loginForm.captcha" @keyup.enter.native="submitForm('loginForm')">
                        <template slot="append">
                            <img :src="captcha" @click="refreshCaptcha" class="append-img" title="点击刷新验证码">
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="rememberMe">
                    <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm('loginForm')">登录</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import api from "../api";
export default {
  data: function() {
    return {
      captcha: baseUrl + "/captcha?length=4&" + Math.random(),
      loginForm: {
        userName: "admin",
        password: "123456",
        captcha: "",
        rememberMe: true
      }
    };
  },
  created() {},
  methods: {
    refreshCaptcha() {
      this.captcha = baseUrl + "/captcha?length=4&" + Math.random();
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          api.User.login(this.loginForm, data => {
            let loginUser = data.loginUser;
            loginUser.roles = data.roles;
            loginUser.permissions = data.permissions;
            this.$store.commit("save", loginUser);
            this.$router.push({ path: "/user" });
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #f5f7f9;
  background-image: url(../assets/images/background.png);
  -moz-background-size: 100% 100%;
  background-size: 100% 100%;
}

.append-img {
  height: 39px;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  margin-left: -20px;
  margin-right: -20px;
  margin-bottom: -5px;
}

.ms-title {
  font-family: -webkit-pictograph;
  text-align: center;
  font-size: 30px;
  color: #018abd;
  padding-bottom: 10px;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 45%;
  width: 300px;
  height: 300px;
  margin: -150px 0 0 -190px;
  padding: 40px;
  border-radius: 5px;
  background: #fff;
}

.login-btn {
  text-align: center;
}

.login-btn button {
  width: 100%;
  height: 36px;
}
</style>
