import request from './request'

export function getAllReservations() {
  return request.get('/admin/reservations')
}

export function getStatistics() {
  return request.get('/admin/statistics')
}

export function blockUser(id) {
  return request.put(`/admin/users/${id}/block`)
}

export function unblockUser(id) {
  return request.put(`/admin/users/${id}/unblock`)
}

export function getBlockedUsers() {
  return request.get('/admin/users/blocked')
}

export function getAllUsers() {
  return request.get('/admin/users')
}
