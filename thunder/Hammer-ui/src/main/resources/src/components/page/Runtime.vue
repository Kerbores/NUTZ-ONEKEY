<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="fa fa-home"></i> 首页</el-breadcrumb-item>
                <el-breadcrumb-item><i class="fa fa-camera"></i> 监控</el-breadcrumb-item>
                <el-breadcrumb-item><i class="fa fa-server"></i> 运行环境</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <el-tabs v-model="activeName2" type="card" @tab-click="handleClick">
            <el-tab-pane label="服务器状态" name="dynamic">
                <el-row :gutter="20">
                    <el-col :span="16">
                        <IEcharts class="echarts" :option="line"></IEcharts>
                    </el-col>
                    <el-col :span="8">
                        <IEcharts class="echarts" :option="gauge"></IEcharts>
                    </el-col>
                </el-row>
            </el-tab-pane>
            <el-tab-pane label="告警配置" name="config">告警配置</el-tab-pane>
            <el-tab-pane label="服务器信息" name="info">服务器信息</el-tab-pane>
        </el-tabs>
    </div>
    <!--sql详情-->
</template>

<script>
    import IEcharts from 'vue-echarts-v3';
    export default {
        components: {
            IEcharts
        },
        data() {
            return {
                activeName2: 'dynamic',
                line: {
                    color: ["#20a0ff", "#13CE66", "#F7BA2A", "#FF4949"],
                    title: {
                        text: '主机实时性能曲线'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        y: 'bottom',
                        data: ['CPU', 'JVM', 'RAM', 'SWAP', "DISK", 'NETWORK IN', 'NETWORK OUT']
                    },
                    xAxis: [{
                        type: 'category',
                        boundaryGap: true,
                        data: []
                    }],
                    yAxis: [{
                        type: 'value',
                        scale: true,
                        name: '百分比',
                        max: 100,
                        min: 0,
                        boundaryGap: [0.2, 0.2]
                    }, {
                        type: 'value',
                        scale: true,
                        max: 5000,
                        min: 0,
                        name: ' k/s',
                        boundaryGap: [0.2, 0.2]
                    }],
                    series: [{
                            name: 'CPU',
                            type: 'line',
                            smooth: true,
                            data: []
                        }, {
                            name: 'JVM',
                            type: 'line',
                            smooth: true,
                            data: []
                        }, {
                            name: 'RAM',
                            type: 'line',
                            smooth: true,
                            data: []
                        },
                        {
                            name: 'SWAP',
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            data: []
                        },
                        {
                            name: 'DISK',
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            data: []
                        },
                        {
                            name: 'NETWORK IN',
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,
                            data: []
                        }, {
                            name: 'NETWORK OUT',
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,
                            data: []
                        }
                    ]
                },
                gauge: {
                    title: {
                        text: '主机实时性能仪表盘'
                    },
                    tooltip: {
                        formatter: "{a} <br/>{c} {b}"
                    },
                    series: [{
                            name: 'CPU',
                            type: 'gauge',
                            z: 3,
                            min: 0,
                            max: 100,
                            splitNumber: 10,
                            radius: '50%',
                            axisLine: { // 坐标轴线
                                lineStyle: { // 属性lineStyle控制线条样式
                                    width: 10
                                }
                            },
                            axisTick: { // 坐标轴小标记
                                length: 15, // 属性length控制线长
                                lineStyle: { // 属性lineStyle控制线条样式
                                    color: 'auto'
                                }
                            },
                            splitLine: { // 分隔线
                                length: 20, // 属性length控制线长
                                lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                                    color: 'auto'
                                }
                            },
                            title: {
                                textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                    fontWeight: 'bolder',
                                    fontSize: 20,
                                    fontStyle: 'italic'
                                }
                            },
                            detail: {
                                textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                    fontWeight: 'bolder'
                                }
                            },
                            data: [{
                                value: 40,
                                name: '%'
                            }]
                        },
                        {
                            name: '网络',
                            type: 'gauge',
                            center: ['20%', '55%'], // 默认全局居中
                            radius: '40%',
                            min: 0,
                            max: 24,
                            endAngle: 45,
                            splitNumber: 6,
                            axisLine: { // 坐标轴线
                                lineStyle: { // 属性lineStyle控制线条样式
                                    width: 8
                                }
                            },
                            axisTick: { // 坐标轴小标记
                                length: 12, // 属性length控制线长
                                lineStyle: { // 属性lineStyle控制线条样式
                                    color: 'auto'
                                }
                            },
                            splitLine: { // 分隔线
                                length: 20, // 属性length控制线长
                                lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                                    color: 'auto'
                                }
                            },
                            pointer: {
                                width: 5
                            },
                            title: {
                                offsetCenter: [0, '-30%'], // x, y，单位px
                            },
                            detail: {
                                textStyle: { // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                                    fontWeight: 'bolder'
                                }
                            },
                            data: [{
                                value: 1.5,
                                name: 'M/s'
                            }]
                        },
                        {
                            name: 'JVM',
                            type: 'gauge',
                            center: ['80%', '50%'], // 默认全局居中
                            radius: '30%',
                            min: 0,
                            max: 100,
                            startAngle: 120,
                            endAngle: 30,
                            splitNumber: 2,
                            axisLine: { // 坐标轴线
                                lineStyle: { // 属性lineStyle控制线条样式
                                    width: 8
                                }
                            },
                            axisTick: { // 坐标轴小标记
                                splitNumber: 5,
                                length: 10, // 属性length控制线长
                                lineStyle: { // 属性lineStyle控制线条样式
                                    color: 'auto'
                                }
                            },
                            axisLabel: {
                                formatter: function(v) {
                                    return v < 25 ? 'L' : v < 75 ? 'M' : 'H';
                                }
                            },
                            splitLine: { // 分隔线
                                length: 15, // 属性length控制线长
                                lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                                    color: 'auto'
                                }
                            },
                            pointer: {
                                width: 2
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [{
                                value: 55,
                                name: '%'
                            }]
                        },
                        {
                            name: 'RAM',
                            type: 'gauge',
                            center: ['80%', '50%'], // 默认全局居中
                            radius: '30%',
                            min: 0,
                            max: 100,
                            startAngle: 330,
                            endAngle: 240,
                            splitNumber: 2,
                            axisLine: { // 坐标轴线
                                lineStyle: { // 属性lineStyle控制线条样式
                                    width: 8
                                }
                            },
                            axisTick: { // 坐标轴小标记
                                show: false
                            },
                            axisLabel: {
                                formatter: function(v) {
                                    return v < 25 ? 'L' : v < 75 ? 'M' : 'H';
                                }
                            },
                            splitLine: { // 分隔线
                                length: 15, // 属性length控制线长
                                lineStyle: { // 属性lineStyle（详见lineStyle）控制线条样式
                                    color: 'auto'
                                }
                            },
                            pointer: {
                                width: 2
                            },
                            title: {
                                show: false
                            },
                            detail: {
                                show: false
                            },
                            data: [{
                                value: 20,
                                name: '%'
                            }]
                        }
                    ]
                },
            };
        },
        methods: {
            handleClick(tab, event) {
                console.log(tab, event);
            }
        },
        mounted() {
            const self = this;
            setInterval(function() {
                self.get('/sigar/api?type=ALL', res => {
                    self.line.xAxis[0].data.push(new Date().toLocaleTimeString()); //时间戳
                    self.line.series[0].data.push(res.data.cpuUsage);
                    self.line.series[1].data.push(res.data.jvmUasge);
                    self.line.series[2].data.push(res.data.ramUasge);
                    self.line.series[3].data.push(res.data.swapUasge);
                    self.line.series[4].data.push(res.data.diskUsage);
                    self.line.series[5].data.push(res.data.niUsage);
                    self.line.series[6].data.push(res.data.noUsage);
                    self.gauge.series[0].data[0].value = res.data.cpuUsage.toFixed(2);
                    self.gauge.series[1].data[0].value =( res.data.niUsage / 1024 ) .toFixed(2);
                    self.gauge.series[2].data[0].value = res.data.jvmUasge.toFixed(2);
                    self.gauge.series[3].data[0].value = res.data.ramUasge.toFixed(2);
                })
            }, 2000)
        }
    };
</script>

<style>
    .echarts {
        float: left;
        width: 100%;
        height: 450px;
    }
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
