<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="{ path: item.path }">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

interface BreadcrumbItem {
  path: string
  title: string
}

const breadcrumbs = computed<BreadcrumbItem[]>(() => {
  const matched = router.currentRoute.value.matched
  const result: BreadcrumbItem[] = []
  
  matched.forEach(item => {
    const title = item.meta?.title as string
    if (title) {
      result.push({
        path: item.path,
        title
      })
    }
  })
  
  return result.length ? result : [{ path: '/', title: '系统首页' }]
})
</script>

<style lang="scss" scoped>
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #3b82f6;
  font-weight: 500;
}
</style>