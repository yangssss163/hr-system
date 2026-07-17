<template>
  <div class="error-403">
    <div class="error-content">
      <div class="error-code">403</div>
      <div class="error-title">访问被拒绝</div>
      <div class="error-desc">抱歉，您没有权限访问此页面，请联系管理员</div>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.error-403 {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;

  .error-content {
    text-align: center;
    padding: 48px;

    .error-code {
      font-size: 120px;
      font-weight: 700;
      color: #dcdfe6;
      line-height: 1;
      margin-bottom: 16px;
    }

    .error-title {
      font-size: 24px;
      color: #303133;
      margin-bottom: 12px;
    }

    .error-desc {
      font-size: 14px;
      color: #909399;
      margin-bottom: 32px;
    }
  }
}
</style>
