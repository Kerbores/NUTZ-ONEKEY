<template>
  <div>
    <group label-width="4.5em" label-margin-right="2em" label-align="right" title="标题分类">
      <selector title="分类" :options="tabs" v-model="topic.tab" :value-map="valueMap"></selector>
      <x-input title="标题" placeholder="请填写话题标题" v-model="topic.title" required :show-clear="true"></x-input>
    </group>
    <group title="话题内容">
      <mavon-editor @imgAdd="imgAdd" v-model="value" :subfield="false" :toolbars="toolbars" @change="editChange" />
    </group>
    <box gap="10px 10px">
      <x-button :gradients="['#1D62F0', '#19D5FD']" :disabled="!logined()" @click.native="submitTopic">我要答案</x-button>
    </box>
  </div>
</template>

<style>

</style>

<script>
  import {
    mapState,
    mapGetters,
    mapMutations
  } from "vuex";
  import {
    Selector,
    Group,
    XInput,
    Box,
    XButton,
    querystring
  } from "vux";
  export default {
    components: {
      Group,
      Selector,
      XInput,
      Box,
      XButton
    },
    data() {
      return {
        topic: {
          title: "",
          tab: "ask",
          content: ""
        },
        valueMap: ["tab", "name"],
        tabs: [{
            name: "问答",
            tab: "ask"
          },
          {
            name: "新闻",
            tab: "news"
          },
          {
            name: "分享",
            tab: "share"
          },
          {
            name: "灌水",
            tab: "nb"
          },
          {
            name: "招聘",
            tab: "job"
          },
          {
            name: "精华",
            tab: "good"
          }
        ],
        list: [{
            key: "gd",
            value: "广东"
          },
          {
            key: "gx",
            value: "广西"
          }
        ],
        value: "",
        toolbars: {
          //bold: true, // 粗体
          //italic: true, // 斜体
          //header: true, // 标题
          //underline: true, // 下划线
          //strikethrough: true, // 中划线
          mark: true, // 标记
          //superscript: true, // 上角标
          //subscript: true, // 下角标
          //quote: true, // 引用
          ol: true, // 有序列表
          ul: true, // 无序列表
          link: true, // 链接
          imagelink: true, // 图片链接
          code: true, // code
          //table: true, // 表格
          //fullscreen: true, // 全屏编辑
          //readmodel: true, // 沉浸式阅读
          //htmlcode: true, // 展示html源码
          //help: true, // 帮助
          /* 1.3.5 */
          //undo: true, // 上一步
          //redo: true, // 下一步
          //trash: true, // 清空
          save: true, // 保存（触发events中的save事件）
          /* 1.4.2 */
          //navigation: true, // 导航目录
          /* 2.1.8 */
          //alignleft: true, // 左对齐
          //aligncenter: true, // 居中
          //alignright: true, // 右对齐
          /* 2.2.1 */
          //subfield: true, // 单双栏模式
          preview: true // 预览
        }
      };
    },
    computed: {
      ...mapState({
        loginUser: state => state.user.user
      }),
      ...mapGetters(["logined", "token"])
    },
    methods: {
      ...mapMutations(["save", "remove"]),
      editChange(value, rended) {
        this.topic.content = rended;
      },
      imgAdd(pos, file){
        console.log(pos,file)
        this.$api.Topic.upload(file,this.token(),result=>{
          console.log(result);
        })
      },
      submitTopic() {
        this.$api.Topic.add(this.topic, this.token(), result => {
          this.$router.push({
            path: "/"
          });
        });
      }
    },
    created() {
      if (location.search) {
        this.openid = querystring.parse(location.search.substr(1)).openid;
        this.$api.Topic.nutzer(this.openid, result => {
          this.save(result);
        });
      } else {
        this.remove();
      }
      if (!this.logined()) {
        this.$router.push({
          path: "/me"
        });
      }
    }
  };
</script>
