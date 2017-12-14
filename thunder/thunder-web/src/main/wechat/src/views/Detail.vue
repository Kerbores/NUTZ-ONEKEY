<template>
  <div>
    详情
  </div>
</template>

<style>

</style>

<script>
  export default {
    data() {
      return {
        topic:{}
      }
    },
    created() {
      this.topic.id = this.$route.params.id
      this.$api.Topic.detail(this.topic.id, result => {
        console.log(result);
        this.topic = result.topic.data;
      })
    }
  }
</script>
