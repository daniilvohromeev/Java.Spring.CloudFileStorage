import { createWebHistory, createRouter } from "vue-router";
import Home from "./components/Home.vue";
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import Profile from "./components/Profile.vue";
import FolderView from "@/components/FolderView.vue";

const routes = [
    {
        path: "/",
        name: "home",
        component: Home,
    },
    {
        path: "/home/:message?",
        component: Home,
    },
    {
        path: "/login",
        component: Login,
    },
    {
        path: "/register",
        component: Register,
    },
    {
        path: "/storage",
        component: FolderView,
    },
    {
        path: "/profile",
        component: Profile,
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});


export default router;