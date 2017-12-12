<template>
    <section>
        <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
            <el-tab-pane label="概况" name="main">
                <template>
                    <el-table
                            :data="main"
                            border
                            style="width: 100%">
                        <el-table-column
                                prop="key"
                                label="属性" width="180">
                        </el-table-column>
                        <el-table-column
                                prop="value"
                                label="值">
                        </el-table-column>
                    </el-table>
                </template>
            </el-tab-pane>
            <el-tab-pane label="数据源" name="datasource">
                <el-tabs v-model="activeDataSource" type="card" addable @tab-click="switchDataSource">
                    <el-tab-pane
                            v-for="(item, index) in datasource"
                            :label="item.name"
                            :name="item.id"
                            :key="item.id">
                        <el-table
                                :data="item.content"
                                border
                                style="width: 100%">
                            <el-table-column
                                    prop="key"
                                    label="属性" width="240">
                            </el-table-column>
                            <el-table-column
                                    prop="value"
                                    label="值">
                            </el-table-column>
                        </el-table>
                    </el-tab-pane>
                </el-tabs>

            </el-tab-pane>
            <el-tab-pane label="Sql监控" name="sql">
                <template>
                    <el-select v-model="refresh" placeholder="请选择" style="margin-bottom: 10px;">
                        <el-option
                                v-for="item in options"
                                :label="item.label"
                                :value="item.value" :key="item.value">
                        </el-option>
                    </el-select>
                </template>
                <template>
                    <el-table
                            :data="sql"
                            border
                            style="width: 100%">
                        <el-table-column
                                sortable
                                prop="ID"
                                label="ID"
                                width="125">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="SQL"
                                label="SQL"
                                show-overflow-tooltip
                                width="425">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="ExecuteCount"
                                label="执行数"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="TotalTime"
                                label="执行时间"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="MaxTimespan"
                                label="最慢"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="InTransactionCount"
                                label="事务中"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="ErrorCount"
                                label="错误数"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="EffectedRowCount"
                                label="更新行数"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="FetchRowCount"
                                label="读取行数"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="RunningCount"
                                label="执行中"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="ConcurrentMax"
                                label="最大并发"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="Histogram"
                                :formatter="arrayFormatter"
                                label="执行时间分布"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="ExecuteAndResultHoldTimeHistogram"
                                :formatter="arrayFormatter"
                                label="执行+RS时分布"
                                width="220">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="FetchRowCountHistogram"
                                :formatter="arrayFormatter"
                                label="读取行分布"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                sortable
                                prop="EffectedRowCountHistogram"
                                :formatter="arrayFormatter"
                                label="更新行分布"
                                width="180">
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="100">
                            <template slot-scope="scope">
                                <el-button
                                        @click.native.prevent="showSqlDetail(scope.row.ID)"
                                        type="text"
                                        size="small">
                                    详情
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-dialog title="Sql详情" :visible.sync="showSqlDetails" :close-on-click-modal="false"
                               :close-on-press-escape="false">
                        <el-row>
                            <el-col :span="24">
                                <h3>格式化Sql</h3>
                            </el-col>
                        </el-row>
                        <el-input
                                type="textarea"
                                :autosize="{ minRows: 2, maxRows: 4}"
                                placeholder="请输入内容"
                                v-model="showingSql.formattedSql">
                        </el-input>
                    </el-dialog>
                </template>
            </el-tab-pane>
            <!--<el-tab-pane label="Sql防火墙" name="wall">sql防火墙</el-tab-pane>-->
        </el-tabs>
    </section>
</template>
<style>

</style>
<script>
    import axios from "axios";

    export default {
        data: () => ({
            timer: 0,
            refresh: 5,
            options: [
                {
                    value: 5,
                    label: "5秒"
                },
                {
                    value: 10,
                    label: "10秒"
                },
                {
                    value: 9999999999,
                    label: "不刷新"
                }
            ],
            activeName: "main",
            main: [],
            activeDataSource: "1",
            datasource: [],
            sql: [],
            wall: [],
            showSqlDetails: false,
            showingSqlId: 0,
            showingSql: {}
        }),
        watch: {
            showingSqlId: function (showingSqlId) {
                axios.get("/druid/sql-" + showingSqlId + ".json").then(resp => {
                    this.showingSql = resp.Content;
                }, this.globalFail);
            },
            refresh: function (t) {
                clearInterval(this.timer);
                this.timer = setInterval(this.loadSqls, t * 1000);
            }
        },
        methods: {
            arrayFormatter(row, column) {
                return "[" + row[column.property].join(",") + "]";
            },
            showSqlDetail(ID) {
                this.showSqlDetails = true;
                this.showingSqlId = ID;
            },
            map2List(obj) {
                var list = [];
                for (var key in obj) {
                    list.push({
                        key: key + "",
                        value: obj[key] + ""
                    });
                }
                return list;
            },
            switchDataSource() {
                //切换数据源
            },
            loadDataSource() {
                //加载数据源信息
                axios.get("/druid/datasource.json").then(
                    resp => {
                        var datasource = (this.$data.datasource = []);
                        this.$data.activeDataSource = resp.Content[0].Identity + "";
                        for (var i = 0; i < resp.Content.length; i++) {
                            var d = resp.Content[i];
                            datasource.push({
                                name: d.Name,
                                id: d.Identity + "",
                                content: this.map2List(d)
                            });
                        }
                    },
                    resp => {
                        this.globalFail(resp);
                    }
                );
            },
            loadSqls() {
                //加载sql监控
                axios.get("/druid/sql.json").then(
                    resp => {
                        this.$data.sql = resp.Content;
                    },
                    resp => {
                        this.globalFail(resp);
                    }
                );
            },
            loadWall() {
                //加载防火墙
            },
            handleClick(tab, event) {
                switch (tab.name) {
                    case "datasource":
                        this.loadDataSource();
                        break;
                    case "sql":
                        this.loadSqls();
                        break;
                    case "wall":
                        this.loadWall();
                        break;
                    default:
                        break;
                }
            }
        },
        destroyed() {
            clearInterval(this.timer);
        },
        created() {
            axios
                .get("/druid/basic.json")
                .then(resp => {
                    this.$data.main = this.map2List(resp.Content);
                })
                .catch(resp => {
                    console.log(resp);
                });
        }
    };
</script>