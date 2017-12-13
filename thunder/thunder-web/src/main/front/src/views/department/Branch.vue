<
<template>
    <section>
        <el-row>
            <el-col :span="8">
                <el-input placeholder="请输入内容" v-model="searchKey">
                    <el-button type="primary" slot="append" icon="search"
                               @click=" pager.pager.pageNumber = 1 ;doSearch()">GO
                    </el-button>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="10">
                <el-button type="primary" icon="el-icon-fa-plus" size="small" @click="addBranch">
                    添加机构
                </el-button>
            </el-col>
        </el-row>
        <el-table :data="pager.dataList" border stripe style="width: 100%">
            <el-table-tree-column :remote="remote" file-icon="icon icon-file" folder-icon="icon icon-folder"
                                  parentKey="parentId" prop="id" label="ID" header-align="center" align="center"
                                  width="125"></el-table-tree-column>
            <el-table-column prop="name" label="名称" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button title="编辑机构" size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button title="删除机构" size="mini" type="primary"
                                   icon="el-icon-fa-trash" @click="handleDelete(scope.$index,scope.row)"></el-button>
                    </el-button-group>
                </template>
            </el-table-column>
        </el-table>

        <el-row>
            <el-col
                    :span="6"
                    :offset="18">
                <el-pagination
                        background
                        small style="float:right"
                        layout="prev, pager, next"
                        :total="pager.pager.recordCount"
                        :page-size="pager.pager.pageSize"
                        :current-page.sync="pager.pager.pageNumber"
                        v-show="pager.pager.pageCount != 0"
                        @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>
        <el-dialog
                :title="branch.id == 0 ? '添加机构' : '编辑机构' "
                :visible.sync="addEditShow"
                @close="resetBranch"
                width="45%">
            <el-form
                    :model="branch"
                    :rules="$rules"
                    ref="branchForm">
                <el-form-item label="上级" :label-width="formLabelWidth" prop="parentId">
                    <el-tree
                            show-checkbox
                            check-strictly
                            lazy
                            :load="loadBranch"
                            node-key="id"
                            ref="tree"
                            highlight-current
                            :props="defaultProps"
                            :default-checked-keys="[branch.parentId]"
                            @check-change="check">
                    </el-tree>
                </el-form-item>
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="branch.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="联系电话" :label-width="formLabelWidth" prop="phone">
                    <el-input v-model="branch.phone" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input type="textarea" :maxlength="500"
                              :autosize="{ minRows: 4, maxRows: 8}"
                              v-model="branch.description" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="地址信息" :label-width="formLabelWidth" prop="address">
                    <ali-map :center="location"
                             @locationSelected="notify"
                             ref="aliMap"></ali-map>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; resetBranch()">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateBranch('branchForm')">确 定</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<style>

</style>

<script>
    import AliMap from "@/components/Map/AliMap";

    export default {
        data() {
            return {
                topBranch: [],
                defaultProps: {
                    children: "children",
                    label: "name"
                },
                searchKey: "",
                addEditShow: false,
                pager: {
                    pager: {
                        pageCount: 0,
                        pageNumber: 1,
                        pageSize: 15,
                        recordCount: 0
                    }
                },
                branch: {
                    code: this.$utils.guid()
                },
                formLabelWidth: "120px"
            };
        },
        computed: {
            location() {
                return [this.branch.longitude || 0, this.branch.latitude || 0];
            }
        },
        methods: {
            resetBranch() {
                this.branch = {
                    code: this.$utils.guid()
                }
            },
            addBranch() {
                this.addEditShow = true;
                this.$refs.aliMap && this.$refs.aliMap.geocoder(0, 0);
            },
            check(data, checked, indeterminate) {
                if (this.$refs.tree.getCheckedNodes().length > 1 && checked) {
                    this.$message("只能选择一个上级机构");
                    this.$refs.tree.setChecked(data, false);
                } else if (checked) {
                    this.branch.parentId = data.id;
                } else {
                    this.branch.parentId = 0;
                }
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
            handleEdit(index, row) {
                this.addEditShow = true;
                this.$refs.aliMap && this.$refs.aliMap.geocoder(row.longitude, row.latitude);
                this.branch = row;
            },
            handleDelete(index, row) {
                this.$confirm("确认删除机构?", "删除确认", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }).then(() => {
                    this.$api.Branch.delete(row.id, result => {
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
            saveOrUpdateBranch(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        var callback = result => {
                            this.changePage();
                            this.addEditShow = false;
                        };
                        this.$confirm("你选择的地址是 " + this.branch.address + " ?", "地址信息确认", {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            type: "warning"
                        }).then(() => {
                            this.branch.id ?
                                this.$api.Branch.update(this.branch, callback) :
                                this.$api.Branch.save(this.branch, callback);
                        });
                    } else {
                        return false;
                    }
                });
            },
            remote(row, callback) {
                this.$api.Branch.sub(row.id, result => {
                    const data = [];
                    result.branchs.forEach(item => {
                        item.children = [{}];
                        item.depth = row.depth ? row.depth + 1 : 1;
                        data.push(item);
                    });
                    callback(data);
                });
            },
            changePage() {
                if (this.searchKey) {
                    this.doSearch();
                } else {
                    this.loadData();
                }
            },
            doSearch() {
                this.$api.Branch.search(
                    this.pager.pager.pageNumber,
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
            loadData() {
                this.$api.Branch.list(this.pager.pager.pageNumber, result => {
                    this.pager = result.pager;
                    this.pager.dataList.forEach(item => {
                        item.children = [{}];
                        item.depth = 1;
                    });
                });
            },
            notify(rs) {
                this.branch.longitude = rs.point.lng;
                this.branch.latitude = rs.point.lat;
                this.branch.province = rs.address.province;
                this.branch.city = rs.address.city;
                this.branch.district = rs.address.district;
                this.branch.address = rs.formattedAddress;
            }
        },
        created() {
            this.loadData();
        },
        components: {
            AliMap
        }
    };
</script>