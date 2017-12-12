 <template>
     <section>
         机构管理
         <ali-map  @locationSelected="notify"></ali-map>
     </section>
 </template>
 <style>

</style>
 <script>
import AliMap from "@/components/Map/AliMap";
export default {
  data() {
    return {};
  },
  methods: {
    notify(rs) {
      console.log(rs);
    }
  },
  created() {},
  components: {
    AliMap
  }
};
</script>