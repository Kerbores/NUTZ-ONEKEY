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
        id: ''
      }
    },
    created() {
      this.id = this.$route.params.id
      this.$api.Topic.detail(this.id, result => {
        console.log(result);
      })
    }
  }
</script>
