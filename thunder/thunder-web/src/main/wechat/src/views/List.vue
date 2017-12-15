<template>
  <div>
    <tab>
      <tab-item v-for="item in tabs" :selected="item.selected" :key="item.tab" @on-item-click="onTabClick">{{ item.name }}</tab-item>
    </tab>
    <search v-model="search" @on-submit="searchSubmit" @on-cancel="searchCancel" position="absolute" auto-scroll-to-top top="46px" ref="search"></search>
    <scroller lock-x scrollbar-y ref="scroller" :use-pullup="true" :pullup-config="config" @on-pullup-loading="loadMore()">
      <panel :list="list" :type="'5'" @on-img-error="onImgError"></panel>
    </scroller>
    <div class="pop-btn" v-if="logined()">
      <i class="fa fa-plus" @click="toAdd"></i>
    </div>
  </div>
</template>

<style scoped>

</style>

<script>
import { Panel, Tab, TabItem, Scroller, Search, Icon, querystring } from "vux";
import { mapState, mapGetters, mapMutations } from "vuex";
export default {
  data() {
    return {
      openid: "",
      config: {
        content: "上拉加载更多",
        pullUpHeight: 60,
        height: 40,
        autoRefresh: false,
        downContent: "松开加载",
        upContent: "上拉加载",
        loadingContent: "数据加载中...",
        clsPrefix: "xs-plugin-pullup-"
      },
      tabs: [
        {
          name: "全部",
          selected: true
        },
        {
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
      list: [],
      tab: "",
      tag: "",
      search: "",
      page: 1,
      limit: 15
    };
  },
  computed: {
    ...mapGetters(["logined"])
  },
  methods: {
    ...mapMutations(["save", "remove"]),
    onImgError(item, $event) {},
    loadMore() {
      this.page++;
      this.loadTopic(false, true);
    },
    searchCancel() {
      this.search = "";
      this.page = 1;
      this.loadTopic(true);
    },
    searchSubmit() {
      this.page = 1;
      this.loadTopic(true);
    },
    onTabClick(index) {
      this.tab = this.tabs[index].tab;
      this.page = 1;
      this.loadTopic(true);
    },
    toAdd() {
      this.$router.push({
        path: "/add"
      });
    },
    loadTopic(clear, done) {
      this.$api.Topic.list(
        this.page,
        this.limit,
        this.tab,
        this.tag,
        this.search,
        result => {
          if (clear) {
            this.list = [];
          }
          if (!result.topics.data.length) {
            if (done) {
              this.$refs.scroller.donePullup();
              this.$vux.toast.text("没有更多的数据了哦...");
            }
            return;
          }
          result.topics.data.forEach(item => {
            this.list.push({
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
            });
            if (done) {
              this.$refs.scroller.donePullup();
            }
            if (clear) {
              this.$nextTick(() => {
                this.$refs.scroller.reset({
                  top: 0
                });
              });
            } else {
              this.$nextTick(() => {
                this.$refs.scroller.reset();
              });
            }
          });
        }
      );
    }
  },
  components: {
    Panel,
    Tab,
    TabItem,
    Scroller,
    Search,
    Icon
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
    this.loadTopic(true);
  }
};
</script>
