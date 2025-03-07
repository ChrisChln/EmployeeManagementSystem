<template>
  <div class="onboarding-container">
    <div class="onboarding-header">
      <h2 class="page-title">Employee Onboarding</h2>
    </div>

    <el-steps :active="activeStep" finish-status="success" class="onboarding-steps">
      <el-step title="Basic Information" />
      <el-step title="Position Information" />
      <el-step title="Department Assignment" />
      <el-step title="Complete" />
    </el-steps>

    <div class="step-content">
      <!-- 步骤1：基本信息 -->
      <div v-if="activeStep === 0" class="step-form">
        <el-form :model="employeeForm" :rules="rules" ref="basicFormRef" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Username" prop="username">
                <el-input v-model="employeeForm.username" placeholder="Enter username" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Name" prop="name">
                <el-input v-model="employeeForm.name" placeholder="Enter name" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Gender" prop="gender">
                <el-radio-group v-model="employeeForm.gender">
                  <el-radio :label="1">Male</el-radio>
                  <el-radio :label="2">Female</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Phone" prop="phone">
                <el-input v-model="employeeForm.phone" placeholder="Enter phone number" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Email" prop="email">
                <el-input v-model="employeeForm.email" placeholder="Enter email" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="ID Card" prop="idCard">
                <el-input v-model="employeeForm.idCard" placeholder="Enter ID card number" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 步骤2：职位信息 -->
      <div v-if="activeStep === 1" class="step-form">
        <el-form :model="employeeForm" :rules="rules" ref="positionFormRef" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Position" prop="position">
                <el-select v-model="employeeForm.position" placeholder="Select position" style="width: 100%">
                  <el-option-group v-for="category in positionCategories" :key="category" :label="getCategoryLabel(category)">
                    <el-option
                      v-for="position in getPositionsByCategory(category)"
                      :key="position.id"
                      :label="position.name"
                      :value="position.id"
                    />
                  </el-option-group>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Salary" prop="salary">
                <el-input-number v-model="employeeForm.salary" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Hire Date" prop="hireDate">
                <el-date-picker
                  v-model="employeeForm.hireDate"
                  type="date"
                  placeholder="Select hire date"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Probation" prop="probation">
                <el-select v-model="employeeForm.probation" placeholder="Select probation period" style="width: 100%">
                  <el-option :value="1" label="1 month" />
                  <el-option :value="2" label="2 months" />
                  <el-option :value="3" label="3 months" />
                  <el-option :value="6" label="6 months" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 步骤3：部门分配 -->
      <div v-if="activeStep === 2" class="step-form">
        <el-form :model="employeeForm" :rules="rules" ref="deptFormRef" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="Department" prop="deptId">
                <el-select v-model="employeeForm.deptId" placeholder="Select department" style="width: 100%">
                  <el-option
                    v-for="dept in departmentList"
                    :key="dept.id"
                    :label="dept.name"
                    :value="dept.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Direct Manager" prop="managerId">
                <el-select v-model="employeeForm.managerId" placeholder="Select direct manager" style="width: 100%">
                  <el-option
                    v-for="manager in managerList"
                    :key="manager.id"
                    :label="manager.name"
                    :value="manager.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 步骤4：完成 -->
      <div v-if="activeStep === 3" class="step-complete">
        <el-result
          icon="success"
          title="Employee Onboarding Process Completed"
          sub-title="Employee information has been successfully entered into the system and can be viewed in the employee management section"
        >
          <template #extra>
            <el-button type="primary" @click="goToEmployeeList">View Employee List</el-button>
            <el-button @click="resetForm">Continue Adding</el-button>
          </template>
        </el-result>
      </div>
    </div>

    <div class="step-actions">
      <el-button v-if="activeStep > 0" @click="prevStep">Previous Step</el-button>
      <el-button v-if="activeStep < 3" type="primary" @click="nextStep">Next Step</el-button>
      <el-button v-if="activeStep === 3" type="success" @click="goToEmployeeList">Complete</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const activeStep = ref(0)
const basicFormRef = ref(null)
const positionFormRef = ref(null)
const deptFormRef = ref(null)

// 员工表单数据
const employeeForm = reactive({
  username: '',
  name: '',
  gender: 1,
  phone: '',
  email: '',
  idCard: '',
  position: '',
  salary: 0,
  hireDate: '',
  probation: 3,
  deptId: '',
  managerId: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Length should be 3 to 20 characters', trigger: 'blur' }
  ],
  name: [
    { required: true, message: 'Please enter name', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: 'Please enter phone number', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Please enter email', trigger: 'blur' },
    { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
  ],
  position: [
    { required: true, message: 'Please select position', trigger: 'change' }
  ],
  salary: [
    { required: true, message: 'Please enter salary', trigger: 'blur' }
  ],
  hireDate: [
    { required: true, message: 'Please select hire date', trigger: 'change' }
  ],
  deptId: [
    { required: true, message: 'Please select department', trigger: 'change' }
  ]
}

// 部门列表
const departmentList = ref([
  { id: 1, name: 'Technology Department' },
  { id: 2, name: 'Marketing Department' },
  { id: 3, name: 'Sales Department' },
  { id: 4, name: 'Human Resources Department' },
  { id: 5, name: 'Finance Department' }
])

// 管理者列表
const managerList = ref([
  { id: 1, name: 'John Smith' },
  { id: 2, name: 'Jane Doe' },
  { id: 3, name: 'Michael Johnson' }
])

// 职位列表
const positionList = ref([])
const positionCategories = ref([])

// 获取职位列表
const fetchPositions = async () => {
  try {
    // 实际项目中应该从后端API获取数据
    // 这里直接使用PositionManagement组件中的数据
    const positions = await mockFetchPositions()
    positionList.value = positions
    
    // 提取职位类别
    const categories = [...new Set(positions.map(item => item.category))]
    positionCategories.value = categories
  } catch (error) {
    console.error('Failed to get position list:', error)
    ElMessage.error('Failed to get position list')
  }
}

// 模拟从后端获取数据
const mockFetchPositions = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 在实际项目中，可以通过API获取或使用Vuex/Pinia状态管理
      // 这里模拟获取所有职位数据
      const positions = [
        { id: 1, name: 'Intern', category: 'Management', description: 'Intern Position' },
        { id: 2, name: 'Junior Employee', category: 'Management', description: 'Junior Employee Position' },
        { id: 3, name: 'Senior Employee', category: 'Management', description: 'Senior Employee Position' },
        { id: 4, name: 'Team Leader', category: 'Management', description: 'Team Leader Position' },
        { id: 5, name: 'Project Manager', category: 'Management', description: 'Project Manager Position' },
        { id: 6, name: 'Department Manager', category: 'Management', description: 'Department Manager Position' },
        { id: 7, name: 'Director', category: 'Management', description: 'Director Position' },
        { id: 8, name: 'Vice President', category: 'Management', description: 'Vice President Position' },
        { id: 9, name: 'CTO', category: 'Management', description: 'CTO Position' },
        { id: 10, name: 'CEO', category: 'Management', description: 'CEO Position' },
        { id: 11, name: 'Junior Software Engineer', category: 'Technical', description: 'Junior Software Engineer Position' },
        { id: 12, name: 'Senior Software Engineer', category: 'Technical', description: 'Senior Software Engineer Position' },
        { id: 13, name: 'Software Architect', category: 'Technical', description: 'Software Architect Position' },
        { id: 14, name: 'DevOps Engineer', category: 'Technical', description: 'DevOps Engineer Position' },
        { id: 15, name: 'Cloud Engineer', category: 'Technical', description: 'Cloud Engineer Position' },
        { id: 16, name: 'Data Scientist', category: 'Technical', description: 'Data Scientist Position' },
        { id: 17, name: 'AI Engineer', category: 'Technical', description: 'AI Engineer Position' },
        { id: 18, name: 'QA Engineer', category: 'Technical', description: 'QA Engineer Position' },
        { id: 19, name: 'UI/UX Designer', category: 'Technical', description: 'UI/UX Designer Position' },
        { id: 20, name: 'Product Manager', category: 'Technical', description: 'Product Manager Position' },
        { id: 21, name: 'Sales Representative', category: 'Sales', description: 'Sales Representative Position' },
        { id: 22, name: 'Sales Manager', category: 'Sales', description: 'Sales Manager Position' },
        { id: 23, name: 'Account Executive', category: 'Sales', description: 'Account Executive Position' },
        { id: 24, name: 'Business Development Manager', category: 'Sales', description: 'Business Development Manager Position' },
        { id: 25, name: 'Customer Success Manager', category: 'Sales', description: 'Customer Success Manager Position' },
        { id: 26, name: 'Marketing Specialist', category: 'Marketing', description: 'Marketing Specialist Position' },
        { id: 27, name: 'Marketing Manager', category: 'Marketing', description: 'Marketing Manager Position' },
        { id: 28, name: 'Content Writer', category: 'Marketing', description: 'Content Writer Position' },
        { id: 29, name: 'SEO Specialist', category: 'Marketing', description: 'SEO Specialist Position' },
        { id: 30, name: 'Social Media Manager', category: 'Marketing', description: 'Social Media Manager Position' },
        { id: 31, name: 'HR Assistant', category: 'HR', description: 'HR Assistant Position' },
        { id: 32, name: 'HR Specialist', category: 'HR', description: 'HR Specialist Position' },
        { id: 33, name: 'HR Manager', category: 'HR', description: 'HR Manager Position' },
        { id: 34, name: 'Talent Acquisition Specialist', category: 'HR', description: 'Talent Acquisition Specialist Position' },
        { id: 35, name: 'Training Manager', category: 'HR', description: 'Training Manager Position' },
        { id: 36, name: 'Accountant', category: 'Finance', description: 'Accountant Position' },
        { id: 37, name: 'Financial Analyst', category: 'Finance', description: 'Financial Analyst Position' },
        { id: 38, name: 'Finance Manager', category: 'Finance', description: 'Finance Manager Position' },
        { id: 39, name: 'Auditor', category: 'Finance', description: 'Auditor Position' },
        { id: 40, name: 'CFO', category: 'Finance', description: 'CFO Position' }
      ]
      resolve(positions)
    }, 500)
  })
}

// 根据类别获取职位
const getPositionsByCategory = (category) => {
  return positionList.value.filter(item => item.category === category)
}

// 获取类别标签
const getCategoryLabel = (category) => {
  const categoryMap = {
    'Management': 'Management Positions',
    'Technical': 'Technical Positions',
    'Sales': 'Sales Positions',
    'Marketing': 'Marketing Positions',
    'HR': 'HR Positions',
    'Finance': 'Finance Positions'
  }
  return categoryMap[category] || category
}

// 下一步
const nextStep = async () => {
  if (activeStep.value === 0) {
    // 验证基本信息
    try {
      await basicFormRef.value.validate()
      activeStep.value++
    } catch (error) {
      ElMessage.warning('Please complete basic information')
    }
  } else if (activeStep.value === 1) {
    // 验证职位信息
    try {
      await positionFormRef.value.validate()
      activeStep.value++
    } catch (error) {
      ElMessage.warning('Please complete position information')
    }
  } else if (activeStep.value === 2) {
    // 验证部门信息
    try {
      await deptFormRef.value.validate()
      // 提交表单
      submitForm()
    } catch (error) {
      ElMessage.warning('Please complete department information')
    }
  }
}

// 上一步
const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

// 提交表单
const submitForm = async () => {
  try {
    // 这里应该是向后端提交数据
    // 由于没有实际接口，这里模拟提交
    ElMessage.success('Employee information submitted successfully')
    activeStep.value++
  } catch (error) {
    ElMessage.error('Submission failed, please try again later')
  }
}

// 重置表单
const resetForm = () => {
  basicFormRef.value.resetFields()
  positionFormRef.value.resetFields()
  deptFormRef.value.resetFields()
  activeStep.value = 0
}

// 跳转到员工列表
const goToEmployeeList = () => {
  router.push('/employee-management')
}

onMounted(() => {
  // 获取部门和管理者列表
  fetchPositions()
})
</script>

<style scoped>
.onboarding-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.onboarding-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 22px;
  color: #303133;
  margin: 0;
}

.onboarding-steps {
  margin-bottom: 40px;
}

.step-content {
  min-height: 400px;
  margin-bottom: 30px;
}

.step-form {
  max-width: 800px;
  margin: 0 auto;
}

.step-complete {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style> 