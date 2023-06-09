<template>
  <div>
    <div v-if="node.type === 'folder'" @click="toggleFolder">
      📁 {{ node.name }}
    </div>
    <div v-if="isFolderOpen" class="folder-content">
      <div class="upload-section" v-if="node.name !== 'root'">
        <input type="file" @change="handleFileUpload" />
        <button @click="uploadFile">Upload</button>
      </div>
      <folder
          v-for="(child, index) in node.children"
          :key="index"
          :node="child"
          :parentPath="getFullPath(node)"
      />
    </div>
    <div v-else>
      <file v-if="node.type === 'file'" :fileData="node.fileData" />
    </div>
  </div>
</template>

<script>
import File from "@/components/File.vue";
import FileMetadataService from '@/services/file.metadata.service';

export default {
  name: "Folder",
  components: {
    File,
  },
  props: {
    node: {
      type: Object,
      required: true,
    },
    parentPath: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      isFolderOpen: false,
      selectedFile: null
    };
  },
  methods: {
    toggleFolder() {
      if (this.node.type === "folder") {
        this.isFolderOpen = !this.isFolderOpen;
      }
    },
    handleFileUpload(event) {
      this.selectedFile = event.target.files[0];
    },
    async uploadFile() {
      if (!this.selectedFile) return;

      const fullPath = this.getFullPath(this.node);
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('path', fullPath);

      try {
        await FileMetadataService.uploadFile(formData);
        alert('File uploaded successfully');
      } catch (error) {
        alert('File upload failed');
      }
    },
    getFullPath(node) {
      if (node.name === 'root') {
        return '';
      } else {
        return this.parentPath ? `${this.parentPath}/${node.name}` : node.name;
      }
    }
  },
};
</script>

<style scoped>
.folder-content {
  padding-left: 20px;
}

.upload-section {
  margin-left: 30px;
}
</style>
