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
  import {
    mapState
  } from "vuex";
  import {
    ViewBox,
    Loading,
    Divider
  } from "vux";
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
    }
  };
</script>

<style lang="less">
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
</style>
