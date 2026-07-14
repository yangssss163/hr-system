<template>
  <el-select
    v-model="modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    clearable
    filterable
    style="width: 100%"
  >
    <el-option
      v-for="item in flatTree"
      :key="item.id"
      :label="item.label"
      :value="item.id"
    />
  </el-select>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface TreeNode {
  id: number
  name: string
  parentId: number
  children?: TreeNode[]
}

const props = defineProps<{
  modelValue?: number
  treeData?: TreeNode[]
  placeholder?: string
  disabled?: boolean
}>()

const flatTree = computed(() => {
  const result: { id: number; label: string }[] = []
  
  const traverse = (nodes: TreeNode[], prefix = '') => {
    nodes.forEach(node => {
      result.push({
        id: node.id,
        label: `${prefix}${node.name}`
      })
      if (node.children && node.children.length) {
        traverse(node.children, prefix + '　└─ ')
      }
    })
  }
  
  if (props.treeData) {
    traverse(props.treeData)
  }
  
  return result
})
</script>