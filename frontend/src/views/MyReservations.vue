<template>
  <div class="reservations-page">
    <h2>我的预约</h2>
    <div class="cards">
      <div v-for="r in reservations" :key="r.id" class="res-card" :class="r.status">
        <div class="res-header">
          <span class="seat-badge">{{ r.seatNo }}</span>
          <el-tag :type="statusType(r.status)" size="small">{{ statusText(r.status) }}</el-tag>
        </div>
        <div class="res-body">
          <div class="res-location">{{ r.location }}</div>
          <div class="res-time">
            <div>{{ r.startTime }}</div>
            <div class="arrow">↓</div>
            <div>{{ r.endTime }}</div>
          </div>
        </div>
        <div class="res-actions">
          <el-button v-if="r.status === 'reserved'" type="success" size="small" @click="handleCheckIn(r.id)">签到</el-button>
          <el-button v-if="r.status === 'reserved' || r.status === 'checked_in'" type="danger" size="small" plain @click="handleCancel(r.id)">取消预约</el-button>
        </div>
      </div>

      <div v-if="reservations.length === 0" class="empty">
        <svg viewBox="0 0 200 200" class="empty-illustration">
          <circle cx="100" cy="100" r="80" fill="#ecf5ff" />
          <rect x="60" y="50" width="80" height="60" rx="8" fill="#d9ecff" stroke="#409eff" stroke-width="2" />
          <rect x="70" y="60" width="60" height="8" rx="4" fill="#a0cfff" />
          <rect x="70" y="74" width="40" height="8" rx="4" fill="#a0cfff" />
          <rect x="70" y="88" width="50" height="8" rx="4" fill="#a0cfff" />
          <circle cx="100" cy="140" r="20" fill="#d9ecff" stroke="#409eff" stroke-width="2" />
          <text x="100" y="146" text-anchor="middle" fill="#409eff" font-size="20" font-weight="bold">?</text>
        </svg>
        <h3>暂无预约记录</h3>
        <p class="empty-desc">你还没有预约过座位，快去挑选一个心仪的座位吧</p>
        <el-button type="primary" size="large" @click="router.push('/seats')" class="empty-btn">
          去预约座位
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyReservations, cancelReservation, checkIn } from '../api/reservation'

const router = useRouter()
const reservations = ref([])

function statusType(s) {
  return { reserved: 'warning', checked_in: 'success', cancelled: 'info', expired: 'danger' }[s] || 'info'
}
function statusText(s) {
  return { reserved: '已预约', checked_in: '已签到', cancelled: '已取消', expired: '已过期' }[s] || s
}

async function fetchReservations() {
  try { reservations.value = await getMyReservations() } catch (e) { console.error(e) }
}

async function handleCheckIn(id) {
  try {
    await checkIn(id)
    ElMessage.success('签到成功')
    fetchReservations()
  } catch (e) {
    ElMessage.error(e.message || '签到失败')
  }
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm('确定取消该预约吗？', '提示', { type: 'warning' })
    await cancelReservation(id)
    ElMessage.success('已取消')
    fetchReservations()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '取消失败')
  }
}

onMounted(fetchReservations)
</script>

<style scoped>
.reservations-page {
  min-height: calc(100vh - 56px);
  background: #f5f7fa;
  padding: 24px;
}
h2 { font-size: 22px; color: #333; margin-bottom: 24px; }

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  max-width: 1000px;
  margin: 0 auto;
}

.res-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.res-card:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.res-card.cancelled { opacity: 0.5; }
.res-card.expired { opacity: 0.5; }

.res-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.seat-badge {
  background: #ecf5ff;
  color: #409eff;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 15px;
}

.res-body { margin-bottom: 16px; }
.res-location { color: #999; font-size: 13px; margin-bottom: 8px; }
.res-time { font-size: 14px; color: #333; }
.arrow { color: #ccc; margin: 4px 0; }

.res-actions { display: flex; gap: 8px; }

.empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 0;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.empty-illustration {
  width: 160px;
  height: 160px;
  margin-bottom: 24px;
}
.empty h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 600;
}
.empty-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 28px;
}
.empty-btn {
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 15px;
}
</style>
