<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-user"></i> 用户</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-list"></i> 用户列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="pager.paras.key" icon="search">
                    <div slot="append">
                        <el-button type="primary" icon="search" @click=" pager.page = 1 ;doSearch()">GO</el-button>
                    </div>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="12">
                <el-button type="primary" icon="plus" @click="addEditShow = true">添加用户</el-button>
            </el-col>
        </el-row>
        <el-table :data="pager.entities" border style="width: 100%">
            <el-table-column prop="id" label="ID" sortable>
            </el-table-column>
            <el-table-column prop="name" label="用户名">
            </el-table-column>
            <el-table-column prop="realName" label="姓名">
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" :formatter="formatter">
            </el-table-column>
            <el-table-column prop="status" label="状态">
                <template scope="scope">
                    <el-tag :type="scope.row.status === 'A' ? 'success' : 'danger'" close-transition>{{scope.row.status == 'A' ? 'ACTIVIED' : 'DISABLED'}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template scope="scope">
                    <el-dropdown>
                        <el-button type="primary">
                            操作
                            <i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item>
                                <div @click="handleEdit(scope.$index,scope.row)">
                                    <i class="fa fa-edit"></i> 编辑用户</div>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <div @click="handleReset(scope.$index,scope.row)">
                                    <i class="fa fa-lock"></i> 重置密码</div>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <div @click="handleGrant(scope.$index,scope.row,'role')">
                                    <i class="fa fa-fire"></i> 设置角色</div>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <div @click="handleGrant(scope.$index,scope.row,'permission')">
                                    <i class="fa fa-bolt"></i> 设置权限</div>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <div @click="handleDelete(scope.$index,scope.row)">
                                    <i class="fa fa-trash-o"></i> 删除用户</div>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination style="float:right" layout="prev, pager, next" :total="pager.count" :page-size="pager.pageSize" :current-page.sync="pager.page" v-show="pager.count != 0" @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>
        <!-- 弹框区域-->
        <el-dialog :title="user.id == 0 ? '添加用户' : '编辑用户' " :visible.sync="addEditShow" size="tiny">
            <el-form :model="user" :rules="checkUser" ref="userForm">
                <el-form-item label="用户名" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="user.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="真实姓名" :label-width="formLabelWidth" prop="realName">
                    <el-input v-model="user.realName" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码" :label-width="formLabelWidth" prop="password" v-show="user.password != '00000000'">
                    <el-input type="password" v-model="user.password" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" :label-width="formLabelWidth" prop="rePassword" v-show="user.rePassword != '00000000'">
                    <el-input type="password" v-model="user.rePassword" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="电话" :label-width="formLabelWidth" prop="phone">
                    <el-input v-model="user.phone" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
                    <el-input v-model="user.email" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="用户状态" :label-width="formLabelWidth">
                    <el-switch v-model="user.status" on-text="ACTIVIED" off-text="DISABLED" on-value="A" off-value="D" :width="100">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {status:'A'}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateUser('userForm')">确 定</el-button>
            </div>
        </el-dialog>
    
        <el-dialog title="重置密码" :visible.sync="resetShow" size="tiny">
            <el-form :model="user" :rules="checkUser" ref="resetForm">
                <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
                    <el-input type="password" v-model="user.password" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="resetShow = false">取 消</el-button>
                <el-button type="primary" @click="resetPassword('resetForm')">确 定</el-button>
            </div>
        </el-dialog>
    
        <el-dialog :title="type=='role' ? '设置角色' : '设置权限'" :visible.sync="grantShow">
            <template>
                <el-transfer v-model="selected" :data="options" :titles="['待选项', '已选项']" filterable></el-transfer>
            </template>
            <div slot="footer" class="dialog-footer">
                <el-button @click="grantShow = false">取 消</el-button>
                <el-button type="primary" @click="grant">确 定</el-button>
            </div>
        </el-dialog>
    
    </div>
</template>

<script>
import axios from 'axios';
import moment from 'moment'
export default {
    data() {
        var validatePassSame = (rule, value, callback) => {
            if (value == this.user.password) {
                callback();
            } else {
                callback(new Error("两次输入密码不一致"));
            }
        };
        var validateMobile = (rule, value, callback) => {
            if (/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(value)) {
                callback();
            } else {
                callback(new Error("请输入正确的手机号码"));
            }
        };
        var validateEmail = (rule, value, callback) => {
            if (/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/.test(value)) {
                callback();
            } else {
                callback(new Error("请输入正确的邮箱地址"));
            }
        };
        return {
            searchKey: '',
            pager: {
                page: 1,
                pageSize: 15,
                paras: {
                    key: '1'
                }
            },
            selected: [],
            options: [],
            addEditShow: false,
            resetShow: false,
            grantShow: false,
            type: 'role',
            user: {
                id: 0,
                name: '',
                realName: '',
                status: 'A',
                password: '',
                rePassword: '',
                phone: '',
                email: ''
            },
            checkUser: {
                name: [
                    { required: true, message: '请输入用户名', trigger: 'blur' }
                ],
                realName: [
                    { required: true, message: '请输入真实姓名', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 8, max: 16, message: '请输入8到16位密码', trigger: 'blur' }
                ],
                rePassword: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 8, max: 16, message: '请输入8到16位密码', trigger: 'blur' },
                    { validator: validatePassSame, trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入手机号', trigger: 'blur' },
                    { validator: validateMobile, trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
                    { validator: validateEmail, trigger: 'blur' }
                ]
            },
            formLabelWidth: '100px'
        }
    },
    watch: {
        options: function () {
            this.selected = [];
            this.options.forEach(item => {
                if (item.selected) {
                    this.selected.push(item.key)
                }
            })
        }
    },
    methods: {
        grant() {
            let url = '/user/grant/' + this.type;
            let data = {
                userId: this.user.id,
                grantIds: this.selected
            }
            this.postBody(url, data, result => {
                this.$message({
                    type: 'success',
                    message: '授权成功!'
                });
                window.setTimeout(() => {
                    this.grantShow = false;
                }, 2000)
            })
        },
        resetPassword(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.postBody('/user/resetPassword', this.user, result => {
                        this.$message({
                            type: 'success',
                            message: '重置成功!'
                        });
                        this.resetShow = false;
                    })
                } else {
                    return false;
                }
            })
        },
        handleReset(index, row) {
            this.user.id = this.pager.entities[index].id;
            this.resetShow = true;
        },
        changePage() {
            if (this.pager.paras.key) {
                this.doSearch();
            } else {
                this.loadData();
            }
        },
        doSearch() {
            this.get('/user/search?page=' + this.pager.page + '&key=' + this.pager.paras.key, result => {
                this.pager = result.data.pager;
            })
        },
        saveOrUpdateUser(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    let url = this.user.id ? '/user/update' : '/user/save'
                    this.postBody(url, this.user, result => {
                        if (this.pager.paras.key) {
                            this.doSearch();
                        } else {
                            this.loadData();
                        }
                        this.addEditShow = false;
                    })
                } else {
                    return false;
                }
            })
        },
        formatter(row, column) {
            return moment(row.createTime, "YYYY-MM-DD hh:mm:ss").format('YYYY年MM月DD日');
        },
        handleEdit(index, row) {
            let id = this.pager.entities[index].id;
            this.get('/user/' + id, result => {
                this.user = result.data.user;
                this.user.password = '00000000';
                this.user.rePassword = '00000000';
                this.addEditShow = true;
            })
        },
        handleGrant(index, row, type) {
            this.user.id = this.pager.entities[index].id;
            this.type = type;
            let url = '/user/' + type + "/" + this.pager.entities[index].id;
            this.get(url, result => {
                this.options = [];
                result.data.infos.forEach((item, index) => {
                    this.options.push({
                        key: item.id,
                        label: item.description,
                        selected: item.selected,
                    })
                });
                this.grantShow = true;
            })
        },
        handleDelete(index, row) {
            let id = this.pager.entities[index].id;
            this.$confirm('确认删除用户?', '删除确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.get('/user/delete/' + id, result => {
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    window.setTimeout(() => {
                        if (this.pager.paras.key) {
                            this.doSearch();
                        } else {
                            this.loadData();
                        }
                    }, 2000)
                })
            }).catch(() => {
            });
        },
        loadData() {
            this.get('/user/list?page=' + this.pager.page, result => {
                this.pager = result.data.pager;
                this.pager.paras = { key: '' }
            })
        }
    },
    mounted: function () {
        this.loadData();
    }
}
</script>
<style>
.el-row {
    margin-bottom: 20px;
    &:last-child {
        margin-bottom: 0;
    }
}

.el-col {
    border-radius: 4px;
}

.bg-purple-dark {
    background: #99a9bf;
}

.bg-purple {
    background: #d3dce6;
}

.bg-purple-light {
    background: #e5e9f2;
}

.grid-content {
    border-radius: 4px;
    min-height: 36px;
}

.row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
}
</style>
