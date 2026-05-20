<template>
  <div class="seat-icon" :class="status" @click="$emit('click')">
    <svg viewBox="0 0 80 100" xmlns="http://www.w3.org/2000/svg">
      <!-- 椅子 -->
      <rect x="25" y="5" width="30" height="8" rx="4" :fill="chairColor" />
      <rect x="28" y="13" width="24" height="6" rx="2" :fill="chairColor" />
      <!-- 桌面 -->
      <rect x="10" y="25" width="60" height="50" rx="6" :fill="deskColor" stroke="#fff" stroke-width="1.5" />
      <!-- 座位号 -->
      <text x="40" y="55" text-anchor="middle" fill="#fff" font-size="14" font-weight="bold">{{ seatNo }}</text>
      <!-- 状态指示灯 -->
      <circle cx="65" cy="32" r="5" :fill="lightColor" stroke="#fff" stroke-width="1"/>
    </svg>
    <div class="seat-label">{{ location }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  seatNo: String,
  location: String,
  status: String
})

defineEmits(['click'])

const colors = {
  available: { desk: '#67c23a', chair: '#95d475', light: '#b3e19d' },
  reserved: { desk: '#e6a23c', chair: '#ebb563', light: '#f3d19e' },
  using: { desk: '#f56c6c', chair: '#f89898', light: '#fab6b6' }
}

const deskColor = computed(() => colors[props.status]?.desk || '#c0c4cc')
const chairColor = computed(() => colors[props.status]?.chair || '#dcdfe6')
const lightColor = computed(() => colors[props.status]?.light || '#e4e7ed')
</script>

<style scoped>
.seat-icon {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  padding: 12px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #e4e7ed;
}
.seat-icon:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}
.seat-icon svg {
  width: 72px;
  height: 90px;
}
.seat-label {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
}
</style>
