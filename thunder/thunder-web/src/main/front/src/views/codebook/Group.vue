<template>
    <section>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="searchKey" prefix-icon="el-icon-fa-search">
                    <div slot="append">
                        <el-button type="primary" icon="el-icon-fa-search"
                                   @click=" pager.pager.pageNumber = 1 ;doSearch()"></el-button>
                    </div>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="12">
                <el-button type="primary" icon="el-icon-fa-plus" @click="addGroup" size="small">添加码本分组</el-button>
            </el-col>
        </el-row>

        <el-table :data="pager.dataList" border stripe style="width: 100%">
            <el-table-column prop="id" label="ID" header-align="center" align="center" width="55">
            </el-table-column>
            <el-table-column prop="name" label="名称" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="status" label="状态" show-overflow-tooltip header-align="center" align="center">
                <template slot-scope="scope">
                    <el-tag :type="!scope.row.delete  ? 'success' : 'danger'" close-transition>{{!scope.row.delete ?
                        '可用' : '禁用'}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button title="编辑码本分组" size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button :title="(scope.row.delete ? '启用':'禁用') +'码本分组'" size="mini" type="primary"
                                   :icon="scope.row.delete ? 'el-icon-fa-recycle': 'el-icon-fa-trash'"
                                   @click="handleDelete(scope.$index,scope.row)"></el-button>
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
        <!-- 弹框区域-->
        <el-dialog :title="Group.id == 0 ? '添加码本分组' : '编辑码本分组' " :visible.sync="addEditShow" width="30%">
            <el-form :model="Group" :rules="$rules" ref="GroupForm">
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="Group.name" auto-complete="off" placeholder="请填写码本分组名称"
                              suffix-icon="el-icon-fa-vcard"></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input type="textarea" :maxlength="500"
                              :autosize="{ minRows: 4, maxRows: 8}" v-model="Group.description" auto-complete="off"
                              placeholder="请填写码本分组描述" suffix-icon="el-icon-fa-file-word-o"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateGroup('GroupForm')">确 定</el-button>
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
                searchKey: "",
                pager: {
                    pager: {
                        pageCount: 0,
                        pageNumber: 1,
                        pageSize: 15,
                        recordCount: 0
                    }
                },
                selected: [],
                addEditShow: false,
                Group: {
                    id: 0,
                    name: "",
                    description: ""
                },
                formLabelWidth: "120px"
            };
        },
        methods: {
            addGroup() {
                this.Group = {
                    id: 0,
                    name: "",
                    description: ""
                };
                this.addEditShow = true;
            },
            changePage() {
                if (this.searchKey) {
                    this.doSearch();
                } else {
                    this.loadData();
                }
            },
            doSearch() {
                this.$api.CodeBookGroup.search(
                    this.pager.pager.pageNumber,
                    this.searchKey,
                    result => {
                        this.pager = result.pager;
                    }
                );
            },
            saveOrUpdateGroup(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        var callback = result => {
                            this.changePage();
                            this.addEditShow = false;
                        };
                        this.Group.id
                            ? this.$api.CodeBookGroup.update(this.Group, callback)
                            : this.$api.CodeBookGroup.save(this.Group, callback);
                    } else {
                        return false;
                    }
                });
            },
            handleEdit(index, row) {
                this.Group = row;
                this.addEditShow = true;
            },
            handleDelete(index, row) {
                this.$confirm(
                    "确认" + (row.delete ? "启用" : "禁用") + "码本分组?",
                    (row.delete ? "启用" : "禁用") + "确认",
                    {
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        type: "warning"
                    }
                ).then(() => {
                    this.$api.CodeBookGroup.delete(row.id, !row.delete, result => {
                        this.$message({
                            type: "success",
                            message: (row.delete ? "启用" : "禁用") + "成功!"
                        });
                        window.setTimeout(() => {
                            this.changePage();
                        }, 2000);
                    });
                });
            },
            loadData() {
                this.$api.CodeBookGroup.list(this.pager.pager.pageNumber, result => {
                    this.pager = result.pager;
                });
            }
        },
        created: function () {
            this.loadData();
        }
    };
</script>