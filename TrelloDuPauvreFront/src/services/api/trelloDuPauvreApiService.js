import RequestService from "@/services/api/requestService"

const baseUrl = "http://localhost:8081/api/v1"

export default {
  get(url) {
    return RequestService.get(`${baseUrl}${url}`)
  },
  post(url, data) {
    return RequestService.post(`${baseUrl}${url}`, data)
  },
  put(url, data) {
    return RequestService.put(`${baseUrl}${url}`, data)
  },
  delete(url) {
      return RequestService.delete(`${baseUrl}${url}`)
  }
}
