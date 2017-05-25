<template>
    <div class="login-wrap">
        <div class="ms-title">Thunder Vue</div>
        <div class="ms-login">
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="demo-ruleForm">
                <el-form-item prop="userName">
                    <el-input v-model="ruleForm.userName" placeholder="请输入用户名" icon="user" @keyup.enter.native="submitForm('ruleForm')">
                        <template slot="prepend">用户</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="请输入密码" v-model="ruleForm.password" icon="lock" @keyup.enter.native="submitForm('ruleForm')">
                        <template slot="prepend">密码</template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="captcha">
                    <el-input placeholder="请输入验证码" v-model="ruleForm.captcha" @keyup.enter.native="submitForm('ruleForm')">
                        <template slot="append">
                            <img :src="captcha" @click="refreshCaptcha" class="append-img" title="点击刷新验证码">
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="rememberMe">
                    <el-checkbox v-model="ruleForm.rememberMe">记住我</el-checkbox>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm('ruleForm')">登录</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    data: function () {
        return {
            ruleForm: {
                userName: 'admin',
                password: '123456',
                rememberMe: true
            },
            captcha: baseUrl + '/captcha',
            rules: {
                userName: [
                    { required: true, message: '请输入用户名', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        refreshCaptcha() {
            this.captcha = baseUrl + '/captcha?' + Math.random();
        },
        submitForm(formName) {
            const self = this;
            self.$refs[formName].validate((valid) => {
                if (valid) {
                    self.postBody('/user/login', { data: self.ruleForm }, data => {
                        localStorage.setItem('roles', JSON.stringify(data.data.roles));
                        localStorage.setItem('permissions', JSON.stringify(data.data.permissions));
                        localStorage.setItem('loginUser', data.data.loginUser.realName || data.data.loginUser.name);
                        localStorage.setItem('head', data.data.loginUser.headKey ? 'http://7xlb6l.com1.z0.glb.clouddn.com/'+data.data.loginUser.headKey : '../../../static/img/img.jpg');
                        self.$router.push('/readme');
                    }, result => {
                        this.$message({
                            showClose: true,
                            message: result.data.data.reason,
                            type: 'error'
                        });
                        this.refreshCaptcha()
                    })
                } else {
                    return false;
                }
            });
        }
    }
}
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