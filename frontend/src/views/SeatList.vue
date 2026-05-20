<template>
  <div class="seat-map-page">
    <div class="map-header">
      <h2>座位查看</h2>
      <div class="header-right">
        <div class="legend">
          <span class="legend-item"><span class="dot available"></span>空闲</span>
          <span class="legend-item"><span class="dot reserved"></span>已预约</span>
          <span class="legend-item"><span class="dot using"></span>使用中</span>
        </div>
        <el-select v-model="location" placeholder="按位置筛选" clearable @change="fetchSeats" style="width:200px">
          <el-option v-for="loc in locations" :key="loc" :label="loc" :value="loc" />
        </el-select>
      </div>
    </div>

    <!-- 楼层标签 -->
    <div class="floor-label">
      <span>{{ location || '全部楼层' }}</span>
    </div>

    <!-- 座位可视化区域 -->
    <div class="map-container">
      <div class="map-area">
        <div class="bookshelf">📚 书架</div>
        <div class="seats-grid">
          <SeatIcon
            v-for="seat in seats"
            :key="seat.id"
            :seatNo="seat.seatNo"
            :location="seat.location"
            :status="seat.status"
            @click="goDetail(seat.id)"
          />
        </div>
        <div class="bookshelf right">📚 书架</div>
      </div>
    </div>

    <!-- 统计栏 -->
    <div class="stats-bar">
      <div class="stat">
        <span class="stat-num">{{ seats.length }}</span>
        <span class="stat-label">总座位</span>
      </div>
      <div class="stat">
        <span class="stat-num green">{{ seats.filter(s => s.status === 'available').length }}</span>
        <span class="stat-label">空闲</span>
      </div>
      <div class="stat">
        <span class="stat-num orange">{{ seats.filter(s => s.status === 'reserved').length }}</span>
        <span class="stat-label">已预约</span>
      </div>
      <div class="stat">
        <span class="stat-num red">{{ seats.filter(s => s.status === 'using').length }}</span>
        <span class="stat-label">使用中</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSeats } from '../api/seat'
import SeatIcon from '../components/SeatIcon.vue'

const router = useRouter()
const seats = ref([])
const location = ref('')
const locations = ref([])

async function fetchSeats() {
  try {
    const data = await getSeats(location.value)
    seats.value = data
    const locs = [...new Set(data.map(s => s.location))]
    locations.value = locs
  } catch (e) {
    console.error(e)
  }
}

function goDetail(id) {
  router.push(`/seats/${id}`)
}

onMounted(fetchSeats)
</script>

<style scoped>
.seat-map-page {
  min-height: calc(100vh - 56px);
  background: #f5f7fa;
  padding: 24px;
}

.map-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.map-header h2 { font-size: 22px; color: #333; }
.header-right { display: flex; align-items: center; gap: 24px; }

.legend { display: flex; gap: 16px; }
.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #666; }
.dot { width: 10px; height: 10px; border-radius: 50%; }
.dot.available { background: #67c23a; }
.dot.reserved { background: #e6a23c; }
.dot.using { background: #f56c6c; }

.floor-label {
  text-align: center;
  padding: 10px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 15px;
  color: #666;
  letter-spacing: 2px;
}

.map-container { display: flex; justify-content: center; }

.map-area {
  display: flex;
  align-items: center;
  gap: 24px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  padding: 32px 24px;
  min-height: 400px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.bookshelf {
  writing-mode: vertical-rl;
  text-orientation: mixed;
  padding: 20px 10px;
  background: #f0f2f5;
  border-radius: 8px;
  font-size: 14px;
  color: #999;
  letter-spacing: 4px;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.seats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  flex: 1;
}

.stats-bar {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-top: 24px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.stat { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.stat-num { font-size: 28px; font-weight: bold; color: #333; }
.stat-num.green { color: #67c23a; }
.stat-num.orange { color: #e6a23c; }
.stat-num.red { color: #f56c6c; }
.stat-label { font-size: 13px; color: #999; }
</style>
