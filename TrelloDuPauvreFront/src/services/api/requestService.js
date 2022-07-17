const axios = require("axios")

const addParamsToUrl = (url, params) => {
  const paramsToAdd = Object.keys(params).reduce((accumulator, key, index) => {
    const value = JSON.stringify(params[key])
    return index > 0 ? (accumulator += "&" + key + "=" + value) : (accumulator += key + "=" + value)
  }, "?")

  return url + paramsToAdd
}

/**
 * Appel http
 * @param {*} axiosMethod method axios à appeler
 * @param {*} url url d'appel
 * @param {*} body body de l'url
 */
async function request(axiosMethod, url, body) {
  const response = await axiosMethod(url, body )
  if (response.status == 204) {
    return response
  } else {
    return response.data
  }
}

/**
 * Request Service
 *  Service for http request
 * @type {{get(*=): Promise<*>}}
 */
const RequestService = {
  get(url) {
    return request(axios.get, url)
  },
  post(url, data) {
    return request(axios.post, url, data)
  },
  put(url, data) {
    return request(axios.put, url, data)
  },
  delete(url) {
    return request(axios.delete, url)
  },
  addParamsToUrl
}

export default RequestService
