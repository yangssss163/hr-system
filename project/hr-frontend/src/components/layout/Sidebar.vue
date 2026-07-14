<template>
  <div class="sidebar" :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }">
    <div class="sidebar-logo">
      <div class="logo-icon">
        <el-icon :size="24"><Setting /></el-icon>
      </div>
      <span v-show="!appStore.sidebarCollapsed" class="logo-text">HR 管理系统</span>
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      background-color="#0f172a"
      text-color="#94a3b8"
      active-text-color="#3b82f6"
      router
      :collapse="appStore.sidebarCollapsed"
    >
      <template v-for="route in menuRoutes" :key="route.path">
        <el-sub-menu v-if="route.children && route.children.length" :index="route.path.startsWith('/') ? route.path : '/' + route.path">
          <template #title>
            <el-icon><component :is="getIcon(String(route.meta?.icon))" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </template>
          <el-menu-item
            v-for="child in route.children"
            :key="child.path"
            :index="child.path.startsWith('/') ? child.path : '/' + route.path + '/' + child.path"
          >
            {{ child.meta?.title }}
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="route.path.startsWith('/') ? route.path : '/' + route.path">
          <el-icon><component :is="getIcon(String(route.meta?.icon))" /></el-icon>
          <template #title>{{ route.meta?.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { constantRoutes } from '@/router/routes'
import { HomeFilled, Setting, FolderOpened, OfficeBuilding, Briefcase, User, Key, Menu, Grid, Clock, TrendCharts, Wallet, UserFilled, Operation } from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()

const iconMap: Record<string, any> = {
  HomeFilled,
  Setting,
  FolderOpened,
  OfficeBuilding,
  Briefcase,
  User,
  Key,
  Menu,
  Grid,
  Clock,
  TrendCharts,
  Wallet,
  UserFilled,
  Operation
}

const getIcon = (iconName: string) => {
  return iconName && iconMap[iconName] ? iconMap[iconName] : Setting
}

const activeMenu = computed(() => {
  return router.currentRoute.value.path
})

const menuRoutes = computed(() => {
  const rootRoute = constantRoutes.find(r => r.path === '/')
  return rootRoute?.children || []
})
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  height: 100%;
  background-color: #0f172a;
  color: #fff;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
  transition: width 0.3s;

  &-collapsed {
    width: 64px;
  }

  &-logo {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid #1e293b;

    .logo-icon {
      width: 36px;
      height: 36px;
      border-radius: 8px;
      background: linear-gradient(135deg, #3b82f6, #10b981);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }

    .logo-text {
      margin-left: 12px;
      font-size: 16px;
      font-weight: 600;
      color: #fff;
    }
  }

  &-menu {
    flex: 1;
    border-right: none;
    overflow-y: auto;
    overflow-x: hidden;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #0f172a;
    }

    &::-webkit-scrollbar-thumb {
      background: #334155;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb:hover {
      background: #475569;
    }
  }
}
</style>