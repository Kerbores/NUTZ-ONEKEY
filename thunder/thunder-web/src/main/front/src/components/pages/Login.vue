<template>
    <div class="login-wrap">
        <div class="ms-title"> 送又来平台Boss系统</div>
        <div class="ms-login">
            <el-form :model="loginForm" status-icon :rules="$rules"  ref="loginForm" label-width="0px" class="demo-ruleForm">
                <el-form-item prop="mobile">
                    <el-input v-model="loginForm.mobile" placeholder="请输入手机号"    prefix-icon="el-icon-fa-phone" @keyup.enter.native="submitForm('loginForm')">
                        <!-- <template slot="prepend">手机号</template> -->
                        <template slot="append">
                            <el-button type="primary" @click="sendCaptcha('loginForm')" :disabled="!loginForm.mobile" icon="el-icon-fa-send-o"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="captcha">
                    <el-input  placeholder="请输入验证码" v-model="loginForm.captcha" prefix-icon="el-icon-fa-key" @keyup.enter.native="submitForm('loginForm')">
                        <!-- <template slot="prepend">验证码</template> -->
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
import axios from "axios";
export default {
  data: function() {
    return {
      loginForm: {
        mobile: "",
        captcha: "",
        key: "",
        rememberMe: true
      }
    };
  },
  created() {
    let me = this.$cookie.get("me");
    this.loginForm.mobile = me ? me : "";
  },
  methods: {
    sendCaptcha(formName) {
      this.$refs[formName].validateField("mobile", msg => {
        if (!msg) {
          this.$http.get(
            "/employee/captcha",
            { mobile: this.loginForm.mobile },
            result => {
              this.$notify({
                title: "成功",
                message: "验证码发送成功!",
                type: "success",
                showClose: false,
                onClose: () => {
                  this.loginForm.key = result.key;
                  this.loginForm.captcha = result.captcha;
                }
              });
            }
          );
        }
      });
    },
    submitForm(formName) {
      const self = this;
      self.$refs[formName].validate(valid => {
        if (valid) {
          self.$http.post("/employee/login", self.loginForm, data => {
            let loginUser = data.loginUser;
            loginUser.roles = data.roles;
            loginUser.permissions = data.permissions;
            this.$store.commit("save", loginUser);
            this.$router.push({ path: "/dashboard" });
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
  background-image: url(../../assets/background.png);
  -moz-background-size: 100% 100%;
  background-size: 100% 100%;
}

.append-img {
  height: 31px;
  cursor: pointer;
  margin-left: -10px;
  margin-right: -10px;
  margin-bottom: -5px;
}

.ms-title {
  position: absolute;
  top: 50%;
  width: 100%;
  margin-top: -230px;
  text-align: center;
  font-size: 30px;
  color: #fff;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 300px;
  height: 280px;
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
