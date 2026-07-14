<template>
  <el-form :model="form" inline @submit.prevent="handleSearch">
    <slot />
    <el-form-item>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const props = defineProps<{
  modelValue?: Record<string, any>
}>()

const emit = defineEmits<{
  search: [form: Record<string, any>]
  reset: []
}>()

const form = reactive(props.modelValue || {})

const handleSearch = () => {
  emit('search', { ...form })
}

const handleReset = () => {
  Object.keys(form).forEach(key => {
    form[key] = ''
  })
  emit('reset')
}

defineExpose({ form })
</script>