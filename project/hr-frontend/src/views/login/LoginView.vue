<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="login-logo">
          <div class="logo-icon">
            <el-icon :size="48"><Setting /></el-icon>
          </div>
          <h1 class="logo-title">HR 管理系统</h1>
          <p class="logo-desc">高效管理人力资源，助力企业发展</p>
        </div>
        <div class="login-illustration">
          <el-icon :size="120" color="#3b82f6"><OfficeBuilding /></el-icon>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-card">
          <h2 class="login-title">欢迎登录</h2>
          <p class="login-subtitle">请输入您的账号和密码</p>
          
          <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-footer">
            <span>忘记密码？</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Setting, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    loading.value = true
    try {
      // ① 调用登录接口，获取 token
      await userStore.login(form)
      // ② 获取用户信息和权限，动态生成路由
      await userStore.generateRoutes()
      // ③ 登录成功后跳转到目标页面（或首页）
      const redirect = route.query.redirect as string || '/'
      await router.push(redirect)
      ElMessage.success(`欢迎回来，${userStore.userInfo?.realName || '用户'}`)
    } catch (error: any) {
      if (error?.code === 'ECONNABORTED' || error?.message?.includes('timeout')) {
        ElMessage.error('请求超时，请检查网络或后端服务')
      } else if (error?.response?.status === 401 || error?.message?.includes('1001') || error?.message?.includes('用户名或密码')) {
        ElMessage.error('用户名或密码错误')
      } else {
        ElMessage.error(error?.message || '登录失败，请稍后重试')
      }
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  display: flex;
  width: 900px;
  height: 500px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.login-left {
  width: 50%;
  background: linear-gradient(135deg, #3b82f6, #10b981);
  padding: 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;

  .login-logo {
    text-align: center;

    .logo-icon {
      width: 72px;
      height: 72px;
      border-radius: 16px;
      background-color: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 16px;
    }

    .logo-title {
      font-size: 28px;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .logo-desc {
      font-size: 14px;
      opacity: 0.9;
    }
  }

  .login-illustration {
    margin-top: 40px;
    opacity: 0.9;
  }
}

.login-right {
  width: 50%;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .login-card {
    width: 100%;
  }

  .login-title {
    font-size: 24px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 4px;
  }

  .login-subtitle {
    font-size: 14px;
    color: #64748b;
    margin-bottom: 32px;
  }

  .login-form {
    .login-btn {
      width: 100%;
      height: 44px;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .login-footer {
    margin-top: 16px;
    text-align: center;
    font-size: 14px;
    color: #94a3b8;

    span {
      cursor: pointer;

      &:hover {
        color: #3b82f6;
      }
    }
  }
}
</style>