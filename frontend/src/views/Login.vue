<template>
  <div class="login-page">
    <div class="login-left">
      <div class="illustration">
        <svg viewBox="0 0 400 300" class="desk-svg">
          <rect x="50" y="80" width="300" height="160" rx="12" fill="#e8f4fd" stroke="#409eff" stroke-width="2"/>
          <rect x="80" y="110" width="80" height="50" rx="6" fill="#409eff" opacity="0.8"/>
          <rect x="180" y="110" width="80" height="50" rx="6" fill="#67c23a" opacity="0.8"/>
          <rect x="280" y="110" width="50" height="50" rx="6" fill="#e6a23c" opacity="0.8"/>
          <rect x="80" y="180" width="80" height="40" rx="6" fill="#409eff" opacity="0.5"/>
          <rect x="180" y="180" width="80" height="40" rx="6" fill="#67c23a" opacity="0.5"/>
          <circle cx="200" cy="50" r="20" fill="#409eff" opacity="0.2"/>
          <text x="200" y="56" text-anchor="middle" fill="#409eff" font-size="18" font-weight="bold">📚</text>
        </svg>
      </div>
      <h2>图书馆座位预约系统</h2>
      <p>高效管理图书馆座位资源</p>
    </div>
    <div class="login-right">
      <div class="login-card">
        <h3>欢迎登录</h3>
        <el-form :model="form" @submit.prevent="handleLogin">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
          </el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" size="large" class="login-btn">登 录</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    router.push(data.role === 'admin' ? '/admin' : '/seats')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.login-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 40px;
}
.login-left h2 { font-size: 28px; margin-top: 24px; }
.login-left p { color: rgba(255,255,255,0.7); margin-top: 8px; }
.illustration { width: 320px; }
.desk-svg { width: 100%; }

.login-right {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 480px;
  background: #fff;
}

.login-card {
  width: 360px;
  padding: 20px;
}
.login-card h3 {
  font-size: 24px;
  color: #333;
  margin-bottom: 32px;
}
.login-btn { width: 100%; height: 44px; font-size: 16px; border-radius: 8px; margin-top: 8px; }
</style>
