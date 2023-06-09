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
}

export default new FileMetadataService();