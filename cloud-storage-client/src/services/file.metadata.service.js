import api from "@/services/api";

class FileMetadataService {
    async getAllByOwner(payload) {
        return api.post("/storage-server/file-metadata/allByOwner", payload);
    }

    async uploadFile(formData) {
        return api.post('/storage-server/file/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    }

    async getBucketStructure(bucketName) {
        return api.post('/storage-server/file/structure', bucketName);
    }

    async deleteFile(fileId) {
        return api.delete('/storage-server/file/delete/' + fileId)
    }

    async downloadFile(fileId, onDownloadProgress) {
        try {
            return await api.get(`/storage-server/file/download/${fileId}`, {
                responseType: 'blob',
                onDownloadProgress
            });
        } catch (error) {
            throw error;
        }
    }
}

export default new FileMetadataService();