<template>
  <div class="home-container">
    <div class="welcome-section">
      <h1 class="welcome-title">Welcome to Employee Management System</h1>
      <p class="welcome-subtitle">A comprehensive enterprise human resource management platform</p>
    </div>

    <div class="dashboard-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="data-card" shadow="hover">
            <div class="data-content">
              <div class="data-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-title">Employee</div>
                <div class="data-value">{{ statistics.empCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="data-card" shadow="hover">
            <div class="data-content">
              <div class="data-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-title">Department</div>
                <div class="data-value">{{ statistics.deptCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="data-card" shadow="hover">
            <div class="data-content">
              <div class="data-icon">
                <el-icon><Briefcase /></el-icon>
              </div>
              <div class="data-info">
                <div class="data-title">Position</div>
                <div class="data-value">40</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <h2 class="section-title">Quick Access</h2>
    
    <div class="quick-access-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="action-card" shadow="hover" @click="navigateTo('/employee-management', '2')">
            <div class="action-content">
              <div class="action-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="action-text">Employee Management</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="action-card" shadow="hover" @click="navigateTo('/department-management', '3')">
            <div class="action-content">
              <div class="action-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="action-text">Department Management</div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="action-card" shadow="hover" @click="navigateTo('/position-management', '3')">
            <div class="action-content">
              <div class="action-icon">
                <el-icon><Briefcase /></el-icon>
              </div>
              <div class="action-text">Position Management</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { School, User, OfficeBuilding, UserFilled, Briefcase } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { inject, onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeIndex = inject('activeIndex')

// 统计数据
const statistics = ref({
  empCount: 0,
  deptCount: 0
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/statistics')
    
    if (response.data.code === 1) {
      statistics.value = response.data.data
    } else {
      console.error('获取统计数据失败:', response.data.msg)
      // 使用模拟数据
      useMockStatistics()
    }
  } catch (error) {
    console.error('获取统计数据错误:', error)
    ElMessage.error('获取统计数据失败，使用模拟数据')
    // 使用模拟数据
    useMockStatistics()
  }
}

// 使用模拟数据
const useMockStatistics = () => {
  statistics.value = {
    empCount: 36,
    deptCount: 15
  }
}

// 获取菜单实例的函数
let menuInstance = null
onMounted(() => {
  // 获取统计数据
  fetchStatistics()
  
  // 等待DOM加载完成后获取菜单实例
  setTimeout(() => {
    menuInstance = document.querySelector('.el-menu')
  }, 100)
})

const navigateTo = (path, menuIndex) => {
  // 设置激活菜单
  if (activeIndex) {
    activeIndex.value = path
  }
  
  // 如果是子菜单项，先展开父菜单
  if (menuIndex !== '/') {
    const subMenu = document.querySelector(`.el-sub-menu[index="${menuIndex}"]`)
    if (subMenu && !subMenu.classList.contains('is-opened')) {
      const title = subMenu.querySelector('.el-sub-menu__title')
      if (title) {
        title.click()
      }
    }
  }
  
  // 导航到目标页面
  router.push(path)
  
  // 手动触发菜单项点击
  setTimeout(() => {
    const menuItem = document.querySelector(`.el-menu-item[index="${path}"]`)
    if (menuItem) {
      menuItem.click()
    }
  }, 50)
}
</script>

<style scoped>
.home-container {
  padding-bottom: 40px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-title {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #606266;
}

.dashboard-section {
  margin-bottom: 40px;
}

.data-card {
  height: 120px;
  display: flex;
  align-items: center;
}

.data-content {
  display: flex;
  align-items: center;
}

.data-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.data-icon .el-icon {
  font-size: 30px;
  color: #409EFF;
}

.data-info {
  flex: 1;
}

.data-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.data-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.section-title {
  font-size: 22px;
  color: #303133;
  margin: 30px 0 20px;
}

.quick-access-section {
  margin-bottom: 40px;
}

.action-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.action-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.action-icon .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.action-text {
  font-size: 16px;
  color: #303133;
}
</style> 