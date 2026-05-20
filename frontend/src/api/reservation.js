import request from './request'

export function createReservation(data) {
  return request.post('/reservations', data)
}

export function getMyReservations() {
  return request.get('/reservations/my')
}

export function cancelReservation(id) {
  return request.delete(`/reservations/${id}`)
}

export function checkIn(id) {
  return request.post(`/reservations/${id}/check-in`)
}
