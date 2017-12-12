<template>
    <section>
        <el-row>
            <el-col :span="8">
                <el-input placeholder="请输入内容" v-model="searchKey">
                    <el-select v-model="groupId" slot="prepend" placeholder="请选择分组" style="min-width:125px">
                        <el-option
                                v-for="item in groups"
                                :label="item.name"
                                :value="item.id" :key="item.id">
                        </el-option>
                    </el-select>
                    <el-button type="primary" slot="append" icon="search"
                               @click=" pager.pager.pageNumber = 1 ;doSearch()">GO
                    </el-button>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="10">
                <el-button type="primary" icon="el-icon-fa-plus" size="small"
                           @click="addEditShow = true ; codebook={groupId:null};nodes=[]">
                    添加码本
                </el-button>
            </el-col>
        </el-row>

        <el-table :data="pager.dataList" border stripe style="width: 100%">
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
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button title="编辑码本" size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button title="删除码本" size="mini" type="primary"
                                   icon="el-icon-fa-trash" @click="handleDelete(scope.$index,scope.row)"></el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>

        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination background small style="float:right" layout="prev, pager, next"
                               :total="pager.pager.recordCount" :page-size="pager.pager.pageSize"
                               :current-page.sync="pager.pager.pageNumber" v-show="pager.pager.pageCount != 0"
                               @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>

        <el-dialog :title="codebook.id == 0 ? '添加码本' : '编辑码本' " :visible.sync="addEditShow" width="30%">
            <el-form :model="codebook" :rules="$rules" ref="codebookForm">
                <el-form-item label="分组" :label-width="formLabelWidth" prop="groupId">
                    <el-select v-model="codebook.groupId" placeholder="请选择码本分组" @change="loadTop" prop="groupId">
                        <el-option v-for="item in groups" :key="item.name" :label="item.description" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="上级" :label-width="formLabelWidth" prop="parentId">
                    <el-tree :data="nodes" show-checkbox check-strictly lazy :load="loadNode" node-key="id" ref="tree"
                             highlight-current :props="defaultProps" @check-change="check">
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
                <el-button @click="addEditShow = false ;">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateCodebook('codebookForm')">确 定</el-button>
            </div>
        </el-dialog>

    </section>
</template>
<style>

</style>
<script>
    export default {
        data() {
            return {
                groupId: "",
                nodes: [],
                defaultProps: {
                    children: "children",
                    label: "value"
                },
                searchKey: "",
                pager: {
                    pager: {
                        pageCount: 0,
                        pageNumber: 1,
                        pageSize: 15,
                        recordCount: 0
                    }
                },
                addEditShow: false,
                groups: [],
                codebook: {
                    id: 0,
                    name: "",
                    value: "",
                    groupId: null,
                    parentId: 0,
                    index: 0
                },
                formLabelWidth: "120px"
            };
        },
        methods: {
            check(node, s, l) {
                if (this.$refs.tree.getCheckedNodes().length > 1) {
                    this.$message("只能选择一个父节点");
                    this.$refs.tree.setChecked(node, false);
                }
            },
            remote(row, callback) {
                this.$api.CodeBook.sub(row.id, result => {
                    const data = [];
                    result.codes.forEach(item => {
                        item.children = [{}];
                        item.depth = row.depth ? row.depth + 1 : 1;
                        data.push(item);
                    });
                    callback(data);
                });
            },
            loadGroups() {
                this.$api.CodeBookGroup.all(result => {
                    this.groups = result.groups;
                    this.loadData();
                });
            },
            loadTop() {
                if (this.codebook.groupId) {
                    this.$api.CodeBook.top(this.codebook.groupId, result => {
                        this.nodes = result.codes;
                    });
                }
            },
            loadNode(node, resolve) {
                if (node.data.id) {
                    this.$api.CodeBook.sub(node.data.id, result => {
                        resolve(result.codes);
                    });
                }
            },
            changePage() {
                if (this.searchKey) {
                    this.doSearch();
                } else {
                    this.loadData();
                }
            },
            doSearch() {
                this.$api.CodeBook.search(
                    this.pager.pager.pageNumber,
                    this.groupId,
                    this.searchKey,
                    result => {
                        this.pager = result.pager;
                        this.pager.dataList.forEach(item => {
                            item.children = [{}];
                            item.depth = 1;
                        });
                    }
                );
            },
            saveOrUpdateCodebook(formName) {
                if (this.$refs.tree.getCheckedNodes().length) {
                    this.codebook.parentId = this.$refs.tree.getCheckedNodes()[0].id;
                }
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        var callback = result => {
                            this.changePage();
                            this.addEditShow = false;
                        };
                        this.codebook.id
                            ? this.$api.CodeBook.update(this.codebook, callback)
                            : this.$api.CodeBook.save(this.codebook, callback);
                    } else {
                        return false;
                    }
                });
            },
            handleEdit(index, row) {
                this.codebook = row;
                this.loadTop();
                this.addEditShow = true;
            },
            handleDelete(index, row) {
                let id = row.id;
                this.$confirm("确认删除码本数据?", "删除确认", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {
                    this.$api.CodeBook.delete(id, result => {
                        this.$message({
                            type: "success",
                            message: "删除成功!"
                        });
                        window.setTimeout(() => {
                            this.changePage();
                        }, 2000);
                    });
                });
            },
            loadData() {
                this.$api.CodeBook.list(this.pager.pager.pageNumber, result => {
                    this.pager = result.pager;
                    this.pager.dataList.forEach(item => {
                        item.children = [{}];
                        item.depth = 1;
                    });
                });
            }
        },
        created: function () {
            this.loadGroups();
        }
    };
</script>