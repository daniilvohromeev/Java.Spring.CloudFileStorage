<template>
  <div class="file">
    📄 {{ fileData.fileName }}
  </div>
</template>

<script>
export default {
  name: 'File',
  props: {
    fileData: {
      type: Object,
      required: true,
    },
  },
};
</script>

<style scoped>
.file {
  padding-left: 20px;
}
</style>