<template>
  <div class="profile-container">
    <el-card>
      <div class="profile-header">
        <h2>个人中心</h2>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="profileForm" label-width="100px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="用户类型">
              <el-input :value="getUserTypeText(profileForm.userType)" disabled></el-input>
            </el-form-item>
            <el-form-item label="会员ID">
              <el-input v-model="profileForm.memberId" disabled></el-input>
            </el-form-item>
            <el-form-item label="教练ID">
              <el-input v-model="profileForm.coachId" disabled></el-input>
            </el-form-item>
            <el-form-item label="状态">
              <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'">
                {{ profileForm.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-form-item>
            <el-form-item label="创建时间">
              <el-input :value="formatDate(profileForm.createTime)" disabled></el-input>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordLoading">修改密码</el-button>
              <el-button @click="handlePasswordReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getInfo, updatePassword } from '@/api/user'
import { mapGetters } from 'vuex'

export default {
  name: 'Profile',
  data() {
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      activeTab: 'info',
      profileForm: {
        username: '',
        userType: null,
        memberId: null,
        coachId: null,
        status: null,
        createTime: null
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      passwordLoading: false
    }
  },
  computed: {
    ...mapGetters(['username', 'userType'])
  },
  created() {
    this.fetchProfile()
  },
  methods: {
    // 获取用户信息
    fetchProfile() {
      getInfo().then(res => {
        if (res.code === 200) {
          this.profileForm = res.data || {}
        } else {
          this.$message.error(res.message || '获取用户信息失败')
        }
      }).catch(err => {
        this.$message.error('获取用户信息失败：' + err.message)
      })
    },

    // 切换标签页
    handleTabClick(tab) {
      console.log('切换到:', tab.name)
    },

    // 获取用户类型文本
    getUserTypeText(userType) {
      const typeMap = {
        1: '管理员',
        2: '教练',
        3: '会员'
      }
      return typeMap[userType] || '未知'
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      return d.toLocaleString()
    },

    // 提交修改密码
    handlePasswordSubmit() {
      this.$refs.passwordFormRef.validate(valid => {
        if (valid) {
          this.passwordLoading = true
          updatePassword({
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then(res => {
            if (res.code === 200) {
              this.$message.success('密码修改成功，请重新登录')
              this.handlePasswordReset()
              // 退出登录
              setTimeout(() => {
                this.$store.dispatch('user/logout')
                this.$router.push('/login')
              }, 1500)
            } else {
              this.$message.error(res.message || '修改密码失败')
            }
          }).catch(err => {
            this.$message.error('修改密码失败：' + err.message)
          }).finally(() => {
            this.passwordLoading = false
          })
        }
      })
    },

    // 重置密码表单
    handlePasswordReset() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$refs.passwordFormRef && this.$refs.passwordFormRef.resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  .profile-header {
    margin-bottom: 20px;
    h2 {
      margin: 0;
      color: #303133;
    }
  }

  .profile-form {
    max-width: 500px;
  }

  .password-form {
    max-width: 500px;
  }
}
</style>
