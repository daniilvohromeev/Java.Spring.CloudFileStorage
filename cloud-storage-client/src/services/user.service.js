import api from './api';
import TokenService from "@/services/token.service";
class UserService {
    getUserBoard() {
        return api.get('/auth-server/'+TokenService.getUser().username);
    }

}

export default new UserService();