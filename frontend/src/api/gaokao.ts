import axios from 'axios'
import type { ApiResponse, DictionaryResponse, RecommendRequest, RecommendResponse } from '../types/dto'

const http = axios.create({
  baseURL: '/',
  timeout: 15000,
})

http.interceptors.response.use(
  (resp) => resp,
  (error) => {
    // Normalize error
    return Promise.reject(error)
  }
)

export async function getDictionaries() {
  const resp = await http.get<ApiResponse<DictionaryResponse>>('/api/gaokao/dictionaries')
  return resp.data
}

export async function getExample() {
  const resp = await http.get<ApiResponse<RecommendRequest>>('/api/gaokao/example')
  return resp.data
}

export async function postRecommend(payload: RecommendRequest) {
  const resp = await http.post<ApiResponse<RecommendResponse>>('/api/gaokao/recommend', payload)
  return resp.data
}
