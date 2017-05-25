<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-cubes"></i> 码本</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-list"></i> 码本列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <el-row>
            <el-col :span="8">
                <el-input placeholder="请输入内容" v-model="pager.paras.key" >
                    <el-select v-model="groupId" slot="prepend" placeholder="请选择分组" style="min-width:125px">
                       <el-option
                            v-for="item in groups"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                    <el-button type="primary" slot="append" icon="search" @click=" pager.page = 1 ;doSearch()">GO</el-button>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="10">
                <el-button type="primary" icon="plus" @click="addEditShow = true ; codebook={groupId:null};nodes=[]">添加码本</el-button>
            </el-col>
        </el-row>
        <el-table :data="pager.entities" border style="width: 100%">
            <el-table-tree-column 
            :remote="remote"
            file-icon="icon icon-file" 
            folder-icon="icon icon-folder" 
            parentKey="parentId"
            prop="id" label="ID"></el-table-tree-column>
            <el-table-column prop="name" label="Key">
            </el-table-column>
            <el-table-column prop="value" label="Value">
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
                                    <i class="fa fa-edit"></i> 编辑码本</div>
                            </el-dropdown-item>
                            <el-dropdown-item v-show="!scope.row.installed">
                                <div @click="handleDelete(scope.$index,scope.row)">
                                    <i class="fa fa-trash-o"></i> 删除码本</div>
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
        <el-dialog :title="codebook.id == 0 ? '添加码本' : '编辑码本' " :visible.sync="addEditShow" size="tiny">
            <el-form :model="codebook" :rules="checkCodebook" ref="codebookForm">
                <el-form-item label="分组" :label-width="formLabelWidth" prop="groupId">
                    <el-select v-model="codebook.groupId" placeholder="请选择码本分组" @change="loadTop" prop="groupId">
                        <el-option v-for="item in groups" :key="item.name" :label="item.description" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="上级" :label-width="formLabelWidth" prop="parentId">
                    <el-tree :data="nodes" show-checkbox check-strictly lazy :load="loadNode" node-key="id" ref="tree" highlight-current :props="defaultProps" @check-change="check">
                    </el-tree>
                </el-form-item>
                <el-form-item label="Key" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="codebook.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="Value" :label-width="formLabelWidth" prop="value">
                    <el-input v-model="codebook.value" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="序号" :label-width="formLabelWidth" prop="index">
                    <el-input v-model="codebook.index" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateCodebook('codebookForm')">确 定</el-button>
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
            groupId:'',
            nodes: [],
            defaultProps: {
                children: 'children',
                label: 'value'
            },
            searchKey: '',
            pager: {
                page: 1,
                pageSize: 15,
                paras: {
                    key: '1'
                }
            },
            addEditShow: false,
            groups: [],
            codebook: {
                id: 0,
                name: '',
                value: '',
                groupId: null,
                parentId: 0,
                index: 0
            },
            checkCodebook: {
                name: [
                    { required: true, message: '请输入码本数据名称', trigger: 'blur' }
                ],
                value: [
                    { required: true, message: '请输入码本数据值', trigger: 'blur' }
                ],
                groupId: [
                    { type: 'number', required: true, message: '请选择数据分组', trigger: 'blur' }
                ]
            },
            formLabelWidth: '120px'
        }
    },
    watch: {},
    methods: {
        check(node,s,l){
            if(this.$refs.tree.getCheckedNodes().length > 1){
                this.$message('只能选择一个父节点');
                this.$refs.tree.setChecked(node,false);
            }
        },
        remote(row,callback){
            this.get('/codebook/sub/'+row.id,result=>{
                const data = [];
                 result.data.codes.forEach(item=>{
                    item.children = [{}];
                    item.depth = row.depth ? row.depth+1:1;
                     data.push(item)
                 })
                // row.children = data;
                callback(data);
            })
        },
        loadGroups() {
            this.get('/group/all', result => {
                this.groups = result.data.groups;
            })
        },
        loadTop() {
            if (this.codebook.groupId) {
                this.get('/codebook/top/' + this.codebook.groupId, result => {
                    this.nodes = result.data.codes;
                })
            }
        },
        loadNode(node, resolve) {
            if (node.data.id) {
                this.get('/codebook/sub/' + node.data.id, result => {
                    resolve(result.data.codes)
                })
            }
        },
        changePage() {
            if (this.pager.paras.key) {
                this.doSearch();
            } else {
                this.loadData();
            }
        },
        doSearch() {
            this.get('/codebook/search?page=' + this.pager.page +'&group='+this.groupId + '&key=' + this.pager.paras.key, result => {
                console.log(result);
                 this.pager = result.data.pager;
                this.pager.entities.forEach(item=>{
                    item.children = [{}];
                    item.depth = 1;
                });
            })
        },
        saveOrUpdateCodebook(formName) {
            debugger;
            if (this.$refs.tree.getCheckedNodes().length) {
                this.codebook.parentId = this.$refs.tree.getCheckedNodes()[0].id
            }
            this.$refs[formName].validate(valid => {
                if (valid) {
                    let url = this.codebook.id ? '/codebook/update' : '/codebook/save'
                    this.postBody(url, this.codebook, result => {
                        this.changePage();
                        this.addEditShow = false;
                    })
                } else {
                    return false;
                }
            });
        },
        handleEdit(index, row) {
            let id = row.id;
            this.get('/codebook/' + id, result => {
                this.codebook = result.data.codebook;
                this.loadTop();
                this.addEditShow = true;
            })
        },
        handleDelete(index, row) {
            let id = row.id;
            this.$confirm('确认删除码本数据?', '删除确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.get('/codebook/delete/' + id, result => {
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
            this.get('/codebook/list?page=' + this.pager.page, result => {
                this.pager = result.data.pager;
                this.pager.paras = { key: '' };
                this.pager.entities.forEach(item=>{
                    item.children = [{}];
                    item.depth = 1;
                });
            })
        }
    },
    mounted: function () {
        this.loadData();
        this.loadGroups();
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