<template>
<section>
  <el-row>
    <el-col :span="8">
      <el-input v-model="key" placeholder="请输入关键词进行检索" id="autoComplete" name="autoComplete">
        <el-button slot="append" icon="el-icon-search"></el-button>
      </el-input>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="24">
      <div id="allmap" :style="style"></div>
    </el-col>
  </el-row>
</section>
  
</template>
<style scoped>
.el-input {
  z-index: 1;
}
#allmap {
  margin: 0;
  margin-top: -50px;
  font-family: "微软雅黑";
}
</style>
<script>
import { MP } from "./map";
export default {
  data() {
    return {
      key: ""
    };
  },
  computed: {
    style() {
      return {
        height: this.mapHeight + "px",
        width: this.mapWidth + "px"
      };
    }
  },
  props: {
    ak: {
      type: String,
      required: true
    },
    mapWidth:{
      type: Number,
      default: 400,
      required: false
    },
    mapHeight: {
      type: Number,
      default: 400,
      required: false
    },
    center: {
      type: Object,
      required: false
    }
  },
  methods: {
    notify(rs) {
      this.$emit("notify", rs);
    }
  },
  created() {
    const self = this;
    this.$nextTick(function() {
      MP(this.ak).then(BMap => {
        var map = new BMap.Map("allmap"); // 创建Map实例
        var point = new BMap.Point(
          this.center ? this.center.lng : 0,
          this.center ? this.center.lat : 0
        ); // 创建点坐标
        map.centerAndZoom(point, 15);
        var geolocation = new BMap.Geolocation();
        geolocation.getCurrentPosition(
          function(r) {
            if (this.getStatus() == BMAP_STATUS_SUCCESS) {
              new BMap.Geocoder().getLocation(r.point, function(rs) {
                var marker = new BMap.Marker(rs.point);
                map.clearOverlays();
                map.centerAndZoom(rs.point, 15);
                map.addOverlay(marker); // 将标注添加到地图中
                self.notify(rs);
              });
            }
          },
          { enableHighAccuracy: true }
        );
        map.addControl(new BMap.MapTypeControl()); //添加地图类型控件
        map.enableScrollWheelZoom(true);
        //点击监听
        map.addEventListener("click", function(e) {
          var pt = e.point;
          new BMap.Geocoder().getLocation(pt, function(rs) {
            var marker = new BMap.Marker(rs.point);
            map.clearOverlays();
            map.addOverlay(marker); // 将标注添加到地图中
            self.notify(rs);
          });
        });
        //自动完成
        var ac = new BMap.Autocomplete({
          //建立一个自动完成的对象
          input: "autoComplete",
          location: map
        });
        //鼠标放在下拉列表上的事件
        ac.addEventListener("onhighlight", function(e) {
          var str = "";
          var _value = e.fromitem.value;
          var value = "";
          if (e.fromitem.index > -1) {
            value =
              _value.province +
              _value.city +
              _value.district +
              _value.street +
              _value.business;
          }
          str =
            "FromItem<br />index = " +
            e.fromitem.index +
            "<br />value = " +
            value;
          value = "";
          if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value =
              _value.province +
              _value.city +
              _value.district +
              _value.street +
              _value.business;
          }
          str +=
            "<br />ToItem<br />index = " +
            e.toitem.index +
            "<br />value = " +
            value;
          // $("#searchResultPanel").html(str);
        });
        //
        ac.addEventListener("onconfirm", function(e) {
          //鼠标点击下拉列表后的事件
          var _value = e.item.value;
          var myValue =
            _value.province +
            _value.city +
            _value.district +
            _value.street +
            _value.business;
          var local = new BMap.LocalSearch(map, {
            //智能搜索
            onSearchComplete: function() {
              var pp = local.getResults().getPoi(0).point; //获取第一个智能搜索的结果
              new BMap.Geocoder().getLocation(pp, function(rs) {
                var marker = new BMap.Marker(rs.point);
                map.centerAndZoom(rs.point, 18);
                map.clearOverlays();
                map.addOverlay(marker); // 将标注添加到地图中
                self.notify(rs);
              });
            }
          });
          local.search(myValue);
        });
      });
    });
  }
};
</script>