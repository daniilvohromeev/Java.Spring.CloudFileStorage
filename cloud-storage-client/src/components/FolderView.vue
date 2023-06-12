<template>
  <div class="container blurred-navbar text-light rounded p-3">
    <div class="d-flex align-items-center justify-content-between mb-2">
      <div class="d-flex align-items-center">
        <div class="mx-2">
          <a href="" @click="goBack"><i class="bi bi-caret-left" style="font-size: 2rem"></i></a>
        </div>
        <nav aria-label="breadcrumb " class="mr-auto">
          <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item" v-for="(segment, index) in path" :key="index">
              <a href="#" @click.prevent="goToPath(index)">{{ segment }}</a>
            </li>
          </ol>
        </nav>
      </div>
      <div class="upload-section ml-auto">
        <button @click="showCreateFolderModal = true" class="btn btn-outline-primary mx-2">
          <i class="bi bi-folder-plus"></i> Создать папку
        </button>
        <button v-if="selectedFiles.length > 0" @click="deleteSelectedFiles" class="btn btn-outline-danger me-2">
          <i class="bi bi-trash"></i>
          Удалить выбранные файлы
        </button>
        <button v-if="selectedFiles.length > 0" @click="downloadSelectedFiles" class="btn btn-outline-success mr-2">
          <i class="bi bi-download"></i>
          Скачать выбранные файлы
        </button>
        <input type="file" ref="fileInput" @change="handleFileSelect" class="d-none"/>
        <button @click="triggerFileInput" class="btn btn-outline-primary mx-2">
          <i class="bi bi-cloud-plus"></i> Загрузить
        </button>
      </div>
    </div>
   <div v-if="downloadProgress > 0" class="">
     <strong>Идет загрузка файлов:</strong>
     <div v-if="downloadProgress > 0" class="progress mb-3">
       <div class="progress-bar" :style="{ width: downloadProgress + '%' }" role="progressbar" :aria-valuenow="downloadProgress" aria-valuemin="0" aria-valuemax="100">{{ downloadProgress }}%</div>
     </div>
   </div>
    <div>
      <div v-if="currentNode.children" class="content d-flex">
        <table class="">
          <thead>
          <tr class="border-bottom-solid-1px">
            <th scope="col">Имя</th>
            <th scope="col">Дата создания</th>
            <th class="text-end" scope="col">Размер</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(child, index) in currentNode.children" :key="index">
            <td class="border-bottom-solid-1px">
              <div v-if="child.type === 'folder'" class="folder d-flex align-items-center" @click="enterFolder(child)">
                📁  {{ child.name }}
              </div>
              <div v-else-if="child.type === 'file'" class="file d-flex align-items-center">
                <input type="checkbox" class="form-check-input me-2" :value="child" v-model="selectedFiles">
                {{ getIcon(child.mimeType) }}   {{ child.name }}
              </div>
            </td>
            <td class="border-bottom-solid-1px">
              <div v-if="child.type === 'file'">
                {{ new Date(child.createDate).toLocaleDateString('ru-RU') }}
              </div>
            </td>
            <td class="border-bottom-solid-1px text-end">
              {{ Math.floor((child.size / 1024) * 100) / 100 }} Кб
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div v-if="showCreateFolderModal" class="modal blurred-navbar" style="display: block">
    <div class="modal-dialog shadow-lg">
      <div class="modal-content blurred-navbar text-light">
        <div class="modal-header">
          <h5 class="modal-title">Создать новую папку</h5>
          <button type="button" class="close btn btn-danger btn-sm m-0 px-auto" @click="showCreateFolderModal = false">
            <i class="bi bi-x p-0 m-0"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="folderName">Имя папки</label>
            <input type="text" class="form-control" id="folderName" v-model="newFolderName">
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" @click="createNewFolder">Создать</button>
          <button type="button" class="btn btn-secondary" @click="showCreateFolderModal = false">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FileMetadataService from '@/services/file.metadata.service';

export default {
  name: 'FolderView',
  data() {
    return {
      tree: null,
      error: null,
      path: ['root'],
      selectedFile: null,
      showCreateFolderModal: false,
      newFolderName: '',
      selectedFiles: [],
      downloadProgress: 0,
    };
  },
  computed: {
    currentNode() {
      let current = this.tree;
      for (let i = 1; i < this.path.length; i++) {
        current = current.children.find(child => child.name === this.path[i]);
      }
      return current;
    }
  },
  async created() {
    try {
      let user = {
        id: this.$store.state.auth.user.id,
        username: this.$store.state.auth.user.username,
      };
      const response = await FileMetadataService.getAllByOwner(user);

      if (!response || response.status >= 400) {
        this.error = 'Ошибка загрузки файлов. Попробуйте еще раз.';
        return;
      }

      this.tree = this.createFolderTree(response.data);
      this.currentFolder = this.tree;
    } catch (error) {
      this.error = error.response ? error.response.data.message : 'Кажется у вас пока нет файлов';
    }
  },
  methods: {
    createFolderTree(files) {
      const tree = {name: 'root', children: [], type: 'folder', size: 0};

      files.forEach((file) => {
        const fullPath = file.filePath ? file.filePath + '/' + file.fileName : file.fileName;
        const pathParts = fullPath.split('/');
        let currentLevel = tree;

        pathParts.forEach((part, index) => {
          let node = currentLevel.children.find((child) => child.name === part);

          if (!node) {
            node = {
              name: part,
              children: [],
              type: index === pathParts.length - 1 ? 'file' : 'folder',
              size: 0,
              mimeType: index === pathParts.length - 1 ? file.contentType : null,
              fileData: index === pathParts.length - 1 ? file : null,
              fileId: index === pathParts.length - 1 ? file.id : null,
              createDate: index === pathParts.length - 1 ? file.createdAt : null,
              //TODO: добавить дату обновления и вообще обновление файлов и включение версионирования
            };
            currentLevel.children.push(node);
          }

          if (index === pathParts.length - 1 && node.type === 'file') {
            node.size = file.fileSize;
            node.fileId = file.id;
            node.createDate = file.createdAt;

            // Update size for all parent folders
            let parent = currentLevel;
            while (parent) {
              parent.size += file.fileSize;
              parent = parent.parent;
            }
          }

          // Add parent reference for traversing up the tree (and remove it before final return)
          node.parent = currentLevel;
          currentLevel = node;
        });
      });

      // Remove parent references from all nodes
      const removeParentReferences = (node) => {
        delete node.parent;
        node.children.forEach(removeParentReferences);
      };
      removeParentReferences(tree);

      return tree;
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    enterFolder(node) {
      if (node.type === 'folder') {
        this.path.push(node.name);
      }
    },
    goToPath(index) {
      this.path = this.path.slice(0, index + 1);
    },
    goBack() {
      if (this.path.length > 1) {
        this.path.pop();
      }
    },
    createNewFolder() {
      if (this.newFolderName) {
        const newFolder = {
          name: this.newFolderName,
          children: [],
          type: 'folder'
        };
        this.currentNode.children.push(newFolder);
        this.newFolderName = '';
        this.enterFolder(newFolder)
        this.showCreateFolderModal = false;
      } else {
        alert('Пожалуйста, введите имя папки.');
      }
    },
    getIcon(mimeType) {
      switch (true) {
        case mimeType.startsWith('text/'):
          return '📄';
        case mimeType.startsWith('image/'):
          return '📷';
        case mimeType.startsWith('audio/'):
          return '🎵';
        case mimeType.startsWith('video/'):
          return '🎞️';
        case mimeType.startsWith('application/json'):
        case mimeType.startsWith('application/xml'):
          return '📊';
        default:
          return '📦';
      }
    },
    handleFileSelect(event) {
      this.selectedFile = event.target.files[0];
      this.uploadSelectedFile();
    },
    async uploadSelectedFile() {
      if (!this.selectedFile) {
        alert('Пожалуйста выберите файл для загрузки.');
        return;
      }

      let formData = new FormData();
      formData.append('file', this.selectedFile);

      // Собираем полный путь к папке, не включая "root"
      let folderPath = this.path.slice(1).join('/');
      formData.append('path', folderPath);
      formData.append('encrypt', false);
      console.log(folderPath)
      try {
        await this.uploadFile(formData);
        alert('Файл выгружен успешно');
        await this.refreshData();
      } catch (error) {
        alert('Ошибка выгрузки файла');
      }
    },
    async uploadFile(formData) {
      return FileMetadataService.uploadFile(formData);
    },
    async refreshData() {
      try {
        let user = {
          id: this.$store.state.auth.user.id,
          username: this.$store.state.auth.user.username,
        };
        const response = await FileMetadataService.getAllByOwner(user);

        if (!response || response.status >= 400) {
          this.error = 'Ошибка загрузки файлов. Попробуйте еще раз.';
          return;
        }

        this.tree = this.createFolderTree(response.data);
        this.currentFolder = this.tree;
      } catch (error) {
        this.error = error.response ? error.response.data.message : 'Ошибка обновления данных';
      }
    },
    async deleteSelectedFiles() {
      for (let file of this.selectedFiles) {
        try {
          await FileMetadataService.deleteFile(file.fileId);
          console.log(file.fileId)
        } catch (error) {
          alert(`Ошибка удаления файла: ${file.name}`);
        }
      }
      this.selectedFiles = []; // очистите массив выбранных файлов
      await this.refreshData();
      let currentPathIsValid = true;
      let current = this.tree;
      for (let i = 1; i < this.path.length; i++) {
        const next = current.children.find(child => child.name === this.path[i]);
        if (!next) {
          currentPathIsValid = false;
          break; // Если следующий узел не найден, прекращаем поиск
        }
        current = next;
      }

// Проверка, существует ли текущий путь, и если нет, то вернуться на уровень выше
      if (!currentPathIsValid) {
        console.log('Current path does not exist, going back');
        this.goBack();
      }
    },
    async downloadSelectedFiles() {
      let totalSize = this.selectedFiles.reduce((sum, file) => sum + file.size, 0);
      let totalLoaded = 0;
      let fileLoadedMap = {};

      for (let file of this.selectedFiles) {
        fileLoadedMap[file.fileId] = 0;

        try {
          const response = await FileMetadataService.downloadFile(file.fileId, (progressEvent) => {
            totalLoaded = totalLoaded - fileLoadedMap[file.fileId] + progressEvent.loaded;
            fileLoadedMap[file.fileId] = progressEvent.loaded;
            this.downloadProgress = Math.round((totalLoaded / totalSize) * 100);
          });

          // Сохраняем файл после его загрузки
          const blob = new Blob([response.data], {type: file.mimeType});
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = file.name;
          a.click();
          URL.revokeObjectURL(url);

        } catch (error) {
          alert(`Ошибка скачивания файла: ${file.name}`);
        }
      }
      this.downloadProgress = 0; // сбрасываем прогресс после окончания загрузки
    }
  },
};
</script>

<style scoped>

.content {
  display: flex;
  flex-direction: column; /* добавлено для отображения элементов в столбик */
}

.folder, .file {
  margin: 10px;
  cursor: pointer;
}
</style>
