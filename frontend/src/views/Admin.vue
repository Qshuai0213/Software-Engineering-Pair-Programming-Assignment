<template>
  <div class="admin-page">
    <h2>管理后台</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="预约记录" name="reservations">
        <el-table :data="reservations" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户" width="100" />
          <el-table-column prop="seatNo" label="座位" width="80" />
          <el-table-column prop="location" label="位置" width="120" />
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="endTime" label="结束时间" width="180" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" size="small">
                {{ row.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'blocked' ? 'danger' : 'success'" size="small">
                {{ row.status === 'blocked' ? '已拉黑' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button v-if="row.role !== 'admin'" :type="row.status === 'blocked' ? 'success' : 'danger'" size="small" plain @click="handleToggleBlock(row)">
                {{ row.status === 'blocked' ? '解除拉黑' : '拉黑' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="统计数据" name="statistics">
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon">🪑</div>
            <div class="stat-num">{{ stats.totalSeats }}</div>
            <div class="stat-label">座位总数</div>
          </div>
          <div class="stat-card green">
            <div class="stat-icon">✅</div>
            <div class="stat-num">{{ stats.currentStatus?.available }}</div>
            <div class="stat-label">空闲</div>
          </div>
          <div class="stat-card orange">
            <div class="stat-icon">📅</div>
            <div class="stat-num">{{ stats.currentStatus?.reserved }}</div>
            <div class="stat-label">已预约</div>
          </div>
          <div class="stat-card red">
            <div class="stat-icon">👤</div>
            <div class="stat-num">{{ stats.currentStatus?.using }}</div>
            <div class="stat-label">使用中</div>
          </div>
        </div>

        <div class="detail-sections">
          <el-card class="section-card">
            <h4>座位使用率 Top 10</h4>
            <el-table :data="stats.seatUsage?.slice(0, 10)" size="small">
              <el-table-column prop="seatNo" label="座位" />
              <el-table-column prop="location" label="位置" />
              <el-table-column prop="count" label="预约次数">
                <template #default="{ row }">
                  <div class="bar-cell">
                    <div class="bar" :style="{ width: (row.count / maxSeatCount * 100) + '%' }"></div>
                    <span>{{ row.count }}</span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card class="section-card">
            <h4>用户预约次数</h4>
            <el-table :data="stats.userUsage" size="small">
              <el-table-column prop="username" label="用户" />
              <el-table-column prop="count" label="预约次数">
                <template #default="{ row }">
                  <div class="bar-cell">
                    <div class="bar" :style="{ width: (row.count / maxUserCount * 100) + '%' }"></div>
                    <span>{{ row.count }}</span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllReservations, getStatistics, blockUser, unblockUser, getAllUsers } from '../api/admin'

const activeTab = ref('reservations')
const reservations = ref([])
const stats = ref({})
const users = ref([])

const maxSeatCount = computed(() => Math.max(...(stats.value.seatUsage?.map(s => s.count) || [1])))
const maxUserCount = computed(() => Math.max(...(stats.value.userUsage?.map(u => u.count) || [1])))

function statusType(s) {
  return { reserved: 'warning', checked_in: 'success', cancelled: 'info', expired: 'danger' }[s] || 'info'
}
function statusText(s) {
  return { reserved: '已预约', checked_in: '已签到', cancelled: '已取消', expired: '已过期' }[s] || s
}

async function fetchReservations() {
  try { reservations.value = await getAllReservations() } catch (e) { console.error(e) }
}
async function fetchStatistics() {
  try { stats.value = await getStatistics() } catch (e) { console.error(e) }
}
async function fetchUsers() {
  try {
    users.value = await getAllUsers()
  } catch (e) { console.error(e) }
}

async function handleToggleBlock(user) {
  try {
    if (user.status === 'blocked') {
      await unblockUser(user.id)
      ElMessage.success('已解除拉黑')
    } else {
      await blockUser(user.id)
      ElMessage.success('已拉黑')
    }
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

watch(activeTab, (tab) => {
  if (tab === 'reservations') fetchReservations()
  else if (tab === 'statistics') fetchStatistics()
  else if (tab === 'users') fetchUsers()
})

onMounted(fetchReservations)
</script>

<style scoped>
.admin-page {
  min-height: calc(100vh - 56px);
  background: #f5f7fa;
  padding: 24px;
}
h2 { font-size: 22px; color: #333; margin-bottom: 20px; }

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}
.stat-card.green .stat-num { color: #67c23a; }
.stat-card.orange .stat-num { color: #e6a23c; }
.stat-card.red .stat-num { color: #f56c6c; }
.stat-icon { font-size: 28px; margin-bottom: 8px; }
.stat-num { font-size: 32px; font-weight: bold; color: #333; }
.stat-label { color: #999; font-size: 13px; margin-top: 4px; }

.detail-sections {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.section-card h4 { color: #333; margin-bottom: 12px; }

.bar-cell { display: flex; align-items: center; gap: 8px; }
.bar {
  height: 6px;
  background: linear-gradient(90deg, #409eff, #06b6d4);
  border-radius: 3px;
  min-width: 20px;
}
</style>
