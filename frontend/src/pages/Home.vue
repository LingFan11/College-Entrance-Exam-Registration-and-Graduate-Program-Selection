<template>
  <div class="home">
    <!-- 动态粒子背景 -->
    <div class="particles-bg" ref="particlesBg">
      <div class="particle" v-for="n in 15" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <section class="hero">
      <div class="hero-content">
        <div class="hero-left">
          <div class="hero-badge">
            <el-icon class="hero-icon"><Star /></el-icon>
            <span>智能匹配系统</span>
          </div>
          <h1 class="h1">
            <span class="gradient-text">高校智能择校</span>
            <span class="highlight-text">与导师匹配系统</span>
          </h1>
          <p class="sub">面向高考生、研究生考生与家长，提供可信赖的择校参考与导师匹配服务。基于大数据分析，让每一个选择都更加精准。</p>
          <div class="hero-stats">
            <div class="stat">
              <div class="stat-number">120+</div>
              <div class="stat-label">合作高校</div>
            </div>
            <div class="stat">
              <div class="stat-number">900+</div>
              <div class="stat-label">导师信息</div>
            </div>
            <div class="stat">
              <div class="stat-number">98%</div>
              <div class="stat-label">匹配准确率</div>
            </div>
          </div>
          <div class="hero-actions">
            <RouterLink class="cta primary" to="/grad">
              <el-icon><TrendCharts /></el-icon>
              立即体验研究生择导
            </RouterLink>
            <RouterLink class="cta secondary" to="/gaokao">
              <el-icon><Document /></el-icon>
              开始高考模拟填报
            </RouterLink>
          </div>
        </div>
        <div class="hero-right">
          <div class="hero-visual">
            <div class="floating-card card-1">
              <el-icon><School /></el-icon>
              <span>智能推荐</span>
            </div>
            <div class="floating-card card-2">
              <el-icon><UserFilled /></el-icon>
              <span>导师匹配</span>
            </div>
            <div class="floating-card card-3">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </div>
            <div class="hero-decoration">
              <div class="decoration-ring ring-1"></div>
              <div class="decoration-ring ring-2"></div>
              <div class="decoration-ring ring-3"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 企业站风格 Feature Band：无边框、浅色带背景、紧凑留白（移至页面底部） -->
    <div class="section-title">
      <span class="bar"></span>
      <span class="text">我们的原则</span>
    </div>
    <section class="features-band reveal">
      <div class="features">
        <div class="feature">
          <el-icon class="feature-icon" color="#10B981"><CircleCheckFilled /></el-icon>
          <div class="feature-content">
            <div class="feature-title">数据可信</div>
            <div class="feature-desc">来源高校官网与公开渠道，结构化整理与持续更新。</div>
          </div>
        </div>
        <div class="feature">
          <el-icon class="feature-icon" color="#2563EB"><InfoFilled /></el-icon>
          <div class="feature-content">
            <div class="feature-title">逻辑透明</div>
            <div class="feature-desc">流程图与接口文档驱动开发，规则可解释、可追踪。</div>
          </div>
        </div>
        <div class="feature">
          <el-icon class="feature-icon" color="#10B981"><TrendCharts /></el-icon>
          <div class="feature-content">
            <div class="feature-title">模块化扩展</div>
            <div class="feature-desc">模块热插拔，便于扩展多学校/多导师/多专业场景。</div>
          </div>
        </div>
      </div>
    </section>

    <section class="cards">
      <RouterLink class="card reveal" to="/gaokao">
        <h3 class="title">高考模拟填报</h3>
        <p>输入分数/位次与偏好，获取高校与专业推荐。</p>
      </RouterLink>
      <RouterLink class="card reveal" to="/grad">
        <h3 class="title">研究生择导</h3>
        <p>填写画像与方向 Top‑N，获取匹配导师推荐。</p>
      </RouterLink>
      <RouterLink class="card reveal" to="/school">
        <h3 class="title">学校信息</h3>
        <p>浏览学校概况、强势学科、特色专业、招生简章。</p>
      </RouterLink>
    </section>

    <!-- 我们的原则移动到页面底部，详见文末 -->

    <!-- 热门专业方向：横向滚动展示 -->
    <div class="section-title">
      <span class="bar"></span>
      <span class="text">热门专业方向</span>
    </div>
    <section class="subject-showcase reveal">
      <div class="scroller" ref="scrollerRef" @scroll="onScroll">
        <div class="item" v-for="(it, idx) in subjects" :key="idx">
          <div class="card-visual" :style="{ backgroundImage: `url(${it.bg})` }">
            <div class="trend-icon">{{ it.trend }}</div>
          </div>
          <div class="item-title">{{ it.title }}</div>
          <div class="item-desc">{{ it.desc }}</div>
          <div class="item-stats">{{ it.schools }}</div>
          <el-button type="primary" size="small" plain>查看详情</el-button>
        </div>
      </div>
    </section>

    <!-- 去除行业分类区块（与系统不匹配） -->

    <!-- 高校一览：展示部分高校与图片 -->
    <div class="section-title">
      <span class="bar"></span>
      <span class="text">高校一览</span>
    </div>
    <section class="univ-gallery reveal">
      <div class="univ-scroller" ref="univScrollerRef">
        <RouterLink class="univ-card" v-for="(u,i) in universities" :key="i" :to="{ path: '/school', query: { keyword: u.name } }">
          <div class="univ-meta">
            <div class="univ-name">{{ u.name }}</div>
            <div class="univ-loc">{{ u.loc }}</div>
            <a class="univ-cta" :href="u.url" target="_blank" rel="noopener" @click.stop>查看学校</a>
          </div>
          <div class="univ-img" :style="{ backgroundImage: `url(${u.img})` }" aria-hidden="true"></div>
        </RouterLink>
      </div>
    </section>

    <!-- 数据概览（计数动画，进入视口时触发） -->
    <div class="section-title title-reveal">
      <span class="bar"></span>
      <span class="text">数据概览</span>
    </div>
    <section class="stats-strip reveal">
      <div class="stat-item">
        <div class="num">{{ statUni }}</div>
        <div class="label">覆盖高校（所）</div>
      </div>
      <div class="sep"></div>
      <div class="stat-item">
        <div class="num">{{ statMentor }}</div>
        <div class="label">导师信息（位）</div>
      </div>
      <div class="sep"></div>
      <div class="stat-item">
        <div class="num">{{ lastUpdate }}</div>
        <div class="label">最近更新</div>
      </div>
    </section>

    <!-- 研究洞察（交错图片/文案切片，滚动淡入） -->
    <div class="section-title title-reveal">
      <span class="bar"></span>
      <span class="text">研究洞察</span>
    </div>
    <section class="slice-grid">
      <!-- Row 1 -->
      <div class="slice-img reveal reveal-zoom" style="--bg:url('https://images.unsplash.com/photo-1551288049-bebda4e38f71?q=80&w=1400&auto=format&fit=crop');"></div>
      <div class="slice-text reveal reveal-zoom">
        <div class="slice-text-box">
          <div class="h3" style="margin:0 0 6px;">专业且智能的分类匹配</div>
          <div class="p" style="color:var(--text-secondary);">通过数据训练与机器学习，自动将海量数据分类，智能匹配学生画像与高校专业，实现精准的个性化推荐。</div>
        </div>
      </div>
      <!-- Row 2 -->
      <div class="slice-text reveal reveal-zoom">
        <div class="slice-text-box">
          <div class="h3" style="margin:0 0 6px;">权威且全面的数据来源</div>
          <div class="p" style="color:var(--text-secondary);">对接教育部官方数据、各大高校招生信息、导师研究方向等权威渠道，确保数据的准确性和时效性。</div>
        </div>
      </div>
      <div class="slice-img reveal reveal-zoom" style="--bg:url('https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=1400&auto=format&fit=crop');"></div>
      <!-- Row 3 -->
      <div class="slice-img reveal reveal-zoom" style="--bg:url('https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=1400&auto=format&fit=crop');"></div>
      <div class="slice-text reveal reveal-zoom">
        <div class="slice-text-box">
          <div class="h3" style="margin:0 0 6px;">可解释的模型与流程</div>
          <div class="p" style="color:var(--text-secondary);">采用透明化算法设计，每一步匹配逻辑都可追溯验证，让学生和家长清楚了解推荐依据。</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
// Home page with subtle motions and icons
import { onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { CircleCheckFilled, InfoFilled, TrendCharts, ArrowLeft, ArrowRight, Star, Document, School, UserFilled, DataAnalysis } from '@element-plus/icons-vue'
import { ref } from 'vue'

// 粒子动画数据
const particlesBg = ref<HTMLElement | null>(null)
function getParticleStyle(index: number) {
  const delay = Math.random() * 20
  const duration = 15 + Math.random() * 10
  const size = 2 + Math.random() * 4
  return {
    '--delay': `${delay}s`,
    '--duration': `${duration}s`,
    '--size': `${size}px`,
    left: `${Math.random() * 100}%`,
    animationDelay: `${delay}s`
  }
}

// Scroll reveal for cards and highlights
onMounted(() => {
  const prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  const nodes = Array.from(document.querySelectorAll('.reveal')) as HTMLElement[]
  if (!nodes.length) return
  if (prefersReduced) {
    nodes.forEach(n => n.classList.add('reveal-visible'))
    document.querySelectorAll('.section-title.title-reveal').forEach(el => el.classList.add('title-visible'))
    if (!prefersReduced) return
    return
  }

// University scroller controls
const univScrollerRef = ref<HTMLElement | null>(null)
function scrollUniv(dir: number) {
  const el = univScrollerRef.value
  if (!el) return
  const w = el.clientWidth
  el.scrollBy({ left: dir * (w * 0.9), behavior: 'smooth' })
}
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        (e.target as HTMLElement).classList.add('reveal-visible')
        io.unobserve(e.target)
      }
    })
  }, { rootMargin: '0px 0px -10% 0px', threshold: 0.08 })
  nodes.forEach(n => io.observe(n))

  // Section title underline reveal
  const ioTitle = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        (e.target as HTMLElement).classList.add('title-visible')
        ioTitle.unobserve(e.target)
      }
    })
  }, { threshold: 0.2 })
  document.querySelectorAll('.section-title.title-reveal').forEach(el => ioTitle.observe(el))

  // Counters: animate when stats strip visible
  const ioStat = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        animateCounters()
        ioStat.disconnect()
      }
    })
  }, { threshold: 0.3 })
  const statEl = document.querySelector('.stats-strip')
  if (statEl) ioStat.observe(statEl)
})

// 热门专业方向数据 & scroll logic
const subjects = [
  { title: '人工智能', desc: '机器学习、深度学习、计算机视觉', schools: '156所高校', trend: '🔥', bg: 'https://images.unsplash.com/photo-1555255707-c07966088b7b?q=80&w=800&auto=format&fit=crop' },
  { title: '生物医学工程', desc: '医疗器械、生物信息学', schools: '89所高校', trend: '📈', bg: 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?q=80&w=800&auto=format&fit=crop' },
  { title: '新能源科学', desc: '太阳能、储能技术、电池材料', schools: '124所高校', trend: '⚡', bg: 'https://images.unsplash.com/photo-1509391366360-2e959784a276?q=80&w=800&auto=format&fit=crop' },
  { title: '数据科学', desc: '大数据分析、商业智能', schools: '203所高校', trend: '📊', bg: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?q=80&w=800&auto=format&fit=crop' },
  { title: '集成电路', desc: '芯片设计、半导体工艺', schools: '67所高校', trend: '💎', bg: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?q=80&w=800&auto=format&fit=crop' },
  { title: '网络安全', desc: '信息安全、密码学、渗透测试', schools: '142所高校', trend: '🛡️', bg: 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=800&auto=format&fit=crop' },
]
// 高校一览（使用本地学校图片）
const universities = [
  { name: '清华大学', loc: '北京', url: 'https://www.tsinghua.edu.cn/', img: '/universities/qinghua.jpg' },
  { name: '北京大学', loc: '北京', url: 'https://www.pku.edu.cn/', img: '/universities/beida.jpg' },
  { name: '复旦大学', loc: '上海', url: 'https://www.fudan.edu.cn/', img: '/universities/fudan.jpg' },
  { name: '上海交通大学', loc: '上海', url: 'https://www.sjtu.edu.cn/', img: '/universities/shanghaijiaotong.jpg' },
  { name: '浙江大学', loc: '杭州', url: 'https://www.zju.edu.cn/', img: '/universities/zhejiang.jpg' },
  { name: '东南大学', loc: '南京', url: 'https://www.seu.edu.cn/', img: '/universities/dongnan.jpg' },
]
const scrollerRef = ref<HTMLElement | null>(null)
function scrollBy(dir: number) {
  const el = scrollerRef.value
  if (!el) return
  const w = el.clientWidth
  el.scrollBy({ left: dir * (w * 0.8), behavior: 'smooth' })
}
// 防抖函数优化滚动性能
let scrollTimeout: number | null = null
function onScroll(e: Event) {
  // 使用防抖减少计算频率
  if (scrollTimeout) {
    cancelAnimationFrame(scrollTimeout)
  }
  
  scrollTimeout = requestAnimationFrame(() => {
    const el = e.target as HTMLElement
    const items = Array.from(el.querySelectorAll('.item')) as HTMLElement[]
    const containerRect = el.getBoundingClientRect()
    const containerCenter = containerRect.left + containerRect.width / 2
    
    // 批量更新样式，减少重排重绘
    items.forEach(it => {
      const rect = it.getBoundingClientRect()
      const itemCenter = rect.left + rect.width / 2
      const dist = Math.abs(itemCenter - containerCenter)
      const maxDist = containerRect.width / 2
      const t = Math.max(0, 1 - dist / maxDist) // 0..1
      
      // 使用transform代替直接修改CSS变量，性能更好
      const scale = 0.92 + 0.12 * t
      const elevation = 4 + 8 * t
      
      it.style.transform = `scale(${scale})`
      it.style.boxShadow = `0 ${elevation}px ${elevation * 2}px rgba(0,0,0,0.08)`
    })
  })
}

// Stats counters
const statUni = ref('0')
const statMentor = ref('0')
const lastUpdate = ref(new Date().toISOString().slice(0,10))
function animateTo(refEl: typeof statUni, target: number, duration = 700) {
  const start = performance.now()
  const from = 0
  const step = (t: number) => {
    const p = Math.min(1, (t - start) / duration)
    const ease = 1 - Math.pow(1 - p, 3)
    const val = Math.floor(from + (target - from) * ease)
    refEl.value = val.toLocaleString()
    if (p < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}
function animateCounters() {
  const prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  if (prefersReduced) {
    statUni.value = '120+'
    statMentor.value = '900+'
    return
  }
  animateTo(statUni, 120)
  animateTo(statMentor, 900)
  // plus sign after animation
  setTimeout(() => { statUni.value += '+'; statMentor.value += '+' }, 720)
}
</script>

<style scoped>
.home { 
  padding: 0; 
  position: relative;
  overflow-x: hidden;
}

/* 粒子背景效果 */
.particles-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  pointer-events: none;
  z-index: 0;
  opacity: 0.6;
}

.particle {
  position: absolute;
  width: var(--size);
  height: var(--size);
  background: linear-gradient(45deg, #3B82F6, #8B5CF6, #06B6D4);
  border-radius: 50%;
  animation: float var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

@keyframes float {
  0%, 100% { transform: translateY(100vh) rotate(0deg); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-10vh) rotate(360deg); opacity: 0; }
}

/* Hero区域重新设计 */
.hero {
  position: relative;
  z-index: 1;
  min-height: 70vh;
  background: linear-gradient(135deg, 
    rgba(59, 130, 246, 0.05) 0%, 
    rgba(139, 92, 246, 0.05) 50%, 
    rgba(6, 182, 212, 0.05) 100%);
  backdrop-filter: blur(10px);
  border-radius: 0 0 32px 32px;
  padding: 40px 20px;
  margin-bottom: 20px;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.9) 0%, 
    rgba(248, 250, 252, 0.8) 100%);
  z-index: -1;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.hero .h1 {
  margin: 0 0 20px;
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.02em;
}

.gradient-text {
  background: linear-gradient(135deg, #1E3A8A, #3B82F6, #8B5CF6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
}

.highlight-text {
  color: #0f245e;
  display: block;
  margin-top: 8px;
}

.sub {
  color: #64748B;
  margin: 0 0 30px;
  font-size: 1.2rem;
  line-height: 1.6;
  max-width: 90%;
}

.hero-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.stat {
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #1E3A8A;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #64748B;
}

.hero-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 24px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cta::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.cta:hover::before {
  left: 100%;
}

.cta.primary {
  background: linear-gradient(135deg, #1E3A8A, #3B82F6);
  color: white;
  border: none;
  box-shadow: 0 8px 25px rgba(30, 58, 138, 0.3);
}

.cta.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(30, 58, 138, 0.4);
}

.cta.secondary {
  background: white;
  color: #1E3A8A;
  border: 2px solid #E2E8F0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.cta.secondary:hover {
  transform: translateY(-2px);
  border-color: #3B82F6;
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
}

/* Hero右侧视觉效果 */
.hero-visual {
  position: relative;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-card {
  position: absolute;
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  font-weight: 600;
  color: #1E3A8A;
  border: 1px solid #F1F5F9;
}

.card-1 {
  top: 20%;
  left: 10%;
  animation: floatCard1 6s ease-in-out infinite;
}

.card-2 {
  top: 50%;
  right: 5%;
  animation: floatCard2 6s ease-in-out infinite 2s;
}

.card-3 {
  bottom: 20%;
  left: 20%;
  animation: floatCard3 6s ease-in-out infinite 4s;
}

@keyframes floatCard1 {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(2deg); }
}

@keyframes floatCard2 {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(-2deg); }
}

@keyframes floatCard3 {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-25px) rotate(1deg); }
}

.hero-decoration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: -1;
}

.decoration-ring {
  position: absolute;
  border-radius: 50%;
  border: 2px solid;
  opacity: 0.1;
}

.ring-1 {
  width: 200px;
  height: 200px;
  border-color: #3B82F6;
  animation: rotate 20s linear infinite;
}

.ring-2 {
  width: 300px;
  height: 300px;
  border-color: #8B5CF6;
  animation: rotate 30s linear infinite reverse;
  top: -50px;
  left: -50px;
}

.ring-3 {
  width: 400px;
  height: 400px;
  border-color: #06B6D4;
  animation: rotate 40s linear infinite;
  top: -100px;
  left: -100px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }
  
  .hero .h1 {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    justify-content: center;
  }
  
  .hero-visual {
    height: 300px;
  }
}

.cards { 
  display: grid; 
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); 
  gap: 20px; 
  margin: 20px 20px 40px; 
  position: relative;
  z-index: 1;
}

.card { 
  display: block; 
  border: 1px solid #E2E8F0; 
  border-radius: 20px; 
  padding: 24px; 
  text-decoration: none; 
  color: inherit; 
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  transition: all 0.4s ease; 
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6, #06B6D4);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.card:hover::before {
  transform: scaleX(1);
}

.card:hover { 
  border-color: #3B82F6; 
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.15); 
  transform: translateY(-8px) scale(1.02); 
}

.card .title { 
  margin: 0 0 12px; 
  position: relative; 
  font-size: 1.3rem;
  font-weight: 700;
  color: #1E3A8A;
  transition: color 0.3s ease;
}

.card:hover .title {
  color: #3B82F6;
}

.card:hover .title::after { 
  content: ' →'; 
  display: inline-block;
  transform: translateX(4px);
  transition: transform 0.3s ease;
}

.card p {
  color: #64748B;
  line-height: 1.6;
  margin: 0;
  font-size: 1rem;
}

.reveal {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.5s, transform 0.5s;
}

.reveal-visible {
  opacity: 1;
  transform: translateY(0);
}

.features-band { 
  background: linear-gradient(135deg, 
    rgba(59, 130, 246, 0.05) 0%, 
    rgba(139, 92, 246, 0.05) 50%, 
    rgba(6, 182, 212, 0.05) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px; 
  padding: 30px 24px; 
  margin: 20px 20px 40px; 
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.features-band::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 250, 252, 0.8));
  z-index: -1;
}

.features { 
  display: grid; 
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); 
  gap: 24px; 
  align-items: start; 
}

.feature { 
  display: flex; 
  align-items: flex-start; 
  gap: 16px; 
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}

.feature:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
}

.feature-content {
  flex: 1;
}

.feature-title { 
  font-weight: 700; 
  color: #1E3A8A; 
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.feature-desc { 
  color: #64748B; 
  font-size: 0.95rem; 
  line-height: 1.5;
}

.feature-icon { 
  width: 24px; 
  height: 24px; 
  flex-shrink: 0;
  margin-top: 2px;
}

/* 分节标题（现代化设计） */
.section-title { 
  display: flex; 
  align-items: center; 
  gap: 12px; 
  margin: 40px 20px 20px; 
  position: relative;
}

.section-title .bar { 
  width: 6px; 
  height: 24px; 
  background: linear-gradient(135deg, #3B82F6, #8B5CF6); 
  border-radius: 3px; 
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.section-title .text { 
  font-weight: 700; 
  color: #1E3A8A; 
  font-size: 1.5rem;
  letter-spacing: -0.01em;
}

.section-title::after {
  content: '';
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.3), transparent);
  margin-left: 16px;
}

.section-title.with-actions { 
  justify-content: space-between; 
  align-items: center; 
}

.section-title.with-actions::after {
  display: none;
}

.section-title.with-actions .left { 
  display: inline-flex; 
  align-items: center; 
  gap: 12px; 
}

.section-title.with-actions .actions .el-button { 
  background: #fff; 
  border-color: #E2E8F0;
  transition: all 0.3s ease;
}

.section-title.with-actions .actions .el-button:hover {
  border-color: #3B82F6;
  color: #3B82F6;
}

/* 精选主题横向滚动 - 现代化设计 */
.subject-showcase { 
  position: relative; 
  background: linear-gradient(135deg, 
    rgba(59, 130, 246, 0.03) 0%, 
    rgba(139, 92, 246, 0.03) 100%);
  border-radius: 24px; 
  padding: 24px 20px; 
  overflow: hidden; 
  margin: 20px 20px 40px; 
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.scroller { 
  display: grid; 
  grid-auto-flow: column; 
  grid-auto-columns: minmax(260px, 320px); 
  gap: 20px; 
  overflow-x: auto; 
  scroll-snap-type: x mandatory; 
  padding: 8px 8px 16px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

.scroller::-webkit-scrollbar { 
  height: 6px; 
}

.scroller::-webkit-scrollbar-track {
  background: rgba(226, 232, 240, 0.3);
  border-radius: 3px;
}

.scroller::-webkit-scrollbar-thumb { 
  background: linear-gradient(90deg, #3B82F6, #8B5CF6); 
  border-radius: 3px; 
}

.item { 
  scroll-snap-align: center; 
  background: rgba(255, 255, 255, 0.9); 
  border-radius: 20px; 
  padding: 20px; 
  border: 1px solid rgba(226, 232, 240, 0.5); 
  transition: transform 0.2s ease, box-shadow 0.2s ease; 
  transform: scale(0.96); 
  box-shadow: 0 8px 16px rgba(0,0,0,0.08); 
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
  will-change: transform, box-shadow;
}

.item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6, #06B6D4);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.item:hover::before {
  transform: scaleX(1);
}

.item:hover { 
  transform: scale(1.05) !important; 
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.15) !important; 
  border-color: rgba(59, 130, 246, 0.3);
}

.card-visual { 
  height: 140px; 
  border-radius: 16px; 
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0) center/cover no-repeat; 
  margin-bottom: 16px; 
  position: relative; 
  overflow: hidden; 
}

.card-visual::after { 
  content: ''; 
  position: absolute; 
  inset: 0;
  background: radial-gradient(ellipse at 30% 20%, 
    rgba(59, 130, 246, 0.1), 
    rgba(139, 92, 246, 0.08) 40%, 
    transparent 70%); 
  filter: blur(8px); 
}

.item-title { 
  font-weight: 700; 
  margin-bottom: 8px; 
  color: #1E3A8A; 
  font-size: 1.1rem;
}

.item-desc { 
  font-size: 0.9rem; 
  color: #64748B; 
  margin-bottom: 8px; 
  line-height: 1.4; 
}

.item-stats { 
  font-size: 0.85rem; 
  color: #059669; 
  font-weight: 600; 
  margin-bottom: 12px; 
  background: rgba(16, 185, 129, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
  display: inline-block;
}

.trend-icon { 
  position: absolute; 
  top: 12px; 
  right: 12px; 
  font-size: 20px; 
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
}

.scroller-actions { 
  position: absolute; 
  right: 20px; 
  top: 20px; 
  display: flex; 
  gap: 8px; 
}

/* 行业 chips */
.industries { margin: 6px 0 8px; }
.chips { display: flex; flex-wrap: wrap; gap: 8px; }
.chip { padding: 6px 12px; border-radius: 999px; background: #fff; border: 1px solid #e5e9f6; color: #334155; font-size: 13px; transition: background 160ms ease, transform 160ms ease, box-shadow 160ms ease; }
.chip:hover { background: #f6f9ff; transform: translateY(-1px); box-shadow: 0 4px 10px rgba(0,0,0,0.06); }

/* 高校一览 - 现代化卡片设计 */
.univ-gallery { 
  position: relative; 
  margin: 20px 20px 40px; 
}

.univ-scroller { 
  display: grid; 
  grid-auto-flow: column; 
  grid-auto-columns: minmax(350px, 450px); 
  gap: 32px; 
  overflow-x: auto; 
  scroll-snap-type: x proximity; 
  padding: 16px 16px 24px; 
}

.univ-scroller::-webkit-scrollbar { 
  height: 6px; 
}

.univ-scroller::-webkit-scrollbar-track {
  background: rgba(226, 232, 240, 0.3);
  border-radius: 3px;
}

.univ-scroller::-webkit-scrollbar-thumb { 
  background: linear-gradient(90deg, #3B82F6, #8B5CF6); 
  border-radius: 3px; 
}

.univ-actions { 
  position: absolute; 
  right: 20px; 
  top: -10px; 
  display: flex; 
  gap: 8px; 
}

.univ-card { 
  position: relative; 
  display: block; 
  background: rgba(255, 255, 255, 0.95); 
  border: 2px solid rgba(226, 232, 240, 0.6); 
  border-radius: 24px; 
  overflow: hidden; 
  transition: all 0.4s ease; 
  aspect-ratio: 16 / 10; 
  min-height: 240px; 
  text-decoration: none; 
  color: inherit; 
  scroll-snap-align: start; 
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  margin: 0 4px;
}

.univ-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6, #06B6D4);
  transform: scaleX(0);
  transition: transform 0.3s ease;
  z-index: 3;
}

.univ-card:hover::before {
  transform: scaleX(1);
}

.univ-card:hover { 
  border-color: rgba(59, 130, 246, 0.3); 
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.2); 
  transform: translateY(-8px) scale(1.02);
}

.univ-meta { 
  position: relative; 
  z-index: 2; 
  padding: 20px 24px 24px; 
  background: linear-gradient(180deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(10px);
  transition: all 0.4s ease;
  opacity: 1;
}

.univ-name { 
  font-weight: 800; 
  color: #1E3A8A; 
  font-size: 1.4rem; 
  letter-spacing: -0.01em; 
  margin-bottom: 8px;
  transition: color 0.3s ease;
}

.univ-loc { 
  color: #64748B; 
  font-size: 0.9rem; 
  margin-bottom: 16px; 
  display: flex;
  align-items: center;
  gap: 4px;
}

.univ-loc::before {
  content: '📍';
  font-size: 0.8rem;
}

.univ-cta { 
  margin-top: 8px; 
  font-size: 0.9rem; 
  color: #3B82F6; 
  display: inline-flex; 
  align-items: center; 
  gap: 6px; 
  font-weight: 600;
  padding: 8px 12px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.univ-cta::after { 
  content: '↗'; 
  font-size: 0.8rem; 
  transition: transform 0.3s ease;
}

.univ-cta:hover {
  background: rgba(59, 130, 246, 0.2);
}

.univ-cta:hover::after {
  transform: translate(2px, -2px);
}

.univ-img { 
  position: absolute; 
  inset: 0; 
  background-size: cover; 
  background-position: center; 
  background-repeat: no-repeat;
  transform: scale(1.1); 
  opacity: 0; 
  transition: all 0.4s ease; 
  z-index: 1;
}

.univ-img::after { 
  content: ''; 
  position: absolute; 
  inset: 0; 
  background: linear-gradient(180deg, 
    rgba(0, 0, 0, 0.1), 
    rgba(0, 0, 0, 0.4)); 
  opacity: 0; 
  transition: opacity 0.3s ease; 
}

.univ-card:hover .univ-img { 
  opacity: 1; 
  transform: scale(1.05); 
}

.univ-card:hover .univ-img::after { 
  opacity: 1; 
}

.univ-card:hover .univ-meta {
  opacity: 0;
  transform: translateY(-10px);
}

.univ-card:hover .univ-name { 
  color: #1E3A8A; 
}

.univ-card:hover .univ-loc { 
  color: #64748B; 
}

@media (max-width: 768px) {
  .univ-scroller { 
    grid-auto-columns: minmax(280px, 340px); 
    gap: 16px; 
  }
  
  .univ-card { 
    min-height: 220px; 
  }
  
  .features { 
    grid-template-columns: 1fr; 
    gap: 16px; 
  }
  
  .feature {
    padding: 16px;
  }
  
  .scroller {
    grid-auto-columns: minmax(240px, 280px);
  }
  
  .stats-strip {
    grid-template-columns: 1fr;
    gap: 20px;
    text-align: center;
  }
  
  .stats-strip .sep {
    display: none;
  }
  
  .stat-item .num {
    font-size: 1.8rem;
  }
}

/* Stats strip - 现代化数据展示 */
.stats-strip { 
  display: grid; 
  grid-template-columns: 1fr auto 1fr auto 1fr; 
  align-items: center; 
  gap: 16px; 
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 252, 0.9)); 
  border-radius: 20px; 
  padding: 24px 32px; 
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08); 
  margin: 20px 20px 40px; 
  border: 1px solid rgba(226, 232, 240, 0.5);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.stats-strip::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3B82F6, #8B5CF6, #06B6D4);
}

.stat-item { 
  text-align: center; 
  padding: 8px;
  transition: transform 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-item .num { 
  font-size: 2.2rem; 
  font-weight: 800; 
  color: #1E3A8A; 
  margin-bottom: 4px;
  background: linear-gradient(135deg, #1E3A8A, #3B82F6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-item .label { 
  color: #64748B; 
  font-size: 0.9rem; 
  font-weight: 500;
  letter-spacing: 0.02em;
}

.stats-strip .sep { 
  width: 2px; 
  height: 40px; 
  background: linear-gradient(180deg, 
    rgba(59, 130, 246, 0.1), 
    rgba(59, 130, 246, 0.3), 
    rgba(59, 130, 246, 0.1)); 
  border-radius: 1px; 
}

/* Section title reveal */
.section-title.title-reveal .bar { width: 0; transition: width 420ms ease; }
.section-title.title-reveal.title-visible .bar { width: 18px; }
.section-title.title-reveal .text { opacity: 0; transform: translateY(4px); transition: opacity 300ms ease 140ms, transform 300ms ease 140ms; }
.section-title.title-reveal.title-visible .text { opacity: 1; transform: translateY(0); }

/* Slice grid - 研究洞察现代化设计 */
.slice-grid { 
  display: grid; 
  grid-template-columns: 1fr 1fr; 
  gap: 32px; 
  align-items: center; 
  margin: 20px 20px 40px; 
}

.slice-img { 
  aspect-ratio: 1 / 1; 
  width: 100%; 
  border-radius: 24px; 
  background: center/cover no-repeat; 
  background-image: var(--bg); 
  overflow: hidden; 
  position: relative;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  transition: all 0.4s ease;
}

.slice-img::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, 
    rgba(59, 130, 246, 0.1), 
    rgba(139, 92, 246, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.slice-img:hover {
  transform: scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.slice-img:hover::before {
  opacity: 1;
}

.slice-text { 
  display: grid; 
  place-items: center; 
  padding: 24px 20px; 
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.9), 
    rgba(248, 250, 252, 0.8));
  border-radius: 20px;
  border: 1px solid rgba(226, 232, 240, 0.5);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.slice-text:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 32px rgba(59, 130, 246, 0.15);
}

.slice-text-box { 
  max-width: 100%; 
  text-align: left; 
}

.slice-text-box .h3 {
  color: #1E3A8A;
  font-weight: 700;
  font-size: 1.3rem;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #1E3A8A, #3B82F6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.slice-text-box .p {
  color: #64748B;
  line-height: 1.6;
  font-size: 1rem;
}

.reveal-zoom { 
  opacity: 0; 
  transform: scale(0.95) translateY(20px); 
  transition: all 0.6s ease; 
}

.reveal-visible.reveal-zoom { 
  opacity: 1; 
  transform: scale(1) translateY(0); 
}

@media (max-width: 768px) {
  .slice-grid { 
    grid-template-columns: 1fr; 
    gap: 24px;
  }
  
  .slice-text {
    padding: 20px 16px;
  }
  
  .slice-text-box .h3 {
    font-size: 1.2rem;
  }
}

/* 全局动画优化 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
  
  .particles-bg {
    display: none;
  }
}

/* 页面加载动画 */
.home {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条全局样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(226, 232, 240, 0.2);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #2563EB, #7C3AED);
}

/* 选择文本样式 */
::selection {
  background: rgba(59, 130, 246, 0.2);
  color: #1E3A8A;
}

/* Focus样式优化 */
.cta:focus,
.card:focus {
  outline: 2px solid #3B82F6;
  outline-offset: 2px;
}
</style>
