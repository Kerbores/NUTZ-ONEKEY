<template>
    <div>
        <label>关键词：<input v-model="keyword"></label>
        <baidu-map :center="{lng: 116.404, lat: 39.915}" :zoom="15">
         <bm-local-search :keyword="keyword" :auto-viewport="true"  @infohtmlset="s"></bm-local-search>
            <bm-view class="map"></bm-view>
        </baidu-map>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                keyword: ''
            }
        },
        methods: {
            cl(point) {
                console.log(point);
            },
            s(){
                console.log(arguments)
            }
        }
    }
</script>
<style>
    .map {
        height: 450px
    }
</style>