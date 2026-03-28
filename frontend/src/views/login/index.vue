<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="bg-animation">
      <div class="spot spot1"></div>
      <div class="spot spot2"></div>
      <div class="spot spot3"></div>
    </div>

    <div class="login-box">
      <div class="form-container">
          <!-- 品牌标题 -->
          <div class="brand">
            <div class="brand-title">GYMSPACE</div>
            <div class="brand-sub">Fitness Management</div>
          </div>

          <!-- 切换标签 -->
          <div class="tabs">
            <div
              class="tab-item"
              :class="{ active: activeTab === 'login' }"
              @click="activeTab = 'login'"
            >
              登录
            </div>
            <div
              class="tab-item"
              :class="{ active: activeTab === 'register' }"
              @click="activeTab = 'register'"
            >
              注册
            </div>
          </div>

          <!-- 登录表单 -->
          <el-form
            v-show="activeTab === 'login'"
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            auto-complete="on"
            label-position="top"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>

            <el-form-item prop="userType">
              <el-radio-group v-model="loginForm.userType" class="user-type-group">
                <el-radio-button :label="1">
                  <i class="el-icon-s-custom"></i> 管理员
                </el-radio-button>
                <el-radio-button :label="2">
                  <i class="el-icon-user"></i> 教练
                </el-radio-button>
                <el-radio-button :label="3">
                  <i class="el-icon-sunny"></i> 会员
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-button
              :loading="loading"
              type="primary"
              size="large"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-show="activeTab === 'register'"
            ref="registerForm"
            :model="registerForm"
            :rules="registerRules"
            class="login-form"
            auto-complete="on"
            label-position="top"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
              ></el-input>
            </el-form-item>

            <el-form-item prop="userType">
              <el-radio-group v-model="registerForm.userType" class="user-type-group">
                <el-radio-button :label="2">
                  <i class="el-icon-user"></i> 教练
                </el-radio-button>
                <el-radio-button :label="3">
                  <i class="el-icon-sunny"></i> 会员
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-button
              :loading="registerLoading"
              type="primary"
              size="large"
              class="login-btn"
              @click.native.prevent="handleRegister"
            >
              {{ registerLoading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Login',
  data() {
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      activeTab: 'login',
      loginForm: {
        username: '',
        password: '',
        userType: 1
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
        userType: [{ required: true, trigger: 'change', message: '请选择用户类型' }]
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        userType: 3
      },
      registerRules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入用户名' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: 'blur', message: '请输入密码' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        userType: [{ required: true, trigger: 'change', message: '请选择用户类型' }]
      },
      loading: false,
      registerLoading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              const ut = Number(this.loginForm.userType)
              let path = '/dashboard/index'
              if (ut === 2) path = '/coach-dashboard/index'
              else if (ut === 3) path = '/member/home'
              const redir = this.$route.query.redirect
              if (redir && typeof redir === 'string') {
                const r = decodeURIComponent(redir).replace(/^#\/?/, '/')
                // 教练/会员不允许通过 redirect 落到管理端首页
                if (ut === 1) {
                  path = r
                } else if (!/^\/dashboard(\/index)?$/.test(r) && !/^\/dashboard$/.test(r)) {
                  path = r
                }
              }
              this.$router.push({ path })
              this.loading = false
            })
            .catch((err) => {
              this.$message.error(err.message || '登录失败')
              this.loading = false
            })
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.registerLoading = true
          register({
            username: this.registerForm.username,
            password: this.registerForm.password,
            userType: this.registerForm.userType
          })
            .then(res => {
              if (res.code === 200) {
                this.$message.success('注册成功，请登录')
                this.activeTab = 'login'
                this.loginForm.username = this.registerForm.username
              } else {
                this.$message.error(res.message || '注册失败')
              }
            })
            .catch(err => {
              this.$message.error(err.message || '注册失败')
            })
            .finally(() => {
              this.registerLoading = false
            })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  // 图片加载前的底色，避免闪白
  background-color: #1a1d24;

  // 全屏铺满（cover）；居中定位保证主体（墙面「健身」区块）始终在视口中央
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-color: #1a1d24;
    background-image: url('D:\\gympicture\\bjt.jpg');
    background-repeat: no-repeat;
    background-position: center center;
    background-size: cover;
    z-index: 0;
  }

  // 深色渐变叠加层：保护表单可读性
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(
      160deg,
      rgba(10, 10, 20, 0.55) 0%,
      rgba(30, 20, 50, 0.45) 50%,
      rgba(5, 15, 30, 0.60) 100%
    );
    z-index: 0;
  }

  // 悬浮光斑装饰
  .bg-animation {
    position: absolute;
    inset: 0;
    z-index: 0;
    overflow: hidden;
    pointer-events: none;

    .spot {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);

      &.spot1 {
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.35) 0%, transparent 70%);
        top: -150px;
        left: -100px;
        animation: drift 18s ease-in-out infinite;
      }

      &.spot2 {
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, rgba(236, 72, 153, 0.30) 0%, transparent 70%);
        bottom: -120px;
        right: -80px;
        animation: drift 14s ease-in-out infinite reverse;
      }

      &.spot3 {
        width: 300px;
        height: 300px;
        background: radial-gradient(circle, rgba(56, 189, 248, 0.25) 0%, transparent 70%);
        top: 50%;
        left: 55%;
        transform: translate(-50%, -50%);
        animation: breathe 12s ease-in-out infinite;
      }
    }
  }

  @keyframes drift {
    0%, 100% { transform: translate(0, 0); }
    50%       { transform: translate(40px, 30px); }
  }

  @keyframes breathe {
    0%, 100% { transform: translate(-50%, -50%) scale(1);   opacity: 0.8; }
    50%       { transform: translate(-50%, -50%) scale(1.3); opacity: 1; }
  }

  // 登录卡片：高级毛玻璃
  .login-box {
    position: relative;
    z-index: 1;
    width: 440px;
    padding: 48px 44px;
    background: rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(28px) saturate(160%);
    -webkit-backdrop-filter: blur(28px) saturate(160%);
    border: 1px solid rgba(255, 255, 255, 0.18);
    border-radius: 28px;
    box-shadow:
      0 24px 64px rgba(0, 0, 0, 0.35),
      0 0 0 1px rgba(255, 255, 255, 0.05) inset;
    animation: cardIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;

    @keyframes cardIn {
      from { opacity: 0; transform: translateY(24px) scale(0.97); }
      to   { opacity: 1; transform: translateY(0)    scale(1); }
    }

    .form-container {
      width: 100%;

      // 标题区
      .brand {
        text-align: center;
        margin-bottom: 36px;

        .brand-title {
          font-size: 26px;
          font-weight: 700;
          color: #fff;
          letter-spacing: 3px;
          margin: 0 0 6px;
          background: linear-gradient(90deg, #a8b4fd 0%, #f0abfc 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .brand-sub {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.45);
          letter-spacing: 4px;
          text-transform: uppercase;
        }
      }

      // 登录/注册切换标签
      .tabs {
        display: flex;
        margin-bottom: 32px;
        border-radius: 12px;
        background: rgba(255, 255, 255, 0.07);
        padding: 4px;
        gap: 4px;

        .tab-item {
          flex: 1;
          text-align: center;
          padding: 10px 0;
          font-size: 14px;
          font-weight: 500;
          color: rgba(255, 255, 255, 0.5);
          cursor: pointer;
          border-radius: 10px;
          transition: all 0.3s;
          letter-spacing: 1px;

          &.active {
            background: rgba(255, 255, 255, 0.15);
            color: #fff;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
            font-weight: 600;
          }

          &:hover:not(.active) {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }

      // 表单
      .login-form {
        .user-type-group {
          width: 100%;
          display: flex;
          gap: 8px;

          ::v-deep .el-radio-button {
            flex: 1;

            .el-radio-button__inner {
              width: 100%;
              padding: 10px 4px;
              font-size: 13px;
              background: rgba(255, 255, 255, 0.07);
              border: 1px solid rgba(255, 255, 255, 0.14);
              border-radius: 10px;
              color: rgba(255, 255, 255, 0.65);
              box-shadow: none !important;
              transition: all 0.25s;

              &:hover {
                background: rgba(255, 255, 255, 0.12);
                color: #fff;
              }
            }

            &.is-active .el-radio-button__inner {
              background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(168, 85, 247, 0.9) 100%);
              border-color: transparent;
              color: #fff;
              box-shadow: 0 4px 16px rgba(102, 126, 234, 0.45) !important;
            }
          }
        }

        // 输入框
        ::v-deep .el-form-item {
          margin-bottom: 20px;
        }

        ::v-deep .el-input {
          .el-input__inner {
            background: rgba(255, 255, 255, 0.10);
            border: 1px solid rgba(255, 255, 255, 0.18);
            color: #fff;
            border-radius: 12px;
            font-size: 14px;
            height: 44px;
            transition: all 0.3s;
            padding-left: 40px;

            &::placeholder {
              color: rgba(255, 255, 255, 0.40);
            }

            &:hover {
              border-color: rgba(255, 255, 255, 0.35);
              background: rgba(255, 255, 255, 0.14);
            }

            &:focus {
              border-color: rgba(102, 126, 234, 0.8);
              background: rgba(255, 255, 255, 0.14);
              box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.20);
            }
          }

          .el-input__prefix {
            left: 12px;
          }

          .el-input__icon {
            color: rgba(255, 255, 255, 0.55);
            font-size: 16px;
          }
        }

        // 提交按钮
        .login-btn {
          width: 100%;
          margin-top: 8px;
          height: 48px;
          font-size: 15px;
          font-weight: 600;
          letter-spacing: 2px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;
          border-radius: 12px;
          color: #fff;
          cursor: pointer;
          transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
          margin-bottom: 16px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 14px 36px rgba(102, 126, 234, 0.50);
            background: linear-gradient(135deg, #7b8ff5 0%, #8d5dc0 100%);
          }

          &:active {
            transform: translateY(0);
            box-shadow: 0 6px 18px rgba(102, 126, 234, 0.35);
          }

          &:disabled {
            opacity: 0.7;
            cursor: not-allowed;
            transform: none;
          }
        }
      }
    }
  }
}

// 响应式
@media (max-width: 500px) {
  .login-container .login-box {
    width: calc(100% - 32px);
    padding: 36px 24px;
    border-radius: 20px;
  }
}
</style>
