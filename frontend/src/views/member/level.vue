<template>
  <div class="member-level-container">
    <el-card>
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增等级</el-button>
        <el-button type="success" icon="el-icon-refresh" @click="initLevels">初始化默认等级</el-button>
      </div>

      <!-- 等级卡片展示 -->
      <el-row :gutter="20" class="level-cards">
        <el-col :span="6" v-for="item in tableData" :key="item.levelId">
          <el-card class="level-card" shadow="hover">
            <div class="level-header" :style="{ background: item.iconColor }">
              <i :class="'el-icon-' + item.icon"></i>
            </div>
            <div class="level-body">
              <h3>{{ item.levelName }}</h3>
              <p class="level-code">{{ item.levelCode }}</p>
              <div class="level-info">
                <div class="info-item">
                  <span class="label">折扣：</span>
                  <span class="value">{{ (item.discountRate * 10).toFixed(1) }}折</span>
                </div>
                <div class="info-item">
                  <span class="label">积分倍率：</span>
                  <span class="value">{{ item.pointsRate }}倍</span>
                </div>
                <div class="info-item">
                  <span class="label">积分范围：</span>
                  <span class="value">{{ item.minPoints }} - {{ item.maxPoints }}</span>
                </div>
                <div class="info-item">
                  <span class="label">开卡费：</span>
                  <span class="value">¥{{ item.cardFee }}</span>
                </div>
              </div>
              <p class="description">{{ item.description }}</p>
              <div class="level-actions">
                <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEdit(item)">编辑</el-button>
                <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(item)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 新增/编辑对话框 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="等级名称" prop="levelName">
            <el-input v-model="form.levelName" placeholder="请输入等级名称"></el-input>
          </el-form-item>
          <el-form-item label="等级编码" prop="levelCode">
            <el-input v-model="form.levelCode" placeholder="请输入等级编码"></el-input>
          </el-form-item>
          <el-form-item label="等级排序" prop="levelOrder">
            <el-input-number v-model="form.levelOrder" :min="1"></el-input-number>
          </el-form-item>
          <el-form-item label="折扣率" prop="discountRate">
            <el-input-number v-model="form.discountRate" :min="0" :max="1" :step="0.05" :precision="2"></el-input-number>
            <span style="margin-left: 10px">{{ (form.discountRate * 10).toFixed(1) }}折</span>
          </el-form-item>
          <el-form-item label="积分倍率" prop="pointsRate">
            <el-input-number v-model="form.pointsRate" :min="0.1" :max="10" :step="0.1" :precision="1"></el-input-number>
            <span style="margin-left: 10员"></span>
          </el-form-item>
          <el-form-item label="最低积分" prop="minPoints">
            <el-input-number v-model="form.minPoints" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="最高积分" prop="maxPoints">
            <el-input-number v-model="form.maxPoints" :min="0"></el-input-number>
          </el-form-item>
          <el-form-item label="开卡费" prop="cardFee">
            <el-input-number v-model="form.cardFee" :min="0" :precision="2"></el-input-number>
          </el-form-item>
          <el-form-item label="图标颜色" prop="iconColor">
            <el-color-picker v-model="form.iconColor"></el-color-picker>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="3"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getMemberLevelList, addMemberLevel, updateMemberLevel, deleteMemberLevel, initMemberLevel } from '@/api/memberLevel'

export default {
  name: 'MemberLevel',
  data() {
    return {
      tableData: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        levelId: null,
        levelName: '',
        levelCode: '',
        levelOrder: 1,
        discountRate: 1,
        pointsRate: 1,
        minPoints: 0,
        maxPoints: 0,
        cardFee: 0,
        icon: 'medal',
        iconColor: '#409EFF',
        description: ''
      },
      rules: {
        levelName: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
        levelCode: [{ required: true, message: '请输入等级编码', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      getMemberLevelList().then(res => {
        if (res.code === 200) {
          this.tableData = res.data || []
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleAdd() {
      this.form = {
        levelId: null,
        levelName: '',
        levelCode: '',
        levelOrder: 1,
        discountRate: 1,
        pointsRate: 1,
        minPoints: 0,
        maxPoints: 0,
        cardFee: 0,
        icon: 'medal',
        iconColor: '#409EFF',
        description: ''
      }
      this.dialogTitle = '新增会员等级'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑会员等级'
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该会员等级吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMemberLevel(row.levelId).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
          }
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const action = this.form.levelId ? updateMemberLevel : addMemberLevel
          action(this.form).then(res => {
            if (res.code === 200) {
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.fetchData()
            }
          })
        }
      })
    },
    initLevels() {
      this.$confirm('将初始化4个默认会员等级（青铜、白银、黄金、钻石），是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        initMemberLevel().then(res => {
          if (res.code === 200) {
            this.$message.success('初始化成功')
            this.fetchData()
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.member-level-container {
  .toolbar {
    margin-bottom: 20px;
  }

  .level-cards {
    .level-card {
      margin-bottom: 20px;
      text-align: center;

      .level-header {
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: -20px -20px 20px -20px;
        border-radius: 4px 4px 0 0;

        i {
          font-size: 40px;
          color: #fff;
        }
      }

      .level-body {
        h3 {
          margin: 0 0 5px 0;
          font-size: 20px;
        }

        .level-code {
          color: #999;
          font-size: 12px;
          margin-bottom: 15px;
        }

        .level-info {
          text-align: left;
          margin-bottom: 15px;

          .info-item {
            display: flex;
            justify-content: space-between;
            padding: 5px 0;
            border-bottom: 1px dashed #eee;

            .label {
              color: #999;
            }

            .value {
              font-weight: bold;
              color: #333;
            }
          }
        }

        .description {
          color: #666;
          font-size: 12px;
          margin-bottom: 15px;
        }

        .level-actions {
          display: flex;
          justify-content: center;
          gap: 10px;
        }
      }
    }
  }
}
</style>
