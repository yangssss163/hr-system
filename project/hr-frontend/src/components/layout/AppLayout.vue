<template>
  <div class="app-layout">
    <Sidebar />
    <div class="main-content" :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }">
      <Navbar />
      <div class="content-wrapper">
        <router-view />
      </div>
      <footer class="app-footer">
        <span>© 2026 HR 管理系统 · All Rights Reserved</span>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.app-layout {
  min-height: 100vh;
  background-color: #f1f5f9;
  display: flex;
  flex-direction: column;
}

.main-content {
  margin-left: 220px;
  transition: margin-left 0.3s;
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  &.sidebar-collapsed {
    margin-left: 64px;
  }
}

.content-wrapper {
  padding: 24px;
  padding-top: 84px;
  flex: 1;
  overflow-y: auto;
}

.app-footer {
  margin-left: 0;
  height: 48px;
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #94a3b8;
}
</style>