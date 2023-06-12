import axiosInstance from "./api";
import TokenService from "./token.service";

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

const setup = (store) => {
    axiosInstance.interceptors.request.use(
        (config) => {
            const token = TokenService.getLocalAccessToken();
            if (config.url !== "/auth-server/refresh" && token) {
                config.headers["Authorization"] = 'Bearer ' + token;
            }
            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );

    axiosInstance.interceptors.response.use(
        (res) => {
            return res;
        },
        async (err) => {
            const originalConfig = err.config;

            if (originalConfig.url !== "/auth-server/token" && err.response) {
                if (err.response.status === 401 && !originalConfig._retry) {
                    if (isRefreshing) {
                        return new Promise((resolve, reject) => {
                            failedQueue.push({resolve, reject});
                        }).then(token => {
                            originalConfig.headers['Authorization'] = 'Bearer ' + token;
                            return axiosInstance(originalConfig);
                        }).catch(err => {
                            return Promise.reject(err);
                        });
                    }

                    originalConfig._retry = true;
                    isRefreshing = true;

                    const refreshToken = TokenService.getLocalRefreshToken();

                    try {
                        const rs = await axiosInstance.post("/auth-server/refresh", {
                            refreshToken: refreshToken,
                        });

                        const accessToken = rs.data.accessToken;
                        store.dispatch('auth/refreshToken', accessToken);
                        TokenService.updateLocalAccessToken(accessToken);

                        processQueue(null, accessToken);
                        isRefreshing = false;

                        originalConfig.headers['Authorization'] = 'Bearer ' + accessToken;
                        return axiosInstance(originalConfig);

                    } catch (_error) {
                        processQueue(_error, null);
                        isRefreshing = false;
                        return Promise.reject(_error);
                    }
                }
            }

            return Promise.reject(err);
        }
    );
};

export default setup;
