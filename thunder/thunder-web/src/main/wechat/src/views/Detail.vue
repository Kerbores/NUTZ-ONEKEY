<template>
  <div>
    <blur :blur-amount=40 :url="topic.author.avatar_url">
      <p class="center">
        <img :src="topic.author.avatar_url"></p>
      <p class="center">{{topic.author.loginname}} <span style="font-size: 8px">{{topic.create_at_forread}}</span></p>
    </blur>
    <group title="话题内容">
      <div class="markdown-text" v-html="topic.content"></div>
    </group>
    <group title="回复列表">
      <panel :list="replyList" :type="'5'" @on-img-error="onImgError"></panel>
    </group>
    <div class="pop-btn" v-if="logined()">
      <i class="fa fa-reply-all" @click="toReply"></i>
    </div>
    <popup v-model="replyShow">
      <div class="popup0">
        <mavon-editor v-model="value" :subfield="false" :toolbars="toolbars" @change="editChange" />
        <group>
          <flexbox>
            <flexbox-item>
              <x-button type="primary" @click.native="replySubmit">回复</x-button>
            </flexbox-item>
            <flexbox-item>
              <x-button type="warn" @click.native="replyShow = false">取消</x-button>
            </flexbox-item>
          </flexbox>
        </group>
      </div>
    </popup>
  </div>
</template>

<style scoped>
.center {
  text-align: center;
  padding-top: 20px;
  color: #fff;
  font-size: 18px;
}
.center img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 4px solid #ececec;
}
</style>

<style>
.weui-dialog {
  max-width: 500px !important;
  width: 100% !important;
}
.markdown-text p,
.preview p {
  white-space: pre-wrap;
  /* CSS3 */
  white-space: -moz-pre-wrap;
  /* Mozilla, since 1999 */
  white-space: -pre-wrap;
  /* Opera 4-6 */
  white-space: -o-pre-wrap;
  /* Opera 7 */
  word-wrap: break-word;
  /* Internet Explorer 5.5+ */
  line-height: 2em;
  margin: 1em 0;
}
.markdown-text > *:first-child,
.preview > *:first-child {
  margin-top: 0;
}
.markdown-text > *:last-child,
.preview > *:last-child {
  margin-bottom: 1em;
}
.markdown-text li,
.preview li {
  font-size: 14px;
  line-height: 2em;
}
.markdown-text p code,
.preview p code,
.markdown-text li code,
.preview li code {
  color: black;
  background-color: #fcfafa;
  padding: 4px 6px;
}
</style>

<script>
import { Blur, Group, Panel, Popup, Flexbox, FlexboxItem, XButton } from "vux";
import { mapState, mapGetters, mapMutations } from "vuex";
export default {
  components: {
    Blur,
    Group,
    Panel,
    Popup,
    Flexbox,
    FlexboxItem,
    XButton
  },
  data() {
    return {
      value: "",
      reply: "",
      toolbars: {
        mark: true,
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        imagelink: true, // 图片链接
        code: true,
        save: true,
        preview: true // 预览
      },
      replyShow: false,
      topic: {}
    };
  },
  computed: {
    ...mapGetters(["logined", "token"]),
    replyList() {
      return this.topic.replies.map(item => {
        return {
          src: item.author.avatar_url,
          title: item.author.loginname,
          desc: item.content,
          url: {},
          meta: {
            source: "",
            date: item.create_at_forread,
            other: "其他信息"
          }
        };
      });
    }
  },
  methods: {
    onImgError() {},
    toReply() {
      this.replyShow = true;
    },
    replySubmit() {
      this.$api.Topic.reply(this.topic.id, this.reply, this.token(), result => {
        this.replyShow = false;
        location.reload();
      });
    },
    editChange(value, rended) {
      this.reply = rended;
    }
  },
  created() {
    this.topic.id = this.$route.params.id;
    this.$api.Topic.detail(this.topic.id, result => {
      this.topic = result.topic.data;
    });
  }
};
</script>
