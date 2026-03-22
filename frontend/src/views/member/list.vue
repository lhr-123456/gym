<template>
  <div class="member-list-container">
    <el-card>
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="会员姓名">
            <el-input v-model="queryForm.memberName" placeholder="请输入会员姓名" clearable></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="queryForm.phoneNum" placeholder="请输入手机号" clearable></el-input>
          </el-form-item>
          <el-form-item label="健身水平">
            <el-select v-model="queryForm.fitnessLevel" placeholder="请选择" clearable>
              <el-option label="初级" value="初级"></el-option>
              <el-option label="中级" value="中级"></el-option>
              <el-option label="高级" value="高级"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增会员</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="tableData" border :style="{ width: '100%' }" v-loading="loading">
        <el-table-column prop="memberId" label="会员 ID" width="80"></el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="100"></el-table-column>
        <el-table-column prop="gender" label="性别" width="60">
          <template slot-scope="scope">
            <span>{{ scope.row.gender === 'M' ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phoneNum" label="手机号" width="120"></el-table-column>
        <el-table-column prop="coachId" label="负责教练" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.coachName">{{ scope.row.coachName }}</span>
            <el-tag v-else size="mini" type="info">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fitnessLevel" label="健身水平" width="80"></el-table-column>
        <el-table-column prop="memberLevel" label="会员等级" width="80">
          <template slot-scope="scope">
            <span>{{ getMemberLevelText(scope.row.memberLevel) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="80"></el-table-column>
        <el-table-column prop="balance" label="余额" width="80"></el-table-column>
        <el-table-column prop="accountStatus" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.accountStatus === 0 ? 'success' : 'danger'">
              {{ scope.row.accountStatus === 0 ? '正常' : '冻结' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="320">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="primary" @click="handleAdjustPoints(scope.row)">调整积分</el-button>
            <el-button size="mini" type="warning" @click="handleAssignCoach(scope.row)">分配教练</el-button>
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
        <el-form-item label="会员姓名" prop="memberName">
          <el-input v-model="formData.memberName" placeholder="请输入会员姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="formData.birthDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNum">
          <el-input v-model="formData.phoneNum" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="emailAddr">
          <el-input v-model="formData.emailAddr" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="健身水平" prop="fitnessLevel">
          <el-select v-model="formData.fitnessLevel" placeholder="请选择" style="width: 100%">
            <el-option label="初级" value="初级"></el-option>
            <el-option label="中级" value="中级"></el-option>
            <el-option label="高级" value="高级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="formData.memberLevel" placeholder="请选择" style="width: 100%">
            <el-option label="普通会员" :value="1"></el-option>
            <el-option label="银卡会员" :value="2"></el-option>
            <el-option label="金卡会员" :value="3"></el-option>
            <el-option label="钻石会员" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="账户状态" prop="accountStatus">
          <el-radio-group v-model="formData.accountStatus">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">冻结</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 积分调整对话框 -->
    <el-dialog :title="pointsDialogTitle" :visible.sync="pointsDialogVisible" width="500px">
      <el-form ref="pointsForm" :model="pointsForm" :rules="pointsRules" label-width="100px">
        <el-form-item label="当前积分">
          <el-input v-model="currentPoints" disabled></el-input>
        </el-form-item>
        <el-form-item label="积分变化" prop="pointsChange">
          <el-input-number
            v-model="pointsForm.pointsChange"
            :min="-currentPoints"
            :step="10"
            placeholder="正数为增加，负数为减少"
            @change="calculateAfterPoints">
          </el-input-number>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="pointsForm.reason"
            type="textarea"
            placeholder="请输入积分调整原因">
          </el-input>
        </el-form-item>
        <el-form-item label="调整后积分" prop="afterPoints">
          <el-input v-model="afterPoints" disabled></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPoints">确定</el-button>
      </div>
    </el-dialog>

    <!-- 分配教练对话框 -->
    <el-dialog title="分配教练" :visible.sync="coachDialogVisible" width="500px">
      <el-form ref="coachForm" :model="coachForm" label-width="100px">
        <el-form-item label="会员">
          <el-input :value="currentMemberForCoach ? currentMemberForCoach.memberName : ''" disabled></el-input>
        </el-form-item>
        <el-form-item label="选择教练" prop="coachId">
          <el-select v-model="coachForm.coachId" placeholder="请选择教练" filterable clearable style="width: 100%">
            <el-option v-for="c in coachList" :key="c.coachId" :label="c.coachName" :value="c.coachId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="coachDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitCoach">确定分配</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMemberPage, addMember, updateMember, deleteMember, adjustPoints, assignCoach } from '@/api/member'
import { getCoachList } from '@/api/coach'

export default {
  name: 'MemberList',
  data() {
    return {
      queryForm: {
        memberName: '',
        phoneNum: '',
        fitnessLevel: ''
      },
      tableData: [],
      loading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        memberId: null,
        memberName: '',
        gender: 'M',
        birthDate: '',
        phoneNum: '',
        emailAddr: '',
        fitnessLevel: '初级',
        memberLevel: 1,
        accountStatus: 0
      },
      rules: {
        memberName: [{ required: true, message: '请输入会员姓名', trigger: 'blur' }],
        phoneNum: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
      },
      // 积分调整相关
      pointsDialogVisible: false,
      pointsDialogTitle: '',
      currentMember: null,
      currentPoints: 0,
      afterPoints: 0,
      pointsForm: {
        pointsChange: 0,
        reason: ''
      },
      pointsRules: {
        pointsChange: [{ required: true, message: '请输入积分变化量', trigger: ['blur', 'change'] }],
        reason: [{ required: true, message: '请输入调整原因', trigger: ['blur', 'change'], min: 2 }]
      },
      // 分配教练
      coachDialogVisible: false,
      currentMemberForCoach: null,
      coachForm: {
        coachId: null
      },
      coachList: []
    }
  },
  created() {
    this.getList()
    this.fetchCoachList()
  },
  methods: {
    fetchCoachList() {
      getCoachList({ pageNum: 1, pageSize: 200 }).then(res => {
        if (res && res.code === 200 && res.data) {
          const page = res.data
          this.coachList = Array.isArray(page) ? page : (page.records || [])
        }
      }).catch(() => {})
    },
    getList() {
      this.loading = true
      getMemberPage({
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
        memberName: '',
        phoneNum: '',
        fitnessLevel: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.dialogTitle = '新增会员'
      this.formData = {
        memberId: null,
        memberName: '',
        gender: 'M',
        birthDate: '',
        phoneNum: '',
        emailAddr: '',
        fitnessLevel: '初级',
        memberLevel: 1,
        accountStatus: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑会员'
      this.formData = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该会员吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMember(row.memberId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          const api = this.formData.memberId ? updateMember : addMember
          api(this.formData).then(() => {
            this.$message.success(this.formData.memberId ? '更新成功' : '添加成功')
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
    // 积分调整相关方法
    handleAdjustPoints(row) {
      this.pointsDialogTitle = '调整会员积分'
      this.currentMember = row
      this.currentPoints = row.points || 0
      this.pointsForm = {
        pointsChange: 0,
        reason: ''
      }
      this.afterPoints = this.currentPoints
      this.pointsDialogVisible = true
      this.$nextTick(() => {
        this.$refs.pointsForm.clearValidate()
      })
    },
    handleSubmitPoints() {
      this.$refs.pointsForm.validate(valid => {
        if (valid) {
          adjustPoints(this.currentMember.memberId, this.pointsForm).then(response => {
            this.$message.success(response.message || '积分调整成功')
            this.pointsDialogVisible = false
            this.getList()
          }).catch(error => {
            this.$message.error('积分调整失败：' + (error.response?.data?.message || error.message))
          })
        }
      })
    },
    // 计算调整后积分
    calculateAfterPoints() {
      const change = parseInt(this.pointsForm.pointsChange) || 0
      this.afterPoints = this.currentPoints + change
    },
    handleAssignCoach(row) {
      this.currentMemberForCoach = row
      this.coachForm.coachId = row.coachId || null
      this.coachDialogVisible = true
      this.$nextTick(() => {
        this.$refs.coachForm && this.$refs.coachForm.clearValidate()
      })
    },
    handleSubmitCoach() {
      assignCoach({
        memberId: this.currentMemberForCoach.memberId,
        coachId: this.coachForm.coachId
      }).then(res => {
        if (res && (res.code === 200 || res.code === '200')) {
          this.$message.success('教练分配成功')
          this.coachDialogVisible = false
          this.getList()
        } else {
          this.$message.error(res.message || '分配失败')
        }
      })
    },
    getMemberLevelText(level) {
      const map = { 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }
      return map[level] || '普通会员'
    }
  },
  watch: {
    'pointsForm.pointsChange'(newValue) {
      this.calculateAfterPoints()
    }
  }
}
</script>

<style lang="scss" scoped>
.member-list-container {
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
