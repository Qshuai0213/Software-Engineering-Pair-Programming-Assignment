import request from './request'

export function getSeats(location) {
  return request.get('/seats', { params: location ? { location } : {} })
}

export function getSeatDetail(id) {
  return request.get(`/seats/${id}`)
}

export function getSeatSchedule(id) {
  return request.get(`/seats/${id}/schedule`)
}
