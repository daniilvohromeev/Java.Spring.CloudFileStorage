import api from "./api";
import TokenService from "./token.service";

class AuthService {
    login({username, password}) {
        return api
            .post("/auth-server/token", {
                username,
                password
            })
            .then(response => {
                if (response.data.accessToken) {
                    TokenService.setUser(response.data)
                }

                return response.data;
            });
    }
    logout() {
        TokenService.removeUser()
    }

    register({username, password}) {
        return api.post("/auth-server/register", {
            username,
            password
        })
    }
}
export default new AuthService();