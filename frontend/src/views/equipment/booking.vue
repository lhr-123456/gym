<template>
  <div class="equipment-booking-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员ID">
            <el-input v-model.number="queryForm.memberId" placeholder="请输入会员ID" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增预约</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border v-loading="loading">
          <el-table-column prop="bookingId" label="预约ID" width="80"></el-table-column>
          <el-table-column prop="memberId" label="会员ID" width="80"></el-table-column>
          <el-table-column prop="equipmentId" label="器材ID" width="80"></el-table-column>
          <el-table-column prop="equipmentName" label="器材名称" width="150"></el-table-column>
          <el-table-column prop="bookingDate" label="预约日期" width="120"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="100"></el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'warning' : 'danger'">
                {{ scope.row.status === 0 ? '已预约' : scope.row.status === 1 ? '已取消' : '已完成' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150">
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
        <el-form-item label="会员ID" prop="memberId">
          <el-input v-model.number="formData.memberId" placeholder="请输入会员ID"></el-input>
        </el-form-item>
        <el-form-item label="器材ID" prop="equipmentId">
          <el-input v-model.number="formData.equipmentId" placeholder="请输入器材ID"></el-input>
        </el-form-item>
        <el-form-item label="预约日期" prop="bookingDate">
          <el-date-picker v-model="formData.bookingDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="formData.startTime" placeholder="选择时间" style="width: 100%"></el-time-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="formData.endTime" placeholder="选择时间" style="width: 100%"></el-time-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">已预约</el-radio>
            <el-radio :label="1">已取消</el-radio>
            <el-radio :label="2">已完成</el-radio>
          </el-radio-group>
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
import { getEquipmentBookingPage, addEquipmentBooking, updateEquipmentBooking, deleteEquipmentBooking } from '@/api/equipmentBooking'

export default {
  name: 'EquipmentBooking',
  data() {
    return {
      queryForm: {
        memberId: null
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        bookingId: null,
        memberId: null,
        equipmentId: null,
        equipmentName: '',
        bookingDate: '',
        startTime: '',
        endTime: '',
        status: 0
      },
      rules: {
        memberId: [{ required: true, message: '请输入会员ID', trigger: 'blur' }],
        equipmentId: [{ required: true, message: '请输入器材ID', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getEquipmentBookingPage({
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
      this.queryForm = { memberId: null }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增预约'
      this.formData = {
        bookingId: null,
        memberId: null,
        equipmentId: null,
        equipmentName: '',
        bookingDate: '',
        startTime: '',
        endTime: '',
        status: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑预约'
      this.formData = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEquipmentBooking(row.bookingId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.bookingId ? updateEquipmentBooking : addEquipmentBooking
          api(this.formData).then(() => {
            this.$message.success(this.formData.bookingId ? '更新成功' : '添加成功')
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
    }
  }
}
</script>

<style lang="scss" scoped>
.equipment-booking-container {
  .toolbar, .table-toolbar { margin-bottom: 20px; }
  .table-wrapper { width: 100%; overflow-x: auto; }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
