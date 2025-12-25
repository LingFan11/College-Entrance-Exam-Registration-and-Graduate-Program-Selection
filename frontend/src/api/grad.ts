// API client for Graduate Advisor Selection module
// Endpoints aligned with backend doc and tests in doc/子模块：研究生择导模块（后端）.md

export interface ApiResponse<T> {
  status?: string
  code?: number
  message?: string
  reason?: string
  data: T | null
}

// Shared fetch helper
let __sessionId: string | null = null
export function setSessionId(id: string) { __sessionId = id }

async function request<T>(input: RequestInfo, init?: RequestInit): Promise<ApiResponse<T>> {
  const headers: HeadersInit = { ...(init?.headers || {}) }
  if (__sessionId) headers['X-Session-Id'] = __sessionId
  const res = await fetch(input, { ...init, headers })
  const text = await res.text()
  // rate limit handling
  if (res.status === 429) {
    const retry = res.headers.get('Retry-After') || ''
    return { status: 'error', code: 429, message: `rate_limited:${retry}`, data: null } as ApiResponse<T>
  }
  try {
    const parsed = JSON.parse(text)
    // If backend returns a wrapped ApiResponse, pass through
    if (parsed && (Object.prototype.hasOwnProperty.call(parsed, 'data') || Object.prototype.hasOwnProperty.call(parsed, 'status'))) {
      return parsed as ApiResponse<T>
    }
    // If backend returns a raw array/object, wrap it
    if (Array.isArray(parsed) || typeof parsed === 'object') {
      return { status: 'ok', data: parsed as T } as ApiResponse<T>
    }
    // Primitive JSON
    return { status: res.ok ? 'ok' : 'error', data: parsed as T } as ApiResponse<T>
  } catch (e) {
    // fallback when backend returns plain text
    return { status: res.ok ? 'ok' : 'error', data: null, message: text } as ApiResponse<T>
  }
}

// DTOs (light duplicates to avoid importing cross-file for simplicity)
export interface GradProfileSaveRequest {
  userId: number
  currentUniversity?: string
  currentMajor?: string
  gpa?: number
  englishType?: string
  englishScore?: number
  competitions?: string
  researchExp?: string
  preferredDirections?: string // JSON string per backend
  regionPref?: string
  stylePref?: string
  targetTier?: string
  targetUniversities?: string // JSON string per backend
  targetDirectionsTopn?: string // JSON string per backend
  // 考研初试成绩字段 (Requirements 1.1, 1.2)
  examTotalScore?: number      // 初试总分 (0-500)
  politicsScore?: number       // 政治成绩 (0-100)
  englishExamScore?: number    // 英语成绩 (0-100)
  mathScore?: number           // 数学成绩 (0-150)
  professionalScore?: number   // 专业课成绩 (0-150)
  targetInstitutionGroupId?: number // 目标机构分组ID
}

export interface GradStudentProfileDTO {
  id?: number
  userId: number
  currentUniversity?: string
  currentMajor?: string
  gpa?: number
  englishType?: string
  englishScore?: number
  competitions?: string
  researchExp?: string
  preferredDirections?: string
  regionPref?: string
  stylePref?: string
  targetTier?: string
  targetUniversities?: string
  targetDirectionsTopn?: string
  // 考研初试成绩字段
  examTotalScore?: number
  politicsScore?: number
  englishExamScore?: number
  mathScore?: number
  professionalScore?: number
  targetInstitutionGroupId?: number
}

export interface MentorDTO {
  id: number
  name: string
  title?: string
  email?: string
  homepage?: string | null
  brief?: string | null
}

export interface MentorSearchRequest {
  universityId?: number
  directionId?: number
  region?: string | null
  title?: string | null
  keyword?: string | null
  page?: number
  size?: number
}

export interface RecommendItemDTO {
  mentorId: number
  mentorName: string
  score: number
  status: 'fit' | 'borderline' | 'unfit' | string
  reasons: string[]
}

export interface RecommendRequestBody {
  userId: number
  page?: number
  size?: number
}

// API functions

// Profile
export async function saveProfile(body: GradProfileSaveRequest) {
  return request<GradStudentProfileDTO>('/api/grad/profile', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export async function getProfile(userId: number) {
  const url = `/api/grad/profile?userId=${encodeURIComponent(userId)}`
  return request<GradStudentProfileDTO>(url)
}

// Mentors
export async function listMentors(params: {
  universityId?: number
  directionId?: number
  region?: string
  title?: string
  keyword?: string
  page?: number
  size?: number
}) {
  const q = new URLSearchParams()
  if (params.universityId != null) q.set('universityId', String(params.universityId))
  if (params.directionId != null) q.set('directionId', String(params.directionId))
  if (params.region) q.set('region', params.region)
  if (params.title) q.set('title', params.title)
  if (params.keyword) q.set('keyword', params.keyword)
  if (params.page != null) q.set('page', String(params.page))
  if (params.size != null) q.set('size', String(params.size))
  const url = `/api/grad/mentors?${q.toString()}`
  return request<MentorDTO[]>(url)
}

export async function searchMentors(body: MentorSearchRequest) {
  return request<MentorDTO[]>('/api/grad/mentors/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

export async function deleteMentor(mentorId: number) {
  return request<null>(`/api/grad/mentors/${mentorId}`, { method: 'DELETE' })
}

// Recommend
export async function getRecommend(userId: number, page = 0, size = 20) {
  const url = `/api/grad/recommend?userId=${encodeURIComponent(userId)}&page=${page}&size=${size}`
  return request<RecommendItemDTO[]>(url)
}

export async function computeRecommend(body: RecommendRequestBody) {
  return request<RecommendItemDTO[]>('/api/grad/recommend/compute', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
}

// Dicts
export interface DirectionDTO { id: number; name: string; parentId?: number | null }
export async function listDirections(parentId?: number) {
  const url = parentId == null
    ? '/api/grad/dicts/directions'
    : `/api/grad/dicts/directions?parentId=${parentId}`
  return request<DirectionDTO[]>(url)
}

// Admin-only: refresh dictionary cache
export async function refreshDictCache() {
  return request<null>('/api/grad/dicts/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  })
}

// Session init
export interface SessionInitResp { userId: number; sessionId: string }
export async function initSession() {
  const resp = await request<SessionInitResp>('/api/grad/session/init', { method: 'POST' })
  if (resp && (resp as any).data && (resp as any).data.sessionId) {
    setSessionId((resp as any).data.sessionId)
  }
  return resp
}

// Institution Groups (Requirements 4.1, 4.2)
export interface InstitutionGroupDTO {
  id: number
  institutionCode: string
  institutionName: string
  groupName: string
  minTotalScore?: number
  maxTotalScore?: number
  priority?: number
  description?: string
}

export interface GroupMatchResponse {
  matchedGroup: InstitutionGroupDTO | null
  alternativeGroups: InstitutionGroupDTO[]
}

export async function listInstitutionGroups(institutionCode?: string) {
  const url = institutionCode
    ? `/api/grad/dicts/institution-groups?institutionCode=${encodeURIComponent(institutionCode)}`
    : '/api/grad/dicts/institution-groups'
  return request<InstitutionGroupDTO[]>(url)
}

export async function matchInstitutionGroup(institutionCode: string, totalScore: number) {
  const url = `/api/grad/institution-groups/match?institutionCode=${encodeURIComponent(institutionCode)}&totalScore=${totalScore}`
  return request<GroupMatchResponse>(url)
}
