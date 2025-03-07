<template>
  <div class="position-container">
    <div class="position-header">
      <h2 class="page-title">Position Management</h2>
      <el-button type="primary" @click="showAddDialog">Add Position</el-button>
    </div>

    <!-- 职位列表 -->
    <el-table
      :data="positionList"
      style="width: 100%"
      border
      v-loading="loading"
      stripe
      size="large">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="Position Name" min-width="150" align="center" />
      <el-table-column prop="category" label="Position Category" width="150" align="center" />
      <el-table-column prop="description" label="Position Description" min-width="200" align="center" />
      <el-table-column label="Operations" width="150" fixed="right" align="center">
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
            @click="handleDelete(scope.row.id)"
            :icon="Delete"
            circle />
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 添加/编辑职位对话框 -->
    <el-dialog
      :title="isEdit ? 'Edit Position' : 'Add Position'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false">
      <el-form :model="positionForm" :rules="rules" ref="positionFormRef" label-width="100px">
        <el-form-item label="Position Name" prop="name">
          <el-input v-model="positionForm.name" placeholder="Please enter position name" />
        </el-form-item>
        
        <el-form-item label="Position Category" prop="category">
          <el-select v-model="positionForm.category" placeholder="Please select position category" style="width: 100%">
            <el-option label="Management" value="Management" />
            <el-option label="Technical" value="Technical" />
            <el-option label="Sales" value="Sales" />
            <el-option label="Marketing" value="Marketing" />
            <el-option label="HR" value="HR" />
            <el-option label="Finance" value="Finance" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Position Description" prop="description">
          <el-input
            v-model="positionForm.description"
            type="textarea"
            :rows="3"
            placeholder="Please enter position description"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">Confirm</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 职位列表数据
const positionList = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const positionFormRef = ref(null)

// 所有职位数据（用于模拟后端数据库）
const allPositionsData = ref([
  { id: 1, name: 'Intern', category: 'Management', description: 'Intern position' },
  { id: 2, name: 'Junior Employee', category: 'Management', description: 'Junior employee position' },
  { id: 3, name: 'Senior Employee', category: 'Management', description: 'Senior employee position' },
  { id: 4, name: 'Team Leader', category: 'Management', description: 'Team leader position' },
  { id: 5, name: 'Project Manager', category: 'Management', description: 'Project manager position' },
  { id: 6, name: 'Department Manager', category: 'Management', description: 'Department manager position' },
  { id: 7, name: 'Director', category: 'Management', description: 'Director position' },
  { id: 8, name: 'Vice President', category: 'Management', description: 'Vice president position' },
  { id: 9, name: 'CTO', category: 'Management', description: 'Chief Technology Officer position' },
  { id: 10, name: 'CEO', category: 'Management', description: 'Chief Executive Officer position' },
  { id: 11, name: 'Junior Software Engineer', category: 'Technical', description: 'Junior software engineer position' },
  { id: 12, name: 'Senior Software Engineer', category: 'Technical', description: 'Senior software engineer position' },
  { id: 13, name: 'Software Architect', category: 'Technical', description: 'Software architect position' },
  { id: 14, name: 'DevOps Engineer', category: 'Technical', description: 'DevOps engineer position' },
  { id: 15, name: 'Cloud Engineer', category: 'Technical', description: 'Cloud engineer position' },
  { id: 16, name: 'Data Scientist', category: 'Technical', description: 'Data scientist position' },
  { id: 17, name: 'AI Engineer', category: 'Technical', description: 'AI engineer position' },
  { id: 18, name: 'QA Engineer', category: 'Technical', description: 'QA engineer position' },
  { id: 19, name: 'UI/UX Designer', category: 'Technical', description: 'UI/UX designer position' },
  { id: 20, name: 'Product Manager', category: 'Technical', description: 'Product manager position' },
  { id: 21, name: 'Sales Representative', category: 'Sales', description: 'Sales representative position' },
  { id: 22, name: 'Sales Manager', category: 'Sales', description: 'Sales manager position' },
  { id: 23, name: 'Account Executive', category: 'Sales', description: 'Account executive position' },
  { id: 24, name: 'Business Development Manager', category: 'Sales', description: 'Business development manager position' },
  { id: 25, name: 'Customer Success Manager', category: 'Sales', description: 'Customer success manager position' },
  { id: 26, name: 'Marketing Specialist', category: 'Marketing', description: 'Marketing specialist position' },
  { id: 27, name: 'Marketing Manager', category: 'Marketing', description: 'Marketing manager position' },
  { id: 28, name: 'Content Writer', category: 'Marketing', description: 'Content writer position' },
  { id: 29, name: 'SEO Specialist', category: 'Marketing', description: 'SEO specialist position' },
  { id: 30, name: 'Social Media Manager', category: 'Marketing', description: 'Social media manager position' },
  { id: 31, name: 'HR Assistant', category: 'HR', description: 'HR assistant position' },
  { id: 32, name: 'HR Specialist', category: 'HR', description: 'HR specialist position' },
  { id: 33, name: 'HR Manager', category: 'HR', description: 'HR manager position' },
  { id: 34, name: 'Talent Acquisition Specialist', category: 'HR', description: 'Talent acquisition specialist position' },
  { id: 35, name: 'Training Manager', category: 'HR', description: 'Training manager position' },
  { id: 36, name: 'Accountant', category: 'Finance', description: 'Accountant position' },
  { id: 37, name: 'Financial Analyst', category: 'Finance', description: 'Financial analyst position' },
  { id: 38, name: 'Finance Manager', category: 'Finance', description: 'Finance manager position' },
  { id: 39, name: 'Auditor', category: 'Finance', description: 'Auditor position' },
  { id: 40, name: 'CFO', category: 'Finance', description: 'Chief Financial Officer position' }
])

// 分页相关
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 职位表单
const positionForm = reactive({
  id: null,
  name: '',
  category: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: 'Please enter position name', trigger: 'blur' },
    { min: 2, max: 50, message: 'Length should be between 2 to 50 characters', trigger: 'blur' }
  ],
  category: [
    { required: true, message: 'Please select position category', trigger: 'change' }
  ],
  description: [
    { required: true, message: 'Please enter position description', trigger: 'blur' },
    { max: 200, message: 'Length should not exceed 200 characters', trigger: 'blur' }
  ]
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchPositions()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  pagination.page = page
  fetchPositions()
}

// 获取职位列表
const fetchPositions = async () => {
  loading.value = true
  try {
    // 模拟从后端获取数据
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    const paginatedData = allPositionsData.value.slice(start, end)
    
    positionList.value = paginatedData
    pagination.total = allPositionsData.value.length
  } catch (error) {
    console.error('Failed to fetch position list:', error)
    ElMessage.error('Failed to fetch position list')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  resetForm()
  
  // 填充表单数据
  Object.assign(positionForm, {
    id: row.id,
    name: row.name,
    category: row.category,
    description: row.description
  })
  
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (positionFormRef.value) {
    positionFormRef.value.resetFields()
  }
  
  Object.assign(positionForm, {
    id: null,
    name: '',
    category: '',
    description: ''
  })
}

// 提交表单
const submitForm = async () => {
  if (!positionFormRef.value) return
  
  try {
    await positionFormRef.value.validate()
    
    submitLoading.value = true
    
    if (isEdit.value) {
      // 编辑现有职位
      const index = allPositionsData.value.findIndex(item => item.id === positionForm.id)
      if (index !== -1) {
        allPositionsData.value[index] = { ...positionForm }
        ElMessage.success('Edit successful')
      } else {
        throw new Error('Position not found')
      }
    } else {
      // 添加新职位
      const newId = Math.max(...allPositionsData.value.map(item => item.id)) + 1
      const newPosition = {
        id: newId,
        name: positionForm.name,
        category: positionForm.category,
        description: positionForm.description
      }
      allPositionsData.value.push(newPosition)
      ElMessage.success('Add successful')
    }
    
    dialogVisible.value = false
    fetchPositions()
  } catch (error) {
    console.error('Form submission failed:', error)
    ElMessage.error('Operation failed, please try again later')
  } finally {
    submitLoading.value = false
  }
}

// 处理删除
const handleDelete = (id) => {
  ElMessageBox.confirm(
    'Are you sure you want to delete this position? Deletion cannot be undone.',
    'Warning',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  ).then(async () => {
    try {
      loading.value = true
      
      // 从数据中删除
      const index = allPositionsData.value.findIndex(item => item.id === id)
      if (index !== -1) {
        allPositionsData.value.splice(index, 1)
        ElMessage.success('Delete successful')
      } else {
        throw new Error('Position not found')
      }
      
      fetchPositions()
    } catch (error) {
      console.error('Delete failed:', error)
      ElMessage.error('Delete failed, please try again later')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消删除
  })
}

// 导出职位数据，供其他组件使用
const getPositionsData = () => {
  return allPositionsData.value
}

// 暴露方法，供其他组件调用
defineExpose({
  getPositionsData
})

onMounted(() => {
  fetchPositions()
})
</script>

<style scoped>
.position-container {
  padding: 16px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.position-header {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 