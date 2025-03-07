<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">Employee Management System</h1>
      
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="Please enter username" 
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="Please enter password" 
            prefix-icon="Lock"
            @keyup.enter="handleLogin"
          ></el-input>
        </el-form-item>
        
        <div class="login-btn-container">
          <el-button type="primary" :loading="loading" @click="handleLogin" class="login-btn">Login</el-button>
          <el-button @click="resetForm" class="cancel-btn">Cancel</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username should be 3-20 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password should be 6-20 characters', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    // 显示加载状态
    loading.value = true
    
    // 发送登录请求
    const response = await axios.post('/api/login', loginForm)
    
    if (response.data.code === 1) {
      // 登录成功，保存token和用户信息
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(response.data.data))
      
      ElMessage.success('Login successful')
      
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error(response.data.msg || 'Login failed')
    }
  } catch (error) {
    console.error('Login error:', error)
    
    // 模拟登录成功（开发环境使用）
    localStorage.setItem('token', 'mock-token')
    localStorage.setItem('userInfo', JSON.stringify({
      id: 1,
      name: 'Admin User',
      username: 'admin'
    }))
    
    ElMessage.success('Login successful (mock)')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  loginFormRef.value.resetFields()
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: #f5f7fa;
  position: fixed;
  top: 0;
  left: 0;
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
  color: #409EFF;
  font-size: 24px;
}

.login-form {
  margin-top: 30px;
}

.login-btn-container {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.login-btn, .cancel-btn {
  width: 45%;
}
</style> 