<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-lock"></i> 角色</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-list"></i> 角色列表</el-breadcrumb-item>
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
                <el-button type="primary" icon="plus" @click="addEditShow = true">添加角色</el-button>
            </el-col>
        </el-row>
        <el-table :data="pager.entities" border style="width: 100%">
            <el-table-column prop="id" label="ID" sortable>
            </el-table-column>
            <el-table-column prop="name" label="名称">
            </el-table-column>
            <el-table-column prop="description" label="描述">
            </el-table-column>
            <el-table-column prop="status" label="状态">
                <template scope="scope">
                    <el-tag :type="scope.row.installed  ? 'success' : 'danger'" close-transition>{{scope.row.installed ? '内置' : '自由'}}</el-tag>
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
                                    <i class="fa fa-edit"></i> 编辑角色</div>
                            </el-dropdown-item>
                            <el-dropdown-item>
                                <div @click="handleGrant(scope.$index,scope.row)">
                                    <i class="fa fa-bolt"></i> 设置权限</div>
                            </el-dropdown-item>
                            <el-dropdown-item v-show="!scope.row.installed">
                                <div @click="handleDelete(scope.$index,scope.row)">
                                    <i class="fa fa-trash-o"></i> 删除角色</div>
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
        <el-dialog :title="role.id == 0 ? '添加角色' : '编辑角色' " :visible.sync="addEditShow" size="tiny">
            <el-form :model="role" :rules="checkRole" ref="roleForm">
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="role.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input v-model="role.description" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateRole('roleForm')">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="设置权限" :visible.sync="grantShow">
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
            grantShow: false,
            role: {
                id: 0,
                name: '',
                description: '',
                installed: false
            },
            checkRole: {
                name: [
                    { required: true, message: '请输入角色名称', trigger: 'blur' }
                ],
                description: [
                    { required: true, message: '请输入角色描述', trigger: 'blur' }
                ]
            },
            formLabelWidth: '120px'
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
            let data = {
                roleId: this.role.id,
                grantIds: this.selected
            }
            this.postBody('/role/grant/', data, result => {
                this.$message({
                    type: 'success',
                    message: '授权成功!'
                });
                window.setTimeout(() => {
                    this.grantShow = false;
                }, 2000)
            })
        },
        changePage() {
            if (this.pager.paras.key) {
                this.doSearch();
            } else {
                this.loadData();
            }
        },
        doSearch() {
            this.get('/role/search?page=' + this.pager.page + '&key=' + this.pager.paras.key, result => {
                this.pager = result.data.pager;
            })
        },
        saveOrUpdateRole(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    let url = this.role.id ? '/role/update' : '/role/save'
                    this.postBody(url, this.role, result => {
                        this.changePage();
                        this.addEditShow = false;
                    })
                } else {
                    return false;
                }
            })
        },
        handleEdit(index, row) {
            let id = this.pager.entities[index].id;
            this.get('/role/' + id, result => {
                this.role = result.data.role;
                this.addEditShow = true;
            })
        },
        handleGrant(index, row) {
            this.role.id = this.pager.entities[index].id;
            let url = '/role/permission/' + this.pager.entities[index].id;
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
            this.$confirm('确认删除角色?', '删除确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.get('/role/delete/' + id, result => {
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    window.setTimeout(() => {
                        this.changePage();
                    }, 2000)
                })
            }).catch(() => {
            });
        },
        loadData() {
            this.get('/role/list?page=' + this.pager.page, result => {
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