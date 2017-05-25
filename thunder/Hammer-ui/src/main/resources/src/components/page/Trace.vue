<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item>
                    <i class="fa fa-history"></i> 登录日志</el-breadcrumb-item>
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
        </el-row>
        <el-table :data="pager.entities" border style="width: 100%">
            <el-table-column prop="id" label="ID" sortable>
            </el-table-column>
            <el-table-column prop="loginTime" label="时间" show-overflow-tooltip :formatter="formatter">
            </el-table-column>
            <el-table-column prop="account" label="用户">
            </el-table-column>
            <el-table-column prop="ip" label="ip">
            </el-table-column>
            <el-table-column prop="ip" label="结果">
             <template scope="scope">
                    <el-tag :type="scope.row.success  ? 'success' : 'danger'" close-transition>{{scope.row.success ? '成功' : '失败'}}</el-tag>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="6" :offset="18">
                <el-pagination style="float:right" layout="prev, pager, next" :total="pager.count" :page-size="pager.pageSize" :current-page.sync="pager.page" v-show="pager.count != 0"  @current-change="changePage">
                </el-pagination>
            </el-col>
        </el-row>
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
            }
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
        formatter(row, column){
             return moment(row.loginTime, "YYYY-MM-DD HH:mm:ss").format('YYYY/MM/DD HH:mm:ss');
        },
        doSearch(){
            this.get('/trace/search?page=' + this.pager.page + '&key=' + this.pager.paras.key, result => {
                this.pager = result.data.pager;
            })
        },
        loadData() {
            this.get('/trace/list?page=' + this.pager.page, result => {
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