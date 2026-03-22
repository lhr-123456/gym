<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo-container">
          <span v-show="!isCollapse" class="logo-title">健身房管理系统</span>
          <span v-show="isCollapse" class="logo-title-short">健身</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :router="true"
        >
          <!-- 管理员菜单 -->
          <template v-if="userType === 1">
            <el-menu-item index="/dashboard/index">
              <i class="el-icon-s-home"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-submenu index="/admin-member">
              <template slot="title">
                <i class="el-icon-user"></i>
                <span slot="title">会员管理</span>
              </template>
              <el-menu-item index="/admin-member/list">
                <i class="el-icon-list"></i>
                <span slot="title">会员列表</span>
              </el-menu-item>
              <el-menu-item index="/admin-member/level">
                <i class="el-icon-medal"></i>
                <span slot="title">会员等级</span>
              </el-menu-item>
              <el-menu-item index="/admin-member/card">
                <i class="el-icon-credit-card"></i>
                <span slot="title">会员卡管理</span>
              </el-menu-item>
              <el-menu-item index="/admin-member/signin">
                <i class="el-icon-calendar"></i>
                <span slot="title">会员签到</span>
              </el-menu-item>
              <el-menu-item index="/admin-member/bodyTest">
                <i class="el-icon-data-analysis"></i>
                <span slot="title">体测记录</span>
              </el-menu-item>
              <el-menu-item index="/admin-member/consumption">
                <i class="el-icon-shopping-cart-2"></i>
                <span slot="title">消费记录</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/coach">
              <template slot="title">
                <i class="el-icon-user-solid"></i>
                <span slot="title">教练管理</span>
              </template>
              <el-menu-item index="/coach/list">
                <i class="el-icon-list"></i>
                <span slot="title">教练列表</span>
              </el-menu-item>
              <el-menu-item index="/coach/certificate">
                <i class="el-icon-medal"></i>
                <span slot="title">教练证书</span>
              </el-menu-item>
              <el-menu-item index="/coach/specialty">
                <i class="el-icon-star-on"></i>
                <span slot="title">教练专长</span>
              </el-menu-item>
              <el-menu-item index="/coach/schedule">
                <i class="el-icon-date"></i>
                <span slot="title">教练排班</span>
              </el-menu-item>
              <el-menu-item index="/coach/shift">
                <i class="el-icon-sort"></i>
                <span slot="title">教练调班</span>
              </el-menu-item>
              <el-menu-item index="/coach/salary">
                <i class="el-icon-money"></i>
                <span slot="title">教练工资</span>
              </el-menu-item>
              <el-menu-item index="/coach/performance">
                <i class="el-icon-data-line"></i>
                <span slot="title">教练绩效</span>
              </el-menu-item>
              <el-menu-item index="/coach/review">
                <i class="el-icon-chat-dot-round"></i>
                <span slot="title">教练评价</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i>
                <span slot="title">课程管理</span>
              </template>
              <el-menu-item index="/course/list">
                <i class="el-icon-list"></i>
                <span slot="title">课程列表</span>
              </el-menu-item>
              <el-menu-item index="/course/category">
                <i class="el-icon-folder"></i>
                <span slot="title">课程分类</span>
              </el-menu-item>
              <el-menu-item index="/course/schedule">
                <i class="el-icon-date"></i>
                <span slot="title">课程排期</span>
              </el-menu-item>
              <el-menu-item index="/course/my">
                <i class="el-icon-list"></i>
                <span slot="title">我的课程</span>
              </el-menu-item>
              <el-menu-item index="/course/available">
                <i class="el-icon-list"></i>
                <span slot="title">可预约课程</span>
              </el-menu-item>
              <el-menu-item index="/course/booking">
                <i class="el-icon-date"></i>
                <span slot="title">课程预约</span>
              </el-menu-item>
              <el-menu-item index="/course/review">
                <i class="el-icon-chat-dot-round"></i>
                <span slot="title">课程评价</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/equipment">
              <template slot="title">
                <i class="el-icon-tools"></i>
                <span slot="title">器材管理</span>
              </template>
              <el-menu-item index="/equipment/list">
                <i class="el-icon-list"></i>
                <span slot="title">器材列表</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/points">
              <template slot="title">
                <i class="el-icon-shopping-bag-2"></i>
                <span slot="title">积分商城</span>
              </template>
              <el-menu-item index="/points/goods">
                <i class="el-icon-shopping-bag-2"></i>
                <span slot="title">商品管理</span>
              </el-menu-item>
            </el-submenu>
          </template>

          <!-- 教练菜单 -->
          <template v-else-if="userType === 2">
            <el-menu-item index="/coach-dashboard/index">
              <i class="el-icon-s-home"></i>
              <span slot="title">工作台</span>
            </el-menu-item>
            <el-menu-item index="/coach-schedule/list">
              <i class="el-icon-date"></i>
              <span slot="title">我的排课</span>
            </el-menu-item>
            <el-menu-item index="/coach-members/list">
              <i class="el-icon-user"></i>
              <span slot="title">我的学员</span>
            </el-menu-item>
            <el-menu-item index="/coach-reviews/list">
              <i class="el-icon-chat-dot-round"></i>
              <span slot="title">我的评价</span>
            </el-menu-item>
            <el-menu-item index="/coach-bodytest/add">
              <i class="el-icon-data-analysis"></i>
              <span slot="title">录入体测</span>
            </el-menu-item>
            <el-menu-item index="/coach-homework/list">
              <i class="el-icon-document-copy"></i>
              <span slot="title">布置作业</span>
            </el-menu-item>
            <el-menu-item index="/profile/index">
              <i class="el-icon-setting"></i>
              <span slot="title">个人中心</span>
            </el-menu-item>
          </template>

          <!-- 会员菜单 -->
          <template v-else-if="userType === 3">
            <el-menu-item index="/member/home">
              <i class="el-icon-s-home"></i>
              <span slot="title">会员首页</span>
            </el-menu-item>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i>
                <span slot="title">课程预约</span>
              </template>
              <el-menu-item index="/course/available">
                <i class="el-icon-search"></i>
                <span slot="title">可预约课程</span>
              </el-menu-item>
              <el-menu-item index="/course/booking">
                <i class="el-icon-tickets"></i>
                <span slot="title">我的预约</span>
              </el-menu-item>
            </el-submenu>
            <el-menu-item index="/member/bodytest">
              <i class="el-icon-data-analysis"></i>
              <span slot="title">体测记录</span>
            </el-menu-item>
            <el-menu-item index="/member/contactCoach">
              <i class="el-icon-chat-line-round"></i>
              <span slot="title">联系教练</span>
            </el-menu-item>
            <el-menu-item index="/member/sportData">
              <i class="el-icon-data-line"></i>
              <span slot="title">运动数据</span>
            </el-menu-item>
            <el-menu-item index="/member/points">
              <i class="el-icon-shopping-bag-2"></i>
              <span slot="title">积分商城</span>
            </el-menu-item>
            <el-menu-item index="/member/messages">
              <i class="el-icon-bell"></i>
              <span slot="title">消息中心</span>
            </el-menu-item>
            <el-menu-item index="/member/homework">
              <i class="el-icon-document-copy"></i>
              <span slot="title">我的作业</span>
            </el-menu-item>
            <el-menu-item index="/member/profile">
              <i class="el-icon-user"></i>
              <span slot="title">个人中心</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <i class="el-icon-s-fold" @click="isCollapse = !isCollapse"></i>
          </div>
          <div class="header-right">
            <span class="username">{{ username }}</span>
            <el-dropdown @command="handleCommand">
              <i class="el-icon-setting"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapGetters(['username']),
    userType() {
      return this.$store.getters.userType || 1
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
          })
        })
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;

  .el-container {
    flex: 1;
    height: 100%;
  }

  .sidebar {
    background-color: #304156;
    transition: width 0.28s;
    height: 100%;

    .logo-container {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #2b3a4b;

      .logo-title {
        color: #fff;
        font-size: 18px;
        font-weight: bold;
      }

      .logo-title-short {
        color: #fff;
        font-size: 18px;
        font-weight: bold;
      }
    }

    .el-menu {
      border-right: none;
    }
  }

  .header {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-left {
      i {
        font-size: 20px;
        cursor: pointer;
      }
    }

    .header-right {
      display: flex;
      align-items: center;

      .username {
        margin-right: 15px;
        font-size: 14px;
      }

      i {
        font-size: 20px;
        cursor: pointer;
      }
    }
  }

  .main-content {
    background-color: #f0f2f5;
    padding: 20px;
    overflow-y: auto;
    height: calc(100vh - 60px);
  }
}
</style>
