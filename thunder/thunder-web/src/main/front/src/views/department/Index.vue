<template>
    <section>
        <el-row>
            <el-col :span="8">
                <el-input placeholder="请输入内容" v-model="searchKey">
                    <el-select v-model="branchId" slot="prepend" placeholder="请选择机构" style="min-width:125px">
                        <el-option
                                v-for="item in branchs"
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
                <el-button type="primary" icon="el-icon-fa-plus" size="small" @click="addDepartment">
                    添加部门
                </el-button>
            </el-col>
        </el-row>

        <el-table :data="pager.dataList" border stripe style="width: 100%">
            <el-table-column prop="id" label="ID" header-align="center" align="center" width="55">
            </el-table-column>
            <el-table-column prop="name" label="名称" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button title="编辑部门" size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button title="删除部门" size="mini" type="primary"
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

        <el-dialog
                :title="department.id == 0 ? '添加部门' : '编辑部门' "
                :visible.sync="addEditShow"
                @close="resetDepartment"
                width="30%">
            <el-form
                    :model="department"
                    :rules="$rules"
                    ref="DepartmentForm">
                <el-form-item label="归属机构" :label-width="formLabelWidth" prop="parentId">
                    <el-tree
                            show-checkbox
                            check-strictly
                            lazy
                            :load="loadBranch"
                            node-key="id"
                            ref="tree"
                            highlight-current
                            :props="defaultProps"
                            :default-checked-keys="[department.branchId]"
                            @check-change="check">
                    </el-tree>
                </el-form-item>
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="department.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input type="textarea" :maxlength="500"
                              :autosize="{ minRows: 4, maxRows: 8}"
                              v-model="department.description" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; resetDepartment()">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateDepartment('DepartmentForm')">确 定</el-button>
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
                branchId:"",
                branchs:[],
                searchKey: '',
                pager: {
                    pager: {
                        pageCount: 0,
                        pageNumber: 1,
                        pageSize: 15,
                        recordCount: 0
                    }
                },
                defaultProps: {
                    children: "children",
                    label: "name"
                },
                formLabelWidth: '120px',
                addEditShow: false,
                department: {}
            }
        },
        methods: {
            check(data, checked, indeterminate) {
                if (this.$refs.tree.getCheckedNodes().length > 1 && checked) {
                    this.$message("只能选择一个上级机构");
                    this.$refs.tree.setChecked(data, false);
                } else if (checked) {
                    this.department.branchId = data.id;
                } else {
                    this.department.branchId = 0;
                }
            },
            resetDepartment() {
                this.department = {}
            },
            loadBranch(node, resolve) {
                if (node.level === 0) {
                    this.$api.Branch.top(result => {
                        resolve(result.tops);
                    });
                } else {
                    this.$api.Branch.sub(node.data.id, result => {
                        resolve(result.branchs);
                    });
                }
            },
            addDepartment() {
                this.addEditShow = true;
            },
            saveOrUpdateDepartment(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        var callback = result => {
                            this.changePage();
                            this.addEditShow = false;
                        };
                        this.department.id ?
                            this.$api.Department.update(this.department, callback) :
                            this.$api.Department.save(this.department, callback);
                    } else {
                        return false;
                    }
                });
            },
            doSearch() {
                this.$api.Department.search(
                    this.pager.pager.pageNumber,
                    this.branchId,
                    this.searchKey,
                    result => {
                        this.pager = result.pager;
                    }
                );
            },
            changePage() {
                this.searchKey?this.doSearch():this.loadData();
            },
            handleEdit(index, row) {
                this.department = row;
                this.addEditShow = true;
            },
            handleDelete(index, row) {
                this.$confirm("确认删除部门?", "删除确认", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {
                    this.$api.Department.delete(row.id, result => {
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
                this.$api.Department.list(this.pager.pager.pageNumber, result => {
                    this.pager = result.pager
                })
            },
            loadBranchs(){
                this.$api.Branch.all(result=>{
                    this.branchs = result.branchs;
                    this.loadData();
                })
            }
        },
        created() {
            this.loadBranchs()
        }
    }
</script>