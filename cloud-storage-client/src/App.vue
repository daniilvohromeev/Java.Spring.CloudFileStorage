<template class="">
  <div id="app">
    <div class="container p-2">
    <nav class="navbar p-2 navbar-expand navbar-dark blurred-navbar rounded box-shadow">
      <a href="/" class="navbar-brand">SkyVault</a>
      <div class="navbar-nav me-auto">
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            <i class="bi bi-house" style="font-size: 1.4rem"/> Главная
          </router-link>
        </li>
        <li class="nav-item">
          <router-link v-if="currentUser" to="/storage" class="nav-link">
            <i class="bi bi-database"  style="font-size: 1.4rem"></i>
            Хранилище
          </router-link>
        </li>
      </div>

      <div v-if="!currentUser" class="navbar-nav ">
        <li class="nav-item">
          <router-link to="/register" class="nav-link">
            <i class="bi bi-person-plus"  style="font-size: 1.4rem"/> Зарегистрироваться
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/login" class="nav-link">
            <i class="bi bi-door-closed"  style="font-size: 1.4rem"/> Войти
          </router-link>
        </li>
      </div>

      <div v-if="currentUser" class="navbar-nav">
        <li class="nav-item">
          <router-link to="/profile" class="nav-link">
            <i class="bi bi-person-fill"  style="font-size: 1.4rem"></i>
            {{ currentUser.username }}
          </router-link>
        </li>
        <li class="nav-item">
          <a class="nav-link" @click.prevent="logOut">
            <i class="bi bi-door-open"  style="font-size: 1.4rem"/> Выйти
          </a>
        </li>
      </div>
    </nav>
    </div>

    <div class="container p-2">
      <router-view />
    </div>
  </div>
</template>

<script>
import EventBus from "./common/EventBus";

export default {
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  },
  mounted() {
    EventBus.on("logout", () => {
      this.logOut();
    });
  },
  beforeUnmount() {
    EventBus.remove("logout");
  }
};
</script>