<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-cog"></i> 配置</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-list"></i> 配置列表</el-breadcrumb-item>
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
                <el-button type="primary" icon="plus" @click="addEditShow = true">添加配置</el-button>
            </el-col>
        </el-row>
        <el-table :data="pager.entities" border style="width: 100%">
            <el-table-column prop="id" label="ID" sortable>
            </el-table-column>
            <el-table-column prop="name" label="名称">
            </el-table-column>
             <el-table-column prop="value" label="值" show-overflow-tooltip>
            </el-table-column>
            <el-table-column prop="description" label="描述">
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
                                    <i class="fa fa-edit"></i> 编辑配置</div>
                            </el-dropdown-item>
                            <el-dropdown-item v-show="!scope.row.installed">
                                <div @click="handleDelete(scope.$index,scope.row)">
                                    <i class="fa fa-trash-o"></i> 删除配置</div>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination style="float:right" layout="prev, pager, next" :total="pager.count" :page-size="pager.pageSize" :current-page.sync="pager.page" v-show="pager.count != 0"  @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>
        <!-- 弹框区域-->
        <el-dialog :title="config.id == 0 ? '添加配置' : '编辑配置' " :visible.sync="addEditShow" size="tiny">
            <el-form :model="config" :rules="checkConfig" ref="configForm">
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="config.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="值" :label-width="formLabelWidth" prop="value">
                    <el-input v-model="config.value" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input v-model="config.description" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateConfig('configForm')">确 定</el-button>
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
                paras:{
                    key:'1'
                }
            },
            addEditShow: false,
            config: {
                id: 0,
                name: '',
                vaule:'',
                description: '',
                installed: false
            },
            checkConfig: {
                name: [
                    { required: true, message: '请输入配置名称', trigger: 'blur' }
                ],
                value:[
                     { required: true, message: '请输入配置值', trigger: 'blur' }
                ]
            },
            formLabelWidth: '120px'
        }
    },
    watch: {},
    methods: {
        changePage(){
            if(this.pager.paras.key){
                this.doSearch();
            }else{
                this.loadData();
            }
        },
        doSearch(){
            this.get('/config/search?page=' + this.pager.page + '&key=' + this.pager.paras.key, result => {
                this.pager = result.data.pager;
            })
        },
        saveOrUpdateConfig(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    let url = this.config.id ? '/config/update' : '/config/save'
                    this.postBody(url, this.config, result => {
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
            this.get('/config/' + id, result => {
                this.config = result.data.config;
                this.addEditShow = true;
            })
        },
        handleDelete(index, row) {
            let id = this.pager.entities[index].id;
            this.$confirm('确认删除配置?', '删除确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.get('/config/delete/' + id, result => {
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    window.setTimeout(()=>{
                        this.changePage();
                    },2000)
                })
            }).catch(() => {
            });
        },
        loadData() {
            this.get('/config/list?page=' + this.pager.page, result => {
                this.pager = result.data.pager;
                this.pager.paras={key:''}
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