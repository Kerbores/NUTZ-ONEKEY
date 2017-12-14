<template>
  <div id="app" style="height:95%;">
    <view-box ref="viewBox">
      <loading v-model="isLoading"></loading>
      <router-view/>
   </view-box>
   <divider>我是有底线的</divider>
  </div>
</template>

<script>
import { Loading } from "vux";
import { mapState } from "vuex";
import { ViewBox } from "vux";
import { Divider } from 'vux'

export default {
  name: "app",
  components: {
    Loading,
    ViewBox,
    Divider
  },
  computed: {
    ...mapState({
      isLoading: state => state.isLoading
    })
  }
};
</script>
<style lang="less">
@import "~vux/src/styles/reset.less";
body {
  background-color: #fbf9fe;
}
html,
body {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
}
</style>
