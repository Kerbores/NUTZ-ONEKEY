<template>
    <section>
        机构管理
        <b-map-component :ak="ak" :mapWidth="mapWidth"  @notify="notify"></b-map-component>
    </section>
</template>
<style>

</style>
<script>
import BMapComponent from "@/components/Map/BaiduMap.vue";
export default {
  data() {
    return {
      ak: "CRHkMGE7Db1USNSyFXqVDmdv",
      mapWidth: 800
    };
  },
  methods: {
    notify(rs) {
      console.log(rs);
    }
  },
  components: {
    BMapComponent
  }
};
</script>