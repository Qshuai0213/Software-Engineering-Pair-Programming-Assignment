<template>
  <div id="app">
    <Navbar v-if="userStore.token" />
    <router-view />
  </div>
</template>

<script setup>
import { useUserStore } from './stores/user'
import Navbar from './components/Navbar.vue'

const userStore = useUserStore()
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f5f7fa;
  color: #333;
}
#app { min-height: 100vh; }
</style>
