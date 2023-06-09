<template>
  <div className="container bg-light border-radius p-3">
    <folder v-if="tree" :node="tree"/>
    <h1 v-if="error">{{ error }}</h1>
  </div>
</template>

<script>
import FileMetadataService from '@/services/file.metadata.service';
import Folder from '@/components/Folder.vue';

export default {
  name: 'FolderView',
  components: {
    Folder,
  },
  data() {
    return {
      tree: null,
      error: null,
    };
  },
  async created() {
    try {
      let user = {
        id: this.$store.state.auth.user.id,
        username: this.$store.state.auth.user.username,
      };
      console.log(user);
      const response = await FileMetadataService.getAllByOwner(user);

      if (!response || response.status >= 400) {
        this.error = 'Error fetching files. Please try again.';
        return;
      }

      this.tree = this.createFolderTree(response.data);

    } catch (error) {
      this.error = error.response ? error.response.data.message : 'Кажется у вас пока нет файлов';
    }
  },
  methods: {
    createFolderTree(files) {
      const tree = {name: 'root', children: [], type: 'folder'};
      files.forEach((file) => {
        const fullPath = file.filePath ? file.filePath + '/' + file.fileName : file.fileName;
        const pathParts = fullPath.split('/');
        let currentLevel = tree;
        pathParts.forEach((part, index) => {
          if (!currentLevel.children.some((child) => child.name === part)) {
            currentLevel.children.push({
              name: part,
              children: [],
              type: index === pathParts.length - 1 ? 'file' : 'folder',
              fileData: index === pathParts.length - 1 ? file : null,
            });
          }
          currentLevel = currentLevel.children.find((child) => child.name === part);
        });
      });
      return tree;
    },
  },
};
</script>
