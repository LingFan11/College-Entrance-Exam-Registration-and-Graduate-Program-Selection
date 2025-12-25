<template>
  <div class="grad-module">
    <!-- 页面标题 -->
    <header class="page-header">
      <h2>研究生择导（学生侧 V1）</h2>
      <p class="desc">按照步骤填写，几分钟内获得匹配的导师推荐。如果不清楚如何填写，直接点击「一键体验」。</p>
    </header>

    <!-- 顶部操作按钮 -->
    <div class="top-actions">
      <el-button
          type="primary"
          :loading="runningDemo"
          @click="runDemo"
      >
        {{ runningDemo ? '准备中…' : '一键体验（自动填入并获取推荐）' }}
      </el-button>
      <el-button
          v-if="isAdminMode"
          :loading="refreshingCache"
          @click="onRefreshCache"
      >
        {{ refreshingCache ? '刷新中…' : '刷新字典缓存' }}
      </el-button>
    </div>
    <el-alert
        v-if="cacheMsgText"
        :title="cacheMsgText"
        :type="cacheMsgTypeClass"
        class="alert-box"
        show-icon
    />

    <!-- 步骤一：填写基本信息 -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">1</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><User /></el-icon>
          步骤一：填写基本信息
        </h3>
      </div>
      <p class="step-desc">请填写你的基本信息；信息越完整，推荐越准确。</p>

      <div class="form-grid">
        <label>本科院校<input v-model="profile.currentUniversity" placeholder="如 东南大学" /></label>
        <label>本科专业<input v-model="profile.currentMajor" placeholder="如 软件工程" /></label>
        <label>GPA<input v-model.number="profile.gpa" type="number" step="0.01" /></label>
        <label>英语类型<input v-model="profile.englishType" placeholder="如 CET6/IELTS/TOEFL" /></label>
        <label>英语成绩<input v-model.number="profile.englishScore" type="number" /></label>
        <label class="full">竞赛/科研经历<input v-model="profile.competitions" placeholder="如 ACM、省创赛、科创项目等" /></label>
        <label class="full">研究经历说明<input v-model="profile.researchExp" placeholder="如 论文/项目/课题等简述" /></label>
        <label>地区偏好<input v-model="profile.regionPref" placeholder="如 华东" /></label>
        <label>导师风格偏好<input v-model="profile.stylePref" placeholder="如 友好/严格" /></label>
        <label>目标层级<input v-model="profile.targetTier" placeholder="如 985/211" /></label>
        <label>目标院校<input v-model="uiTargetUniversityName" placeholder="如 东南大学（可留空）" /></label>
      </div>

      <div class="action-buttons">
        <el-button type="primary" :loading="savingProfile" @click="onSaveProfile">保存/更新画像</el-button>
        <el-button type="default" :loading="loadingProfile" @click="onLoadProfile">加载画像</el-button>
      </div>

      <el-alert
          v-if="profileMsgText"
          :title="profileMsgText"
          :type="profileMsgTypeClass"
          show-icon
      />
    </section>

    <!-- 步骤 1.5：初试成绩录入 (Requirements 1.1, 1.2) -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">1.5</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><Document /></el-icon>
          初试成绩录入（可选）
        </h3>
      </div>
      <p class="step-desc">填写考研初试成绩，系统将根据成绩进行更精准的导师匹配筛选。</p>

      <div class="form-grid">
        <label>
          初试总分 (0-500)
          <el-input-number 
            v-model="profile.examTotalScore" 
            :min="0" 
            :max="500" 
            :precision="0"
            placeholder="如 380"
            style="width: 100%"
          />
          <span v-if="examScoreErrors.total" class="field-error">{{ examScoreErrors.total }}</span>
        </label>
        <label>
          政治 (0-100)
          <el-input-number 
            v-model="profile.politicsScore" 
            :min="0" 
            :max="100" 
            :precision="0"
            placeholder="如 70"
            style="width: 100%"
          />
          <span v-if="examScoreErrors.politics" class="field-error">{{ examScoreErrors.politics }}</span>
        </label>
        <label>
          英语 (0-100)
          <el-input-number 
            v-model="profile.englishExamScore" 
            :min="0" 
            :max="100" 
            :precision="0"
            placeholder="如 65"
            style="width: 100%"
          />
          <span v-if="examScoreErrors.english" class="field-error">{{ examScoreErrors.english }}</span>
        </label>
        <label>
          数学 (0-150)
          <el-input-number 
            v-model="profile.mathScore" 
            :min="0" 
            :max="150" 
            :precision="0"
            placeholder="如 120"
            style="width: 100%"
          />
          <span v-if="examScoreErrors.math" class="field-error">{{ examScoreErrors.math }}</span>
        </label>
        <label>
          专业课 (0-150)
          <el-input-number 
            v-model="profile.professionalScore" 
            :min="0" 
            :max="150" 
            :precision="0"
            placeholder="如 125"
            style="width: 100%"
          />
          <span v-if="examScoreErrors.professional" class="field-error">{{ examScoreErrors.professional }}</span>
        </label>
      </div>

      <!-- 机构分组选择器 (Requirements 4.1, 4.2, 4.5) -->
      <div class="institution-group-section" v-if="showInstitutionGroupSelector">
        <h4 class="section-subtitle">机构分组选择（中科院等特殊机构）</h4>
        <div class="form-grid">
          <label>
            目标机构
            <el-select v-model="selectedInstitutionCode" placeholder="选择机构" @change="onInstitutionChange">
              <el-option value="" label="不限" />
              <el-option value="CAS" label="中国科学院" />
            </el-select>
          </label>
          <label v-if="institutionGroups.length > 0">
            研究所分组
            <el-select v-model="profile.targetInstitutionGroupId" placeholder="选择分组">
              <el-option :value="undefined" label="不限" />
              <el-option 
                v-for="g in institutionGroups" 
                :key="g.id" 
                :value="g.id" 
                :label="`${g.groupName} (${g.minTotalScore || 0}-${g.maxTotalScore || 500}分)`"
              />
            </el-select>
          </label>
        </div>
        <div class="group-match-result" v-if="groupMatchResult">
          <el-alert
            v-if="groupMatchResult.matchedGroup"
            :title="`根据您的成绩，推荐分组：${groupMatchResult.matchedGroup.groupName}`"
            type="success"
            show-icon
          />
          <el-alert
            v-else
            title="您的成绩暂无匹配的分组，建议查看其他机构"
            type="warning"
            show-icon
          />
          <div v-if="groupMatchResult.alternativeGroups?.length" class="alternative-groups">
            <span class="mini-hint">其他可选分组：</span>
            <el-tag 
              v-for="g in groupMatchResult.alternativeGroups" 
              :key="g.id" 
              type="info" 
              size="small"
              class="group-tag"
              @click="profile.targetInstitutionGroupId = g.id"
            >
              {{ g.groupName }}
            </el-tag>
          </div>
        </div>
        <el-button 
          size="small" 
          type="primary" 
          :loading="loadingGroupMatch" 
          :disabled="!profile.examTotalScore || !selectedInstitutionCode"
          @click="onMatchGroup"
        >
          自动匹配分组
        </el-button>
      </div>

      <el-alert
          v-if="examScoreMsgText"
          :title="examScoreMsgText"
          :type="examScoreMsgTypeClass"
          show-icon
          class="mini-alert"
      />
    </section>

    <!-- 步骤二：方向字典 -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">2</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><Document /></el-icon>
          步骤二：方向字典（可选）
        </h3>
      </div>
      <p class="step-desc">需要了解方向层级时，可在此浏览方向列表。</p>

      <div class="action-buttons">
        <el-button :loading="loadingDirs" @click="loadDirections()">加载方向列表</el-button>
        <details v-if="isAdminMode">
          <summary class="mini-hint">高级：按父级ID筛选</summary>
          <div class="inline">
            <input v-model.number="parentId" type="number" placeholder="父级ID（可选）" />
            <el-button :loading="loadingDirs" @click="loadDirections(parentId)">按父级筛选</el-button>
          </div>
        </details>
      </div>

      <el-tag
          v-for="d in directions"
          :key="d.id"
          type="info"
          class="dir-tag"
      >
        {{ d.name }}
      </el-tag>
    </section>

    <!-- 步骤三：TopN 重点方向 -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">3</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><TrendCharts /></el-icon>
          步骤三：TopN 重点方向（计算必填）
        </h3>
      </div>

      <div class="dir-box">
        <div class="dir-actions">
          <el-button :loading="loadingDirs" size="small" @click="loadDirections()">加载方向列表</el-button>
          <span class="mini-hint">勾选你感兴趣的方向</span>
        </div>
        <div class="dir-list" v-if="directions.length">
          <label class="dir-item" v-for="d in directions" :key="d.id">
            <input type="checkbox" :value="d.id" v-model="uiPreferredDirectionIds" />
            <span>{{ d.name }}</span>
          </label>
        </div>
        <div class="mini-hint" v-else>尚未加载方向，请点击“加载方向列表”。</div>
      </div>

      <div class="topn" :class="{ error: topNError || hasDupTopN }" ref="dirSection">
        <div class="topn-row" v-for="(item, idx) in uiTopN" :key="idx">
          <el-select v-model="item.directionId" placeholder="选择方向" style="width: 200px">
            <el-option
                v-for="d in directions"
                :key="d.id"
                :label="d.name"
                :value="d.id"
            />
          </el-select>
          <el-input-number v-model="item.weight" :min="0" :max="1" :step="0.1" size="small" />
          <el-button size="small" @click="removeTopN(idx)" :disabled="uiTopN.length <= 1">删除</el-button>
        </div>
        <el-button size="small" @click="addTopN">添加重点方向</el-button>
        <el-button type="success" size="small" :disabled="hasDupTopN" @click="onConfirmTopN">确认 Top-N 方向</el-button>

        <el-alert
            v-if="topNError"
            title="请至少选择一个研究方向"
            type="warning"
            class="mini-alert"
        />
        <el-alert
            v-if="hasDupTopN"
            title="存在重复的研究方向，请修改后再确认"
            type="warning"
            class="mini-alert"
        />
        <el-alert
            v-if="topNLocalMsg"
            :title="topNLocalMsg"
            :type="topNLocalStatus"
            class="mini-alert"
        />
      </div>
    </section>

    <!-- 步骤四：检索导师 -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">4</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><School /></el-icon>
          步骤四：检索导师（可选）
        </h3>
      </div>
      <p class="step-desc">按学校、方向或关键词快速浏览导师，便于对比与筛选。</p>

      <div class="form-grid">
        <label>学校
          <el-select v-model.number="mentorQuery.universityId" placeholder="不限">
            <el-option :value="undefined" label="不限" />
            <el-option :value="1" label="东南大学" />
          </el-select>
        </label>
        <label>方向
          <el-select v-model.number="mentorQuery.directionId" placeholder="不限">
            <el-option :value="undefined" label="不限" />
            <el-option v-for="d in directions" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </label>
        <label>职称<input v-model="mentorQuery.title" /></label>
        <label>关键词<input v-model="mentorQuery.keyword" placeholder="如 AI" /></label>
        <label>每页数量<input v-model.number="mentorQuery.size" type="number" min="1" placeholder="默认10" /></label>
      </div>

      <div class="action-buttons">
        <el-button :loading="loadingMentors" type="primary" @click="onListMentors">快速查看</el-button>
        <el-button :loading="loadingMentors" type="success" @click="onSearchMentors">按条件搜索</el-button>
        <el-button v-if="isAdminMode" :loading="loadingMentors" type="warning" @click="quickSearchSEUAI">快速查看：东南大学 AI 导师</el-button>
      </div>

      <el-alert
          v-if="mentorMsgText"
          :title="mentorMsgText"
          :type="mentorMsgTypeClass"
          show-icon
      />

      <el-card v-for="m in mentors" :key="m.id" class="mentor-card">
        <h4>{{ m.name }}</h4>
        <p>{{ m.title }} {{ m.email }}</p>
      </el-card>
    </section>

    <!-- 步骤五：获取推荐 -->
    <section class="step-card">
      <div class="step-header">
        <div class="step-number">5</div>
        <h3 class="step-title">
          <el-icon class="step-icon"><CircleCheckFilled /></el-icon>
          {{ t('step5Title') }}
        </h3>
      </div>
      <p class="step-desc">{{ t('step5Tip') }}</p>

      <div class="form-grid">
        <label>{{ t('perPage') }}<el-input-number v-model.number="recommendSize" :min="1" /></label>
      </div>

      <div class="action-buttons">
        <el-button type="primary" size="large" :loading="loadingRec" :disabled="rateBlockedSeconds>0" @click="onGetRecommend">
          <el-icon><Search /></el-icon>
          <span v-if="loadingRec">{{ t('loading') }}</span>
          <span v-else-if="rateBlockedSeconds>0">{{ t('viewRec') }} ({{ rateBlockedSeconds }}s)</span>
          <span v-else>{{ t('viewRec') }}</span>
        </el-button>

        <el-button type="success" size="large" :loading="loadingRec" :disabled="rateBlockedSeconds>0" @click="onComputeRecommend">
          <el-icon><DataAnalysis /></el-icon>
          <span v-if="loadingRec">{{ t('computing') }}</span>
          <span v-else-if="rateBlockedSeconds>0">{{ t('recompute') }} ({{ rateBlockedSeconds }}s)</span>
          <span v-else>{{ t('recompute') }}</span>
        </el-button>
      </div>

      <el-alert
          v-if="recMsgText"
          :title="recMsgText"
          :type="recMsgTypeClass"
          show-icon
      />

      <el-table v-if="recommends.length" :data="recommends" style="width: 100%">
        <el-table-column prop="mentorName" label="导师" width="120" />
        <el-table-column prop="score" label="分数" width="80" :formatter="row => row.score.toFixed(1)" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.status === 'fit' ? 'success' : scope.row.status === 'borderline' ? 'warning' : 'danger'"
              size="small"
            >
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="原因/说明" min-width="200">
          <template #default="scope">
            <div class="reasons-list">
              <template v-for="(rs, i) in scope.row.reasons" :key="i">
                <el-tag 
                  v-if="isThresholdReason(rs)" 
                  type="danger" 
                  size="small" 
                  class="reason-tag"
                >
                  {{ rs }}
                </el-tag>
                <el-tag 
                  v-else-if="isGroupReason(rs)" 
                  type="warning" 
                  size="small" 
                  class="reason-tag"
                >
                  {{ rs }}
                </el-tag>
                <span v-else class="reason-text">{{ rs }}</span>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElButton, ElAlert, ElSelect, ElOption, ElInputNumber, ElInput, ElCard, ElTable, ElTableColumn, ElIcon } from 'element-plus'
import { Search, DataAnalysis, User, School, Document, Setting, InfoFilled, CircleCheckFilled, TrendCharts } from '@element-plus/icons-vue'
import type { DirectionDTO, MentorDTO, RecommendItemDTO, GradProfileSaveRequest, InstitutionGroupDTO, GroupMatchResponse } from '../api/grad'
import { listDirections, listMentors, searchMentors, getRecommend, computeRecommend, saveProfile, getProfile, refreshDictCache, listInstitutionGroups, matchInstitutionGroup } from '../api/grad'

// 假设使用一个简易的字典来替换中文，避免 JSON 解析时出问题
const dict = {
  step5Title: "步骤五：获取推荐",
  step5Tip: "确认 TopN 后，点击\"查看推荐/重新计算推荐\"获取结果。",
  perPage: "每页",
  loading: "加载中",
  viewRec: "查看推荐",
  computing: "计算中",
  recompute: "重新计算推荐",
  mentor: "导师",
  score: "分数",
  status: "状态",
  reason: "原因"
}

const t = (k: keyof typeof dict) => dict[k]

// 画像
const profile = ref<GradProfileSaveRequest>({
  // userId 由会话初始化自动填充
  userId: 0,
  englishType: 'CET6',
  englishScore: 550,
  // 考研初试成绩字段
  examTotalScore: undefined,
  politicsScore: undefined,
  englishExamScore: undefined,
  mathScore: undefined,
  professionalScore: undefined,
  targetInstitutionGroupId: undefined,
})

// 考研成绩验证 (Requirements 1.1, 1.2, 1.5)
const examScoreErrors = ref<{
  total?: string
  politics?: string
  english?: string
  math?: string
  professional?: string
}>({})

const examScoreMsg = ref('')
const examScoreStatus = ref<'ok'|'warning'|'error'|'info'|''>('')
const examScoreMsgText = computed(() => examScoreMsg.value)
const examScoreMsgTypeClass = computed(() => mapTypeClass(examScoreStatus.value))

function validateExamScores(): boolean {
  examScoreErrors.value = {}
  let valid = true
  
  const total = profile.value.examTotalScore
  if (total !== undefined && total !== null) {
    if (total < 0 || total > 500) {
      examScoreErrors.value.total = '总分必须在0-500之间'
      valid = false
    }
  }
  
  const politics = profile.value.politicsScore
  if (politics !== undefined && politics !== null) {
    if (politics < 0 || politics > 100) {
      examScoreErrors.value.politics = '政治分数必须在0-100之间'
      valid = false
    }
  }
  
  const english = profile.value.englishExamScore
  if (english !== undefined && english !== null) {
    if (english < 0 || english > 100) {
      examScoreErrors.value.english = '英语分数必须在0-100之间'
      valid = false
    }
  }
  
  const math = profile.value.mathScore
  if (math !== undefined && math !== null) {
    if (math < 0 || math > 150) {
      examScoreErrors.value.math = '数学分数必须在0-150之间'
      valid = false
    }
  }
  
  const professional = profile.value.professionalScore
  if (professional !== undefined && professional !== null) {
    if (professional < 0 || professional > 150) {
      examScoreErrors.value.professional = '专业课分数必须在0-150之间'
      valid = false
    }
  }
  
  return valid
}

// 机构分组相关 (Requirements 4.1, 4.2, 4.5)
const showInstitutionGroupSelector = ref(true)
const selectedInstitutionCode = ref('')
const institutionGroups = ref<InstitutionGroupDTO[]>([])
const groupMatchResult = ref<GroupMatchResponse | null>(null)
const loadingGroupMatch = ref(false)

async function onInstitutionChange(code: string) {
  institutionGroups.value = []
  groupMatchResult.value = null
  profile.value.targetInstitutionGroupId = undefined
  
  if (!code) return
  
  try {
    const resp = await listInstitutionGroups(code)
    institutionGroups.value = resp.data ?? []
  } catch (e) {
    console.error('Failed to load institution groups:', e)
  }
}

async function onMatchGroup() {
  if (!selectedInstitutionCode.value || !profile.value.examTotalScore) {
    examScoreStatus.value = 'warning'
    examScoreMsg.value = '请先填写初试总分并选择目标机构'
    return
  }
  
  loadingGroupMatch.value = true
  try {
    const resp = await matchInstitutionGroup(selectedInstitutionCode.value, profile.value.examTotalScore)
    groupMatchResult.value = resp.data
    
    // 自动选择匹配的分组
    if (resp.data?.matchedGroup) {
      profile.value.targetInstitutionGroupId = resp.data.matchedGroup.id
      examScoreStatus.value = 'success' as any
      examScoreMsg.value = `已自动匹配到分组：${resp.data.matchedGroup.groupName}`
    } else {
      examScoreStatus.value = 'warning'
      examScoreMsg.value = '您的成绩暂无匹配的分组'
    }
  } catch (e) {
    examScoreStatus.value = 'error'
    examScoreMsg.value = '分组匹配失败，请稍后重试'
  } finally {
    loadingGroupMatch.value = false
  }
}

const savingProfile = ref(false)
const loadingProfile = ref(false)
const profileRespMsg = ref('')
const profileRespStatus = ref<'ok'|'unfit'|'error'|'info'|''>('')

// 面向普通用户的简化字段
const uiTargetUniversityName = ref('东南大学')
const uiPreferredDirectionIds = ref<number[]>([])
const uiTopN = ref<Array<{ directionId?: number; weight: number }>>([{ directionId: undefined, weight: 1.0 }])

const runningDemo = ref(false)

function fillSample(field: 'targetUniversities'|'preferredDirections'|'targetDirectionsTopn') {
  if (field === 'targetUniversities') profile.value.targetUniversities = '[{"id":1,"name":"东南大学"}]'
  if (field === 'preferredDirections') profile.value.preferredDirections = '[102,103]'
  if (field === 'targetDirectionsTopn') profile.value.targetDirectionsTopn = '[{"directionId":102,"weight":1.0},{"directionId":103,"weight":0.7}]'
}

// Top-N 操作与提示
const topNLocalMsg = ref('')
const topNLocalStatus = ref<'ok'|'unfit'|'error'|'info'|''>('info')

function addTopN() {
  uiTopN.value.push({ directionId: undefined, weight: 1.0 })
  topNLocalStatus.value = 'info'
  topNLocalMsg.value = '已添加一行，请选择方向并设置权重'
}

function removeTopN(idx: number) {
  if (uiTopN.value.length <= 1) {
    topNLocalStatus.value = 'warning' as any
    topNLocalMsg.value = '至少保留一条 Top-N 项'
    return
  }
  uiTopN.value.splice(idx, 1)
  topNLocalStatus.value = 'success' as any
  topNLocalMsg.value = '已删除一行'
}

async function onConfirmTopN() {
  const cleaned = cleanedTopNList()
  if (hasDupTopN.value) {
    topNError.value = true
    topNLocalStatus.value = 'warning' as any
    topNLocalMsg.value = '存在重复的研究方向，请修改后再确认'
    await scrollToDir()
    return
  }
  if (cleaned.length === 0) {
    topNError.value = true
    topNLocalStatus.value = 'warning' as any
    topNLocalMsg.value = '请至少选择一个研究方向'
    await scrollToDir()
    return
  }
  profile.value.targetDirectionsTopn = JSON.stringify(cleaned)
  topNError.value = false
  topNLocalStatus.value = 'success' as any
  topNLocalMsg.value = '已确认 Top-N 方向，可前往下方获取推荐'
}

// Admin-only: refresh dict cache (only enabled when VITE_ADMIN_MODE === 'true')
const isAdminMode = import.meta.env.VITE_ADMIN_MODE === 'true'
const refreshingCache = ref(false)
const cacheRespMsg = ref('')
const cacheRespStatus = ref<'ok'|'unfit'|'error'|'info'|''>('')

const cacheMsgText = computed(() => cacheRespMsg.value)
const cacheMsgTypeClass = computed(() => mapTypeClass(cacheRespStatus.value))

async function onRefreshCache() {
  refreshingCache.value = true
  cacheRespMsg.value = ''
  try {
    const resp = await refreshDictCache()
    cacheRespStatus.value = (resp.status as any) || 'ok'
    cacheRespMsg.value = resp.status === 'ok' ? '缓存已刷新。如未看到变化，请重新计算推荐。' : (resp.message || '操作已提交')
  } catch (e) {
    cacheRespStatus.value = 'error'
    cacheRespMsg.value = '刷新失败，请稍后重试'
  } finally {
    refreshingCache.value = false
  }
}

async function onSaveProfile() {
  if (!profile.value.userId) {
    // 若会话尚未就绪，尝试初始化会话
    try {
      const { initSession } = await import('../api/grad')
      const s = await initSession()
      const u = (s as any)?.data?.userId
      if (u) {
        profile.value.userId = u
        recommendUserId.value = u
      }
    } catch {}
    if (!profile.value.userId) {
      profileRespStatus.value = 'error'
      profileRespMsg.value = '系统未获取到用户ID，请稍后重试或刷新页面'
      return
    }
  }

  // 验证考研成绩 (Requirements 1.1, 1.2, 1.5)
  if (!validateExamScores()) {
    profileRespStatus.value = 'error'
    profileRespMsg.value = '请检查考研成绩填写是否正确'
    return
  }

  // 将用户友好字段组装为后端需要的 JSON 字符串
  try {
    const tu = uiTargetUniversityName.value?.trim()
    if (tu) profile.value.targetUniversities = JSON.stringify([{ id: 1, name: tu }])
    if (uiPreferredDirectionIds.value?.length) profile.value.preferredDirections = JSON.stringify(uiPreferredDirectionIds.value)
    const cleanedTopN = uiTopN.value.filter(x => x.directionId && x.weight != null)
    if (cleanedTopN.length) profile.value.targetDirectionsTopn = JSON.stringify(cleanedTopN)
  } catch (e) {
    // 忽略组装错误，保持原值
  }

  savingProfile.value = true
  profileRespMsg.value = ''
  try {
    const resp = await saveProfile(profile.value)
    profileRespStatus.value = (resp.status as any) || 'ok'
    profileRespMsg.value = resp.status === 'ok' ? '已保存。下一步：点击下方"查看推荐"。' : (resp.message || '已提交')
    // 自动带入推荐的用户ID，避免用户重复填写
    recommendUserId.value = profile.value.userId
  } finally {
    savingProfile.value = false
  }
}

async function onLoadProfile() {
  if (!profile.value.userId) {
    profileRespStatus.value = 'error'
    profileRespMsg.value = '请先填写「用户ID」'
    return
  }
  loadingProfile.value = true
  profileRespMsg.value = ''
  try {
    const resp = await getProfile(profile.value.userId)
    if (resp.status === 'unfit' || resp.data == null) {
      profileRespStatus.value = 'unfit'
      profileRespMsg.value = resp.message ?? '未找到画像（可先保存）'
      return
    }
    Object.assign(profile.value, resp.data)
    // 将 JSON 字符串反显到用户友好字段
    try {
      uiTargetUniversityName.value = JSON.parse(profile.value.targetUniversities || '[]')[0]?.name || uiTargetUniversityName.value
      uiPreferredDirectionIds.value = JSON.parse(profile.value.preferredDirections || '[]') || []
      uiTopN.value = JSON.parse(profile.value.targetDirectionsTopn || '[]') || uiTopN.value
      if (!uiTopN.value.length) uiTopN.value = [{ directionId: undefined, weight: 1.0 }]
    } catch {}
    // 如果有机构分组ID，尝试加载对应的机构分组列表
    if (profile.value.targetInstitutionGroupId) {
      // 默认假设是中科院，可以根据实际情况扩展
      selectedInstitutionCode.value = 'CAS'
      await onInstitutionChange('CAS')
    }
    profileRespStatus.value = 'ok'
    profileRespMsg.value = '加载成功'
  } finally {
    loadingProfile.value = false
  }
}

// 方向
const directions = ref<DirectionDTO[]>([])
const parentId = ref<number | undefined>(undefined)
const loadingDirs = ref(false)

async function loadDirections(pid?: number) {
  loadingDirs.value = true
  try {
    const resp = await listDirections(pid)
    directions.value = resp.data ?? []
  } finally {
    loadingDirs.value = false
  }
}

onMounted(() => {
  // 默认加载一次方向，方便用户直接勾选
  loadDirections()
  // 初始化会话（获取 userId, sessionId），并自动带入
  ;(async () => {
    try {
      const { initSession } = await import('../api/grad')
      const s = await initSession()
      const u = (s as any)?.data?.userId
      if (u) {
        if (!profile.value.userId) profile.value.userId = u
        recommendUserId.value = u
      }
    } catch {}
  })()
})

// 导师检索
const mentors = ref<MentorDTO[]>([])
const mentorRespMsg = ref('')
const mentorRespStatus = ref<'ok'|'unfit'|'error'|'info'|''>('')
const loadingMentors = ref(false)
const mentorQuery = ref<{
  universityId?: number;
  directionId?: number;
  title?: string;
  keyword?: string;
  page?: number;
  size?: number
}>({
  page: 0,
  size: 10,
})

async function onListMentors() {
  loadingMentors.value = true
  mentorRespMsg.value = ''
  try {
    const resp = await listMentors(mentorQuery.value)
    mentors.value = resp.data ?? []
    mentorRespStatus.value = (resp.status as any) || 'ok'
    mentorRespMsg.value = resp.status === 'ok' ? `共返回 ${mentors.value.length} 条导师记录` : (resp.message || '')
  } finally {
    loadingMentors.value = false
  }
}

async function onSearchMentors() {
  loadingMentors.value = true
  mentorRespMsg.value = ''
  try {
    const resp = await searchMentors({ ...mentorQuery.value })
    mentors.value = resp.data ?? []
    mentorRespStatus.value = (resp.status as any) || 'ok'
    mentorRespMsg.value = resp.status === 'ok' ? `共返回 ${mentors.value.length} 条导师记录` : (resp.message || '')
  } finally {
    loadingMentors.value = false
  }
}

async function quickSearchSEUAI() {
  mentorQuery.value = { universityId: 1, keyword: 'AI', page: 0, size: 10 }
  await onListMentors()
}

// 推荐
const recommends = ref<RecommendItemDTO[]>([])
const recommendUserId = ref<number>(10001)
const recommendPage = ref<number>(0)
const recommendSize = ref<number>(10)
const recRespMsg = ref('')
const recRespStatus = ref<'ok'|'unfit'|'error'|'info'|''>('')
const loadingRec = ref(false)
const topNError = ref(false)
const dirSection = ref<HTMLElement | null>(null)
const rateBlockedSeconds = ref(0)
let rateTimer: any = null

const hasDupTopN = computed(() => {
  const ids = cleanedTopNList().map(x => x.directionId as number)
  const set = new Set<number>()
  for (const id of ids) {
    if (set.has(id)) return true
    set.add(id)
  }
  return false
})

function cleanedTopNList() {
  return uiTopN.value.filter(x => x.directionId && x.weight != null)
}

async function scrollToDir() {
  await nextTick()
  dirSection.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

async function ensureTopNOrWarn(): Promise<boolean> {
  const hasTopN = cleanedTopNList().length > 0
  if (!hasTopN) {
    topNError.value = true
    recRespStatus.value = 'warning'
    recRespMsg.value = '请至少选择一个研究方向'
    await scrollToDir()
    return false
  }
  topNError.value = false
  return true
}

async function onGetRecommend() {
  if (!recommendUserId.value) {
    recRespStatus.value = 'error'
    recRespMsg.value = '请先填写「用户ID」'
    return
  }
  // 前端必填校验：Top-N 必须至少一个
  const ok = await ensureTopNOrWarn()
  if (!ok) return

  if (hasDupTopN.value) {
    recRespStatus.value = 'warning'
    recRespMsg.value = '存在重复的研究方向，请修改后再计算'
    await scrollToDir()
    return
  }

  loadingRec.value = true
  recRespMsg.value = ''
  try {
    const resp = await getRecommend(recommendUserId.value, recommendPage.value, recommendSize.value)
    recommends.value = (resp.data as any) ?? []
    recRespStatus.value = (resp.status as any) || 'ok'
    
    // 429 / 限流处理
    if (((resp as any).code === 429) || (typeof resp.message === 'string' && resp.message.startsWith('rate_limited:'))) {
      const secs = parseInt((resp.message || '').split(':')[1] || '30') || 30
      rateBlockedSeconds.value = secs
      if (rateTimer) clearInterval(rateTimer)
      rateTimer = setInterval(() => {
        rateBlockedSeconds.value = Math.max(0, rateBlockedSeconds.value - 1)
        if (rateBlockedSeconds.value === 0) {
          clearInterval(rateTimer); rateTimer = null
        }
      }, 1000)
      recRespStatus.value = 'warning'
      recRespMsg.value = `请求频繁，请 ${secs} 秒后重试`
      return
    }

    if (resp.status === 'unfit' && ((resp as any).code === 'insufficient_profile' || (resp as any).message?.includes('insufficient_profile'))) {
      recRespStatus.value = 'warning'
      recRespMsg.value = '请补充Top-N方向后再计算'
      topNError.value = true
      await scrollToDir()
      return
    }

    if (recommends.value.length > 0) {
      recRespMsg.value = `已获取 ${recommends.value.length} 条推荐结果（按分数降序）`
    } else {
      recRespMsg.value = resp.message || '暂无推荐结果。可以补充画像信息或稍后重试。'
    }
  } catch (e: any) {
    recRespStatus.value = 'error'
    recRespMsg.value = '获取推荐失败，请稍后重试'
  } finally {
    loadingRec.value = false
  }
}

async function onComputeRecommend() {
  if (!recommendUserId.value) {
    recRespStatus.value = 'error'
    recRespMsg.value = '请先填写「用户ID」'
    return
  }
  // 前端必填校验：Top-N 必须至少一个
  const ok = await ensureTopNOrWarn()
  if (!ok) return

  // 重复校验：不允许有重复方向
  if (hasDupTopN.value) {
    recRespStatus.value = 'warning'
    recRespMsg.value = '存在重复的研究方向，请修改后再计算'
    await scrollToDir()
    return
  }

  loadingRec.value = true
  recRespMsg.value = ''
  try {
    const resp = await computeRecommend({
      userId: recommendUserId.value,
      page: recommendPage.value,
      size: recommendSize.value
    })
    recommends.value = (resp.data as any) ?? []
    recRespStatus.value = (resp.status as any) || 'ok'
    
    if (((resp as any).code === 429) || (typeof resp.message === 'string' && resp.message.startsWith('rate_limited:'))) {
      const secs = parseInt((resp.message || '').split(':')[1] || '30') || 30
      rateBlockedSeconds.value = secs
      if (rateTimer) clearInterval(rateTimer)
      rateTimer = setInterval(() => {
        rateBlockedSeconds.value = Math.max(0, rateBlockedSeconds.value - 1)
        if (rateBlockedSeconds.value === 0) {
          clearInterval(rateTimer); rateTimer = null
        }
      }, 1000)
      recRespStatus.value = 'warning'
      recRespMsg.value = `请求频繁，请 ${secs} 秒后重试`
      return
    }

    if (resp.status === 'unfit' && ((resp as any).code === 'insufficient_profile' || (resp as any).message?.includes('insufficient_profile'))) {
      recRespStatus.value = 'warning'
      recRespMsg.value = '请补充Top-N方向后再计算'
      topNError.value = true
      await scrollToDir()
      return
    }

    if (recommends.value.length > 0) {
      recRespMsg.value = `计算完成，返回 ${recommends.value.length} 条推荐结果`
    } else {
      recRespMsg.value = resp.message || '暂无推荐结果。可以补充画像信息后再试。'
    }
  } catch (e: any) {
    recRespStatus.value = 'error'
    recRespMsg.value = '推荐计算失败，请稍后重试'
  } finally {
    loadingRec.value = false
  }
}

// 友好提示样式映射
const profileMsgText = computed(() => profileRespMsg.value)
const profileMsgTypeClass = computed(() => mapTypeClass(profileRespStatus.value))
const mentorMsgText = computed(() => mentorRespMsg.value)
const mentorMsgTypeClass = computed(() => mapTypeClass(mentorRespStatus.value))
const recMsgText = computed(() => recRespMsg.value)
const recMsgTypeClass = computed(() => mapTypeClass(recRespStatus.value))

function mapTypeClass(t: string) {
  if (t === 'ok') return 'success'
  if (t === 'unfit') return 'warning'
  if (t === 'error') return 'error'
  return 'info'
}

// 推荐结果状态格式化 (Requirements 3.2)
function formatStatus(status: string): string {
  switch (status) {
    case 'fit': return '符合条件'
    case 'borderline': return '边缘情况'
    case 'unfit': return '不符合'
    default: return status
  }
}

// 检测是否为科目门槛不达标原因 (Requirements 3.2)
function isThresholdReason(reason: string): boolean {
  const thresholdKeywords = ['不达标', '低于', '门槛', '最低分', '政治', '英语', '数学', '专业课', '总分']
  return thresholdKeywords.some(kw => reason.includes(kw))
}

// 检测是否为分组相关原因 (Requirements 4.4)
function isGroupReason(reason: string): boolean {
  const groupKeywords = ['分组', '研究所', '机构', '不在']
  return groupKeywords.some(kw => reason.includes(kw))
}

// 一键体验：自动填入示例 → 保存画像 → 计算推荐
async function runDemo() {
  runningDemo.value = true
  try {
    profile.value.userId = 10001
    profile.value.currentUniversity = '某高校'
    profile.value.currentMajor = '软件工程'
    profile.value.gpa = 3.6
    profile.value.englishType = 'CET6'
    profile.value.englishScore = 570
    profile.value.regionPref = '华东'
    profile.value.stylePref = '友好'
    profile.value.targetTier = '985'
    // 考研初试成绩示例
    profile.value.examTotalScore = 380
    profile.value.politicsScore = 70
    profile.value.englishExamScore = 65
    profile.value.mathScore = 120
    profile.value.professionalScore = 125
    uiTargetUniversityName.value = '东南大学'
    uiPreferredDirectionIds.value = [102, 103]
    uiTopN.value = [
      { directionId: 102, weight: 1.0 },
      { directionId: 103, weight: 0.7 }
    ]
    await onSaveProfile()
    recommendUserId.value = profile.value.userId
    await onComputeRecommend()
  } finally {
    runningDemo.value = false
  }
}
</script>

<style scoped>
/* 使用与首页一致的CSS变量 */
.grad-module {
  --brand-primary: #1E3A8A;
  --brand-accent: #2563EB;
  --brand-success: #10B981;
  --text-regular: #111827;
  --text-secondary: #6B7280;
  --bg-soft: #F8FAFC;
  --card-radius: 12px;
}

/* 页面整体布局 */
.grad-module {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  font-family: 'Noto Sans SC', system-ui, -apple-system, sans-serif;
}

/* 页面标题区域 - 与首页保持一致 */
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

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.page-header .desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.6;
}

/* 顶部操作按钮 */
.top-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.alert-box {
  margin-bottom: 20px;
}

/* 步骤卡片统一样式 */
.step-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.step-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1E3A8A, #2563EB);
}

.step-card:hover {
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
  transform: translateY(-2px);
}

.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.step-number {
  width: 40px;
  height: 40px;
  background: linear-gradient(180deg, #0f245e 0%, #1E3A8A 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(30, 58, 138, 0.4);
}

.step-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-icon {
  color: var(--brand-primary);
  font-size: 18px;
}

.step-desc {
  color: #64748b;
  margin: 8px 0 0 0;
  font-size: 14px;
  line-height: 1.5;
}

/* 表单网格 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin: 20px 0;
}

.form-grid label {
  display: flex;
  flex-direction: column;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-grid label.full {
  grid-column: 1 / -1;
}

.form-grid input {
  margin-top: 6px;
  padding: 10px 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.form-grid input:focus {
  outline: none;
  border-color: var(--brand-primary);
  box-shadow: 0 0 0 3px rgba(30, 58, 138, 0.1);
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 20px;
  align-items: center;
}

/* 方向选择区域 */
.dir-box {
  background: #f8fafc;
  border: 2px dashed #cbd5e0;
  border-radius: 12px;
  padding: 20px;
  margin: 16px 0;
}

.dir-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.mini-hint {
  color: #64748b;
  font-size: 12px;
  font-style: italic;
}

.dir-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.dir-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dir-item:hover {
  background: #f1f5f9;
  border-color: var(--brand-primary);
}

.dir-item input[type="checkbox"] {
  margin: 0;
}

/* 方向标签 */
.dir-tag {
  margin: 4px 8px 4px 0;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
}

/* TopN 配置区域 */
.topn {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  margin: 16px 0;
  border: 2px solid #e2e8f0;
}

.topn.error {
  border-color: #f56565;
  background: #fef5e7;
}

.topn-row {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.topn-row:last-of-type {
  margin-bottom: 16px;
}

/* 导师卡片 */
.mentor-card {
  margin-top: 12px;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.mentor-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.mentor-card h4 {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 16px;
  font-weight: 600;
}

.mentor-card p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 高级选项 */
.advanced {
  margin-top: 20px;
  padding: 16px;
  background: #f7fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.advanced summary {
  cursor: pointer;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 12px;
}

.advanced textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-family: monospace;
  font-size: 12px;
  resize: vertical;
}

/* 内联元素 */
.inline {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 8px;
}

.inline input {
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
}

/* 警告和提示 */
.mini-alert {
  margin-top: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 24px 16px;
  }
  
  .page-header h2 {
    font-size: 24px;
  }
  
  .step-card {
    padding: 20px 16px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .topn-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .action-buttons {
    justify-content: center;
  }
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

/* 按钮悬停效果 */
.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

/* 考研成绩录入区域样式 */
.field-error {
  color: #f56565;
  font-size: 12px;
  font-weight: normal;
  margin-top: 4px;
}

/* 机构分组选择器样式 */
.institution-group-section {
  margin-top: 24px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 12px;
  border: 1px solid #bae6fd;
}

.section-subtitle {
  font-size: 16px;
  font-weight: 600;
  color: #0369a1;
  margin: 0 0 16px 0;
}

.group-match-result {
  margin: 16px 0;
}

.alternative-groups {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.group-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.group-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 推荐结果原因列表样式 */
.reasons-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.reason-tag {
  margin: 2px 0;
}

.reason-text {
  font-size: 13px;
  color: #64748b;
}
</style>

