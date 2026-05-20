<template>
  <div class="detail-page">
    <el-button @click="router.back()" class="back-btn" text>
      ← 返回座位图
    </el-button>

    <div class="detail-content" v-if="seat">
      <div class="seat-visual">
        <svg viewBox="0 0 200 240" class="seat-big">
          <rect x="55" y="10" width="90" height="20" rx="8" :fill="colors.chair" />
          <rect x="65" y="30" width="70" height="14" rx="4" :fill="colors.chair" />
          <rect x="20" y="55" width="160" height="130" rx="12" :fill="colors.desk" stroke="#fff" stroke-width="2"/>
          <text x="100" y="130" text-anchor="middle" fill="#fff" font-size="36" font-weight="bold">{{ seat.seatNo }}</text>
          <circle cx="170" cy="70" r="10" :fill="colors.light" stroke="#fff" stroke-width="1.5"/>
        </svg>
        <el-tag :type="statusType(seat.status)" size="large" effect="dark" class="status-tag">
          {{ statusText(seat.status) }}
        </el-tag>
      </div>

      <div class="info-panel">
        <el-card class="info-card">
          <h3>座位信息</h3>
          <div class="info-row"><span class="label">编号</span><span>{{ seat.seatNo }}</span></div>
          <div class="info-row"><span class="label">位置</span><span>{{ seat.location }}</span></div>
          <div class="info-row"><span class="label">状态</span>
            <el-tag :type="statusType(seat.status)" size="small">{{ statusText(seat.status) }}</el-tag>
          </div>
          <div v-if="seat.currentReservation" class="info-row">
            <span class="label">当前预约</span>
            <span>{{ seat.currentReservation.startTime }} ~ {{ seat.currentReservation.endTime }}</span>
          </div>
        </el-card>

        <el-card class="schedule-card">
          <h3>预约时间表（今天 ~ 明天）</h3>
          <div v-if="schedule.length === 0" class="schedule-empty">暂无预约，座位空闲中</div>
          <div v-else class="schedule-list">
            <div v-for="item in schedule" :key="item.reservationId" class="schedule-item">
              <div class="schedule-user">
                <span class="user-avatar">{{ item.username?.charAt(0) }}</span>
                <span>{{ item.username }}</span>
              </div>
              <div class="schedule-time">
                {{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}
              </div>
              <el-tag :type="item.status === 'checked_in' ? 'success' : 'warning'" size="small">
                {{ item.status === 'checked_in' ? '使用中' : '已预约' }}
              </el-tag>
            </div>
          </div>
        </el-card>

        <el-card class="reserve-card">
          <h3>预约座位</h3>
          <el-form :model="form" label-position="top" style="margin-top:16px">
            <el-form-item label="开始时间">
              <el-date-picker v-model="form.startTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择开始时间" style="width:100%" />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-date-picker v-model="form.endTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择结束时间" style="width:100%" />
            </el-form-item>
            <el-button type="primary" :loading="loading" @click="handleReserve" size="large" class="reserve-btn">
              提交预约
            </el-button>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeatDetail, getSeatSchedule } from '../api/seat'
import { createReservation } from '../api/reservation'

const route = useRoute()
const router = useRouter()
const seat = ref(null)
const loading = ref(false)
const schedule = ref([])
const form = reactive({ startTime: '', endTime: '' })

const colorMap = {
  available: { desk: '#67c23a', chair: '#95d475', light: '#b3e19d' },
  reserved: { desk: '#e6a23c', chair: '#ebb563', light: '#f3d19e' },
  using: { desk: '#f56c6c', chair: '#f89898', light: '#fab6b6' }
}
const colors = computed(() => colorMap[seat.value?.status] || colorMap.available)

function statusType(s) {
  return { available: 'success', reserved: 'warning', using: 'danger' }[s] || 'info'
}
function statusText(s) {
  return { available: '空闲', reserved: '已预约', using: '使用中' }[s] || s
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hour}:${minute}`
}

async function fetchSeat() {
  try {
    seat.value = await getSeatDetail(route.params.id)
  } catch (e) {
    ElMessage.error('获取座位信息失败')
  }
}

async function fetchSchedule() {
  try {
    schedule.value = await getSeatSchedule(route.params.id)
  } catch (e) {
    console.error(e)
  }
}

async function handleReserve() {
  if (!form.startTime || !form.endTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  loading.value = true
  try {
    await createReservation({ seatId: seat.value.id, startTime: form.startTime, endTime: form.endTime })
    ElMessage.success('预约成功')
    router.push('/my-reservations')
  } catch (e) {
    ElMessage.error(e.message || '预约失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSeat()
  fetchSchedule()
})
</script>

<style scoped>
.detail-page {
  min-height: calc(100vh - 56px);
  background: #f5f7fa;
  padding: 24px;
}

.back-btn { color: #666; margin-bottom: 20px; font-size: 14px; }
.back-btn:hover { color: #409eff; }

.detail-content {
  display: flex;
  gap: 32px;
  max-width: 1000px;
  margin: 0 auto;
}

.seat-visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.seat-big { width: 200px; height: 240px; }
.status-tag { margin-top: 12px; }

.info-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card h3, .reserve-card h3 {
  color: #333;
  margin-bottom: 12px;
  font-size: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #333;
}
.info-row:last-child { border-bottom: none; }
.label { color: #999; }

.reserve-btn { width: 100%; border-radius: 8px; }

.schedule-card h3 {
  color: #333;
  margin-bottom: 16px;
  font-size: 16px;
}
.schedule-empty {
  color: #999;
  font-size: 14px;
  text-align: center;
  padding: 20px 0;
}
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.schedule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 13px;
}
.schedule-user {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-weight: 500;
}
.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: bold;
}
.schedule-time {
  color: #666;
  font-size: 13px;
}
</style>
