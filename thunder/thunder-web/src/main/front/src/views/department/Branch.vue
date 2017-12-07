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
                    <el-button type="primary" slot="append" icon="search" @click=" pager.page = 1 ;doSearch()">GO
                    </el-button>
                </el-input>
            </el-col>
            <el-col :span="6" :offset="10">
                <el-button type="primary"  icon="el-icon-fa-plus" size="small" @click="addEditShow = true ; Branch={groupId:null};nodes=[]">
                    添加机构
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
                        <el-button title="编辑机构"  size="mini" type="primary" icon="el-icon-fa-edit"
                                   @click="handleEdit(scope.$index,scope.row)"></el-button>
                        <el-button title="删除机构"  size="mini" type="primary"
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

        <el-dialog :title="!Branch.id ||Branch.id == 0 ? '添加机构' : '编辑机构' " :visible.sync="addEditShow" width="35%">
            <el-form :model="Branch" :rules="$rules" ref="BranchForm">
                <el-form-item label="上级机构" :label-width="formLabelWidth" prop="parentId">
                    <el-tree :data="nodes" show-checkbox check-strictly lazy :load="loadNode" node-key="id" ref="tree"
                             highlight-current :props="defaultProps" @check-change="check">
                    </el-tree>
                </el-form-item>
                <el-form-item label="名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="Branch.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="电话" :label-width="formLabelWidth" prop="phone">
                    <el-input v-model="Branch.phone" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" :label-width="formLabelWidth" prop="description">
                    <el-input v-model="Branch.description" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="位置" :label-width="formLabelWidth" prop="index">
                    <b-map-component :seachSpan="20" :ak="ak" :mapWidth="mapWidth"  @notify="notify"></b-map-component>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="addEditShow = false ; user = {installed:false}">取 消</el-button>
                <el-button type="primary" @click="saveOrUpdateBranch('BranchForm')">确 定</el-button>
            </div>
        </el-dialog>

    </section>
</template>
<style>

</style>
<script>
import BMapComponent from "@/components/Map/BaiduMap.vue";
export default {
  data() {
    return {
      ak: "CRHkMGE7Db1USNSyFXqVDmdv",
      mapWidth: 400,
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
      Branch: {
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
      this.$api.Branch.sub(row.id, result => {
        const data = [];
        result.codes.forEach(item => {
          item.children = [{}];
          item.depth = row.depth ? row.depth + 1 : 1;
          data.push(item);
        });
        callback(data);
      });
    },
    loadTop() {
      if (this.Branch.groupId) {
        this.$api.Branch.top(this.Branch.groupId, result => {
          this.nodes = result.codes;
        });
      }
    },
    loadNode(node, resolve) {
      if (node.data.id) {
        this.$api.Branch.sub(node.data.id, result => {
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
      this.$api.Branch.search(
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
    saveOrUpdateBranch(formName) {
      if (this.$refs.tree.getCheckedNodes().length) {
        this.Branch.parentId = this.$refs.tree.getCheckedNodes()[0].id;
      }
      this.$refs[formName].validate(valid => {
        if (valid) {
          var callback = result => {
            this.changePage();
            this.addEditShow = false;
          };
          this.Branch.id
            ? this.$api.Branch.update(this.Branch, callback)
            : this.$api.Branch.save(this.Branch, callback);
        } else {
          return false;
        }
      });
    },
    handleEdit(index, row) {
      this.Branch = row;
      this.loadTop();
      this.addEditShow = true;
    },
    handleDelete(index, row) {
      let id = row.id;
      this.$confirm("确认删除机构数据?", "删除确认", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$api.Branch.delete(id, result => {
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
      this.$api.Branch.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
        this.pager.dataList.forEach(item => {
          item.children = [{}];
          item.depth = 1;
        });
      });
    },
    notify(rs) {
      console.log(rs);
    }
  },
  created: function() {
    this.loadData();
  },
  components: {
    BMapComponent
  }
};
</script>
 <!--template>
     <section>
         机构管理
         <b-map-component :ak="ak" :mapWidth="mapWidth"  @notify="notify"></b-map-component>
     </section>
 </template>
 <style>

 </style --!>
 <!--script>
// import BMapComponent from "@/components/Map/BaiduMap.vue";
// export default {
//   data() {
//     return {
//       ak: "CRHkMGE7Db1USNSyFXqVDmdv",
//       mapWidth: 800
//     };
//   },
//   methods: {
//     notify(rs) {
//       console.log(rs);
//     }
//   },
//   created() {
//       console.log(this.$utils.guid())
//   },
//   components: {
//     BMapComponent
//   }
// };
// </script --!>