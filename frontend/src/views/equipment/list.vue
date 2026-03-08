<template>
  <div class="equipment-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="器材名称">
            <el-input v-model="queryForm.equipmentName" placeholder="请输入器材名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="器材类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable>
              <el-option label="有氧" value="有氧"></el-option>
              <el-option label="力量" value="力量"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增器材</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border :style="{ width: '100%' }" v-loading="loading">
        <el-table-column prop="equipmentId" label="器材 ID" width="80"></el-table-column>
        <el-table-column prop="equipmentName" label="器材名称" width="150"></el-table-column>
        <el-table-column prop="type" label="器材类型" width="80"></el-table-column>
        <el-table-column prop="brand" label="品牌" width="100"></el-table-column>
        <el-table-column prop="model" label="型号" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购买日期" width="120"></el-table-column>
        <el-table-column prop="location" label="位置" width="120"></el-table-column>
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>

      <div class="pagination">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="器材名称" prop="equipmentName">
          <el-input v-model="formData.equipmentName" placeholder="请输入器材名称"></el-input>
        </el-form-item>
        <el-form-item label="器材类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择" style="width: 100%">
            <el-option label="有氧" value="有氧"></el-option>
            <el-option label="力量" value="力量"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="formData.brand" placeholder="请输入品牌"></el-input>
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="formData.model" placeholder="请输入型号"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
            <el-option label="可用" value="可用"></el-option>
            <el-option label="维修中" value="维修中"></el-option>
            <el-option label="已报废" value="已报废"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="购买日期" prop="purchaseDate">
          <el-date-picker v-model="formData.purchaseDate" type="date" placeholder="选择日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入位置"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEquipmentPage, addEquipment, updateEquipment, deleteEquipment } from '@/api/equipment'

export default {
  name: 'EquipmentList',
  data() {
    return {
      queryForm: {
        equipmentName: '',
        type: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        equipmentId: null,
        equipmentName: '',
        type: '',
        brand: '',
        model: '',
        status: '可用',
        purchaseDate: '',
        location: '',
        description: ''
      },
      rules: {
        equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择器材类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getEquipmentPage({
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }).then(response => {
        this.tableData = response.data.records
        this.total = response.data.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.queryForm = {
        equipmentName: '',
        type: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增器材'
      this.formData = {
        equipmentId: null,
        equipmentName: '',
        type: '',
        brand: '',
        model: '',
        status: '可用',
        purchaseDate: '',
        location: '',
        description: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑器材'
      this.formData = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该器材吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEquipment(row.equipmentId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.equipmentId ? updateEquipment : addEquipment
          api(this.formData).then(() => {
            this.$message.success(this.formData.equipmentId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.getList()
          })
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
    },
    getStatusType(status) {
      const map = { '可用': 'success', '维修中': 'warning', '已报废': 'danger' }
      return map[status] || ''
    },
    getStatusText(status) {
      return status || '未知'
    }
  }
}
</script>

<style lang="scss" scoped>
.equipment-list-container {
  width: 100%;

  .toolbar {
    margin-bottom: 20px;
  }

  .table-toolbar {
    margin-bottom: 20px;
  }

  .table-wrapper {
    width: 100%;
    overflow-x: auto;
  }

  .el-table {
    min-width: 1000px;
  }

  .pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
