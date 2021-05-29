import axios from 'axios'

export function request1(config) {
  const instance=axios.create({
    baseURL:'http://127.0.0.1:8088/',
    timeout:5000
  })
  return instance(config)
}
