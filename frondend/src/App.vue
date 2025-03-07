<script setup>
import { ref, onMounted, provide, computed, watch } from 'vue'
import { House, School, Setting, ArrowDown, User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeIndex = ref('/')
provide('activeIndex', activeIndex)

// 计算属性：判断是否在登录页面
const isLoginPage = computed(() => {
  return router.currentRoute.value.path === '/login'
})

// 获取用户信息
const userInfo = ref(null)

// 监听路由变化，确保在路由变化时检查用户信息
watch(() => router.currentRoute.value, () => {
  if (!isLoginPage.value && !userInfo.value) {
    loadUserInfo()
  }
})

// 加载用户信息
const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
  } else if (!isLoginPage.value) {
    // 如果没有用户信息且不在登录页，跳转到登录页
    router.push('/login')
  }
}

const handleSelect = (index) => {
  activeIndex.value = index
  router.push(index)
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    // 处理个人信息
    console.log('查看个人信息')
  } else if (command === 'changePassword') {
    // 处理修改密码
    console.log('修改密码')
  }
}

// 处理登出
const handleLogout = () => {
  // 清除本地存储的token和用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  // 清空用户信息
  userInfo.value = null
  // 跳转到登录页面
  router.push('/login')
}

onMounted(() => {
  document.body.classList.add('app-body')
  
  // 根据当前路径设置初始激活菜单
  const path = window.location.pathname
  if (path) {
    activeIndex.value = path
  }
  
  // 加载用户信息
  loadUserInfo()
})
</script>

<template>
  <!-- 登录页面不显示布局 -->
  <router-view v-if="isLoginPage"></router-view>
  
  <!-- 非登录页面显示完整布局 -->
  <div class="app-wrapper" v-else>
    <el-container class="layout-container">
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <div class="header-title">Employee Management System</div>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">{{ userInfo?.name?.charAt(0) || 'U' }}</el-avatar>
              <span class="user-name">{{ userInfo?.name || 'User' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">Profile</el-dropdown-item>
                <el-dropdown-item command="changePassword">Change Password</el-dropdown-item>
                <el-dropdown-item divided command="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-container class="main-container">
        <!-- 侧边栏 -->
        <el-aside width="220px" class="sidebar">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-vertical"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            :router="false"
            @select="handleSelect">
            
            <el-menu-item index="/">
              <el-icon><House /></el-icon>
              <span>Home</span>
            </el-menu-item>

            <el-sub-menu index="2">
              <template #title>
                <el-icon><User /></el-icon>
                <span>Employee Management</span>
              </template>
              <el-menu-item index="/employee-management">Employee List</el-menu-item>
              <el-menu-item index="/employee-onboarding">Employee Onboarding</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="3">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>System Settings</span>
              </template>
              <el-menu-item index="/department-management">Department Management</el-menu-item>
              <el-menu-item index="/position-management">Position Management</el-menu-item>
            </el-sub-menu>
          </el-menu>
          <div class="sidebar-footer">
            <span>© 2024 Employee Management System</span>
          </div>
        </el-aside>

        <!-- 主要内容区域 -->
        <el-main class="main-content">
          <div class="content-body">
            <router-view></router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style>
html, body, #app, .app-body {
  margin: 0 !important;
  padding: 0 !important;
  height: 100% !important;
  width: 100% !important;
  overflow: hidden !important;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

body {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background-color: #f5f7fa !important;
}

#app {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  display: block !important;
}

.el-container {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  height: auto !important;
  min-height: 0 !important;
}

.el-header {
  padding: 0 20px !important;
  height: 60px !important;
}

.el-aside {
  margin: 0 !important;
  padding: 0 !important;
}

.el-main {
  margin: 0 !important;
  padding: 0 !important;
}

.el-menu-item.is-active {
  background-color: #263445 !important;
  border-left: 4px solid #409EFF !important;
}

.el-menu-item:hover, .el-sub-menu__title:hover {
  background-color: #263445 !important;
}
</style>

<style scoped>
.app-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.layout-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.main-container {
  flex: 1;
  display: flex;
  min-height: 0;
}

.header {
  background-color: #ffffff;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-family: 'Times New Roman', serif;
  font-size: 22px;
  font-weight: bold;
  color: #3c8dbc;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
  height: 40px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  background-color: #3c8dbc;
  margin-right: 8px;
}

.user-name {
  font-size: 14px;
  margin-right: 4px;
}

.sidebar {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  z-index: 999;
}

.el-menu {
  border-right: none;
  flex: 1;
}

.el-menu-item, .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
}

.sidebar-footer {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  color: #bfcbd9;
  font-size: 12px;
}

.main-content {
  background-color: #f5f7fa;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-body {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.el-icon {
  margin-right: 10px;
  font-size: 18px;
}
</style>
