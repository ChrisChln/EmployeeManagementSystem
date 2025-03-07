<template>
  <div class="employee-container">
    <div class="employee-header">
      <h2 class="page-title">Employee Management</h2>
    </div>

    <!-- Search Area -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="Name">
          <el-input v-model="searchForm.name" placeholder="Enter name" clearable />
        </el-form-item>
        <el-form-item label="Gender">
          <el-select v-model="searchForm.gender" placeholder="Select gender" clearable style="width: 120px">
            <el-option label="Male" :value="1" />
            <el-option label="Female" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="Hire Date">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="To"
            start-placeholder="Start date"
            end-placeholder="End date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">Search</el-button>
          <el-button @click="resetSearch">Reset</el-button>
          <el-button type="primary" @click="showAddDialog">Add Employee</el-button>
          <el-button 
            type="danger" 
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete">
            Batch Delete
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Employee List -->
    <el-table
      :data="employeeList"
      style="width: 100%"
      border
      v-loading="loading"
      stripe
      size="large"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column 
        prop="id" 
        label="ID" 
        width="60" 
        align="center"
      />
      <el-table-column prop="name" label="Name" min-width="100" align="center" />
      <el-table-column prop="gender" label="Gender" width="80" align="center">
        <template #default="scope">
          {{ scope.row.gender === 1 ? 'Male' : 'Female' }}
        </template>
      </el-table-column>
      <el-table-column prop="image" label="Avatar" width="100" align="center">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.image" />
        </template>
      </el-table-column>
      <el-table-column prop="position" label="Position" width="120" align="center">
        <template #default="scope">
          {{ getPositionName(scope.row.position) }}
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="Department" width="120" align="center" />
      <el-table-column prop="salary" label="Salary" width="100" align="center">
        <template #default="scope">
          {{ scope.row.salary.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="hireDate" label="Hire Date" width="120" align="center" />
      <el-table-column prop="updateTime" label="Update Time" width="180" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Operations" width="180" fixed="right" align="center">
        <template #default="scope">
          <el-button 
            type="info" 
            size="small" 
            @click="showDetailDialog(scope.row)"
            :icon="View"
            circle />
          <el-button 
            type="primary" 
            size="small" 
            @click="showEditDialog(scope.row)"
            :icon="Edit"
            circle />
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete([scope.row.id])"
            :icon="Delete"
            circle />
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? 'Edit Employee' : 'Add Employee'"
      width="800px"
      :close-on-click-modal="false">
      <el-form
        ref="empFormRef"
        :model="empForm"
        :rules="rules"
        label-width="120px">
        <el-row :gutter="20">
          <!-- Username and Name -->
          <el-col :span="12">
            <el-form-item label="Username" prop="username">
              <el-input v-model="empForm.username" placeholder="Enter username" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Name" prop="name">
              <el-input v-model="empForm.name" placeholder="Enter name" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- Gender and Phone -->
          <el-col :span="12">
            <el-form-item label="Gender" prop="gender">
              <el-radio-group v-model="empForm.gender">
                <el-radio :label="1">Male</el-radio>
                <el-radio :label="2">Female</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Phone" prop="phone">
              <el-input v-model="empForm.phone" placeholder="Enter phone number" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- Position and Salary -->
          <el-col :span="12">
            <el-form-item label="Position" prop="position">
              <el-select v-model="empForm.position" placeholder="Select position" style="width: 100%">
                <!-- Management Positions -->
                <el-option-group label="Management Positions">
                  <el-option :value="1" label="Intern" />
                  <el-option :value="2" label="Junior Employee" />
                  <el-option :value="3" label="Senior Employee" />
                  <el-option :value="4" label="Team Leader" />
                  <el-option :value="5" label="Project Manager" />
                  <el-option :value="6" label="Department Manager" />
                  <el-option :value="7" label="Director" />
                  <el-option :value="8" label="Vice President" />
                  <el-option :value="9" label="CTO" />
                  <el-option :value="10" label="CEO" />
                </el-option-group>
                
                <!-- Technical Positions -->
                <el-option-group label="Technical Positions">
                  <el-option :value="11" label="Junior Software Engineer" />
                  <el-option :value="12" label="Senior Software Engineer" />
                  <el-option :value="13" label="Software Architect" />
                  <el-option :value="14" label="DevOps Engineer" />
                  <el-option :value="15" label="Cloud Engineer" />
                  <el-option :value="16" label="Data Scientist" />
                  <el-option :value="17" label="AI Engineer" />
                  <el-option :value="18" label="QA Engineer" />
                  <el-option :value="19" label="UI/UX Designer" />
                  <el-option :value="20" label="Product Manager" />
                </el-option-group>
                
                <!-- Sales Positions -->
                <el-option-group label="Sales Positions">
                  <el-option :value="21" label="Sales Representative" />
                  <el-option :value="22" label="Sales Manager" />
                  <el-option :value="23" label="Account Executive" />
                  <el-option :value="24" label="Business Development Manager" />
                  <el-option :value="25" label="Customer Success Manager" />
                </el-option-group>
                
                <!-- Marketing Positions -->
                <el-option-group label="Marketing Positions">
                  <el-option :value="26" label="Marketing Specialist" />
                  <el-option :value="27" label="Marketing Manager" />
                  <el-option :value="28" label="Content Writer" />
                  <el-option :value="29" label="SEO Specialist" />
                  <el-option :value="30" label="Social Media Manager" />
                </el-option-group>
                
                <!-- HR Positions -->
                <el-option-group label="HR Positions">
                  <el-option :value="31" label="HR Assistant" />
                  <el-option :value="32" label="HR Specialist" />
                  <el-option :value="33" label="HR Manager" />
                  <el-option :value="34" label="Talent Acquisition Specialist" />
                  <el-option :value="35" label="Training Manager" />
                </el-option-group>
                
                <!-- Finance Positions -->
                <el-option-group label="Finance Positions">
                  <el-option :value="36" label="Accountant" />
                  <el-option :value="37" label="Financial Analyst" />
                  <el-option :value="38" label="Finance Manager" />
                  <el-option :value="39" label="Auditor" />
                  <el-option :value="40" label="CFO" />
                </el-option-group>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Salary" prop="salary">
              <el-input-number v-model="empForm.salary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <!-- Department and Hire Date -->
          <el-col :span="12">
            <el-form-item label="Department" prop="deptId">
              <el-select v-model="empForm.deptId">
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
            <el-form-item label="Hire Date" prop="hireDate">
              <el-date-picker
                v-model="empForm.hireDate"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 头像上传 -->
        <el-form-item label="Avatar" prop="image">
          <el-upload
            class="avatar-uploader"
            :action="`${baseURL}/upload`"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="empForm.image" :src="empForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 工作经历 -->
        <el-form-item label="Experience">
          <!-- 当前工作经历显示区域 -->
          <div class="current-experience">
            <div class="experience-title">Current experience:</div>
            <div class="experience-tags">
              <template v-if="empForm.exprList && empForm.exprList.length > 0">
                <el-tag
                  v-for="(exp, index) in empForm.exprList"
                  :key="index"
                  size="small"
                  style="margin: 2px 4px"
                  type="info"
                  effect="plain"
                >
                  {{ getExperienceText(exp) }}
                </el-tag>
              </template>
              <span v-else class="no-experience">~</span>
            </div>
          </div>
          
          <el-divider />
          
          <el-button type="primary" size="small" @click="addExperience">Add work experience</el-button>
          
          <div class="experience-list-container">
            <div v-for="(exp, index) in empForm.exprList" :key="index" class="experience-item">
              <el-row :gutter="12">
                <el-col :span="10">
                  <el-date-picker
                    v-model="exp.dateRange"
                    type="daterange"
                    range-separator="to"
                    start-placeholder="Start date"
                    end-placeholder="End date"
                    value-format="YYYY-MM-DD"
                    size="small"
                    style="width: 100%"
                    @change="handleDateRangeChange(exp)"
                  />
                </el-col>
                <el-col :span="6">
                  <el-input
                    v-model="exp.company"
                    placeholder="Enter company name"
                    size="small"
                  />
                </el-col>
                <el-col :span="6">
                  <el-input
                    v-model="exp.position"
                    placeholder="Enter position"
                    size="small"
                  />
                </el-col>
                <el-col :span="2" class="delete-btn-col">
                  <el-button 
                    type="danger" 
                    circle 
                    size="small"
                    @click="removeExperience(index)"
                    class="delete-exp-btn">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitForm">Confirm</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Delete Confirmation Dialog -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="Confirm Delete"
      width="400px"
      center>
      <div class="delete-dialog-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <p>Are you sure you want to delete the selected employee(s)?</p>
        <p class="warning-text">This action cannot be undone.</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">Cancel</el-button>
          <el-button type="danger" @click="confirmDelete">Delete</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="Employee Details"
      width="700px">
      <div class="employee-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ detailForm.id }}</el-descriptions-item>
          <el-descriptions-item label="Username">{{ detailForm.username }}</el-descriptions-item>
          <el-descriptions-item label="Name">{{ detailForm.name }}</el-descriptions-item>
          <el-descriptions-item label="Gender">
            {{ detailForm.gender === 1 ? 'Male' : 'Female' }}
          </el-descriptions-item>
          <el-descriptions-item label="Phone">{{ detailForm.phone }}</el-descriptions-item>
          <el-descriptions-item label="Position">
            {{ getPositionName(detailForm.position) }}
          </el-descriptions-item>
          <el-descriptions-item label="Department">{{ detailForm.deptName }}</el-descriptions-item>
          <el-descriptions-item label="Salary">{{ detailForm.salary?.toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="Hire Date">{{ detailForm.hireDate }}</el-descriptions-item>
          <el-descriptions-item label="Update Time">
            {{ formatDate(detailForm.updateTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="Avatar" :span="2">
            <el-avatar :size="60" :src="detailForm.image" />
          </el-descriptions-item>
        </el-descriptions>

        <!-- 工作经历部分 -->
        <div class="experience-detail">
          <h3>Work experience</h3>
          <el-timeline v-if="detailForm.exprList && detailForm.exprList.length">
            <el-timeline-item
              v-for="(exp, index) in detailForm.exprList"
              :key="index"
              :timestamp="`${exp.startDate} ~ ${exp.endDate}`"
              placement="top"
            >
              <el-card>
                <h4>{{ exp.company }}</h4>
                <p>Position: {{ exp.position }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="No work experience" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Edit, Delete, Plus, Warning, View } from '@element-plus/icons-vue'
import axios from 'axios'

// 修改基础URL为相对路径
const baseURL = '/api'  // 使用代理路径

const employeeList = ref([])
const departmentList = ref([])
const empForm = reactive({
  id: '',
  username: '',
  name: '',
  gender: 1,
  phone: '',
  position: 1,
  salary: 0,
  hireDate: '',
  deptId: '',
  image: '',
  exprList: []
})
const empFormRef = ref(null)
const loading = ref(false)
const selectedIds = ref([])
const uploadLoading = ref(false)

// 搜索表单数据
const searchForm = reactive({
  name: '',
  gender: '',
  begin: '',
  end: ''
})

// 日期范围
const dateRange = ref([])

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)

// 删除确认对话框
const deleteDialogVisible = ref(false)
const currentDeleteIds = ref([])

// 新旧职位ID映射
const positionMapping = {
  // 新职位ID -> 旧职位ID
  1: 1,  // Intern -> 班主任
  2: 2,  // Junior Employee -> 讲师
  3: 3,  // Senior Employee -> 学工主管
  4: 4,  // Team Leader -> 教研主管
  5: 5,  // Project Manager -> 咨询师
  // 其他职位暂时映射到默认值
  default: 1
}

// 获取职位名称
const getPositionName = (positionId) => {
  // 职位映射表
  const positionMap = {
    1: "Intern",
    2: "Junior Employee",
    3: "Senior Employee",
    4: "Team Leader",
    5: "Project Manager",
    6: "Department Manager",
    7: "Director",
    8: "Vice President",
    9: "CTO",
    10: "CEO",
    11: "Junior Software Engineer",
    12: "Senior Software Engineer",
    13: "Software Architect",
    14: "DevOps Engineer",
    15: "Cloud Engineer",
    16: "Data Scientist",
    17: "AI Engineer",
    18: "QA Engineer",
    19: "UI/UX Designer",
    20: "Product Manager",
    21: "Sales Representative",
    22: "Sales Manager",
    23: "Account Executive",
    24: "Business Development Manager",
    25: "Customer Success Manager",
    26: "Marketing Specialist",
    27: "Marketing Manager",
    28: "Content Writer",
    29: "SEO Specialist",
    30: "Social Media Manager",
    31: "HR Assistant",
    32: "HR Specialist",
    33: "HR Manager",
    34: "Talent Acquisition Specialist",
    35: "Training Manager",
    36: "Accountant",
    37: "Financial Analyst",
    38: "Finance Manager",
    39: "Auditor",
    40: "CFO"
  }
  
  return positionMap[positionId] || `Unknown position(${positionId})`
}

// 修改表单验证规则
const rules = {
  username: [{ required: true, message: 'Please input username', trigger: 'blur' }],
  name: [{ required: true, message: 'Please input name', trigger: 'blur' }],
  gender: [{ required: true, message: 'Please select gender', trigger: 'change' }],
  phone: [{ required: true, message: 'Please input phone number', trigger: 'blur' }],
  position: [{ required: true, message: 'Please select position', trigger: 'change' }],
  deptId: [{ required: true, message: 'Please select department', trigger: 'change' }],
  salary: [{ required: true, message: 'Please input salary', trigger: 'blur' }],
  hireDate: [{ required: true, message: 'Please select hire date', trigger: 'change' }]
}

const submitLoading = ref(false)

const fetchEmployees = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      name: searchForm.name || undefined,
      gender: searchForm.gender || undefined,
      begin: searchForm.begin || undefined,
      end: searchForm.end || undefined
    }
    
    console.log('Fetching employees with params:', params)
    const response = await axios.get(`${baseURL}/emps`, { params })
    console.log('Response:', response)
    
    if (response.data.code === 1) {
      // 反向映射职位ID
      const reversePositionMapping = Object.entries(positionMapping).reduce((acc, [newId, oldId]) => {
        if (newId !== 'default') {
          acc[oldId] = parseInt(newId);
        }
        return acc;
      }, {});
      
      employeeList.value = response.data.data.rows.map(emp => ({
        ...emp,
        position: reversePositionMapping[emp.position] || emp.position
      }))
      pagination.total = response.data.data.total
      console.log('Employees loaded:', employeeList.value)
    } else {
      console.error('API error:', response.data)
      ElMessage.error(response.data.msg || 'Failed to fetch employees')
      useMockData()
    }
  } catch (error) {
    console.error('Error fetching employees:', error)
    ElMessage.error('Network error, please try again later')
    useMockData()
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const response = await axios.get(`${baseURL}/depts`)
    departmentList.value = response.data.data
  } catch (error) {
    console.error('Error fetching departments:', error)
  }
}

const handleSave = async () => {
  try {
    if (empForm.id) {
      // 更新现有员工
      const response = await axios.put(`${baseURL}/emps/${empForm.id}`, empForm)
      if (response.data.code === 1) {
        ElMessage.success('Updated successfully')
        fetchEmployees() // 刷新列表
      } else {
        ElMessage.error(response.data.msg || 'Update failed')
      }
    } else {
      // 添加新员工
      const dept = departmentList.value.find(d => d.id === empForm.deptId)
      const newEmp = {
        username: empForm.username,
        name: empForm.name,
        gender: empForm.gender,
        image: empForm.image || 'https://example.com/default-avatar.jpg',
        position: empForm.position,
        salary: empForm.salary,
        hireDate: empForm.hireDate,
        deptId: empForm.deptId,
        deptName: dept?.name || '',
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      const response = await axios.post(`${baseURL}/emps`, newEmp)
      if (response.data.code === 1) {
        ElMessage.success('Added successfully')
        fetchEmployees() // 刷新列表
      } else {
        ElMessage.error(response.data.msg || 'Add failed')
      }
    }
  } catch (error) {
    console.error('Error saving employee:', error)
    ElMessage.error('Network error, please try again later')
  }
}

const handleDelete = async (ids) => {
  currentDeleteIds.value = ids
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  try {
    loading.value = true
    const response = await axios.delete(`${baseURL}/emps?ids=${currentDeleteIds.value.join(',')}`)
    
    if (response.data.code === 1) {
      ElMessage.success('Deleted successfully')
      deleteDialogVisible.value = false
      fetchEmployees() // 刷新列表
    } else {
      ElMessage.error(response.data.msg || 'Delete failed')
    }
  } catch (error) {
    console.error('Error deleting employees:', error)
    ElMessage.error('Network error, please try again later')
  } finally {
    loading.value = false
  }
}

const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('Please select employees to delete')
    return
  }
  handleDelete(selectedIds.value)
}

const resetForm = () => {
  if (empFormRef.value) {
    empFormRef.value.resetFields()
  }
  Object.assign(empForm, {
    id: '',
    username: '',
    name: '',
    gender: 1,
    phone: '',
    position: 1,
    salary: 0,
    hireDate: '',
    deptId: '',
    image: '',
    exprList: []
  })
}

const addExperience = () => {
  if (!empForm.exprList) {
    empForm.exprList = []
  }
  empForm.exprList.push({
    id: undefined,
    empId: isEdit.value ? empForm.id : undefined,
    company: '',
    position: '',
    dateRange: null,
    startDate: '',
    endDate: ''
  })
}

const removeExperience = (index) => {
  empForm.exprList.splice(index, 1)
}

const handleDateRangeChange = (exp) => {
  if (exp.dateRange?.length === 2) {
    [exp.startDate, exp.endDate] = exp.dateRange
  } else {
    exp.startDate = ''
    exp.endDate = ''
  }
}

const handleAvatarSuccess = (response) => {
  if (response.code === 1) {
    empForm.image = response.data
    ElMessage.success('Avatar uploaded successfully')
  } else {
    ElMessage.error(response.msg || 'Upload failed')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('Avatar must be JPG or PNG format!')
  }
  if (!isLt2M) {
    ElMessage.error('Avatar size can not exceed 2MB!')
  }
  return isJPG && isLt2M
}

// 使用模拟数据的辅助函数
const useMockData = () => {
  console.log('Using mock data')
  employeeList.value = [
    {
      id: 1,
      username: "jinyong",
      name: "Jin Yong",
      gender: 1,
      image: "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-02-00-27-53B.jpg",
      position: 2,
      salary: 8000,
      hireDate: "2015-01-01",
      deptId: 2,
      deptName: "Research Department",
      createTime: "2022-09-01T23:06:30",
      updateTime: "2022-09-02T00:29:04"
    },
    {
      id: 2,
      username: "zhangwuji",
      name: "Zhang Wuji",
      gender: 1,
      image: "https://web-framework.oss-cn-hangzhou.aliyuncs.com/2022-09-02-00-27-53B.jpg",
      position: 2,
      salary: 6000,
      hireDate: "2015-01-01",
      deptId: 2,
      deptName: "Research Department",
      createTime: "2022-09-01T23:06:30",
      updateTime: "2022-09-02T00:29:04"
    }
  ]
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 处理搜索
const handleSearch = () => {
  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.begin = dateRange.value[0]
    searchForm.end = dateRange.value[1]
  } else {
    searchForm.begin = ''
    searchForm.end = ''
  }
  
  pagination.page = 1 // 重置页码
  fetchEmployees()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.gender = ''
  dateRange.value = []
  searchForm.begin = ''
  searchForm.end = ''
  pagination.page = 1
  fetchEmployees()
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchEmployees()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.page = val
  fetchEmployees()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  // 重置表单数据
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = async (row) => {
  isEdit.value = true
  
  try {
    loading.value = true
    
    // 获取员工详细信息，包括工作经验
    const response = await axios.get(`${baseURL}/emps/${row.id}`)
    
    if (response.data.code === 1) {
      // 重置表单
      resetForm()
      
      const employeeData = response.data.data
      
      // 处理工作经历数据
      if (employeeData.exprList && Array.isArray(employeeData.exprList)) {
        employeeData.exprList = employeeData.exprList.map(exp => ({
          id: exp.id,
          empId: exp.empId,
          company: exp.company,
          position: exp.position,
          startDate: exp.startDate,
          endDate: exp.endDate,
          dateRange: [exp.startDate, exp.endDate] // 设置日期范围
        }))
      } else {
        employeeData.exprList = []
      }
      
      // 设置表单数据
      Object.assign(empForm, employeeData)
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.msg || 'Failed to get employee information')
    }
  } catch (error) {
    console.error('Error getting employee information:', error)
    ElMessage.error('Network error, please try again later')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  if (format === 'YYYY-MM-DD') {
    return `${year}-${month}-${day}`;
  }
  
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 添加工作经历文本格式化方法
const getExperienceText = (exp) => {
  if (!exp.company && !exp.position && !exp.dateRange) return 'Not specified'
  
  const dateText = exp.dateRange?.length === 2 
    ? `${exp.dateRange[0]} ~ ${exp.dateRange[1]}`
    : (exp.startDate && exp.endDate ? `${exp.startDate} ~ ${exp.endDate}` : 'Date pending')
  
  const companyText = exp.company || 'Company pending'
  const positionText = exp.position || 'Position pending'
  
  return `${dateText} | ${companyText} | ${positionText}`
}

// 提交表单
const submitForm = async () => {
  if (!empFormRef.value) return
  
  try {
    await empFormRef.value.validate()
    
    submitLoading.value = true
    
    // 准备提交数据
    const submitData = {
      username: empForm.username,
      name: empForm.name,
      gender: empForm.gender,
      phone: empForm.phone,
      // 使用映射将新职位ID转换为旧职位ID
      position: positionMapping[empForm.position] || positionMapping.default,
      salary: empForm.salary,
      hireDate: empForm.hireDate instanceof Date 
        ? empForm.hireDate.toISOString().split('T')[0] 
        : empForm.hireDate,
      deptId: empForm.deptId,
      image: empForm.image || ''
    }
    
    // 如果是编辑模式，添加ID
    if (isEdit.value) {
      submitData.id = empForm.id
    }

    // 处理工作经历数据 - 确保只提交有效的工作经历
    if (empForm.exprList && empForm.exprList.length > 0) {
      const validExperiences = empForm.exprList
        .filter(exp => exp.company && exp.position && 
                (exp.dateRange || (exp.startDate && exp.endDate)))
        .map(exp => ({
          id: exp.id, // 保留已有记录的ID
          empId: isEdit.value ? empForm.id : undefined, // 编辑模式时使用现有ID
          company: exp.company.trim(),
          position: exp.position.trim(),
          startDate: exp.dateRange ? exp.dateRange[0] : exp.startDate,
          endDate: exp.dateRange ? exp.dateRange[1] : exp.endDate
        }))

      // 将工作经历数据添加到提交数据中
      submitData.exprList = validExperiences
    } else {
      submitData.exprList = []
    }

    console.log('Submission data:', submitData) // For debugging

    // 发送请求
    const url = isEdit.value ? `/api/emps/${submitData.id}` : `/api/emps`
    const method = isEdit.value ? 'put' : 'post'
    
    const response = await axios[method](url, submitData)
    
    if (response.data.code === 1) {
      ElMessage.success(isEdit.value ? 'Updated successfully' : 'Added successfully')
      dialogVisible.value = false
      await fetchEmployees() // 重新获取列表数据
    } else {
      throw new Error(response.data.msg || 'Operation failed')
    }
  } catch (error) {
    console.error('Error:', error)
    // 更详细的错误信息
    if (error.response) {
      console.error('Response status:', error.response.status)
      console.error('Response data:', error.response.data)
      ElMessage.error(`Operation failed: ${error.response.data.msg || error.response.statusText || 'Unknown error'}`)
    } else if (error.request) {
      console.error('Request did not receive response')
      ElMessage.error('Network error, please check your connection')
    } else {
      console.error('Error message:', error.message)
      ElMessage.error(`Operation failed: ${error.message}`)
    }
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  resetForm()
}

// 头像上传相关
const uploadHeaders = {
  // 如果需要认证，在这里添加认证头
}

// 添加详情相关的响应式变量
const detailDialogVisible = ref(false)
const detailForm = reactive({
  id: '',
  username: '',
  name: '',
  gender: 1,
  phone: '',
  position: 1,
  deptName: '',
  salary: 0,
  hireDate: '',
  updateTime: '',
  image: '',
  exprList: []
})

// 显示详情对话框
const showDetailDialog = async (row) => {
  try {
    loading.value = true;
    // 获取员工详细信息，包括工作经验
    const response = await axios.get(`${baseURL}/emps/${row.id}`);
    
    if (response.data.code === 1) {
      // 将API返回的数据赋值给detailForm
      Object.assign(detailForm, response.data.data);
      
      // 确保exprList存在
      if (!detailForm.exprList) {
        detailForm.exprList = [];
      }
      
      // 格式化日期
      if (detailForm.exprList && detailForm.exprList.length > 0) {
        detailForm.exprList.forEach(exp => {
          // 确保日期格式正确
          if (exp.startDate) {
            exp.startDate = formatDate(exp.startDate, 'YYYY-MM-DD');
          }
          if (exp.endDate) {
            exp.endDate = formatDate(exp.endDate, 'YYYY-MM-DD');
          }
        });
      }
      
      detailDialogVisible.value = true;
    } else {
      ElMessage.error(response.data.msg || 'Failed to get employee information');
    }
  } catch (error) {
    console.error('Error getting employee information:', error);
    ElMessage.error('Network error, please try again later');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchEmployees()
  fetchDepartments()
})
</script>

<style scoped>
.employee-container {
  padding: 16px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.employee-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  color: #303133;
  margin: 0;
}

.search-area {
  margin-bottom: 20px;
  padding: 18px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 24px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.delete-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.warning-icon {
  font-size: 24px;
  color: #f56c6c;
  margin-bottom: 10px;
}

.warning-text {
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-label {
  font-weight: bold;
  color: #606266;
}

.detail-value {
  color: #303133;
}

.detail-row {
  margin-bottom: 15px;
}

/* 工作经历列表容器样式 */
.experience-list-container {
  margin-top: 8px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

/* 工作经历项样式 */
.experience-item {
  margin-top: 4px;
}

.experience-item:first-child {
  margin-top: 0;
}

/* 删除按钮列样式 */
.delete-btn-col {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 删除按钮样式 */
.delete-exp-btn {
  margin: 0;
}

/* 调整列间距 */
.el-col {
  padding-right: 6px;
  padding-left: 6px;
}

/* 确保所有输入框宽度一致 */
:deep(.el-date-editor.el-input),
:deep(.el-date-editor.el-input__wrapper),
:deep(.el-input) {
  width: 100%;
}

.current-experience {
  margin-bottom: 12px;
}

.experience-title {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.experience-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 24px; /* 确保没有标签时也有一定高度 */
}

.no-experience {
  color: #909399;
  font-size: 14px;
  line-height: 24px;
}

/* 添加详情相关样式 */
.employee-detail {
  padding: 20px;
}

.experience-detail {
  margin-top: 30px;
}

.experience-detail h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-timeline-item__content) {
  min-width: 300px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 13px;
  color: #909399;
}

:deep(.el-card) {
  --el-card-padding: 12px;
}

:deep(.el-card h4) {
  margin: 0 0 8px;
  color: #303133;
}

:deep(.el-card p) {
  margin: 0;
  color: #606266;
  font-size: 13px;
}
</style> 