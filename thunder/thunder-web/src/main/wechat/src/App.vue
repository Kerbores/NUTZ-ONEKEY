<template>
  <div id="app" style="height:95%;">
    <view-box ref="viewBox">
      <loading v-model="isLoading"></loading>
      <router-view/>
    </view-box>
    <divider>power by nutz-onekey</divider>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { ViewBox, Loading, Divider } from "vux";
export default {
  name: "app",
  components: {
    Loading,
    ViewBox,
    Divider
  },
  computed: {
    ...mapState({
      isLoading: state => state.loading.isLoading
    })
  },
  created() {
    this.$api.wechat.config(location.href, result => {
      this.$wechat.config(result.config);

      this.$wechat.ready(() => {
      });
      this.$wechat.error(res => {
      });
    });
  }
};
</script>

<style lang="less">
@import "~font-awesome/less/font-awesome.less";
@import "~vux/src/styles/reset.less";
body {
  background-color: #eee;
}
html,
body {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
}
.pop-btn {
  width: 45px;
  padding: 5px 0;
  cursor: pointer;
  text-align: center;
  color: #0c79e6;
  border-radius: 2px;
  position: fixed;
  bottom: 45px;
  right: 30px;
  font-size: 30px;
}
</style>
