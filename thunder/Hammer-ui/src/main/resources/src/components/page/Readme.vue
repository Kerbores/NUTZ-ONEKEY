<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><i class="el-icon- fa fa-tachometer"></i> 项目概述</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="ms-doc">
            <h3>README</h3>
            <article>
                <h1>Thunder vue</h1>
                <p>nutz-onekey的vue版本</p>
                <h2>技术栈</h2>
                <el-collapse v-model="activeNames"
                             accordion>
                    <el-collapse-item title="Nutz"
                                      name="1">
                        <div>在力所能及的情况下，最大限度的提高Web开发人员的生产力。</div>
                        <div>我可以保证，在未来，Nutz 的代码 绝对不会膨胀。 所有的功能设计的出发点就是最大限度给予程序员实惠。</div>
                        <div>DAO：如果你觉得 Hibernate 控制比较繁琐，iBatis 编写SQL又比较麻烦，Nutz.Dao 专为你设计</div>
                        <div>IOC：如果你觉得在多个服务器部署或者修改 Spring 配置文件很麻烦，Nutz.Ioc 专为你设计</div>
                        <div>MVC: 如果你觉得直接写 XML 配置文件很麻烦，可视化编辑器又没控制感，Nutz.Mvc 专为你设计</div>
                        <div>JSON: 如果你觉得 JSON 转换很麻烦（要写超过一行以上的代码），Nutz.Json 专为你设计</div>
                        <div>Lang: 如果你觉得 Java 语法不如 Ruby 便捷， Nutz.Castor 以及 Nutz.Lang 专为你设计</div>
                        <div>ALL: 如果你以前根本没接触过 SSH ，只使用 JDBC 编程， 整个 Nutz 专门为你设计</div>
                    </el-collapse-item>
                    <el-collapse-item title="VUE"
                                      name="2">
                        <div>易用：已经会了HTML,CSS,JavaScript？即刻阅读指南开始构建应用；</div>
                        <div>灵活：简单小巧的核心，渐进式技术栈，足以应付任何规模的应用。</div>
                        <div>性能: 17kb min+gzip 运行大小 超快虚拟 DOM 最省心的优化</div>
                    </el-collapse-item>
                    <el-collapse-item title="Element"
                                      name="3">
                        <el-collapse v-model="activeNames">
                            <el-collapse-item title="一致性 Consistency"
                                              name="3-1">
                                <div>与现实生活一致：与现实生活的流程、逻辑保持一致，遵循用户习惯的语言和概念；</div>
                                <div>在界面中一致：所有的元素和结构需保持一致，比如：设计样式、图标和文本、元素的位置等。</div>
                            </el-collapse-item>
                            <el-collapse-item title="反馈 Feedback"
                                              name="3-2">
                                <div>控制反馈：通过界面样式和交互动效让用户可以清晰的感知自己的操作；</div>
                                <div>页面反馈：操作后，通过页面元素的变化清晰地展现当前状态。</div>
                            </el-collapse-item>
                            <el-collapse-item title="效率 Efficiency"
                                              name="3-3">
                                <div>简化流程：设计简洁直观的操作流程；</div>
                                <div>清晰明确：语言表达清晰且表意明确，让用户快速理解进而作出决策；</div>
                                <div>帮助用户识别：界面简单直白，让用户快速识别而非回忆，减少用户记忆负担。</div>
                            </el-collapse-item>
                            <el-collapse-item title="可控 Controllability"
                                              name="3-4">
                                <div>用户决策：根据场景可给予用户操作建议或安全提示，但不能代替用户进行决策；</div>
                                <div>结果可控：用户可以自由的进行操作，包括撤销、回退和终止当前操作等。</div>
                            </el-collapse-item>
                        </el-collapse>
                    </el-collapse-item>
                    <el-collapse-item title="webpack"
                                      name="4">
                        <div>模块化：覆盖了现有的所有主流的 JavaScript 模块化方案,在webpack 中，甚至可以把以最传统全局变量形式暴露的库当作模块require 进来</div>
                        <div>兼容性：对 CommonJS 、 AMD 、ES6的语法做了兼容</div>
                        <div>扩展性: 具有强大的Plugin接口，大多是内部插件，使用起来比较灵活</div>
                    </el-collapse-item>
                </el-collapse>
                <h2>运行项目</h2>
    
                <el-steps :space="300"
                          direction="vertical"
                          :active="runStep">
                    <el-step title="步骤 1"
                             icon="import">
                        <template slot="description">
                            导入项目
                        </template>
                    </el-step>
                    <el-step title="步骤 2"
                             description="编译项目"
                             icon="compile"></el-step>
                    <el-step title="步骤 3"
                             description="部署项目"
                             icon="deploy"></el-step>
                </el-steps>
                <el-button type="primary"
                           icon="d-arrow-right"
                           size="mini"
                           @click="next"></el-button>
    
                <h2>功能列表</h2>
                <el-checkbox disabled
                             checked>ACL</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>Monitor</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>Config</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>Codebook</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>Log</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>Tree</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>NOP</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>APIDOC</el-checkbox>
                <br>
                <el-checkbox disabled
                             checked>wechat</el-checkbox>
                <br>
            </article>
        </div>
    
    </div>
</template>

<script>
export default {
    data: function () {
        return {
            activeNames: 1,
            runStep: 1
        }
    },
    methods: {
        changeStep(step) {
            this.runStep = step;
        },
        next() {
            if (this.runStep++ > 2) this.runStep = 1;
        }
    },
    created:function(){
    }
}
</script>

<style scoped>
.ms-doc {
    width: 100%;
    max-width: 100%;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
}

.ms-doc h3 {
    padding: 9px 10px 10px;
    margin: 0;
    font-size: 14px;
    line-height: 17px;
    background-color: #f5f5f5;
    border: 1px solid #d8d8d8;
    border-bottom: 0;
    border-radius: 3px 3px 0 0;
}

.ms-doc article {
    padding: 45px;
    word-wrap: break-word;
    background-color: #fff;
    border: 1px solid #ddd;
    border-bottom-right-radius: 3px;
    border-bottom-left-radius: 3px;
}

.ms-doc article h1 {
    font-size: 32px;
    padding-bottom: 10px;
    margin-bottom: 15px;
    border-bottom: 1px solid #ddd;
}

.ms-doc article h2 {
    margin: 24px 0 16px;
    font-weight: 600;
    line-height: 1.25;
    padding-bottom: 7px;
    font-size: 24px;
    border-bottom: 1px solid #eee;
}

.ms-doc article p {
    margin-bottom: 15px;
    line-height: 1.5;
}

.ms-doc article .el-checkbox {
    margin-bottom: 5px;
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
}
</style>