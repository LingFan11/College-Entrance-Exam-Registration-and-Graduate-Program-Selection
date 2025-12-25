// DTO types aligned with backend Java classes

export interface ApiResponse<T> {
  status?: string // ok | unfit | error
  code?: number
  message?: string
  reason?: string
  traceId?: string
  data: T | null
}

export interface DictionaryResponse {
  provinces: string[]
  subjectTypes: string[]
  batches: string[]
  tiers: string[]
  majorCategories: string[]
  cities: string[]
  firstSubjects?: string[]
  secondSubjects?: string[]
}

export interface RecommendRequest {
  province: string
  subjectType: string // 支持中文“物理/历史”或英文
  score?: number
  rank?: number
  year?: number
  batch?: string
  firstSubject?: string
  secondSubjects?: string[]
  preferences?: {
    cities?: string[]
    majors?: string[]
    tiers?: string[]
    tuitionMax?: number
  }
  risk?: 'aggressive' | 'balanced' | 'conservative'
  page?: number
  size?: number
}

export interface RecommendationItem {
  universityId: number
  universityName: string
  city: string
  tier: string
  majorId: number
  majorName: string
  subjectType: string
  batch: string
  tuition: number
  lastYearMinRank: number
  lastYearMinScore?: number
  admitTrend: string
  fitScore: number
}

export interface RecommendResponseBuckets {
  rush: RecommendationItem[]
  stable: RecommendationItem[]
  safe: RecommendationItem[]
}

export interface RecommendResponse {
  // rank resolution meta (optional)
  resolvedRank?: number
  rankSource?: 'band' | 'mapping_exact' | 'mapping_nearest' | 'clamp_high' | 'clamp_low' | 'input_rank' | 'input_rank_checked' | string
  rankBandMin?: number | null
  rankBandMax?: number | null
  scoreUsed?: number | null
  buckets: RecommendResponseBuckets
  explain: string
}
