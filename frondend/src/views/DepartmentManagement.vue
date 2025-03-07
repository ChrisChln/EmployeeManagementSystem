<template>
  <div class="department-container">
    <div class="department-header">
      <h2 class="page-title">Department Management</h2>
      <div class="header-operations">
        <el-button type="primary" @click="showAddDialog">Add Department</el-button>
      </div>
    </div>

    <!-- 部门列表 -->
    <el-table
      :data="departmentList"
      style="width: 100%"
      border
      v-loading="loading"
      stripe>
      <el-table-column type="index" label="#" width="60" align="center" />
      <el-table-column prop="name" label="Department Name" min-width="180" />
      <el-table-column prop="createTime" label="Create Time" min-width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="Update Time" min-width="180">
        <template #default="scope">
          {{ formatDate(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Operations" width="200" align="center">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="showEditDialog(scope.row)"
            :icon="Edit"
            circle />
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
            :icon="Delete"
            circle />
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑部门对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? 'Edit Department' : 'Add Department'"
      width="30%"
      @close="resetForm">
      <el-form 
        ref="deptFormRef"
        :model="deptForm"
        :rules="deptRules"
        label-width="120px">
        <el-form-item label="Department Name" prop="name">
          <el-input v-model="deptForm.name" placeholder="Please enter department name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitForm">Confirm</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="Confirm Delete"
      width="30%">
      <span>Are you sure you want to delete this department?</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">Cancel</el-button>
          <el-button type="danger" @click="confirmDelete">Delete</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Edit, Delete } from '@element-plus/icons-vue'

// 使用代理配置的路径
const baseURL = '/api'

// 部门列表数据
const departmentList = ref([])
const loading = ref(false)

// 对话框控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const deleteDialogVisible = ref(false)
const currentDept = ref(null)

// 表单相关
const deptFormRef = ref(null)
const deptForm = reactive({
  id: '',
  name: ''
})
const deptRules = {
  name: [
    { required: true, message: 'Please enter department name', trigger: 'blur' },
    { min: 2, max: 20, message: 'Length should be 2 to 20 characters', trigger: 'blur' }
  ]
}

// 获取部门列表
const fetchDepartments = async () => {
  loading.value = true
  try {
    console.log('Fetching departments from:', `${baseURL}/depts`)
    const response = await axios.get(`${baseURL}/depts`)
    console.log('Response:', response)
    
    if (response.data.code === 1) {
      departmentList.value = response.data.data
      console.log('Departments loaded:', departmentList.value)
    } else {
      console.error('API error:', response.data)
      ElMessage.error(response.data.msg || 'Failed to fetch departments')
      
      // 如果API调用失败，使用模拟数据
      useMockData()
    }
  } catch (error) {
    console.error('Error fetching departments:', error)
    ElMessage.error('Network error, please try again later')
    
    // 如果发生错误，使用模拟数据
    useMockData()
  } finally {
    loading.value = false
  }
}

// 使用模拟数据的辅助函数
const useMockData = () => {
  console.log('Using mock data')
  departmentList.value = [
    {
      id: 1,
      name: "学工部",
      createTime: "2022-09-01T23:06:29",
      updateTime: "2022-09-01T23:06:29"
    },
    {
      id: 2,
      name: "教研部",
      createTime: "2022-09-01T23:06:29",
      updateTime: "2022-09-01T23:06:29"
    },
    {
      id: 3,
      name: "人事部",
      createTime: "2022-09-01T23:06:29",
      updateTime: "2022-09-01T23:06:29"
    },
    {
      id: 4,
      name: "财务部",
      createTime: "2022-09-01T23:06:29",
      updateTime: "2022-09-01T23:06:29"
    }
  ]
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = async (row) => {
  try {
    // 实际环境中的API调用
    /*
    const response = await axios.get(`${baseURL}/depts/${row.id}`)
    if (response.data.code === 1) {
      const dept = response.data.data
      deptForm.id = dept.id
      deptForm.name = dept.name
      isEdit.value = true
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.msg || 'Failed to get department details')
    }
    */
    
    // 模拟数据，仅用于演示
    deptForm.id = row.id
    deptForm.name = row.name
    isEdit.value = true
    dialogVisible.value = true
  } catch (error) {
    console.error('Error fetching department details:', error)
    ElMessage.error('Network error, please try again later')
  }
}

// 提交表单
const submitForm = async () => {
  if (!deptFormRef.value) return
  
  await deptFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 实际环境中的API调用
        /*
        let response
        if (isEdit.value) {
          // 编辑部门
          response = await axios.put(`${baseURL}/depts`, {
            id: deptForm.id,
            name: deptForm.name
          })
        } else {
          // 添加部门
          response = await axios.post(`${baseURL}/depts`, {
            name: deptForm.name
          })
        }
        
        if (response.data.code === 1) {
          ElMessage.success(isEdit.value ? 'Department updated successfully' : 'Department added successfully')
          dialogVisible.value = false
          fetchDepartments() // 刷新列表
        } else {
          ElMessage.error(response.data.msg || 'Operation failed')
        }
        */
        
        // 模拟成功响应，仅用于演示
        ElMessage.success(isEdit.value ? 'Department updated successfully' : 'Department added successfully')
        dialogVisible.value = false
        
        // 模拟更新列表
        if (isEdit.value) {
          // 更新现有部门
          const index = departmentList.value.findIndex(item => item.id === deptForm.id)
          if (index !== -1) {
            departmentList.value[index].name = deptForm.name
          }
        } else {
          // 添加新部门
          const newDept = {
            id: Date.now(), // 模拟生成ID
            name: deptForm.name,
            createTime: new Date().toISOString(),
            updateTime: new Date().toISOString()
          }
          departmentList.value.push(newDept)
        }
      } catch (error) {
        console.error('Error submitting form:', error)
        ElMessage.error('Network error, please try again later')
      }
    }
  })
}

// 处理删除
const handleDelete = (row) => {
  currentDept.value = row
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!currentDept.value) return
  
  try {
    // 实际环境中的API调用
    /*
    const response = await axios.delete(`${baseURL}/depts/${currentDept.value.id}`)
    if (response.data.code === 1) {
      ElMessage.success('Department deleted successfully')
      deleteDialogVisible.value = false
      fetchDepartments() // 刷新列表
    } else {
      ElMessage.error(response.data.msg || 'Delete failed')
    }
    */
    
    // 模拟成功响应，仅用于演示
    ElMessage.success('Department deleted successfully')
    deleteDialogVisible.value = false
    
    // 从列表中移除
    departmentList.value = departmentList.value.filter(item => item.id !== currentDept.value.id)
  } catch (error) {
    console.error('Error deleting department:', error)
    ElMessage.error('Network error, please try again later')
  }
}

// 重置表单
const resetForm = () => {
  if (deptFormRef.value) {
    deptFormRef.value.resetFields()
  }
  deptForm.id = ''
  deptForm.name = ''
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 页面加载时获取部门列表
onMounted(() => {
  fetchDepartments()
})
</script>

<style scoped>
.department-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.department-header {
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

.header-operations {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 