<template>
  <div class="coach-homework-container">
    <el-card>
      <div slot="header" class="card-header">
        <span><i class="el-icon-document-copy"></i> 课后作业管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">布置作业</el-button>
      </div>

      <!-- 筛选 -->
      <div class="filter-row">
        <el-select v-model="filterStatus" size="small" placeholder="状态筛选" clearable @change="fetchList" style="width: 140px">
          <el-option label="全部" value=""></el-option>
          <el-option label="未完成" :value="0"></el-option>
          <el-option label="已完成" :value="1"></el-option>
        </el-select>
        <el-select v-model="filterMemberId" size="small" placeholder="学员筛选" clearable filterable @change="fetchList" style="width: 160px">
          <el-option v-for="m in memberList" :key="m.memberId" :label="m.memberName" :value="m.memberId"></el-option>
        </el-select>
      </div>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="memberName" label="学员" width="120"></el-table-column>
        <el-table-column prop="title" label="作业标题" min-width="160"></el-table-column>
        <el-table-column prop="content" label="作业内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="targetDate" label="目标日期" width="110">
          <template slot-scope="scope">{{ scope.row.targetDate || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="mini">
              {{ scope.row.status === 1 ? '已完成' : '未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="completeTime" label="完成时间" width="160">
          <template slot-scope="scope">{{ scope.row.completeTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="coachRemark" label="教练备注" width="160" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 布置/编辑作业对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px">
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="选择学员" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择学员" filterable style="width: 100%">
            <el-option v-for="m in memberList" :key="m.memberId" :label="m.memberName" :value="m.memberId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="form.title" placeholder="如：今日训练内容" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="作业内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述作业内容" maxlength="500" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="目标日期" prop="targetDate">
          <el-date-picker v-model="form.targetDate" type="date" value-format="yyyy-MM-dd" placeholder="目标完成日期" style="width: 100%"></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getHomeworkPage, saveHomework, updateHomework, deleteHomework } from '@/api/coachHomework'
import { getCoachMembersList } from '@/api/coachDashboard'

export default {
  name: 'CoachHomework',
  data() {
    return {
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      filterStatus: '',
      filterMemberId: null,
      memberList: [],
      dialogVisible: false,
      dialogTitle: '布置作业',
      submitting: false,
      editingId: null,
      form: {
        memberId: null,
        title: '',
        content: '',
        targetDate: ''
      },
      rules: {
        memberId: [{ required: true, message: '请选择学员', trigger: 'change' }],
        title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['userId', 'coachId']),
    currentCoachId() {
      return this.coachId || this.userId
    }
  },
  created() {
    this.fetchMembers()
    this.fetchList()
  },
  methods: {
    fetchMembers() {
      getCoachMembersList().then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          const raw = res.data
          // 后端返回 List，不是 { records: [] }
          this.memberList = Array.isArray(raw) ? raw : []
        }
      }).catch(() => {
        this.memberList = []
      })
    },
    fetchList() {
      if (!this.currentCoachId) return
      this.loading = true
      const params = {
        coachId: this.currentCoachId,
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        memberId: this.filterMemberId || null,
        status: this.filterStatus !== '' ? this.filterStatus : null
      }
      getHomeworkPage(params).then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          const page = res.data || {}
          this.tableData = page.records || []
          this.total = page.total || 0
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handlePageChange(page) {
      this.pageNum = page
      this.fetchList()
    },
    handleAdd() {
      this.editingId = null
      this.dialogTitle = '布置作业'
      this.form = { memberId: null, title: '', content: '', targetDate: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleEdit(row) {
      this.editingId = row.id
      this.dialogTitle = '编辑作业'
      this.form = {
        memberId: row.memberId,
        title: row.title,
        content: row.content,
        targetDate: row.targetDate || ''
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        const data = {
          coachId: this.currentCoachId,
          memberId: this.form.memberId,
          title: this.form.title,
          content: this.form.content,
          targetDate: this.form.targetDate || null,
          status: 0
        }
        const action = this.editingId
          ? updateHomework({ ...data, id: this.editingId })
          : saveHomework(data)
        action.then(res => {
          if (res && (res.code === 200 || res.code === '200')) {
            this.$message.success(this.editingId ? '更新成功' : '布置成功')
            this.dialogVisible = false
            this.fetchList()
          } else {
            this.$message.error(res.message || '操作失败')
          }
          this.submitting = false
        }).catch(() => {
          this.submitting = false
        })
      })
    },
    handleDelete(row) {
      this.$confirm(`确认删除作业「${row.title}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteHomework(row.id).then(res => {
          if (res && (res.code === 200 || res.code === '200')) {
            this.$message.success('删除成功')
            this.fetchList()
          } else {
            this.$message.error(res.message || '删除失败')
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.coach-homework-container {
  .card-header {
    font-size: 16px; font-weight: bold;
    display: flex; justify-content: space-between; align-items: center;
    i { margin-right: 8px; }
  }
  .filter-row {
    display: flex; gap: 12px; margin-bottom: 16px;
  }
  .pagination { margin-top: 20px; text-align: right; }
}
</style>
