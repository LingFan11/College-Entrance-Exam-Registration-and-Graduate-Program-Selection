<template>
  <div class="app-container">
    <header class="site-header" :class="{ shrink: isShrink }">
      <div class="container header-inner">
        <div class="brand">
          <div class="badge" aria-hidden="true">🎓</div>
          <div class="brand-title">高校智能择校与导师匹配系统</div>
        </div>
        <nav class="navbar pill-nav" aria-label="主导航">
          <el-space :size="8" wrap>
            <el-button class="nav-btn" text :class="{ active: isActive('/') }" @click="go('/')">首页</el-button>
            <el-button class="nav-btn" text :class="{ active: isActive('/gaokao') }" @click="go('/gaokao')">高考模拟填报</el-button>
            <el-button class="nav-btn" text :class="{ active: isActive('/grad') }" @click="go('/grad')">研究生择导</el-button>
            <el-button class="nav-btn" text :class="{ active: isActive('/school') }" @click="go('/school')">学校信息</el-button>
          </el-space>
        </nav>
      </div>
    </header>

    <main class="content">
      <transition name="fade-slide" mode="out-in">
        <RouterView />
      </transition>
    </main>
  </div>
</template>

<script setup lang="ts">
// App-level shell; pages are routed via vue-router
import { useRoute, useRouter } from 'vue-router'
import { onMounted, onBeforeUnmount, ref } from 'vue'
const route = useRoute()
const router = useRouter()
const isActive = (path: string) => route.path === path
const go = (path: string) => router.push(path)

// Header shrink on scroll
const isShrink = ref(false)
function handleScroll() {
  isShrink.value = window.scrollY > 10
}
onMounted(() => {
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })
})
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app-container { min-height: 100%; }
.content { margin-top: 16px; }
</style>

