<template>
  <div class="school-info">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-left">
          <h1 class="hero-title">学校信息查询</h1>
          <p class="hero-subtitle">探索全国高校详细信息，包括强势学科、特色专业和招生简章</p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number animated-number">{{ animatedCurrentCount }}</div>
              <div class="stat-label">当前结果</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number animated-number">{{ animatedTotalCount || '—' }}</div>
              <div class="stat-label">总计学校</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number animated-number">{{ selectedSchoolCount }}</div>
              <div class="stat-label">已查看</div>
            </div>
          </div>
        </div>
        <div class="hero-right">
          <div class="hero-badge" aria-hidden="true">
            <div class="badge-icon">🏫</div>
            <div class="badge-glow"></div>
          </div>
          <div class="floating-elements">
            <div class="float-element" style="--delay: 0s; --duration: 3s;">📚</div>
            <div class="float-element" style="--delay: 1s; --duration: 4s;">🎓</div>
            <div class="float-element" style="--delay: 2s; --duration: 3.5s;">📖</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Search & Filters -->
    <section class="search-section">
      <div class="section-title">
        <span class="bar"></span>
        <span class="text">智能检索</span>
      </div>
      <div class="search-panel">
        <div class="search-row">
          <div class="search-field">
            <el-icon class="field-icon"><Search /></el-icon>
            <el-input 
              v-model="keyword" 
              placeholder="按学校名称检索，如 东南大学" 
              clearable
              size="large"
              @focus="showSuggestions = true"
              @blur="setTimeout(() => showSuggestions = false, 200)"
              @keydown.enter="doSearch"
            />
            <div v-if="filteredSuggestions.length" class="search-suggestions">
              <div 
                v-for="suggestion in filteredSuggestions" 
                :key="suggestion"
                class="suggestion-item"
                @click="selectSuggestion(suggestion)"
              >
                <el-icon><Search /></el-icon>
                <span>{{ suggestion }}</span>
              </div>
            </div>
          </div>
          <div class="filter-field">
            <el-icon class="field-icon"><Filter /></el-icon>
            <el-select 
              v-model="level" 
              placeholder="选择层级" 
              clearable
              size="large"
            >
              <el-option label="全部层级" value="" />
              <el-option v-for="lv in levels" :key="lv" :value="lv" :label="lv" />
            </el-select>
          </div>
          <div class="location-field">
            <el-icon class="field-icon"><Location /></el-icon>
            <el-input 
              v-model="province" 
              placeholder="省份，如 江苏" 
              clearable
              size="large"
            />
          </div>
          <el-button 
            type="primary" 
            size="large" 
            @click="doSearch" 
            :loading="loading"
            :icon="Search"
          >
            检索
          </el-button>
        </div>
      </div>
    </section>

    <!-- School List -->
    <section class="school-list-section">
      <div class="section-title">
        <span class="bar"></span>
        <span class="text">检索结果</span>
        <div class="section-actions" v-if="list.length">
          <el-select v-model="listSize" @change="doSearch()" size="small">
            <el-option label="10条/页" :value="10" />
            <el-option label="20条/页" :value="20" />
            <el-option label="50条/页" :value="50" />
          </el-select>
        </div>
      </div>
      
      <div v-if="listLoading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>正在加载学校信息...</span>
      </div>
      
      <div v-else-if="!list.length" class="empty-state">
        <el-icon class="empty-icon"><DocumentRemove /></el-icon>
        <div class="empty-title">暂无匹配结果</div>
        <div class="empty-desc">请尝试调整搜索条件或关键词</div>
      </div>
      
      <div v-else class="school-grid">
        <div 
          v-for="school in list" 
          :key="school.id" 
          class="school-card"
          :class="{ active: selectedId === school.id, 'card-flip': cardFlipStates[school.id] }"
          @click="selectSchool(school.id)"
          @mouseenter="handleCardHover(school.id, true)"
          @mouseleave="handleCardHover(school.id, false)"
        >
          <div class="card-inner">
            <div class="card-front">
              <div class="card-header">
                <div class="school-name">{{ school.name }}</div>
                <div class="school-badge" v-if="school.level">
                  {{ school.level }}
                </div>
              </div>
              <div class="card-body">
                <div class="school-location" v-if="school.province || school.city">
                  <el-icon><Location /></el-icon>
                  <span>{{ school.province || '' }} {{ school.city || '' }}</span>
                </div>
                <div class="school-type" v-if="getSchoolTypeIcon(school.level)">
                  <span class="type-icon">{{ getSchoolTypeIcon(school.level) }}</span>
                  <span class="type-text">{{ getSchoolTypeText(school.level) }}</span>
                </div>
                <div class="school-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    :icon="View"
                    @click.stop="handleSchoolAction(school)"
                    class="action-btn"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
            <div class="card-back">
              <div class="card-back-content">
                <div class="quick-info">
                  <div class="info-title">快速预览</div>
                  <div class="info-items">
                    <div class="info-row">
                      <span class="info-icon">🏛️</span>
                      <span>{{ school.level || '普通高校' }}</span>
                    </div>
                    <div class="info-row">
                      <span class="info-icon">📍</span>
                      <span>{{ school.province || '' }} {{ school.city || '' }}</span>
                    </div>
                    <div class="info-row" v-if="school.foundedYear">
                      <span class="info-icon">📅</span>
                      <span>建校 {{ school.foundedYear }}</span>
                    </div>
                  </div>
                </div>
                <div class="quick-actions">
                  <el-button 
                    size="small" 
                    :icon="View"
                    @click.stop="selectSchool(school.id)"
                  >
                    详细信息
                  </el-button>
                  <el-button 
                    size="small" 
                    :icon="Link"
                    @click.stop="handleSchoolAction(school)"
                    v-if="school.website"
                  >
                    官网
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="list.length" class="pagination-section">
        <div class="pagination-info">
          <span v-if="listTotalElements != null && listTotalPages != null">
            共 {{ listTotalElements }} 条记录，第 {{ listPage + 1 }} / {{ listTotalPages }} 页
          </span>
          <span v-else>
            第 {{ listPage + 1 }} 页
          </span>
        </div>
        <div class="pagination-controls">
          <el-button 
            @click="prevListPage" 
            :disabled="listPage === 0 || listLoading"
            :icon="ArrowLeft"
          >
            上一页
          </el-button>
          <el-button 
            @click="nextListPage" 
            :disabled="!listHasNext || listLoading"
            :icon="ArrowRight"
          >
            下一页
          </el-button>
        </div>
      </div>
    </section>

    <!-- School Detail -->
    <section v-if="detail" class="school-detail-section">
      <div class="detail-header">
        <h2 class="detail-title">{{ detail.name }}</h2>
        <div class="detail-meta">
          <div class="meta-item">
            <el-icon><School /></el-icon>
            <span>{{ detail.level || '—' }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Location /></el-icon>
            <span>{{ detail.province || '' }} {{ detail.city || '' }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>建校 {{ detail.foundedYear || '—' }}</span>
          </div>
        </div>
      </div>
      
      <div v-if="detail.brief" class="school-brief">
        <div class="brief-title">
          <el-icon><Document /></el-icon>
          <span>学校简介</span>
        </div>
        <div class="brief-content">{{ detail.brief }}</div>
      </div>

      <div class="detail-tabs">
        <el-tabs v-model="tab" class="custom-tabs">
          <el-tab-pane label="强势学科" name="disc">
            <template #label>
              <div class="tab-label">
                <el-icon><TrendCharts /></el-icon>
                <span>强势学科</span>
              </div>
            </template>
          </el-tab-pane>
          <el-tab-pane label="特色专业" name="major">
            <template #label>
              <div class="tab-label">
                <el-icon><Medal /></el-icon>
                <span>特色专业</span>
              </div>
            </template>
          </el-tab-pane>
          <el-tab-pane label="招生简章" name="bro">
            <template #label>
              <div class="tab-label">
                <el-icon><Document /></el-icon>
                <span>招生简章</span>
              </div>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- Disciplines -->
      <div v-if="tab==='disc'" class="tab-content">
        <div class="content-filters">
          <div class="filter-row">
            <div class="filter-group">
              <label class="filter-label">年份</label>
              <el-input-number 
                v-model="discParams.year" 
                :min="2020" 
                :max="2024" 
                placeholder="2024"
                size="small"
              />
            </div>
            <div class="filter-group">
              <label class="filter-label">显示数量</label>
              <el-input-number 
                v-model="discParams.topN" 
                :min="1" 
                :max="50" 
                placeholder="5"
                size="small"
              />
            </div>
            <div class="filter-group">
              <label class="filter-label">排序方式</label>
              <el-select v-model="discParams.sortBy" size="small">
                <el-option label="按等级" value="grade" />
                <el-option label="按排名" value="rankValue" />
              </el-select>
            </div>
            <div class="filter-group">
              <label class="filter-label">排序</label>
              <el-select v-model="discParams.order" size="small">
                <el-option label="升序" value="asc" />
                <el-option label="降序" value="desc" />
              </el-select>
            </div>
            <div class="filter-group">
              <label class="filter-label">每页</label>
              <el-select v-model="discSize" @change="resetAndLoadDisciplines" size="small">
                <el-option label="10" :value="10" />
                <el-option label="20" :value="20" />
                <el-option label="50" :value="50" />
              </el-select>
            </div>
            <el-button 
              @click="loadDisciplines" 
              :loading="discLoading"
              :icon="Refresh"
              size="small"
            >
              刷新
            </el-button>
          </div>
        </div>
        
        <div v-if="discLoading" class="loading-state">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>正在加载学科信息...</span>
        </div>
        
        <div v-else-if="!disciplines.length" class="empty-state">
          <el-icon class="empty-icon"><DocumentRemove /></el-icon>
          <div class="empty-title">暂无学科数据</div>
          <div class="empty-desc">该学校暂无强势学科信息</div>
        </div>
        
        <div v-else class="discipline-grid">
          <div 
            v-for="discipline in disciplines" 
            :key="discipline.disciplineId" 
            class="discipline-card"
          >
            <div class="discipline-header">
              <div class="discipline-name">{{ discipline.disciplineName }}</div>
              <div class="discipline-grade" :class="getGradeClass(discipline.grade || discipline.rank)">
                {{ discipline.grade || discipline.rank || '—' }}
              </div>
            </div>
            <div class="discipline-body">
              <div class="discipline-info">
                <div class="info-item" v-if="discipline.rankValue != null">
                  <span class="info-label">排名</span>
                  <span class="info-value">{{ discipline.rankValue }}</span>
                </div>
                <div class="info-item" v-if="discipline.source">
                  <span class="info-label">来源</span>
                  <span class="info-value">{{ discipline.source }}</span>
                </div>
                <div class="info-item" v-if="discipline.year">
                  <span class="info-label">年份</span>
                  <span class="info-value">{{ discipline.year }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="disciplines.length" class="content-pagination">
          <el-button 
            @click="discPrev" 
            :disabled="discPage === 0 || discLoading"
            :icon="ArrowLeft"
            size="small"
          >
            上一页
          </el-button>
          <span class="page-info">第 {{ discPage + 1 }} 页</span>
          <el-button 
            @click="discNext" 
            :disabled="!discHasNext || discLoading"
            :icon="ArrowRight"
            size="small"
          >
            下一页
          </el-button>
        </div>
      </div>

      <!-- Majors -->
      <div v-if="tab==='major'" class="tab-content">
        <div class="content-filters">
          <div class="filter-row">
            <div class="filter-group">
              <label class="filter-label">学历层次</label>
              <el-select v-model="majorParams.degree" placeholder="选择层次" size="small">
                <el-option label="全部层次" value="" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </div>
            <div class="filter-group">
              <label class="filter-label">每页显示</label>
              <el-select v-model="majorSize" @change="resetAndLoadMajors" size="small">
                <el-option label="10" :value="10" />
                <el-option label="20" :value="20" />
                <el-option label="50" :value="50" />
              </el-select>
            </div>
            <el-button 
              @click="loadMajors" 
              :loading="majorLoading"
              :icon="Refresh"
              size="small"
            >
              刷新
            </el-button>
          </div>
        </div>
        
        <div v-if="majorLoading" class="loading-state">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>正在加载专业信息...</span>
        </div>
        
        <div v-else-if="!majors.length" class="empty-state">
          <el-icon class="empty-icon"><DocumentRemove /></el-icon>
          <div class="empty-title">暂无专业数据</div>
          <div class="empty-desc">该学校暂无特色专业信息</div>
        </div>
        
        <div v-else class="major-grid">
          <div 
            v-for="major in majors" 
            :key="major.id" 
            class="major-card"
          >
            <div class="major-header">
              <div class="major-name">{{ major.name }}</div>
              <div class="major-degree" v-if="major.degree">
                {{ major.degree }}
              </div>
            </div>
            <div class="major-body">
              <div class="major-info">
                <div class="info-item" v-if="major.departmentName">
                  <el-icon><School /></el-icon>
                  <span>{{ major.departmentName }}</span>
                </div>
                <div class="info-item feature-tag" v-if="major.featureTag">
                  <el-icon><Medal /></el-icon>
                  <span>{{ major.featureTag }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="majors.length" class="content-pagination">
          <el-button 
            @click="majorPrev" 
            :disabled="majorPage === 0 || majorLoading"
            :icon="ArrowLeft"
            size="small"
          >
            上一页
          </el-button>
          <span class="page-info">第 {{ majorPage + 1 }} 页</span>
          <el-button 
            @click="majorNext" 
            :disabled="!majorHasNext || majorLoading"
            :icon="ArrowRight"
            size="small"
          >
            下一页
          </el-button>
        </div>
      </div>

      <!-- Brochures -->
      <div v-if="tab==='bro'" class="tab-content">
        <div class="content-filters">
          <div class="filter-row">
            <div class="filter-group">
              <label class="filter-label">学历层次</label>
              <el-select v-model="broParams.degree" placeholder="选择层次" size="small">
                <el-option label="全部层次" value="" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </div>
            <div class="filter-group">
              <label class="filter-label">年份</label>
              <el-input-number 
                v-model="broParams.year" 
                :min="2020" 
                :max="2024" 
                placeholder="2024"
                size="small"
              />
            </div>
            <div class="filter-group">
              <label class="filter-label">每页显示</label>
              <el-select v-model="broSize" @change="resetAndLoadBrochures" size="small">
                <el-option label="10" :value="10" />
                <el-option label="20" :value="20" />
                <el-option label="50" :value="50" />
              </el-select>
            </div>
            <el-button 
              @click="loadBrochures" 
              :loading="broLoading"
              :icon="Refresh"
              size="small"
            >
              刷新
            </el-button>
          </div>
        </div>
        
        <div v-if="broLoading" class="loading-state">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>正在加载招生简章...</span>
        </div>
        
        <div v-else-if="!brochures.length" class="empty-state">
          <el-icon class="empty-icon"><DocumentRemove /></el-icon>
          <div class="empty-title">暂无招生简章</div>
          <div class="empty-desc">该学校暂无招生简章信息</div>
        </div>
        
        <div v-else class="brochure-list">
          <div 
            v-for="brochure in brochures" 
            :key="brochure.id" 
            class="brochure-card"
          >
            <div class="brochure-header">
              <el-icon class="brochure-icon"><Document /></el-icon>
              <div class="brochure-title">
                <a 
                  v-if="brochure.link" 
                  :href="brochure.link" 
                  target="_blank" 
                  class="brochure-link"
                >
                  {{ brochure.title }}
                  <el-icon><Link /></el-icon>
                </a>
                <span v-else class="brochure-text">{{ brochure.title }}</span>
              </div>
            </div>
            <div class="brochure-meta">
              <span class="meta-tag" v-if="brochure.degree">{{ brochure.degree }}</span>
              <span class="meta-year" v-if="brochure.year">{{ brochure.year }}年</span>
            </div>
          </div>
        </div>
        
        <div v-if="brochures.length" class="content-pagination">
          <el-button 
            @click="broPrev" 
            :disabled="broPage === 0 || broLoading"
            :icon="ArrowLeft"
            size="small"
          >
            上一页
          </el-button>
          <span class="page-info">第 {{ broPage + 1 }} 页</span>
          <el-button 
            @click="broNext" 
            :disabled="!broHasNext || broLoading"
            :icon="ArrowRight"
            size="small"
          >
            下一页
          </el-button>
        </div>
      </div>
    </section>

    <div v-if="error" class="error-message">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ error }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import {
  Search,
  Filter,
  Location,
  Loading,
  DocumentRemove,
  View,
  ArrowLeft,
  ArrowRight,
  School,
  Calendar,
  Document,
  TrendCharts,
  Medal,
  Refresh,
  WarningFilled,
  Link
} from '@element-plus/icons-vue'
import {
  listLevels,
  listSchools,
  getSchool,
  listStrengthDisciplines,
  listMajors,
  listBrochures,
  type SchoolBriefDTO,
  type SchoolDetailDTO,
  type DisciplineStrengthDTO,
  type MajorDTO,
  type BrochureDTO,
} from '../api/school'

const levels = ref<string[]>([])
const keyword = ref('东南')
const level = ref('')
const province = ref('')
const loading = ref(false) // global search action
const listLoading = ref(false)
const discLoading = ref(false)
const majorLoading = ref(false)
const broLoading = ref(false)
const error = ref('')

const list = ref<SchoolBriefDTO[]>([])
const listPage = ref(0)
const listSize = ref(20)
const listHasNext = ref(false)
const listTotalElements = ref<number | null>(null)
const listTotalPages = ref<number | null>(null)
const selectedId = ref<number | null>(null)
const detail = ref<SchoolDetailDTO | null>(null)
const tab = ref<'disc' | 'major' | 'bro'>('disc')

// 动画和交互状态
const animatedCurrentCount = ref(0)
const animatedTotalCount = ref(0)
const selectedSchoolCount = ref(0)
const viewedSchools = ref<Set<number>>(new Set())
const cardFlipStates = ref<Record<number, boolean>>({})

// 搜索建议和快捷操作
const searchSuggestions = ref<string[]>([
  '清华大学', '北京大学', '复旦大学', '上海交通大学', 
  '浙江大学', '南京大学', '中国科学技术大学', '华中科技大学',
  '西安交通大学', '哈尔滨工业大学', '同济大学', '北京航空航天大学'
])
const showSuggestions = ref(false)
const filteredSuggestions = computed(() => {
  if (!keyword.value || !showSuggestions.value) return []
  return searchSuggestions.value.filter(s => 
    s.toLowerCase().includes(keyword.value.toLowerCase())
  ).slice(0, 6)
})

const disciplines = ref<DisciplineStrengthDTO[]>([])
const majors = ref<MajorDTO[]>([])
const brochures = ref<BrochureDTO[]>([])

const discParams = ref({ year: 2024 as number | undefined, topN: 5, sortBy: 'grade' as 'grade' | 'rankValue', order: 'asc' as 'asc' | 'desc' })
const discPage = ref(0)
const discSize = ref(20)
const discHasNext = ref(false)

const majorParams = ref({ degree: '' as string | undefined, sortBy: 'name' as 'name' | 'code', order: 'asc' as 'asc' | 'desc' })
const majorPage = ref(0)
const majorSize = ref(20)
const majorHasNext = ref(false)

const broParams = ref({ degree: '' as string | undefined, year: undefined as number | undefined })
const broPage = ref(0)
const broSize = ref(20)
const broHasNext = ref(false)

// 数字动画函数
function animateNumber(targetRef: { value: number }, from: number, to: number, duration: number = 1000): Promise<void> {
  return new Promise((resolve) => {
    const startTime = performance.now()
    const animate = (currentTime: number) => {
      const elapsed = currentTime - startTime
      const progress = Math.min(elapsed / duration, 1)
      
      // 使用缓动函数
      const easeOutQuart = 1 - Math.pow(1 - progress, 4)
      const current = Math.round(from + (to - from) * easeOutQuart)
      
      targetRef.value = current
      
      if (progress < 1) {
        requestAnimationFrame(animate)
      } else {
        resolve()
      }
    }
    requestAnimationFrame(animate)
  })
}

async function bootstrap() {
  const lv = await listLevels()
  levels.value = (lv.data as any) || []
  await doSearch()
}

async function doSearch() {
  loading.value = true
  listLoading.value = true
  error.value = ''
  try {
    listPage.value = 0
    const resp = await listSchools({ keyword: keyword.value, level: level.value, province: province.value, page: listPage.value, size: listSize.value })
    const data: any = resp.data
    const arr: any[] = Array.isArray(data) ? data : (data && Array.isArray(data.content) ? data.content : [])
    list.value = arr
    // prefer Spring Page 'last' flag when available; otherwise fallback to length heuristic
    if (data && typeof data.totalElements === 'number' && typeof data.totalPages === 'number') {
      listTotalElements.value = data.totalElements
      listTotalPages.value = data.totalPages
    } else {
      listTotalElements.value = null
      listTotalPages.value = null
    }
    listHasNext.value = data && typeof data.last === 'boolean'
      ? !data.last
      : (listTotalPages.value != null ? (listPage.value + 1) < (listTotalPages.value) : (arr.length >= listSize.value))
    // 动画更新数字
    await nextTick()
    animateNumber(animatedCurrentCount, animatedCurrentCount.value, list.value.length, 800)
    
    if (listTotalElements.value !== null) {
      animateNumber(animatedTotalCount, animatedTotalCount.value, listTotalElements.value, 1000)
    }
    
    // auto select first
    if (list.value.length) {
      await selectSchool(list.value[0].id)
    } else {
      detail.value = null
      disciplines.value = []
      majors.value = []
      brochures.value = []
    }
  } catch (e: any) {
    error.value = e?.message || String(e)
  } finally {
    loading.value = false
    listLoading.value = false
  }
}

async function selectSchool(id: number) {
  selectedId.value = id
  
  // 记录已查看的学校
  if (!viewedSchools.value.has(id)) {
    viewedSchools.value.add(id)
    animateNumber(selectedSchoolCount, selectedSchoolCount.value, viewedSchools.value.size, 600)
  }
  
  const d = await getSchool(id)
  detail.value = d.data
  tab.value = 'disc'
  // reset pages
  discPage.value = 0; majorPage.value = 0; broPage.value = 0
  await Promise.all([loadDisciplines(), loadMajors(), loadBrochures()])
}

async function loadDisciplines() {
  if (!selectedId.value) return
  discLoading.value = true
  try {
    const resp = await listStrengthDisciplines(selectedId.value, { ...discParams.value, page: discPage.value, size: discSize.value })
    const arr = resp.data || []
    disciplines.value = arr
    discHasNext.value = arr.length >= discSize.value
  } finally {
    discLoading.value = false
  }
}

async function loadMajors() {
  if (!selectedId.value) return
  majorLoading.value = true
  try {
    const resp = await listMajors(selectedId.value, { ...majorParams.value, page: majorPage.value, size: majorSize.value })
    const arr = resp.data || []
    majors.value = arr
    majorHasNext.value = arr.length >= majorSize.value
  } finally {
    majorLoading.value = false
  }
}

async function loadBrochures() {
  if (!selectedId.value) return
  broLoading.value = true
  try {
    const resp = await listBrochures(selectedId.value, { ...broParams.value, page: broPage.value, size: broSize.value })
    const arr = resp.data || []
    brochures.value = arr
    broHasNext.value = arr.length >= broSize.value
  } finally {
    broLoading.value = false
  }
}

function prevListPage() {
  if (listPage.value === 0) return
  listPage.value--
  refreshListPage()
}
function nextListPage() {
  if (!listHasNext.value) return
  listPage.value++
  refreshListPage()
}
async function refreshListPage() {
  listLoading.value = true
  const resp = await listSchools({ keyword: keyword.value, level: level.value, province: province.value, page: listPage.value, size: listSize.value })
  const data: any = resp.data
  const arr: any[] = Array.isArray(data) ? data : (data && Array.isArray(data.content) ? data.content : [])
  list.value = arr
  if (data && typeof data.totalElements === 'number' && typeof data.totalPages === 'number') {
    listTotalElements.value = data.totalElements
    listTotalPages.value = data.totalPages
  } else {
    listTotalElements.value = null
    listTotalPages.value = null
  }
  listHasNext.value = data && typeof data.last === 'boolean'
    ? !data.last
    : (listTotalPages.value != null ? (listPage.value + 1) < (listTotalPages.value) : (arr.length >= listSize.value))
  listLoading.value = false
}

function discPrev() { if (discPage.value===0) return; discPage.value--; loadDisciplines() }
function discNext() { if (!discHasNext.value) return; discPage.value++; loadDisciplines() }
function resetAndLoadDisciplines() { discPage.value = 0; loadDisciplines() }

function majorPrev() { if (majorPage.value===0) return; majorPage.value--; loadMajors() }
function majorNext() { if (!majorHasNext.value) return; majorPage.value++; loadMajors() }
function resetAndLoadMajors() { majorPage.value = 0; loadMajors() }

function broPrev() { if (broPage.value===0) return; broPage.value--; loadBrochures() }
function broNext() { if (!broHasNext.value) return; broPage.value++; loadBrochures() }
function resetAndLoadBrochures() { broPage.value = 0; loadBrochures() }

// Helper function for grade styling
function getGradeClass(grade: string | undefined): string {
  if (!grade) return 'grade-default'
  const gradeStr = grade.toString().toLowerCase()
  if (gradeStr.includes('a+') || gradeStr.includes('a-') || gradeStr === 'a') return 'grade-a'
  if (gradeStr.includes('b+') || gradeStr.includes('b-') || gradeStr === 'b') return 'grade-b'
  if (gradeStr.includes('c+') || gradeStr.includes('c-') || gradeStr === 'c') return 'grade-c'
  if (gradeStr.includes('1') || gradeStr.includes('top')) return 'grade-top'
  return 'grade-default'
}

// 卡片交互处理
function handleCardHover(schoolId: number, isHover: boolean) {
  if (isHover) {
    setTimeout(() => {
      cardFlipStates.value[schoolId] = true
    }, 200)
  } else {
    cardFlipStates.value[schoolId] = false
  }
}

// 学校类型图标
function getSchoolTypeIcon(level: string | undefined): string {
  if (!level) return '🏫'
  const levelLower = level.toLowerCase()
  if (levelLower.includes('985')) return '⭐'
  if (levelLower.includes('211')) return '🌟'
  if (levelLower.includes('双一流')) return '💎'
  if (levelLower.includes('重点')) return '🏆'
  return '🏫'
}

function getSchoolTypeText(level: string | undefined): string {
  if (!level) return '普通高校'
  return level
}

// 搜索建议处理
function selectSuggestion(suggestion: string) {
  keyword.value = suggestion
  showSuggestions.value = false
  doSearch()
}

// Handle school action: jump to website if available, otherwise show details
function handleSchoolAction(school: SchoolBriefDTO) {
  if (school.website) {
    window.open(school.website, '_blank')
  } else {
    selectSchool(school.id)
  }
}

// 组件挂载时初始化动画
onMounted(() => {
  // 初始化数字动画
  setTimeout(() => {
    animatedCurrentCount.value = list.value.length
    animatedTotalCount.value = listTotalElements.value || 0
  }, 500)
})

bootstrap()
</script>

<style scoped>
/* 基础布局 */
.school-info {
  padding: 8px 8px 0;
  font-family: 'Noto Sans SC', system-ui, sans-serif;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #f7faff 0%, #e8f2ff 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 200%;
  height: 200%;
  background: radial-gradient(ellipse at 30% 20%, rgba(30,58,138,0.08), rgba(37,99,235,0.05) 40%, transparent 60%);
  filter: blur(20px);
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 24px;
  align-items: center;
}

.hero-title {
  font-size: 32px;
  font-weight: 700;
  color: #0f245e;
  margin: 0 0 12px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 16px;
  color: var(--text-secondary, #6B7280);
  margin: 0 0 20px;
  line-height: 1.5;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1E3A8A;
  line-height: 1;
}

.animated-number {
  transition: all 0.3s ease;
  position: relative;
}

.animated-number::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #1E3A8A, #2563EB);
  transition: width 0.3s ease;
}

.stat-item:hover .animated-number::after {
  width: 100%;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary, #6B7280);
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: linear-gradient(180deg, rgba(15,36,94,0.06), rgba(15,36,94,0.16), rgba(15,36,94,0.06));
  border-radius: 1px;
}

.hero-badge {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: #fff;
  display: grid;
  place-items: center;
  font-size: 40px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  animation: floatBadge 2800ms ease-in-out infinite;
  position: relative;
  overflow: hidden;
}

.badge-icon {
  position: relative;
  z-index: 2;
}

.badge-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(30,58,138,0.1) 0%, rgba(37,99,235,0.05) 50%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.float-element {
  position: absolute;
  font-size: 20px;
  opacity: 0.6;
  animation: float var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

.float-element:nth-child(1) {
  top: 20%;
  right: 10%;
}

.float-element:nth-child(2) {
  top: 60%;
  right: 20%;
}

.float-element:nth-child(3) {
  top: 40%;
  right: 5%;
}

@keyframes floatBadge {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

@keyframes pulse {
  0%, 100% { 
    opacity: 0.3; 
    transform: translate(-50%, -50%) scale(1); 
  }
  50% { 
    opacity: 0.6; 
    transform: translate(-50%, -50%) scale(1.1); 
  }
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0) rotate(0deg); 
    opacity: 0.4; 
  }
  25% { 
    transform: translateY(-10px) rotate(5deg); 
    opacity: 0.7; 
  }
  50% { 
    transform: translateY(-20px) rotate(0deg); 
    opacity: 0.6; 
  }
  75% { 
    transform: translateY(-10px) rotate(-5deg); 
    opacity: 0.7; 
  }
}

/* Section Titles */
.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin: 16px 2px 12px;
}

.section-title .bar {
  width: 4px;
  height: 18px;
  background: #1E3A8A;
  border-radius: 2px;
}

.section-title .text {
  font-weight: 600;
  color: #0f245e;
  font-size: 16px;
  flex: 1;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Search Section */
.search-section {
  margin-bottom: 16px;
}

.search-panel {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  border: 1px solid #eef2f7;
}

.search-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 12px;
  align-items: end;
}

.search-field,
.filter-field,
.location-field {
  position: relative;
}

.search-field {
  position: relative;
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-top: none;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  z-index: 1000;
  max-height: 240px;
  overflow-y: auto;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f3f4f6;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover {
  background: #f8fafc;
  color: #1E3A8A;
  transform: translateX(4px);
}

.suggestion-item .el-icon {
  color: #6b7280;
  font-size: 14px;
}

.suggestion-item:hover .el-icon {
  color: #1E3A8A;
}

.field-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary, #6B7280);
  z-index: 2;
}

.search-field :deep(.el-input__wrapper) {
  padding-left: 36px;
}

.filter-field :deep(.el-select .el-input__wrapper),
.location-field :deep(.el-input__wrapper) {
  padding-left: 36px;
}

/* School List Section */
.school-list-section {
  margin-bottom: 16px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #eef2f7;
}

.loading-icon,
.empty-icon {
  font-size: 48px;
  color: var(--text-secondary, #6B7280);
  margin-bottom: 16px;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f245e;
  margin-bottom: 8px;
}

.empty-desc {
  color: var(--text-secondary, #6B7280);
  font-size: 14px;
}

/* School Grid */
.school-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.school-card {
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  height: 200px;
  perspective: 1000px;
}

.card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: left;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.school-card.card-flip .card-inner {
  transform: rotateY(180deg);
}

.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #eef2f7;
  background: #fff;
  transition: all 220ms ease;
}

.card-back {
  transform: rotateY(180deg);
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.school-card:hover .card-front {
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  transform: translateY(-2px);
}

.school-card.active .card-front {
  border-color: #1E3A8A;
  box-shadow: 0 8px 24px rgba(30,58,138,0.15);
}

.card-front::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #1E3A8A, #2563EB);
  border-radius: 12px 12px 0 0;
  transform: scaleX(0);
  transition: transform 220ms ease;
}

.school-card:hover .card-front::before,
.school-card.active .card-front::before {
  transform: scaleX(1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.school-name {
  font-size: 18px;
  font-weight: 600;
  color: #0f245e;
  line-height: 1.3;
}

.school-badge {
  background: linear-gradient(135deg, #1E3A8A, #2563EB);
  color: #fff;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.school-location {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary, #6B7280);
  font-size: 14px;
}

.school-type {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: rgba(30,58,138,0.05);
  border-radius: 8px;
  font-size: 13px;
}

.type-icon {
  font-size: 16px;
}

.type-text {
  color: #1E3A8A;
  font-weight: 500;
}

.school-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.action-btn {
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
}

/* 卡片背面样式 */
.card-back-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.quick-info {
  flex: 1;
}

.info-title {
  font-size: 16px;
  font-weight: 600;
  color: #1E3A8A;
  margin-bottom: 12px;
  text-align: center;
}

.info-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #475569;
}

.info-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.quick-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 16px;
}

/* Pagination */
.pagination-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #eef2f7;
}

.pagination-info {
  color: var(--text-secondary, #6B7280);
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  gap: 8px;
}

/* School Detail Section */
.school-detail-section {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #eef2f7;
  overflow: hidden;
  margin-bottom: 16px;
}

.detail-header {
  background: linear-gradient(135deg, #f7faff 0%, #e8f2ff 100%);
  padding: 24px;
}

.detail-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f245e;
  margin: 0 0 12px;
}

.detail-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-secondary, #6B7280);
  font-size: 14px;
}


/* School Brief */
.school-brief {
  padding: 20px 24px;
  border-bottom: 1px solid #eef2f7;
}

.brief-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #0f245e;
  margin-bottom: 12px;
}

.brief-content {
  color: var(--text-secondary, #6B7280);
  line-height: 1.6;
  white-space: pre-wrap;
}

/* Custom Tabs */
.detail-tabs {
  padding: 0 24px;
}

.custom-tabs :deep(.el-tabs__header) {
  margin: 0;
  border-bottom: 1px solid #eef2f7;
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.custom-tabs :deep(.el-tabs__item) {
  padding: 16px 0;
  margin-right: 32px;
  font-weight: 500;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Tab Content */
.tab-content {
  padding: 24px;
}

/* Content Filters */
.content-filters {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  gap: 16px;
  align-items: end;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 120px;
}

.filter-label {
  font-size: 13px;
  color: var(--text-secondary, #6B7280);
  font-weight: 500;
}

/* Discipline Grid */
.discipline-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.discipline-card {
  background: #fff;
  border: 1px solid #eef2f7;
  border-radius: 8px;
  padding: 16px;
  transition: all 180ms ease;
}

.discipline-card:hover {
  border-color: #cfe0ff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}

.discipline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.discipline-name {
  font-weight: 600;
  color: #0f245e;
  font-size: 15px;
}

.discipline-grade {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.grade-a { background: #dcfce7; color: #166534; }
.grade-b { background: #fef3c7; color: #92400e; }
.grade-c { background: #fee2e2; color: #991b1b; }
.grade-top { background: #dbeafe; color: #1e40af; }
.grade-default { background: #f3f4f6; color: #6b7280; }

.discipline-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 13px;
  color: var(--text-secondary, #6B7280);
}

.info-value {
  font-size: 13px;
  font-weight: 500;
  color: #0f245e;
}

/* Major Grid */
.major-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.major-card {
  background: #fff;
  border: 1px solid #eef2f7;
  border-radius: 8px;
  padding: 16px;
  transition: all 180ms ease;
}

.major-card:hover {
  border-color: #cfe0ff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}

.major-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.major-name {
  font-weight: 600;
  color: #0f245e;
  font-size: 15px;
}

.major-degree {
  background: #f0f9ff;
  color: #0369a1;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.major-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.major-info .info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary, #6B7280);
}

.major-info .feature-tag {
  color: #10B981;
  font-weight: 500;
}

/* Brochure List */
.brochure-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.brochure-card {
  background: #fff;
  border: 1px solid #eef2f7;
  border-radius: 8px;
  padding: 16px;
  transition: all 180ms ease;
}

.brochure-card:hover {
  border-color: #cfe0ff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.brochure-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.brochure-icon {
  color: #1E3A8A;
  font-size: 18px;
}

.brochure-title {
  flex: 1;
}

.brochure-link {
  color: #1E3A8A;
  text-decoration: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.brochure-link:hover {
  color: #2563EB;
}

.brochure-text {
  font-weight: 500;
  color: #0f245e;
}

.brochure-meta {
  display: flex;
  gap: 8px;
}

.meta-tag,
.meta-year {
  background: #f3f4f6;
  color: #6b7280;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

/* Content Pagination */
.content-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 0;
}

.page-info {
  color: var(--text-secondary, #6B7280);
  font-size: 14px;
  margin: 0 8px;
}

/* Error Message */
.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #dc2626;
  margin-top: 16px;
}

/* 微交互动画增强 */
.search-panel {
  transition: all 0.3s ease;
}

.search-panel:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transform: translateY(-1px);
}

.section-title .bar {
  transition: all 0.3s ease;
}

.section-title:hover .bar {
  width: 6px;
  background: linear-gradient(180deg, #1E3A8A, #2563EB);
}

.hero-stats .stat-item {
  transition: all 0.3s ease;
  cursor: pointer;
}

.hero-stats .stat-item:hover {
  transform: translateY(-2px);
}

.hero-stats .stat-item:hover .stat-number {
  color: #2563EB;
  text-shadow: 0 2px 4px rgba(37,99,235,0.2);
}

/* 按钮增强动画 */
.el-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.el-button:active {
  transform: translateY(0);
}

/* 标签页增强 */
.custom-tabs :deep(.el-tabs__item) {
  transition: all 0.3s ease;
}

.custom-tabs :deep(.el-tabs__item:hover) {
  color: #1E3A8A;
  transform: translateY(-1px);
}

/* 加载状态增强 */
.loading-state {
  animation: fadeInUp 0.6s ease-out;
}

.empty-state {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 卡片网格动画 */
.school-grid .school-card {
  animation: slideInUp 0.6s ease-out;
  animation-fill-mode: both;
}

.school-grid .school-card:nth-child(1) { animation-delay: 0.1s; }
.school-grid .school-card:nth-child(2) { animation-delay: 0.2s; }
.school-grid .school-card:nth-child(3) { animation-delay: 0.3s; }
.school-grid .school-card:nth-child(4) { animation-delay: 0.4s; }
.school-grid .school-card:nth-child(5) { animation-delay: 0.5s; }
.school-grid .school-card:nth-child(6) { animation-delay: 0.6s; }

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-badge {
    width: 64px;
    height: 64px;
    font-size: 28px;
  }
  
  .floating-elements {
    display: none;
  }
  
  .search-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .school-grid {
    grid-template-columns: 1fr;
  }
  
  .school-card {
    height: auto;
    min-height: 180px;
  }
  
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .detail-meta {
    gap: 16px;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .discipline-grid,
  .major-grid {
    grid-template-columns: 1fr;
  }
  
  .pagination-section {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-suggestions {
    max-height: 200px;
  }
}

@media (max-width: 480px) {
  .school-info {
    padding: 4px;
  }
  
  .hero-section {
    padding: 16px;
  }
  
  .hero-title {
    font-size: 24px;
  }
  
  .search-panel,
  .tab-content {
    padding: 12px;
  }
  
  .detail-header {
    padding: 16px;
  }
  
  .detail-title {
    font-size: 22px;
  }
}
</style>
