<template>
    <div>
        <div class="header">
            <div class="logo">Thunder VUE</div>
            <div class="user-info">
                <el-dropdown trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                                                                                            <img class="user-logo" :src="logo">
                                                                                            {{username}}
                                                                                        </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="avatar">上传头像</el-dropdown-item>
                        <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>
        <el-dialog title="上传头像" v-model="avatarShow" size="tiny">
            <el-row>
                <el-col :span="12" :offset="6">
                    <el-upload class="avatar-uploader" :action="upload" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                        <img v-if="imageUrl" :src="imageUrl" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="avatarShow = false">取 消</el-button>
                <el-button type="primary" @click="avatarShow = false">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        data() {
            return {
                avatarShow: false,
                imageUrl: '',
                name: 'Kerbores',
                upload: baseUrl + '/file/upload',
                logo:'../../../static/img/img.jpg'
            }
        },
        mounted(){
            this.logo =  localStorage.getItem('head');
        },
        computed: {
            username() {
                let loginUser = localStorage.getItem('loginUser');
                return loginUser ? loginUser : this.name;
            }
        },
        methods: {
            handleAvatarSuccess(res, file) {
                this.logo = res.data.url;
                this.imageUrl = res.data.url;
            },
            beforeAvatarUpload(file) {
                const isPic = file.type === 'image/jpeg' || file.type === 'image/png';
                const isLt2M = file.size / 1024 / 1024 < 2;
                if (!isPic) {
                    this.$message.error('只能上传图片哦!');
                }
                if (!isLt2M) {
                    this.$message.error('上传头像图片大小不能超过 2MB!');
                }
                return isPic && isLt2M;
            },
            handleCommand(command) {
                if (command == 'loginout') {
                    const self = this;
                    self.get('/user/logout', data => {
                        localStorage.removeItem('loginUser')
                        this.$router.push('/login');
                    })
                }
                if (command == 'avatar') {
                    this.avatarShow = true;
                }
            }
        }
    }
</script>

<style>
    .avatar-uploader {
        width: 180px
    }
    .el-upload--text {
        width: 180px
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
    }
    .header .logo {
        float: left;
        width: 250px;
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
