// API client for School Information module
// Endpoints aligned with backend doc in doc/子模块：学校信息模块（后端）.md

export interface ApiResponse<T> {
  status?: string
  code?: number
  message?: string
  reason?: string
  data: T | null
}

// Shared fetch helper (simple wrapper, no session header required for this module)
async function request<T>(input: RequestInfo, init?: RequestInit): Promise<ApiResponse<T>> {
  const res = await fetch(input, init)
  const text = await res.text()
  if (res.status === 429) {
    const retry = res.headers.get('Retry-After') || ''
    return { status: 'error', code: 429, message: `rate_limited:${retry}`, data: null } as ApiResponse<T>
  }
  try {
    const parsed = JSON.parse(text)
    if (parsed && (Object.prototype.hasOwnProperty.call(parsed, 'data') || Object.prototype.hasOwnProperty.call(parsed, 'status'))) {
      return parsed as ApiResponse<T>
    }
    if (Array.isArray(parsed) || typeof parsed === 'object') {
      return { status: 'ok', data: parsed as T } as ApiResponse<T>
    }
    return { status: res.ok ? 'ok' : 'error', data: parsed as T } as ApiResponse<T>
  } catch (e) {
    return { status: res.ok ? 'ok' : 'error', data: null, message: text } as ApiResponse<T>
  }
}

// DTOs
export interface SchoolBriefDTO {
  id: number
  name: string
  level?: string
  province?: string
  city?: string
  tags?: string[]
  website?: string
}

export interface SchoolDetailDTO extends SchoolBriefDTO {
  brief?: string
  foundedYear?: number
}

export interface DisciplineStrengthDTO {
  disciplineId: number
  disciplineName: string
  rank?: string | null
  source?: string | null
  year?: number
  grade?: string | null
  rankValue?: number | null
}

export interface MajorDTO {
  id: number
  name: string
  degree?: string
  disciplineId?: number
  departmentName?: string
  featureTag?: string
}

export interface BrochureDTO {
  id: number
  degree?: string
  year?: number
  title: string
  link?: string | null
}

// Query helpers
export function buildQuery(params: Record<string, any>) {
  const q = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') q.set(k, String(v))
  })
  const s = q.toString()
  return s ? `?${s}` : ''
}

// API functions
export async function listSchools(params: {
  keyword?: string
  level?: string
  province?: string
  page?: number
  size?: number
}) {
  const url = `/api/school/list${buildQuery(params)}`
  return request<SchoolBriefDTO[]>(url)
}

export async function getSchool(id: number) {
  return request<SchoolDetailDTO>(`/api/school/${id}`)
}

export async function listStrengthDisciplines(schoolId: number, params: {
  year?: number
  topN?: number
  page?: number
  size?: number
  sortBy?: 'grade' | 'rankValue'
  order?: 'asc' | 'desc'
}) {
  const url = `/api/school/${schoolId}/disciplines/strength${buildQuery(params)}`
  return request<DisciplineStrengthDTO[]>(url)
}

export async function listMajors(schoolId: number, params: {
  degree?: string
  disciplineId?: number
  page?: number
  size?: number
  sortBy?: 'name' | 'code'
  order?: 'asc' | 'desc'
}) {
  const url = `/api/school/${schoolId}/majors${buildQuery(params)}`
  return request<MajorDTO[]>(url)
}

export async function listBrochures(schoolId: number, params: {
  degree?: string
  year?: number
  page?: number
  size?: number
}) {
  const url = `/api/school/${schoolId}/brochures${buildQuery(params)}`
  return request<BrochureDTO[]>(url)
}

// Dicts
export async function listLevels() {
  return request<string[]>(`/api/school/dicts/levels`)
}

export interface DisciplineDictItem { id: number; name: string; parentId?: number | null }
export async function listDisciplineDict(parentId?: number) {
  const url = parentId == null
    ? '/api/school/dicts/disciplines'
    : `/api/school/dicts/disciplines?parentId=${parentId}`
  return request<DisciplineDictItem[]>(url)
}

export async function searchTags(params: { type: 'school' | 'department' | 'major'; keyword?: string }) {
  const url = `/api/school/tags${buildQuery(params)}`
  return request<{ id: number; name: string; type: string }[]>(url)
}
