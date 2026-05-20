<template>
  <nav class="navbar">
    <div class="nav-left">
      <svg viewBox="0 0 60 60" class="nav-logo">
        <rect x="5" y="15" width="50" height="35" rx="4" fill="#409eff" opacity="0.9"/>
        <rect x="12" y="22" width="16" height="10" rx="2" fill="#fff" opacity="0.9"/>
        <rect x="32" y="22" width="16" height="10" rx="2" fill="#fff" opacity="0.9"/>
        <rect x="12" y="36" width="16" height="10" rx="2" fill="#fff" opacity="0.7"/>
        <rect x="32" y="36" width="16" height="10" rx="2" fill="#fff" opacity="0.7"/>
        <circle cx="30" cy="8" r="4" fill="#409eff"/>
      </svg>
      <span class="nav-title">座位预约系统</span>
    </div>
    <div class="nav-center">
      <router-link to="/seats" class="nav-link" active-class="active">
        <span class="nav-icon">🪑</span> 座位查看
      </router-link>
      <router-link to="/my-reservations" class="nav-link" active-class="active">
        <span class="nav-icon">📋</span> 我的预约
      </router-link>
      <router-link v-if="userStore.role === 'admin'" to="/admin" class="nav-link" active-class="active">
        <span class="nav-icon">⚙️</span> 管理后台
      </router-link>
    </div>
    <div class="nav-right">
      <span class="username">{{ userStore.username }}</span>
      <el-button type="primary" text size="small" @click="handleLogout">退出</el-button>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.nav-logo { width: 28px; height: 28px; }
.nav-title { color: #333; font-size: 15px; font-weight: 600; }

.nav-center {
  display: flex;
  gap: 4px;
}
.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
}
.nav-link:hover { color: #409eff; background: #ecf5ff; }
.nav-link.active { color: #409eff; background: #ecf5ff; font-weight: 500; }
.nav-icon { font-size: 16px; }

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username { color: #999; font-size: 13px; }
</style>
