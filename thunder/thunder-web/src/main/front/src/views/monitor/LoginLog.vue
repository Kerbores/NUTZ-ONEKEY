<template>
    <section>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="searchKey" icon="search">
                    <div slot="append">
                        <el-button type="primary" icon="search" @click=" pager.pager.pageNumber = 1 ;doSearch()">GO</el-button>
                    </div>
                </el-input>
            </el-col>
        </el-row>
        <el-table :data="pager.dataList" border style="width: 100%">
            <el-table-column prop="id" label="ID" header-align="center" align="center" width="55">
            </el-table-column>
            <el-table-column prop="loginTime" label="时间" show-overflow-tooltip :formatter="formatter" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="account" label="用户" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="ip" label="ip" show-overflow-tooltip header-align="center" align="center">
            </el-table-column>
            <el-table-column prop="ip" label="结果" show-overflow-tooltip header-align="center" align="center">
             <template slot-scope="scope">
                    <el-tag :type="scope.row.success  ? 'success' : 'danger'" close-transition>{{scope.row.success ? '成功' : '失败'}}</el-tag>
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
    </section>
</template>
<style>

</style>
<script>
import moment from "moment";
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
      }
    };
  },
  watch: {},
  methods: {
    formatter(row, column) {
      return moment(row.loginTime, "YYYY-MM-DD HH:mm:ss").format("YYYY/MM/DD HH:mm:ss"
      );
    },
    changePage() {
      if (this.searchKey) {
        this.doSearch();
      } else {
        this.loadData();
      }
    },
    doSearch() {
      this.$api.Trace.search(
        this.pager.pager.pageNumber,
        this.searchKey,
        result => {
          this.pager = result.pager;
        }
      );
    },
    loadData() {
      this.$api.Trace.list(this.pager.pager.pageNumber, result => {
        this.pager = result.pager;
      });
    }
  },
  mounted: function() {
    this.loadData();
  }
};
</script>