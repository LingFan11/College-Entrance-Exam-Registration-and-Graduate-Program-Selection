<template>
  <div class="gaokao-mock-fill">
    <!-- 页面标题 -->
    <header class="page-header">
      <h2>高考模拟填报</h2>
      <p class="desc">按照步骤填写，几分钟内获得匹配的院校专业推荐。如果不清楚如何填写，直接点击「示例填充」。</p>
    </header>

    <!-- 条件填写卡片 -->
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <div class="section-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
            </div>
            <div>
              <h3 class="section-title">填写基本信息</h3>
              <p class="section-desc">请准确填写您的高考成绩和志愿偏好</p>
            </div>
          </div>
          <div class="actions">
            <el-button size="small" @click="fillExample" :loading="loadingExample" class="example-btn">
              <svg viewBox="0 0 24 24" style="width: 14px; height: 14px; margin-right: 4px;">
                <path fill="currentColor" d="M9,12L11,14L15,10L20,15H4L9,12Z"/>
              </svg>
              示例填充
            </el-button>
          </div>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" size="default" class="modern-form">
        <!-- 基础信息区块 -->
        <div class="form-section">
          <div class="section-header">
            <h4 class="section-subtitle">基础信息</h4>
            <div class="section-line"></div>
          </div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="省份" prop="province" class="form-item-enhanced">
                <el-select v-model="form.province" placeholder="请选择省份" filterable :loading="loadingDicts" class="select-enhanced">
                  <el-option v-for="p in dicts?.provinces || []" :key="p" :label="p" :value="p" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="选科类型" prop="subjectType" class="form-item-enhanced">
                <el-select v-model="form.subjectType" placeholder="请选择" class="select-enhanced">
                  <el-option v-for="s in dicts?.subjectTypes || []" :key="s" :label="s" :value="s as any" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 次选科目（仅当字典存在时显示，且必须选择 2 个） -->
        <div class="form-section" v-if="dicts?.secondSubjects?.length">
          <el-row :gutter="20">
            <el-col :xs="24">
              <el-form-item label="次选科目" prop="secondSubjects" :required="!!(dicts?.secondSubjects?.length)" class="form-item-enhanced">
                <el-select v-model="form.secondSubjects" multiple :multiple-limit="2" clearable filterable placeholder="请选择 2 个" class="select-enhanced">
                  <el-option v-for="ss in dicts?.secondSubjects || []" :key="ss" :label="ss" :value="ss" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 成绩信息区块 -->
        <div class="form-section">
          <div class="section-header">
            <h4 class="section-subtitle">成绩信息</h4>
            <div class="section-line"></div>
          </div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="分数" prop="score" class="form-item-enhanced">
                <el-input-number v-model="form.score" :min="0" :max="750" :step="1" :precision="0" :controls="false" style="width:100%" class="number-input-enhanced" placeholder="请输入高考分数" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="位次" prop="rank" class="form-item-enhanced">
                <el-input-number v-model="form.rank" :min="0" :max="100000" :step="50" :precision="0" :controls="false" style="width:100%" class="number-input-enhanced" placeholder="请输入省内位次" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 年份批次区块 -->
        <div class="form-section">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="年份" class="form-item-enhanced">
                <el-date-picker
                  v-model="yearStr"
                  type="year"
                  placeholder="选择年份"
                  format="YYYY"
                  value-format="YYYY"
                  style="width:100%"
                  @change="onYearChange"
                  class="date-picker-enhanced"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="批次" class="form-item-enhanced">
                <el-select v-model="form.batch" clearable filterable placeholder="可选" class="select-enhanced">
                  <el-option v-for="b in dicts?.batches || []" :key="b" :label="b" :value="b" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 偏好设置区块 -->
        <div class="form-section">
          <div class="section-header">
            <h4 class="section-subtitle">偏好设置</h4>
            <div class="section-line"></div>
          </div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="风险偏好" class="form-item-enhanced">
                <el-select v-model="form.risk" placeholder="balanced" class="select-enhanced">
                  <el-option label="激进 (aggressive)" value="aggressive">
                    <div class="option-content">
                      <span class="option-label">激进</span>
                      <span class="option-desc">冲击更高层次院校</span>
                    </div>
                  </el-option>
                  <el-option label="平衡 (balanced)" value="balanced">
                    <div class="option-content">
                      <span class="option-label">平衡</span>
                      <span class="option-desc">冲稳保均衡分布</span>
                    </div>
                  </el-option>
                  <el-option label="保守 (conservative)" value="conservative">
                    <div class="option-content">
                      <span class="option-label">保守</span>
                      <span class="option-desc">优先确保录取</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="最高学费" class="form-item-enhanced">
                <el-input-number v-model="form.preferences.tuitionMax" :min="0" :max="200000" :step="100" :precision="0" :controls="false" style="width:100%" class="number-input-enhanced" placeholder="元/年" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 意向选择区块 -->
        <div class="form-section">
          <div class="section-header">
            <h4 class="section-subtitle">意向选择</h4>
            <div class="section-line"></div>
          </div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="意向城市" class="form-item-enhanced">
                <el-select v-model="form.preferences.cities" multiple filterable clearable placeholder="可多选" class="select-enhanced">
                  <el-option v-for="c in dicts?.cities || []" :key="c" :label="c" :value="c" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="8">
              <el-form-item label="意向层次" class="form-item-enhanced">
                <el-select v-model="form.preferences.tiers" multiple clearable placeholder="985/211/双一流/普通" class="select-enhanced">
                  <el-option v-for="t in dicts?.tiers || []" :key="t" :label="t" :value="t" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :xs="24">
              <el-form-item label="意向专业" class="form-item-enhanced">
                <el-select v-model="form.preferences.majors" multiple filterable clearable placeholder="按专业大类选择" class="select-enhanced">
                  <el-option v-for="m in dicts?.majorCategories || []" :key="m" :label="m" :value="m" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 提交按钮区域 -->
        <div class="submit-section">
          <!-- 必填字段提示 -->
          <div v-if="!canSubmit" class="submit-hint">
            <el-alert
              title="请完善必填信息"
              type="info"
              :closable="false"
              show-icon
              class="submit-alert"
            >
              <template #default>
                <div class="hint-content">
                  <p class="hint-title">还需要填写以下必填项：</p>
                  <ul class="hint-list">
                    <li v-if="!form.province">• 请选择省份</li>
                    <li v-if="!form.subjectType">• 请选择选科类型</li>
                    <li v-if="form.score == null && form.rank == null">• 请填写分数或位次（至少填写一项）</li>
                    <li v-if="dicts?.secondSubjects?.length && (!Array.isArray(form.secondSubjects) || form.secondSubjects.length !== 2)">• 请选择2个次选科目</li>
                    <li v-if="form.rank != null && form.rank > 50 && (form.score == null || form.score === undefined)">• 位次较高时需要同时填写分数</li>
                  </ul>
                </div>
              </template>
            </el-alert>
          </div>

          <el-button type="primary" size="large" @click="onSubmit" :disabled="!canSubmit" :loading="submitting" class="submit-btn-large">
            <svg viewBox="0 0 24 24" style="width: 16px; height: 16px; margin-right: 6px;" v-if="!submitting">
              <path fill="currentColor" d="M4,6H2V20A2,2 0 0,0 4,22H18V20H4V6M20,2H8A2,2 0 0,0 6,4V16A2,2 0 0,0 8,18H20A2,2 0 0,0 22,16V4A2,2 0 0,0 20,2M20,16H8V4H20V16M16,6H18V8H16V6M16,9H18V11H16V9M16,12H18V14H16V12M11,13.5L13.5,16L18,11.5L16.5,10L13.5,13L11.5,11L11,13.5Z"/>
            </svg>
            {{ submitting ? '推荐中...' : '获取推荐' }}
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 推荐结果卡片 -->
    <el-card shadow="never" class="results-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <div class="section-icon">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M19,3H5C3.9,3 3,3.9 3,5V19C3,20.1 3.9,21 5,21H19C20.1,21 21,20.1 21,19V5C21,3.9 20.1,3 19,3M19,19H5V5H19V19M13.96,12.29L11.21,15.83L9.25,13.47L6.5,17H17.5L13.96,12.29Z"/>
              </svg>
            </div>
            <div>
              <h3 class="section-title">推荐结果</h3>
              <p class="section-desc">基于您的条件为您推荐合适的院校专业</p>
            </div>
          </div>
          <div class="actions">
            <el-tag type="info" v-if="respExplain" class="explain-tag">{{ respExplain }}</el-tag>
            <el-button size="small" @click="onExportCsv" :disabled="!hasDataInActiveTab" plain class="export-btn">
              <svg viewBox="0 0 24 24" style="width: 14px; height: 14px; margin-right: 4px;">
                <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              导出 CSV
            </el-button>
          </div>
        </div>
      </template>

      <el-alert
        v-if="lastStatus && lastStatus !== 'ok'"
        :title="lastReason || '请求未通过'"
        type="warning"
        :closable="false"
        show-icon
        class="alert-enhanced"
        :description="`traceId: ${lastTraceId || '-'}`"
      />

      <!-- 位次解析元信息 -->
      <div 
        v-if="lastStatus === 'ok' && (metaResolvedRank != null || metaRankSource || metaScoreUsed != null)"
        class="meta-info-card"
      >
        <div class="meta-header">
          <svg viewBox="0 0 24 24" class="meta-icon">
            <path fill="currentColor" d="M13,9H18.5L13,3.5V9M6,2H14L20,8V20A2,2 0 0,1 18,22H6C4.89,22 4,21.1 4,20V4C4,2.89 4.89,2 6,2M11,4H6V6H11V4M9,8H6V10H9V8M16,12H6V14H16V12M16,16H6V18H16V16Z"/>
          </svg>
          <span class="meta-title">位次解析信息</span>
        </div>
        <el-descriptions
          :column="4"
          size="small"
          class="meta-descriptions"
        >
          <el-descriptions-item label="最终位次">
            <span class="meta-value">{{ metaResolvedRank ?? '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="数据来源">
            <span class="meta-value">{{ metaRankSource || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="映射分数">
            <span class="meta-value">{{ metaScoreUsed ?? '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="分段区间">
            <span class="meta-value" v-if="metaRankBandMin != null || metaRankBandMax != null">
              [{{ metaRankBandMin ?? '-' }}, {{ metaRankBandMax ?? '-' }}]
            </span>
            <span class="meta-value" v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <div v-if="!hasAnyResults" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M12,4A8,8 0 0,1 20,12A8,8 0 0,1 12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4M12,6A6,6 0 0,0 6,12A6,6 0 0,0 12,18A6,6 0 0,0 18,12A6,6 0 0,0 12,6M12,8A4,4 0 0,1 16,12A4,4 0 0,1 12,16A4,4 0 0,1 8,12A4,4 0 0,1 12,8Z"/>
          </svg>
        </div>
        <h4 class="empty-title">暂无推荐结果</h4>
        <p class="empty-desc">请填写完整的条件信息并点击"获取推荐"按钮</p>
      </div>

      <el-tabs v-else v-model="activeTab">
        <el-tab-pane label="冲 (rush)" name="rush">
          <el-table :data="results.rush" border stripe style="width: 100%">
            <el-table-column prop="universityName" label="院校" min-width="160" />
            <el-table-column prop="majorName" label="专业" min-width="160" />
            <el-table-column prop="city" label="城市" width="100" />
            <el-table-column prop="tier" label="层次" width="120" />
            <el-table-column prop="batch" label="批次" width="120" />
            <el-table-column prop="subjectType" label="选科" width="90" />
            <el-table-column prop="lastYearMinRank" label="去年最低位次" width="140" />
            <el-table-column prop="lastYearMinScore" label="去年最低分数" width="140" />
            <el-table-column prop="tuition" label="学费" width="100" />
            <el-table-column prop="admitTrend" label="趋势" width="90" />
            <el-table-column prop="fitScore" label="匹配度" width="100">
              <template #default="{ row }">{{ row.fitScore != null ? row.fitScore + '%' : '-' }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="稳 (stable)" name="stable">
          <el-table :data="results.stable" border stripe style="width: 100%">
            <el-table-column prop="universityName" label="院校" min-width="160" />
            <el-table-column prop="majorName" label="专业" min-width="160" />
            <el-table-column prop="city" label="城市" width="100" />
            <el-table-column prop="tier" label="层次" width="120" />
            <el-table-column prop="batch" label="批次" width="120" />
            <el-table-column prop="subjectType" label="选科" width="90" />
            <el-table-column prop="lastYearMinRank" label="去年最低位次" width="140" />
            <el-table-column prop="lastYearMinScore" label="去年最低分数" width="140" />
            <el-table-column prop="tuition" label="学费" width="100" />
            <el-table-column prop="admitTrend" label="趋势" width="90" />
            <el-table-column prop="fitScore" label="匹配度" width="100">
              <template #default="{ row }">{{ row.fitScore != null ? row.fitScore + '%' : '-' }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="保 (safe)" name="safe">
          <el-table :data="results.safe" border stripe style="width: 100%">
            <el-table-column prop="universityName" label="院校" min-width="160" />
            <el-table-column prop="majorName" label="专业" min-width="160" />
            <el-table-column prop="city" label="城市" width="100" />
            <el-table-column prop="tier" label="层次" width="120" />
            <el-table-column prop="batch" label="批次" width="120" />
            <el-table-column prop="subjectType" label="选科" width="90" />
            <el-table-column prop="lastYearMinRank" label="去年最低位次" width="140" />
            <el-table-column prop="lastYearMinScore" label="去年最低分数" width="140" />
            <el-table-column prop="tuition" label="学费" width="100" />
            <el-table-column prop="admitTrend" label="趋势" width="90" />
            <el-table-column prop="fitScore" label="匹配度" width="100">
              <template #default="{ row }">{{ row.fitScore != null ? row.fitScore + '%' : '-' }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getDictionaries, getExample, postRecommend } from '../api/gaokao'
import type { DictionaryResponse, RecommendRequest, RecommendResponse, RecommendationItem } from '../types/dto'

const dicts = ref<DictionaryResponse | null>(null)
const loadingDicts = ref(false)
const loadingExample = ref(false)
const submitting = ref(false)
const respExplain = ref('')
const lastStatus = ref<string>('')
const lastReason = ref<string>('')
const lastTraceId = ref<string>('')
// rank resolution meta
const metaResolvedRank = ref<number | null>(null)
const metaRankSource = ref<string>('')
const metaRankBandMin = ref<number | null>(null)
const metaRankBandMax = ref<number | null>(null)
const metaScoreUsed = ref<number | null>(null)

const results = reactive<{ rush: RecommendationItem[]; stable: RecommendationItem[]; safe: RecommendationItem[] }>({
  rush: [],
  stable: [],
  safe: [],
})

const activeTab = ref<'rush' | 'stable' | 'safe'>('stable')

const formRef = ref()
const form = reactive<RecommendRequest>({
  province: '',
  subjectType: '',
  score: undefined,
  rank: undefined,
  year: undefined,
  batch: undefined,
  secondSubjects: [],
  risk: 'balanced',
  preferences: { cities: [], majors: [], tiers: [], tuitionMax: undefined },
  page: undefined,
  size: undefined,
})
const yearStr = ref<string | null>(null)

const hasAnyResults = computed(() => results.rush.length + results.stable.length + results.safe.length > 0)
const hasDataInActiveTab = computed(() => {
  if (activeTab.value === 'rush') return results.rush.length > 0
  if (activeTab.value === 'stable') return results.stable.length > 0
  return results.safe.length > 0
})

const canSubmit = computed(() => {
  const basic = !!form.province && !!form.subjectType
  const hasRankOrScore = form.score != null || form.rank != null
  const maskOk = !(form.rank != null && form.rank > MASK_TOP_RANK && (form.score == null || form.score === undefined))
  const needSecond = !!(dicts.value?.secondSubjects && dicts.value.secondSubjects.length)
  const secondsOk = !needSecond || (Array.isArray(form.secondSubjects) && form.secondSubjects.length === 2)
  return basic && hasRankOrScore && maskOk && secondsOk && !submitting.value
})

// 与后端 application.yml 保持一致（可根据需要改为后端下发或接口返回）
const MASK_TOP_RANK = 50

const rules = {
  province: [{ required: true, message: '请选择省份', trigger: 'change' }],
  subjectType: [{ required: true, message: '请选择选科类型', trigger: 'change' }],
  secondSubjects: [
    {
      validator: (_: any, value: any[], cb: any) => {
        // 当字典提供 secondSubjects 时，必须且只能选择 2 个
        const hasDict = !!(dicts.value?.secondSubjects && dicts.value.secondSubjects.length)
        if (hasDict) {
          const len = Array.isArray(value) ? value.length : 0
          if (len !== 2) return cb(new Error('请恰好选择 2 个次选科目'))
        }
        cb()
      },
      trigger: 'change',
    },
  ],
  // 自定义校验：score 与 rank 二选一
  rank: [
    {
      validator: (_: any, __: any, cb: any) => {
        // 二选一
        if (form.score == null && form.rank == null) {
          return cb(new Error('分数与位次需至少填写一个'))
        }
        // 屏蔽规则：rank > MASK_TOP_RANK 必须提供分数
        if (form.rank != null && form.rank > MASK_TOP_RANK && (form.score == null || form.score === undefined)) {
          return cb(new Error(`该位次需提供分数用于屏蔽规则校验（>${MASK_TOP_RANK}）`))
        }
        cb()
      },
      trigger: 'change',
    },
  ],
}

async function loadDictionaries() {
  loadingDicts.value = true
  try {
    const resp = await getDictionaries()
    dicts.value = resp.data as any
    // 若选科为空且字典有值，则预填第一个选项
    if (!form.subjectType && dicts.value?.subjectTypes?.length) {
      form.subjectType = dicts.value.subjectTypes[0]
    }
  } catch (e: any) {
    ElMessage.error('加载字典失败')
  } finally {
    loadingDicts.value = false
  }
}

async function fillExample() {
  loadingExample.value = true
  try {
    const resp = await getExample()
    const ex = resp.data as any
    form.province = ex?.province ?? ''
    // subjectType：若与字典不一致，则回退为字典第一个
    const exSubject: string = ex?.subjectType ?? ''
    if (dicts.value?.subjectTypes?.length) {
      if (exSubject && (dicts.value.subjectTypes as string[]).includes(exSubject)) {
        form.subjectType = exSubject
      } else if (!form.subjectType) {
        form.subjectType = dicts.value.subjectTypes[0]
      }
    } else {
      form.subjectType = exSubject
    }
    // 次选科目
    const exSeconds: string[] | undefined = ex?.secondSubjects
    form.secondSubjects = Array.isArray(exSeconds) ? exSeconds.slice(0,2) : []
    form.rank = ex?.rank ?? undefined
    form.score = ex?.score ?? undefined
    form.year = ex?.year ?? undefined
    yearStr.value = ex?.year ? String(ex.year) : null
    form.batch = ex?.batch ?? undefined
    form.risk = (ex?.risk as any) ?? 'balanced'
    // 保持响应式对象结构，避免覆盖为 null
    form.preferences = {
      cities: ex?.preferences?.cities ?? [],
      majors: ex?.preferences?.majors ?? [],
      tiers: ex?.preferences?.tiers ?? [],
      tuitionMax: ex?.preferences?.tuitionMax ?? undefined,
    }
  } catch (e) {
    ElMessage.warning('获取示例失败')
  } finally {
    loadingExample.value = false
  }
}

async function onSubmit() {
  // 表单校验
  await new Promise<void>((resolve) => {
    formRef.value.validate((ok: boolean) => {
      if (ok) resolve()
      else resolve() // 依赖 rules 提示
    })
  })

  // 兜底前端拦截（避免异步场景漏校验）
  if (form.score == null && form.rank == null) {
    ElMessage.error('请至少填写分数或位次')
    return
  }
  if (form.rank != null && form.rank > MASK_TOP_RANK && (form.score == null || form.score === undefined)) {
    ElMessage.error(`rank>${MASK_TOP_RANK} 时需提供分数（屏蔽规则）`)
    return
  }

  submitting.value = true
  try {
    // 构造请求，剔除空字段
    const payload: RecommendRequest = JSON.parse(JSON.stringify(form))
    if (!payload.score) delete payload.score
    if (!payload.rank) delete payload.rank
    if (!payload.year) delete payload.year
    if (!payload.batch) delete payload.batch
    if (!payload.page) delete payload.page
    if (!payload.size) delete payload.size
    if (!payload.secondSubjects || !payload.secondSubjects.length) delete (payload as any).secondSubjects
    if (payload.preferences) {
      if (!payload.preferences.cities?.length) delete (payload.preferences as any).cities
      if (!payload.preferences.majors?.length) delete (payload.preferences as any).majors
      if (!payload.preferences.tiers?.length) delete (payload.preferences as any).tiers
      if (!payload.preferences.tuitionMax) delete (payload.preferences as any).tuitionMax
      if (Object.keys(payload.preferences).length === 0) delete (payload as any).preferences
    }

    const resp = await postRecommend(payload)
    lastStatus.value = resp.status || ''
    lastReason.value = resp.reason || resp.message || ''
    lastTraceId.value = resp.traceId || ''
    if (resp.status && resp.status !== 'ok') {
      // unfit 或 error
      results.rush = []
      results.stable = []
      results.safe = []
      respExplain.value = ''
      const trace = lastTraceId.value
      const reason = lastReason.value || '请求未通过'
      ElMessage.info(`${reason}${trace ? '，traceId: ' + trace : ''}`)
      // clear meta
      metaResolvedRank.value = null
      metaRankSource.value = ''
      metaRankBandMin.value = null
      metaRankBandMax.value = null
      metaScoreUsed.value = null
      return
    }
    const data: RecommendResponse = resp.data as any
    results.rush = data.buckets?.rush || []
    results.stable = data.buckets?.stable || []
    results.safe = data.buckets?.safe || []
    respExplain.value = data.explain || ''
    lastStatus.value = 'ok'
    lastReason.value = ''
    lastTraceId.value = ''
    // set meta
    metaResolvedRank.value = (data as any).resolvedRank ?? null
    metaRankSource.value = (data as any).rankSource ?? ''
    metaRankBandMin.value = (data as any).rankBandMin ?? null
    metaRankBandMax.value = (data as any).rankBandMax ?? null
    metaScoreUsed.value = (data as any).scoreUsed ?? null
    if (!hasAnyResults.value) {
      ElMessage.info('无匹配结果，请调整条件再试')
    } else {
      ElMessage.success('获取推荐成功')
    }
  } catch (e: any) {
    const trace = e?.response?.data?.traceId || e?.traceId
    const backendReason: string | undefined = e?.response?.data?.reason || e?.response?.data?.message
    let friendly = '推荐失败'
    if (backendReason && /分数无法映射为位次|无法映射/.test(backendReason)) {
      friendly = '分数与年份不匹配或映射缺失，请调整年份或分数后重试'
    }
    ElMessage.error(`${friendly}${trace ? '，traceId: ' + trace : ''}`)
  } finally {
    submitting.value = false
  }
}

function onYearChange(val: string | null) {
  if (!val) {
    form.year = undefined
  } else {
    const n = Number(val)
    form.year = Number.isFinite(n) ? n : undefined
  }
}

function onExportCsv() {
  let data: RecommendationItem[] = []
  if (activeTab.value === 'rush') data = results.rush
  else if (activeTab.value === 'stable') data = results.stable
  else data = results.safe
  if (!data || data.length === 0) {
    ElMessage.warning('当前分组无数据可导出')
    return
  }
  const headers = [
    '院校','专业','城市','层次','批次','选科','去年最低位次','去年最低分数','学费','趋势','匹配度'
  ]
  const rows = data.map(it => [
    it.universityName ?? '',
    it.majorName ?? '',
    it.city ?? '',
    it.tier ?? '',
    it.batch ?? '',
    it.subjectType ?? '',
    it.lastYearMinRank ?? '',
    (it as any).lastYearMinScore ?? '',
    it.tuition ?? '',
    it.admitTrend ?? '',
    it.fitScore != null ? it.fitScore + '%' : ''
  ])
  const csv = buildCsv([headers, ...rows])
  const blob = new Blob(["\ufeff" + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  const now = new Date()
  const pad = (n:number) => n.toString().padStart(2,'0')
  const ts = `${now.getFullYear()}${pad(now.getMonth()+1)}${pad(now.getDate())}_${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`
  a.href = url
  a.download = `gaokao_${activeTab.value}_${ts}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

function buildCsv(matrix: any[][]): string {
  return matrix
    .map(row => row.map(cell => formatCsvCell(cell)).join(','))
    .join('\r\n')
}

function formatCsvCell(val: any): string {
  if (val == null) return ''
  const s = String(val)
  // 如果包含逗号、换行或双引号，使用双引号包裹并转义内部双引号
  if (/[",\n\r]/.test(s)) {
    return '"' + s.replace(/"/g, '""') + '"'
  }
  return s
}

onMounted(() => {
  loadDictionaries()
})
</script>

<style scoped>
/* 使用与研究生择导页面一致的CSS变量 */
.gaokao-mock-fill {
  --brand-primary: #1E3A8A;
  --brand-accent: #2563EB;
  --brand-success: #10B981;
  --text-regular: #111827;
  --text-secondary: #6B7280;
  --bg-soft: #F8FAFC;
  --card-radius: 12px;
}

/* 页面整体布局 */
.gaokao-mock-fill {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  font-family: 'Noto Sans SC', system-ui, -apple-system, sans-serif;
}

/* 页面标题区域 - 重写背景特效 */
.page-header {
  text-align: center;
  padding: 32px 20px;
  background: linear-gradient(180deg, #0f245e 0%, #1E3A8A 100%);
  border-radius: 16px;
  color: white;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(30, 58, 138, 0.15);
}

/* 背景动画特效 - 重新实现 */
.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.05) 40%, transparent 70%);
  animation: headerFloat 6s ease-in-out infinite;
  z-index: 0;
}

/* 添加第二层光晕效果 */
.page-header::after {
  content: '';
  position: absolute;
  top: 20%;
  right: 20%;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 60%);
  border-radius: 50%;
  animation: pulse 4s ease-in-out infinite;
  z-index: 0;
}

@keyframes headerFloat {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg);
    opacity: 0.8;
  }
  50% { 
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% { 
    transform: scale(1);
    opacity: 0.3;
  }
  50% { 
    transform: scale(1.2);
    opacity: 0.6;
  }
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  position: relative;
  z-index: 1;
}

.page-header .desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.6;
  position: relative;
  z-index: 1;
}

/* 提交按钮区域 */
.submit-section {
  text-align: center;
  padding: 32px 0 16px 0;
  border-top: 1px solid #f0f2f5;
  margin-top: 24px;
}

/* 必填字段提示样式 */
.submit-hint {
  margin-bottom: 24px;
  text-align: left;
}

.submit-alert {
  border-radius: 8px;
  border: 1px solid #e1f5fe;
  background: #f8fbff;
}

.hint-content {
  font-size: 14px;
}

.hint-title {
  margin: 0 0 8px 0;
  font-weight: 600;
  color: #374151;
}

.hint-list {
  margin: 0;
  padding-left: 16px;
  list-style: none;
}

.hint-list li {
  margin: 4px 0;
  color: #6b7280;
  line-height: 1.5;
}

.submit-btn-large {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  min-width: 160px;
  height: 48px;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s ease;
}

.submit-btn-large:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

.submit-btn-large:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn-large:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

/* 卡片样式 */
.form-card, .results-card {
  background: white;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(226, 232, 240, 0.8);
  overflow: hidden;
  transition: all 0.3s ease;
}

.form-card:hover, .results-card:hover {
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.section-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--brand-primary), var(--brand-accent));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(30, 58, 138, 0.2);
}

.section-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: var(--text-regular);
}

.section-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 按钮样式 */
.example-btn, .submit-btn, .export-btn {
  display: flex;
  align-items: center;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.example-btn:hover {
  background: var(--el-color-primary-light-9);
  color: var(--brand-primary);
}

.submit-btn {
  background: linear-gradient(135deg, var(--brand-primary), var(--brand-accent));
  border: none;
  box-shadow: 0 4px 12px rgba(30, 58, 138, 0.3);
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(30, 58, 138, 0.4);
}

.export-btn {
  border-color: var(--brand-primary);
  color: var(--brand-primary);
}

/* 表单样式 */
.modern-form {
  padding: 32px;
}

.form-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.section-subtitle {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-regular);
  margin: 0;
  white-space: nowrap;
}

.section-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, var(--brand-primary), transparent);
  border-radius: 1px;
}

.form-item-enhanced {
  margin-bottom: 24px;
}

.select-enhanced, .number-input-enhanced, .date-picker-enhanced {
  width: 100%;
}

/* 选项内容样式 */
.option-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.option-label {
  font-weight: 500;
}

.option-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 警告样式 */
.alert-enhanced {
  margin-bottom: 20px;
  border-radius: 12px;
}

/* 元信息卡片 */
.meta-info-card {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid #e2e8f0;
}

.meta-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-icon {
  width: 20px;
  height: 20px;
  color: var(--brand-primary);
}

.meta-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-regular);
}

.meta-descriptions {
  background: white;
  border-radius: 8px;
  padding: 16px;
}

.meta-value {
  font-weight: 500;
  color: var(--brand-primary);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon svg {
  width: 40px;
  height: 40px;
  color: var(--text-secondary);
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-regular);
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 标签页样式 */
.results-tabs {
  margin-top: 20px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.tab-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-icon svg {
  width: 16px;
  height: 16px;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tab-title {
  font-weight: 600;
  font-size: 14px;
}

.tab-count {
  font-size: 12px;
  opacity: 0.7;
}

.rush-tab {
  color: #dc2626;
}

.stable-tab {
  color: #059669;
}

.safe-tab {
  color: #2563eb;
}

/* 表格容器 */
.table-container {
  margin-top: 20px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.results-table {
  border-radius: 12px;
  overflow: hidden;
}

/* 大学名称单元格 */
.university-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.university-name {
  font-weight: 500;
}

/* 匹配度样式 */
.fit-score {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.score-value {
  font-size: 12px;
  font-weight: 600;
  color: var(--brand-primary);
}

.score-bar {
  width: 100%;
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--brand-primary), var(--brand-accent));
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 标签样式 */
.explain-tag {
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border: 1px solid #0ea5e9;
  color: #0369a1;
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: rotate(0deg) translate(-20px) rotate(0deg); }
  50% { transform: rotate(180deg) translate(-20px) rotate(-180deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .gaokao-mock-fill {
    padding: 16px;
  }
  
  .page-header {
    padding: 24px 20px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .stats-overview {
    gap: 20px;
  }
  
  .modern-form {
    padding: 24px 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .section-line {
    width: 100%;
  }
  
  .actions {
    flex-direction: column;
    width: 100%;
  }
  
  .tab-label {
    padding: 6px 12px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .icon-badge {
    width: 48px;
    height: 48px;
  }
  
  .icon-badge svg {
    width: 20px;
    height: 20px;
  }
}

/* 无障碍支持 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation: none !important;
    transition: none !important;
  }
}
</style>
