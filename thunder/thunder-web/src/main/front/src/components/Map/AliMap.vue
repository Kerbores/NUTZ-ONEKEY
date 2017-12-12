<template>
    <div class="amap-page-container">
        <el-amap-search-box class="search-box"
                            :search-option="searchOption"
                            :on-search-result="onSearchResult"></el-amap-search-box>
        <el-amap :plugin="plugin" vid="ali-map"
                 :center="mapCenter"
                 :events="events"
                 :zoom="12" class="ali-map"
                 :class="mapClass">
            <el-amap-marker :position="mapCenter"></el-amap-marker>
        </el-amap>
    </div>
</template>
<style scoped>
    .ali-map {
        height: 400px;
    }

    .amap-scalecontrol {
        right: 0;
    }

    .search-box {
        position: absolute;
        top: 5px;
        left: 60px;
    }

    .amap-page-container {
        position: relative;
    }
</style>
<style>
    .amap-copyright,
    .amap-logo {
        display: none !important;
    }

    .el-vue-search-box-container .search-box-wrapper .search-btn {
        background: #ccc !important;
    }
</style>

<script>
    export default {
        props: {
            center: {
                type: Array,
                default: function () {
                    return [0, 0]
                },
                required: false
            },
            mapClass: {
                type: String,
                default: "",
                required: false
            }
        },
        data() {
            let self = this;
            return {
                searchOption: {
                    city: "重庆",
                    citylimit: false
                },
                mapCenter: [this.center[0], this.center[1]],
                events: {
                    click(e) {
                        let {lng, lat} = e.lnglat;
                        self.geocoder(lng, lat);
                    }
                },
                plugin: [
                    {
                        pName: "MapType",
                        defaultType: 0,
                        events: {
                            init(instance) {
                                console.log("Amap.MapType inited!");
                            }
                        }
                    },
                    {
                        pName: "ToolBar",
                        events: {
                            init(instance) {
                                console.log("Amap.ToolBar inited!");
                            }
                        }
                    },
                    {
                        pName: "Geolocation",
                        events: {
                            init(o) {
                                if (self.mapCenter[0] === 0 && self.mapCenter[1] === 0) {
                                    o.getCurrentPosition((status, result) => {
                                        if (result && result.position) {
                                            self.geocoder(result.position.lng, result.position.lat);
                                            self.$nextTick();
                                        }
                                    });
                                } else {
                                    self.geocoder(self.mapCenter[0], self.mapCenter[1]);
                                    self.$nextTick();
                                }
                            }
                        }
                    }
                ]
            };
        },
        methods: {
            reset() {
                this.center = [0, 0];
            },
            notify(rs) {
                this.$emit("locationSelected", rs);
            },
            lacationAndnotify(lng, lat) {
                let self = this;
                var geocoder = new AMap.Geocoder({
                    radius: 1000,
                    extensions: "all"
                });
                geocoder.getAddress([lng, lat], function (status, result) {
                    if (status === "complete" && result.info === "OK") {
                        if (result && result.regeocode) {
                            self.mapCenter = [lng, lat];
                            self.notify({
                                point: {lng: lng, lat: lat},
                                address: result.regeocode.addressComponent,
                                formattedAddress: result.regeocode.formattedAddress
                            });
                            self.$nextTick();
                        }
                    }
                });
            },
            geocoder(lng, lat) {
                let self = this;
                if (lng === 0 && lat === 0) {
                    new AMap.Geolocation().getCurrentPosition((status, result) => {
                        if (result && result.position) {
                            self.lacationAndnotify(result.position.lng, result.position.lat);
                        }
                    });
                } else {
                    self.lacationAndnotify(lng, lat);
                }
            },
            onSearchResult(pois) {
                let latSum = 0;
                let lngSum = 0;
                if (pois.length > 0) {
                    pois.forEach(poi => {
                        let {lng, lat} = poi;
                        lngSum += lng;
                        latSum += lat;
                    });
                    let center = {
                        lng: lngSum / pois.length,
                        lat: latSum / pois.length
                    };
                    this.geocoder(center.lng, center.lat);
                }
            }
        },
        created() {
        }
    };
</script>
