<template>
  <div class="navbar">
    <div class="navbar-left">
      <el-button
        link
        class="toggle-btn"
        @click="appStore.toggleSidebar"
      >
        <el-icon><Menu /></el-icon>
      </el-button>
      <Breadcrumb />
    </div>
    
    <div class="navbar-right">
      <HeaderNotice />
      <HeaderMessage />
      <HeaderTask />
      
      <el-dropdown trigger="click">
        <span class="user-info">
          <el-avatar :size="32" class="user-avatar">
            <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" />
            <el-icon v-else><User /></el-icon>
          </el-avatar>
          <span class="user-name">{{ userStore.userInfo?.realName || '用户' }}</span>
          <el-icon :size="14"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-item @click="handleChangePassword">修改密码</el-dropdown-item>
          <el-dropdown-item divided @click="triggerAvatarUpload">上传头像</el-dropdown-item>
          <el-dropdown-item divided @click="userStore.logout">退出登录</el-dropdown-item>
        </template>
      </el-dropdown>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangePassword" :loading="submittingPassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 隐藏的头像上传 input -->
    <input ref="avatarInputRef" type="file" accept="image/*" style="display:none" @change="handleAvatarUpload" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { Menu, User, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { changePassword } from '@/api/auth'
import { uploadAvatar } from '@/api/system/user'
import HeaderNotice from '@/components/office/HeaderNotice.vue'
import HeaderMessage from '@/components/office/HeaderMessage.vue'
import HeaderTask from '@/components/office/HeaderTask.vue'
import Breadcrumb from './Breadcrumb.vue'

const userStore = useUserStore()
const appStore = useAppStore()

// ===== 修改密码 =====
const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleChangePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const submittingPassword = ref(false)

const submitChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    submittingPassword.value = true
    try {
      await changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
      ElMessage.success('密码修改成功，请重新登录')
      passwordDialogVisible.value = false
      userStore.logout()
    } catch {
      ElMessage.error('密码修改失败，请检查原密码是否正确')
    } finally {
      submittingPassword.value = false
    }
  })
}

// ===== 头像上传 =====
const avatarInputRef = ref<HTMLInputElement>()

const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

const handleAvatarUpload = async () => {
  const file = avatarInputRef.value?.files?.[0]
  if (!file) return
  const userId = userStore.userInfo?.id
  if (!userId) return
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await uploadAvatar(userId, fd)
    const body = res.data as { code: number; data: string }
    if (body.code === 200 && body.data) {
      if (userStore.userInfo) userStore.userInfo.avatar = body.data
      ElMessage.success('头像已更新')
    }
  } catch {
    ElMessage.error('头像上传失败')
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: fixed;
  left: 220px;
  right: 0;
  top: 0;
  z-index: 90;
  transition: left 0.3s;

  .sidebar-collapsed & {
    left: 64px;
  }

  &-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  &-right {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .toggle-btn {
    font-size: 20px;
    color: #64748b;
    padding: 0;
    margin-right: 8px;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: #475569;

    .user-avatar {
      border: 2px solid #e2e8f0;
    }

    .user-name {
      font-size: 14px;
      font-weight: 500;
    }
  }
}
</style>
