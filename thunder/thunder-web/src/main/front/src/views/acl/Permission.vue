<template>
    <section>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="searchKey" prefix-icon="el-icon-fa-search">
                    <div slot="append">
                        <el-button type="primary" icon="el-icon-fa-search"
                                   @click=" pager.page = 1 ;doSearch()"></el-button>
                    </div>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="12">
                <el-button type="primary" icon="el-icon-fa-plus" @click="addPermission" size="small">添加权限</el-button>
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
                    <el-tag :type="scope.row.installed  ? 'success' : 'danger'" close-transition>{{scope.row.installed ?
                        '内置' : '自由'}}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" show-overflow-tooltip header-align="center" align="center">
                <template slot-scope="scope">
                    <el-button-group>
                        <el-button title="编辑权限" v-if="!scope.row.installed" size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button title="删除权限" v-if="!scope.row.installed" size="mini" type="primary"
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
        <!-- 弹框区域-->
        <el-dialog :title="Permission.id == 0 ? '添加权限' : '编辑权限' " :visible.sync="addEditShow" width="30%">
            <el-form :model="Permission" :rules="$rules" ref="PermissionForm">
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="Permission.name" auto-complete="off" placeholder="请填写权限名称" suffix-icon="el-icon-fa-vcard" ></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input type="textarea" :maxlength="500"
                    :autosize="{ minRows: 4, maxRows: 8}" v-model="Permission.description" auto-complete="off" placeholder="请填写权限描述" suffix-icon="el-icon-fa-file-word-o"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdatePermission('PermissionForm')">确 定</el-button>
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
      Permission: {
        id: 0,
        name: "",
        description: "",
        installed: false
      },
      formLabelWidth: "120px"
    };
  },
  methods: {
    addPermission() {
      this.Permission = {
        id: 0,
        name: "",
        description: "",
        installed: false
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
      this.$api.Permission.search(this.pager.pager.pageNumber, this.searchKey, result => {
        this.pager = result.pager;
      });
    },
    saveOrUpdatePermission(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          var callback = result => {
            this.changePage();
            this.addEditShow = false;
          };
          this.Permission.id
            ? this.$api.Permission.update(this.Permission, callback)
            : this.$api.Permission.save(this.Permission, callback);
        } else {
          return false;
        }
      });
    },
    handleEdit(index, row) {
      this.Permission = row;
      this.addEditShow = true;
    },
    handleDelete(index, row) {
      this.$confirm("确认删除权限?", "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$api.Permission.delete(row.id, result => {
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
      this.$api.Permission.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>