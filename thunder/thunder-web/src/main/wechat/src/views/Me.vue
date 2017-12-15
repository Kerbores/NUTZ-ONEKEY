<template>
  <div>
    <section v-if="logined()">
      <blur :blur-amount=40 :url="url">
        <p class="center">
          <img :src="url"></p>
        <p class="center">{{loginUser.nutzer.data.loginname}} <span style="font-size: 8px"> {{loginUser.nutzer.data.score}}</span></p>
      </blur>
      <group title="最近发帖">
        <panel :list="topicList" :type="'5'" @on-img-error="onImgError"></panel>
      </group>
      <group title="最近回帖">
        <panel :list="replyList" :type="'5'" @on-img-error="onImgError"></panel>
      </group>
    </section>
    <section v-else>
      <box gap="10px 10px">
        <x-button :gradients="['#1D62F0', '#19D5FD']" @click.native="scan">扫码绑定用户</x-button>
      </box>
    </section>
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

<script>
import { mapState, mapGetters, mapMutations } from "vuex";
import {
  Blur,
  Flexbox,
  FlexboxItem,
  Group,
  Panel,
  Box,
  XButton,
  querystring
} from "vux";
export default {
  components: {
    Blur,
    Flexbox,
    FlexboxItem,
    Group,
    Panel,
    Box,
    XButton
  },
  data() {
    return {
      openid: ""
    };
  },
  computed: {
    ...mapState({
      loginUser: state => state.user.user,
      url: state => state.user.user.nutzer.data.avatar_url
    }),
    ...mapGetters(["logined", "token"]),
    topicList() {
      return this.loginUser.nutzer.data.recent_topics.map(item => {
        return {
          src: item.author.avatar_url,
          title: item.author.loginname,
          desc: item.title,
          url: {
            path: "/detail/" + item.id,
            replace: false
          },
          meta: {
            source: item.reply_count + "/" + item.visit_count,
            date: item.create_at_forread,
            other: "其他信息"
          }
        };
      });
    },
    replyList() {
      return this.loginUser.nutzer.data.recent_replies.map(item => {
        return {
          src: item.author.avatar_url,
          title: item.author.loginname,
          desc: item.title,
          url: {
            path: "/detail/" + item.id,
            replace: false
          },
          meta: {
            source: item.reply_count + "/" + item.visit_count,
            date: item.create_at_forread,
            other: "其他信息"
          }
        };
      });
    }
  },
  methods: {
    ...mapMutations(["save", "remove"]),
    onImgError() {},
    scan() {
      let self = this;
      this.$wechat.scanQRCode({
        needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
        scanType: ["qrCode", "barCode"], // 可以指定扫二维码还是一维码，默认二者都有
        success: function(res) {
          self.bind(res.resultStr);
        }
      });
    },
    bind(nutzToken) {
      this.$api.Topic.bind(this.openid, nutzToken, result => {
        this.loadUser();
      });
    },
    loadUser() {
      this.$api.Topic.nutzer(this.openid, result => {
        this.save(result);
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
  }
};
</script>
