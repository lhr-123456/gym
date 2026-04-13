# 健身房管理系统 - Gym Management System

## 项目技术文档

> **项目类型**：Spring Boot + Vue 全栈管理系统
> **作者**：待填写
> **指导老师**：待填写
> **完成日期**：2026年3月

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术架构](#2-技术架构)
3. [数据库设计](#3-数据库设计)
4. [功能模块详解](#4-功能模块详解)
5. [核心业务逻辑](#5-核心业务逻辑)
6. [接口设计](#6-接口设计)
7. [前端实现](#7-前端实现)
8. [系统亮点与创新](#8-系统亮点与创新)
9. [部署说明](#9-部署说明)
10. [使用指南](#10-使用指南)

---

## 1. 项目概述

### 1.1 项目背景

随着全民健身意识的不断增强，健身房作为体育产业的重要载体，其管理效率和用户体验直接决定了场馆的竞争力。传统健身房普遍依赖纸质记录或简单Excel表格进行管理，存在以下问题：

- **会员管理混乱**：会员信息、卡种、签到记录分散存储，查找困难
- **课程预约效率低**：人工排课、人工确认预约，人力成本高且易出错
- **教练管理不透明**：教练排班、绩效、工资计算缺乏系统化管理
- **数据孤岛**：会员消费、体测数据、积分兑换等信息无法打通
- **用户体验差**：会员无法在线预约课程、查看积分、联系教练

本系统旨在构建一套**完整的健身房数字化管理平台**，实现会员、教练、管理员三方协同的智能化运营。

### 1.2 项目目标

本项目旨在开发一套功能完善、界面友好、安全可靠的健身房管理系统，主要实现以下目标：

1. **会员管理数字化**：在线注册、会员卡管理、签到积分、体测记录全流程线上化
2. **课程管理智能化**：在线排课、预约、签到、满员自动管控
3. **教练管理规范化**：证书管理、排班调班、绩效评估、工资结算系统化
4. **积分商城活跃化**：签到积分、消费积分、课程兑换、实物兑换增强会员粘性
5. **数据分析可视化**：多维度数据统计，支持管理决策

### 1.3 用户角色

本系统设计三种用户角色，各角色拥有独立门户：

| 角色 | 说明 | 主要功能 |
|------|------|----------|
| **管理员 (Admin)** | 健身房经营者/前台工作人员 | 系统管理、会员管理、教练管理、课程管理、器材管理、积分商城管理、数据统计 |
| **教练 (Coach)** | 签约健身教练 | 我的排课、我的学员、我的评价、体测录入、个人中心 |
| **会员 (Member)** | 健身房注册用户 | 课程预约、积分商城、签到记录、体测记录、个人中心 |

### 1.4 功能需求对照表

| 需求编号 | 需求名称 | 功能描述 | 优先级 |
|---------|---------|---------|--------|
| F001 | 用户认证 | 支持管理员、教练、会员三种角色的注册和登录，JWT无状态认证 | P0 |
| F002 | 会员管理 | 会员增删改查、分页、等级管理、积分调整 | P0 |
| F003 | 会员卡管理 | 开卡、充值、挂失、补办、次数核销 | P0 |
| F004 | 会员签到 | 每日签到、连续签到天数统计、自动积分奖励 | P0 |
| F005 | 体测管理 | 体测数据录入、自动计算BMI和健康评分、历史记录查看 | P1 |
| F006 | 消费记录 | 消费流水记录、订单号自动生成、积分同步 | P1 |
| F007 | 课程管理 | 课程增删改查、分类管理、教练分配、容量控制 | P0 |
| F008 | 课程预约 | 会员在线预约、自动校验容量、满员自动标记 | P0 |
| F009 | 课程评价 | 课程评分、内容评价、教练回复 | P1 |
| F010 | 教练管理 | 教练信息管理、证书管理、专长标签 | P0 |
| F011 | 教练排班 | 排课、调班申请与审批、课时统计 | P1 |
| F012 | 教练绩效 | 月度绩效评分（出勤、教学、服务、销售多维度） | P1 |
| F013 | 教练工资 | 基本工资+课时费+销售提成+奖金-罚款 | P1 |
| F014 | 器材管理 | 器材信息管理、借用预约、归还 | P2 |
| F015 | 积分商城 | 积分商品管理上下架、实物兑换、课程兑换 | P1 |
| F016 | 数据统计 | 会员/教练/课程/器材总量看板 | P1 |

---

## 2. 技术架构

### 2.1 整体架构

本系统采用**前后端分离**架构（RESTful API + SPA），分为表现层、业务逻辑层、数据访问层三层结构。

```
┌─────────────────────────────────────────────────────────┐
│                    前端表现层 (Vue.js)                     │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐          │
│  │ 管理员  │ │  教练   │ │  会员   │ │  登录   │          │
│  │  门户   │ │  门户   │ │  门户   │ │  注册   │          │
│  └────┬────┘ └────┬────┘ └────┬────┘ └─────────┘          │
│       └───────────┴───────────┴───────────┘               │
│                      Axios HTTP Client                    │
└──────────────────────┬────────────────────────────────────┘
                       │ HTTPS + JSON (REST API)
┌──────────────────────▼────────────────────────────────────┐
│                   后端业务层 (Spring Boot)                   │
│  ┌──────────────────────────────────────────────────────┐ │
│  │              Controller（REST接口）                    │ │
│  ├──────────────────────────────────────────────────────┤ │
│  │     Service（业务逻辑）│  Filter（JWT认证）│ Exception │ │
│  ├──────────────────────────────────────────────────────┤ │
│  │           MyBatis Plus（ORM映射层）                   │ │
│  └──────────────────────────────────────────────────────┘ │
└──────────────────────┬────────────────────────────────────┘
                       │ JDBC
┌──────────────────────▼────────────────────────────────────┐
│                      数据存储层                             │
│   ┌──────────────┐           ┌──────────────────┐          │
│   │   MySQL 8.0  │           │  Redis（可选缓存） │          │
│   │  gym_db      │           │  Token缓存/会话   │          │
│   └──────────────┘           └──────────────────┘          │
└──────────────────────────────────────────────────────────┘
```

### 2.2 技术栈明细

#### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 2.7.x | Web框架 |
| Spring Security | 5.7.x | 安全框架（JWT认证） |
| MyBatis Plus | 3.5.x | ORM框架（自动CRUD+分页+逻辑删除） |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 6.x | 缓存（可选，当前已注释） |
| Lombok | 1.18.x | 简化实体类代码 |
| Jackson | 2.13.x | JSON序列化/反序列化 |
| JJWT | 0.11.x | JWT Token生成与验证 |
| Hutool | 5.8.x | Java工具库 |

#### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 2.6.x | 前端框架 |
| Vue Router | 3.x | 路由管理 |
| Vuex | 3.x | 状态管理 |
| Element UI | 2.15.x | UI组件库 |
| Axios | 0.21.x | HTTP客户端 |
| SCSS | - | CSS预处理器 |
| Vue CLI | 5.x | 前端构建工具 |

### 2.3 项目目录结构

#### 后端目录结构

```
gym/                                  # 后端根目录（Spring Boot Maven项目）
├── src/main/java/com/gym/
│   ├── GymManagementApplication.java  # Spring Boot启动类
│   ├── config/
│   │   ├── SecurityConfig.java       # Spring Security安全配置
│   │   ├── JwtUtil.java              # JWT工具类（生成/验证Token）
│   │   ├── WebMvcConfig.java          # CORS跨域配置
│   │   └── MybatisPlusConfig.java     # MyBatis Plus配置
│   ├── controller/                    # REST控制器层（19个控制器）
│   │   ├── AuthController.java        # 认证（登录/注册）
│   │   ├── MemberInfoController.java  # 会员管理
│   │   ├── MemberCardController.java  # 会员卡管理
│   │   ├── MemberLevelController.java # 会员等级
│   │   ├── MemberSigninController.java # 会员签到
│   │   ├── MemberBodyTestController.java # 体测管理
│   │   ├── MemberConsumptionController.java # 消费记录
│   │   ├── CoachInfoController.java   # 教练管理
│   │   ├── CoachReviewController.java # 教练评价
│   │   ├── CoachCertificateController.java # 教练证书
│   │   ├── CoachSpecialtyController.java # 教练专长
│   │   ├── CoachScheduleController.java # 教练排班
│   │   ├── CoachShiftChangeController.java # 教练调班
│   │   ├── CoachSalaryController.java # 教练工资
│   │   ├── CoachPerformanceController.java # 教练绩效
│   │   ├── CourseInfoController.java  # 课程管理
│   │   ├── CourseCategoryController.java # 课程分类
│   │   ├── CourseReviewController.java # 课程评价
│   │   ├── CourseBookingController.java # 课程预约
│   │   ├── EquipmentInfoController.java # 器材信息
│   │   ├── EquipmentBookingController.java # 器材预约
│   │   ├── PointsGoodsController.java  # 积分商品
│   │   ├── PointsExchangeController.java # 积分兑换
│   │   └── StatisticsController.java   # 数据统计
│   ├── dto/
│   │   ├── ApiResponse.java           # 统一响应封装 {code, message, data}
│   │   ├── LoginRequest.java          # 登录请求DTO
│   │   └── LoginResponse.java          # 登录响应DTO
│   ├── entity/                        # 数据库实体（18个实体类）
│   │   ├── UserInfo.java              # 用户信息（统一认证表）
│   │   ├── MemberInfo.java            # 会员信息
│   │   ├── MemberCard.java            # 会员卡
│   │   ├── MemberLevel.java            # 会员等级
│   │   ├── MemberSignin.java          # 签到记录
│   │   ├── MemberBodyTest.java        # 体测记录
│   │   ├── MemberConsumption.java     # 消费记录
│   │   ├── CoachInfo.java             # 教练信息
│   │   ├── CoachReview.java           # 教练评价
│   │   ├── CoachCertificate.java      # 教练证书
│   │   ├── CoachSpecialty.java        # 教练专长
│   │   ├── CoachSchedule.java         # 教练排班
│   │   ├── CoachShiftChange.java      # 教练调班
│   │   ├── CoachSalary.java           # 教练工资
│   │   ├── CoachPerformance.java      # 教练绩效
│   │   ├── CourseInfo.java            # 课程信息
│   │   ├── CourseCategory.java        # 课程分类
│   │   ├── CourseBooking.java         # 课程预约
│   │   ├── CourseReview.java          # 课程评价
│   │   ├── EquipmentInfo.java         # 器材信息
│   │   ├── EquipmentBooking.java      # 器材预约
│   │   ├── PointsGoods.java           # 积分商品
│   │   └── PointsExchange.java       # 积分兑换记录
│   ├── exception/
│   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java # JWT认证过滤器
│   ├── mapper/                        # MyBatis Mapper接口（18个）
│   └── service/
│       ├── impl/                      # 服务实现类（15+个）
│       └── *.java                     # 服务接口
├── src/main/resources/
│   ├── application.yml               # 主配置文件
│   ├── application.properties        # 属性配置
│   ├── application-no-redis.yml     # 无Redis配置
│   └── sql/                           # 数据库脚本
│       ├── init.sql                   # 完整中文建库脚本+种子数据
│       ├── init_en.sql               # 英文版建库脚本+种子数据
│       ├── init_simple.sql           # 简化版建库脚本
│       ├── course_review.sql         # 课程评价表补充脚本
│       └── course_category.sql        # 课程分类表补充脚本
├── sql/                               # 前端SQL脚本
│   ├── init_course_data.sql          # 课程初始化数据
│   └── points_mall.sql               # 积分商城建表脚本
└── pom.xml                           # Maven依赖配置
```

#### 前端目录结构

```
frontend/                             # 前端根目录（Vue CLI项目）
├── public/
│   └── gympicture/                   # 静态商品图片资源
│       ├── gymshoutao.jpeg           # 健身手套
│       ├── gymshuihu.jpeg            # 运动水壶
│       ├── gymyujiadian.jpeg         # 瑜伽垫
│       ├── gymmaojin.jpeg            # 运动毛巾
│       ├── gymjianshenbao.jpeg       # 健身包
│       └── gymtiaosheng.jpeg         # 跳绳
├── src/
│   ├── main.js                       # Vue应用入口
│   ├── App.vue                       # 根组件
│   ├── api/                          # API接口模块（25个JS文件）
│   │   ├── auth.js                   # 认证接口
│   │   ├── user.js                   # 用户信息接口
│   │   ├── member.js                 # 会员管理
│   │   ├── memberSignin.js           # 会员签到
│   │   ├── memberCard.js             # 会员卡
│   │   ├── memberLevel.js            # 会员等级
│   │   ├── memberBodyTest.js         # 体测
│   │   ├── memberConsumption.js     # 消费
│   │   ├── coach.js                  # 教练管理
│   │   ├── coachDashboard.js        # 教练工作台
│   │   ├── coachReview.js            # 教练评价
│   │   ├── coachCertificate.js       # 教练证书
│   │   ├── coachSpecialty.js        # 教练专长
│   │   ├── coachSchedule.js         # 教练排班
│   │   ├── coachShift.js            # 教练调班
│   │   ├── coachSalary.js           # 教练工资
│   │   ├── coachPerformance.js      # 教练绩效
│   │   ├── course.js                # 课程管理
│   │   ├── courseCategory.js        # 课程分类
│   │   ├── courseBooking.js         # 课程预约
│   │   ├── courseReview.js          # 课程评价
│   │   ├── equipment.js             # 器材
│   │   ├── equipmentBooking.js      # 器材预约
│   │   ├── points.js                # 积分商城
│   │   └── statistics.js            # 统计
│   ├── router/
│   │   └── index.js                  # 路由配置（含3角色路由守卫）
│   ├── store/
│   │   ├── index.js                  # Vuex根store
│   │   └── modules/
│   │       └── user.js              # 用户状态模块（token/userInfo/userType）
│   ├── utils/
│   │   ├── auth.js                  # Token存取工具
│   │   └── request.js              # Axios封装（请求/响应拦截器）
│   └── views/                        # 页面组件（44个Vue文件）
│       ├── login/index.vue           # 登录注册页
│       ├── layout/index.vue          # 主布局（侧边栏+顶栏+内容区）
│       ├── dashboard/index.vue       # 管理员首页统计
│       ├── profile/index.vue         # 个人中心
│       ├── 404.vue                   # 404页面
│       ├── coach/                    # 教练端页面（13个）
│       │   ├── dashboard.vue         # 教练工作台
│       │   ├── mySchedule.vue       # 我的排课
│       │   ├── members.vue          # 我的学员
│       │   ├── myReviews.vue        # 我的评价
│       │   ├── bodyTest.vue         # 录入体测
│       │   ├── list.vue             # 教练列表
│       │   ├── certificate.vue       # 教练证书
│       │   ├── specialty.vue         # 教练专长
│       │   ├── schedule.vue         # 教练排班
│       │   ├── shift.vue            # 教练调班
│       │   ├── salary.vue            # 教练工资
│       │   ├── performance.vue       # 教练绩效
│       │   └── review.vue           # 教练评价
│       ├── member/                   # 会员端页面（13个）
│       │   ├── dashboard.vue         # 会员首页
│       │   ├── booking.vue           # 我的预约
│       │   ├── bodyTest.vue          # 体测记录
│       │   ├── points.vue            # 积分商城
│       │   ├── messages.vue          # 消息中心
│       │   ├── profile.vue          # 个人中心
│       │   ├── contactCoach.vue     # 联系教练
│       │   ├── sportData.vue         # 运动数据
│       │   ├── list.vue             # 会员列表（管理员视角）
│       │   ├── card.vue             # 会员卡管理
│       │   ├── level.vue            # 会员等级
│       │   ├── signin.vue           # 会员签到
│       │   └── consumption.vue      # 消费记录
│       ├── course/                   # 课程页面（8个）
│       │   ├── list.vue             # 课程列表
│       │   ├── category.vue         # 课程分类
│       │   ├── schedule.vue         # 课程排期
│       │   ├── available.vue        # 可预约课程
│       │   ├── my.vue               # 我的课程
│       │   ├── booking.vue          # 课程预约（会员）
│       │   └── review.vue          # 课程评价
│       ├── equipment/                # 器材页面（2个）
│       │   ├── list.vue             # 器材列表
│       │   └── booking.vue         # 器材预约
│       └── points/                   # 积分商城页面（1个）
│           └── goods.vue             # 商品管理（管理员）
└── package.json                      # npm依赖配置
```

---

## 3. 数据库设计

### 3.1 ER图概览

本系统共设计 **22 张数据表**，围绕以下核心实体展开：

- **UserInfo（用户）**：统一认证入口，通过 `userType` 区分管理员/教练/会员，通过 `memberId`/`coachId` 关联各自详细信息
- **MemberInfo（会员）**：会员个人信息、等级、积分、余额
- **CoachInfo（教练）**：教练个人信息、专长、工作状态
- **CourseInfo（课程）**：课程基本信息、容量、上课时间
- **CourseBooking（预约）**：会员对课程的预约记录，含签到状态
- **PointsGoods（积分商品）**：积分商城商品，支持实物和课程两类
- **PointsExchange（积分兑换）**：会员积分兑换流水

### 3.2 核心表结构

#### 3.2.1 用户认证表（user_info）

> **用途**：统一认证表，所有用户（管理员/教练/会员）共用此表登录
> **特点**：通过 `user_type` 区分角色（1=管理员, 2=教练, 3=会员），通过 `member_id`/`coach_id` 与业务表关联

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| user_id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（BCrypt加密存储） |
| user_type | INT | NOT NULL | 用户类型：1=管理员，2=教练，3=会员 |
| member_id | BIGINT | FK(会员) | 关联会员信息（user_type=3时有效） |
| coach_id | BIGINT | FK(教练) | 关联教练信息（user_type=2时有效） |
| phone | VARCHAR(20) | | 手机号 |
| email | VARCHAR(100) | | 邮箱 |
| status | INT | DEFAULT 1 | 状态：1=正常，0=禁用 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

#### 3.2.2 会员信息表（member_info）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| member_id | BIGINT | PK, AUTO_INCREMENT | 会员ID |
| member_name | VARCHAR(50) | NOT NULL | 会员姓名 |
| gender | VARCHAR(10) | | 性别 |
| phone_num | VARCHAR(20) | | 手机号 |
| email_addr | VARCHAR(100) | | 邮箱 |
| fitness_level | VARCHAR(20) | | 健身水平（初学/中级/高级） |
| account_status | INT | DEFAULT 1 | 账户状态：1=正常，0=冻结 |
| member_level | VARCHAR(20) | | 等级名称（青铜/白银/黄金/钻石） |
| points | INT | DEFAULT 0 | 当前积分余额 |
| balance | DECIMAL(10,2) | DEFAULT 0 | 账户余额 |
| create_time | DATETIME | | 注册时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

#### 3.2.3 会员等级表（member_level）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| level_id | BIGINT | PK, AUTO_INCREMENT | 等级ID |
| level_name | VARCHAR(20) | NOT NULL | 等级名称 |
| level_code | VARCHAR(20) | NOT NULL | 等级代码 |
| level_order | INT | | 等级顺序 |
| discount_rate | DECIMAL(4,2) | | 消费折扣率 |
| points_rate | DECIMAL(4,2) | | 积分获取倍率 |
| min_points | INT | | 最低积分门槛 |
| max_points | INT | | 最高积分门槛 |
| card_fee | DECIMAL(10,2) | | 办卡费用 |
| description | VARCHAR(500) | | 等级权益描述 |

**默认等级配置**：

| 等级 | 折扣 | 积分倍率 | 积分区间 | 办卡费 |
|------|------|----------|---------|--------|
| 青铜 | 98% | 1.0x | 0-999 | 99元 |
| 白银 | 95% | 1.2x | 1000-4999 | 199元 |
| 黄金 | 90% | 1.5x | 5000-19999 | 399元 |
| 钻石 | 80% | 2.0x | 20000+ | 799元 |

#### 3.2.4 课程信息表（course_info）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| course_id | BIGINT | PK, AUTO_INCREMENT | 课程ID |
| course_name | VARCHAR(100) | NOT NULL | 课程名称 |
| coach_id | BIGINT | FK(教练) | 授课教练 |
| course_type | VARCHAR(20) | | 课程类型：团课/私教课 |
| category_id | BIGINT | FK(分类) | 课程分类 |
| description | TEXT | | 课程描述 |
| duration_min | INT | | 时长（分钟） |
| price | DECIMAL(10,2) | | 价格 |
| max_capacity | INT | | 最大容量（人数上限） |
| current_capacity | INT | DEFAULT 0 | 当前已预约人数 |
| start_time | DATETIME | | 开始时间 |
| end_time | DATETIME | | 结束时间 |
| status | INT | DEFAULT 0 | 状态：0=正常，1=已取消，2=已满员 |
| room | VARCHAR(50) | | 上课教室 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

**课程分类**：

| 分类ID | 分类名称 |
|--------|----------|
| 1 | 有氧课程 |
| 2 | 力量训练 |
| 3 | 瑜伽/普拉提 |
| 4 | 舞蹈课程 |
| 5 | 私教课程 |

#### 3.2.5 课程预约表（course_booking）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| booking_id | BIGINT | PK, AUTO_INCREMENT | 预约ID |
| member_id | BIGINT | FK(会员) | 预约会员 |
| course_id | BIGINT | FK(课程) | 预约课程 |
| coach_id | BIGINT | FK(教练) | 授课教练 |
| booking_time | DATETIME | NOT NULL | 预约时间 |
| class_time | DATETIME | NOT NULL | 课程时间 |
| status | VARCHAR(20) | DEFAULT '已预约' | 预约状态：已预约/已取消 |
| remark | VARCHAR(255) | | 备注 |
| signin_time | DATETIME | | 签到时间 |
| create_time | DATETIME | | 创建时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

#### 3.2.6 积分商品表（points_goods）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 商品ID |
| name | VARCHAR(100) | NOT NULL | 商品/课程名称 |
| description | VARCHAR(500) | | 商品描述 |
| points | INT | NOT NULL | 所需积分 |
| stock | INT | DEFAULT 0 | 库存数量（实物商品用） |
| image | VARCHAR(255) | | 图片路径 |
| type | VARCHAR(20) | DEFAULT 'goods' | 类型：goods=实物，course=课程 |
| ref_id | BIGINT | | 关联ID（如课程ID） |
| status | TINYINT | DEFAULT 1 | 状态：1=上架，0=下架 |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

#### 3.2.7 积分兑换记录表（points_exchange）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 兑换记录ID |
| member_id | BIGINT | FK(会员) | 兑换会员 |
| goods_id | BIGINT | FK(商品) | 商品ID（实物商品时） |
| ref_id | BIGINT | | 关联ID（课程ID等） |
| goods_name | VARCHAR(100) | NOT NULL | 商品/课程名称（冗余存储） |
| points | INT | NOT NULL | 消耗积分 |
| status | TINYINT | DEFAULT 1 | 状态：0=待处理，1=已完成，2=已取消 |
| exchange_time | DATETIME | | 兑换时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

#### 3.2.8 教练信息表（coach_info）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| coach_id | BIGINT | PK, AUTO_INCREMENT | 教练ID |
| coach_name | VARCHAR(50) | NOT NULL | 教练姓名 |
| gender | VARCHAR(10) | | 性别 |
| phone_num | VARCHAR(20) | | 手机号 |
| specialty | VARCHAR(100) | | 专业领域 |
| experience_years | INT | | 从业年限 |
| certification | VARCHAR(100) | | 资格证书 |
| status | INT | DEFAULT 1 | 状态：1=在职，0=离职 |
| hire_date | DATE | | 入职日期 |
| create_time | DATETIME | | 创建时间 |
| deleted | INT | DEFAULT 0 | 逻辑删除 |

---

## 4. 功能模块详解

### 4.1 认证模块（Authentication）

#### 4.1.1 技术实现

认证模块是整个系统的安全入口，采用 **Spring Security + JWT** 实现无状态认证。

**登录流程**：
```
1. 前端 POST /api/auth/login {username, password, userType}
2. 后端 AuthenticationManager 验证用户名+密码+角色
3. 验证通过后，JwtUtil 生成包含 {userId, userType, username, exp} 的Token
4. 前端将Token存入localStorage，后续请求在请求头携带 Authorization: Bearer <token>
5. JwtAuthenticationFilter 拦截所有请求，验证Token并设置SecurityContext
```

**密码加密**：使用 BCryptPasswordEncoder，默认强度10。数据库存储的是哈希值，不可逆。

**Token配置**：
- 有效期：24小时（86400000ms）
- 算法：HS256
- 密钥：`gym-management-system-secret-key-2024`

**CORS跨域**：配置允许的前端地址包括 `localhost:3000/8081/8082`，支持携带Cookie和自定义请求头。

#### 4.1.2 路由守卫

前端路由守卫（`router.beforeEach`）根据用户类型自动跳转：

| 用户类型 | 访问路径限制 | 默认首页 |
|---------|------------|---------|
| 管理员 (userType=1) | 所有页面 | `/dashboard/index` |
| 教练 (userType=2) | 禁止访问 `/dashboard/*` | `/coach-dashboard/index` |
| 会员 (userType=3) | 禁止访问 `/dashboard/*` | `/member/home` |

无Token时强制跳转登录页；Token无效（401）时自动登出并跳转。

### 4.2 会员管理模块（Member Management）

#### 4.2.1 会员注册与档案

- **注册**：在 `user_info` 表创建统一认证账号，同时在 `member_info` 表创建会员档案
- **档案信息**：姓名、性别、手机号、邮箱、健身水平、注册时间
- **账户状态**：支持冻结/解冻（`account_status`），冻结后无法登录

#### 4.2.2 会员等级体系

采用**阶梯式等级制度**，根据累计积分自动升降级：

- 系统初始化时插入4个默认等级（青铜→钻石）
- 消费、签到时根据当前积分自动计算应属等级
- 不同等级享受不同折扣率（98%~80%）和积分倍率（1.0x~2.0x）

#### 4.2.3 会员卡管理

**核心功能**：
- **开卡**：自动生成卡号 `C{timestamp}{随机4位}`，绑定会员
- **充值**：支持延长有效期（`end_date`）和增加余额
- **挂失/补办**：状态流转 Normal(1) → LossReport(2) → Reissue(3)
- **次数核销**：私教课等按次消费，核销 `remaining_times`
- **有效期检查**：课程预约时自动校验会员卡是否在有效期内

### 4.3 签到积分模块（Sign-in & Points）

#### 4.3.1 每日签到

会员每日可在系统中完成签到，系统自动：
1. 校验当日是否已签到（同一天不可重复签到）
2. 计算连续签到天数（跨零点断签重置为1）
3. 根据连续天数奖励积分：基础10分，连续7天+50%，连续30天+100%

**积分奖励规则**：

| 连续签到天数 | 积分奖励 |
|------------|---------|
| 1-6天 | 10分/次 |
| 7-29天 | 15分/次 |
| 30天以上 | 20分/次 |

#### 4.3.2 积分商城

**两类兑换商品**：

1. **实物商品**（`type=goods`）
   - 管理员上下架管理
   - 兑换时原子性扣减库存（`stock - 1`）和会员积分
   - 兑换后生成 `points_exchange` 记录（状态=已完成）
   - 库存为0时自动禁止兑换

2. **课程兑换**（`type=course`）
   - 管理员在积分商品表中关联 `ref_id=课程ID`
   - 兑换时同步完成课程预约（调用 `bookCourse`）
   - 同时记录积分兑换流水

**积分来源**：

| 途径 | 积分 | 说明 |
|------|------|------|
| 每日签到 | 10-20分 | 根据连续天数浮动 |
| 完善资料 | 50分 | 一次性奖励 |
| 消费 | 不定 | 根据消费金额和等级倍率计算 |

### 4.4 课程管理模块（Course Management）

#### 4.4.1 课程管理

管理员可进行：
- 新增/编辑/删除课程（名称、分类、教练、时长、价格、容量、上课时间、教室）
- 课程分类管理（支持有氧、力量、瑜伽、舞蹈、私教等）
- 课程状态管理（正常/已取消）

#### 4.4.2 课程预约与容量控制

**预约逻辑（核心亮点）**：

采用**子查询实时统计**方式判断课程是否满员，避免 `current_capacity` 字段与实际预约数不一致的问题：

```sql
SELECT COUNT(*) FROM course_booking
WHERE course_id = ? AND deleted = 0 AND status != '已取消'
  AND (SELECT COUNT(*) FROM course_booking ...) < max_capacity
```

**预约流程**：
```
1. 会员点击预约 → POST /course/book/{courseId}?memberId=?&coachId=?
2. 查询课程信息，校验状态(status=0)
3. 用子查询统计当前有效预约数
4. 同一会员不能重复预约同一课程
5. 有效预约数 >= maxCapacity → 标记课程为满员(status=2)，拒绝预约
6. 插入 course_booking 记录
7. 更新 course_info.current_capacity
8. 如达到上限，自动将课程状态置为 2（已满员）
```

**取消预约**：
- 将 `course_booking.status` 置为 '已取消'
- 重新统计有效预约数，写回 `current_capacity`
- 如果课程原状态为满员（2），自动恢复为正常（0）

#### 4.4.3 课程评价

- 已完成课程的会员可提交评价（评分1-5星+文字内容）
- 教练可回复评价
- 评价关联课程和教练，影响教练绩效

### 4.5 教练管理模块（Coach Management）

#### 4.5.1 教练档案

- 个人信息管理（姓名、性别、电话、专长）
- 证书管理（证书名称、类型、编号、发证机构、有效期，支持审核状态）
- 专长标签管理

#### 4.5.2 教练排班

- 按日排课（schedule_date + start_time ~ end_time）
- 状态流转：待上课(1) → 进行中(2) → 已结束(3)
- 支持开始/结束操作

#### 4.5.3 教练调班

- 教练提交调班申请（original_date/time → target_date/time）
- 填写调班原因
- 审核状态：申请中/已批准/已拒绝

#### 4.5.4 教练绩效评估

月度多维度绩效评分：

| 维度 | 权重 | 评分标准 |
|------|------|---------|
| 出勤评分 | 20% | 排班到课率 |
| 教学评分 | 30% | 课程完成度、学员反馈 |
| 服务评分 | 25% | 态度、沟通 |
| 销售评分 | 25% | 私教课销售、新会员转化 |

综合评分 → 绩效等级（A/B/C/D）

#### 4.5.5 教练工资结算

工资组成：

```
总工资 = 基本工资 + 课时费（课时数 × 课时单价）
        + 销售提成（销售额 × 提成比例）
        + 奖金 - 罚款
```

- 每月按教练维度结算一次
- 课时数由 `coach_schedule` 统计（status=3即已完成课时）
- 销售数据由 `course_booking` 统计（私教课消费）

### 4.6 体测管理模块（Body Test）

- 教练为会员录入体测数据
- 自动计算 **BMI** = 体重(kg) / 身高(m)²
- 自动计算 **健康评分**（满分100分，根据BMI、体脂率等指标扣分）
- 保留历史记录，支持对比分析
- 体测数据为教练制定个性化健身计划提供依据

### 4.7 器材管理模块（Equipment）

- 器材信息管理（名称、类型、品牌、型号、采购日期、维护日期）
- 器材状态：可用 / 维修中 / 已报废
- 器材借用预约与归还管理

---

## 5. 核心业务逻辑

### 5.1 课程容量同步机制

为解决 `current_capacity` 与 `course_booking` 实际预约数可能不一致的问题，系统实现了三重保障：

**① 列表查询层（/course/available）**：
```java
wrapper.apply("(SELECT COUNT(*) FROM course_booking
    WHERE course_id = course_info.course_id AND deleted = 0
    AND NOT (status <=> '已取消')) < max_capacity");
```

**② 预约写入层（bookCourse方法内）**：
```java
// 预约前以预约表实际人数为准同步 current_capacity
int actualBookings = countActiveBookings(courseId);
courseInfo.setCurrentCapacity(actualBookings);
courseInfoMapper.updateById(courseInfo);

if (actualBookings >= maxCap) {
    courseInfo.setStatus(2); // 满员
    throw new RuntimeException("课程已满员");
}
```

**③ 取消预约层（cancelBooking方法内）**：
```java
// 取消后用子查询重新统计，写回同步
courseInfo.setCurrentCapacity(countActiveBookings(courseId));
```

### 5.2 积分兑换原子性

实物兑换和课程兑换均使用 `@Transactional(rollbackFor = Exception.class)` 保证原子性：

**实物兑换**：
```
BEGIN TRANSACTION
  ① 检查商品存在、状态、库存
  ② 检查会员存在、积分充足
  ③ points_goods.stock - 1
  ④ member_info.points - needPoints
  ⑤ INSERT points_exchange
COMMIT
（任一步骤失败全部回滚）
```

**课程兑换**：
```
BEGIN TRANSACTION
  ① 检查会员存在、积分充足
  ② 调用 courseInfoService.bookCourse()（含满员检查）
  ③ member_info.points - needPoints
  ④ INSERT points_exchange
COMMIT
```

### 5.3 签到连续天数计算

```java
// 查询上次签到日期
MemberSignin last = 查询该会员最近一条记录
if (last != null) {
    LocalDate lastDate = last.getSigninDate().toLocalDate()
    LocalDate today = LocalDate.now()
    if (lastDate.equals(today.minusDays(1))) {
        // 昨天签过，今天继续 → 连续天数+1
        consecutiveDays = last.getConsecutiveDays() + 1
    } else if (!lastDate.equals(today)) {
        // 昨天没签 → 连续天数重置为1
        consecutiveDays = 1
    }
}
// 基础10分，连续7天+50%，连续30天+100%
int basePoints = 10
if (consecutiveDays >= 30) basePoints = 20
else if (consecutiveDays >= 7) basePoints = 15
```

---

## 6. 接口设计

### 6.1 统一响应格式

所有接口返回统一的 `ApiResponse<T>` 结构：

```json
{
  "code": 200,          // 业务状态码：200=成功，500=错误
  "message": "success", // 提示信息
  "data": { ... }       // 响应数据（可为null）
}
```

### 6.2 主要接口一览

#### 6.2.1 认证接口

| 接口 | 方法 | 参数 | 说明 |
|------|------|------|------|
| `/api/auth/login` | POST | `{username, password, userType}` | 登录，返回 `{token, userType, userId, username}` |
| `/api/auth/register` | POST | `{username, password, userType, memberName, phone}` | 注册新账号 |
| `/api/auth/profile` | GET | - | 获取当前用户信息 |
| `/api/auth/password` | PUT | `{oldPassword, newPassword}` | 修改密码 |

#### 6.2.2 会员管理接口

| 接口 | 方法 | 参数 | 说明 |
|------|------|------|------|
| `/api/member/page` | GET | `pageNum, pageSize, keyword` | 分页查询会员 |
| `/api/member` | POST | 会员信息 | 新增会员 |
| `/api/member` | PUT | 会员信息 | 更新会员 |
| `/api/member/{id}` | DELETE | - | 删除会员 |
| `/api/member/points/{id}` | GET | - | 获取会员积分 |
| `/api/member/points/{id}` | PUT | `{points, reason}` | 调整积分 |

#### 6.2.3 会员卡接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/member/card` | POST | 开卡（自动生成卡号） |
| `/api/member/card` | PUT | 更新卡信息 |
| `/api/member/card/renew/{id}` | POST | 续卡充值 |
| `/api/member/card/reportLoss/{id}` | POST | 挂失 |
| `/api/member/card/reissue/{id}` | POST | 补办 |

#### 6.2.4 签到接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/member/signin/sign` | POST | 执行签到，返回积分奖励 |
| `/api/member/signin/today/{memberId}` | GET | 今日是否已签到 |
| `/api/member/signin/statistics/{memberId}` | GET | 签到统计数据 |

#### 6.2.5 课程接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/course/available` | GET | 可预约课程（已过滤满员） |
| `/api/course/book/{courseId}` | POST | 预约课程 |
| `/api/course/cancel/{bookingId}` | POST | 取消预约 |
| `/api/course-booking/signin/{id}` | PUT | 课程签到 |

#### 6.2.6 积分商城接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/points/exchange/goods` | POST | 兑换实物 |
| `/api/points/exchange/course` | POST | 兑换课程 |
| `/api/points/exchange/cancel/{id}` | PUT | 取消兑换 |
| `/api/points/exchange/page` | GET | 会员兑换记录 |
| `/api/points/goods/page` | GET | 后台商品分页 |
| `/api/points/goods` | POST/PUT/DELETE | 后台CRUD商品 |

---

## 7. 前端实现

### 7.1 技术选型理由

- **Vue 2.6**：成熟稳定，社区生态完善，与 Element UI 兼容性最佳
- **Vue Router 3**：hash模式（兼容性好，无需后端配置支持SPA路由）
- **Vuex 3**：集中管理用户登录状态（token、userInfo、userType）
- **Element UI 2.15**：企业级Vue组件库，表格、表单、对话框等组件开箱即用，风格统一
- **Axios**：支持请求/响应拦截，统一处理 Token 注入和错误提示
- **SCSS**：CSS预处理器，支持嵌套、变量、mixin，提高样式复用性

### 7.2 页面组织结构

前端采用**布局组件包裹子页面**的模式：

```
Layout (侧边栏+顶栏)
  ├── AdminMenu / CoachMenu / MemberMenu（根据userType动态渲染）
  └── RouterView（动态渲染当前路由页面）
```

侧边栏菜单通过遍历路由配置自动生成，支持折叠/展开。

### 7.3 请求封装

`request.js` 对 Axios 做了统一封装：

- **请求拦截器**：自动在请求头注入 `Authorization: Bearer <token>`
- **响应拦截器**：
  - `code !== 200`：弹出 Element UI `Message.error` 提示，自动处理 401（跳转登录）
  - `code === 200`：直接返回 `res` 数据
- **数据转换**：自动将 `Date` 对象转换为 `yyyy-MM-dd HH:mm:ss` 字符串（解决 Spring Boot 日期接收问题）

### 7.4 角色路由守卫

```javascript
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (!token) {
    next('/login')
    return
  }
  store.dispatch('user/getInfo').then(userInfo => {
    const userType = normalizeUserType(store)
    if (userType === 2 && isAdminRoute(to.path)) {
      next('/coach-dashboard/index') // 教练禁止进管理后台
      return
    }
    if (userType === 3 && isAdminRoute(to.path)) {
      next('/member/home') // 会员禁止进管理后台
      return
    }
    next()
  }).catch(() => logout())
})
```

---

## 8. 系统亮点与创新

### 8.1 容量一致性保证机制

**问题**：传统方案中，`course_info.current_capacity` 依赖业务代码手动更新，若出现以下情况会导致数据不一致：
- 历史数据导入时未更新容量字段
- 通过SQL直接修改了预约记录
- 并发预约导致更新丢失

**本系统解决方案**：在查询和预约两个关键节点均使用**子查询实时统计**实际预约数：
1. 列表查询：子查询判断是否 `< max_capacity`
2. 预约写入：预约前同步 `current_capacity`，预约后也同步
3. 取消预约：取消后重新统计

### 8.2 积分兑换事务完整性

实物兑换涉及3张表（`points_goods`、`member_info`、`points_exchange`）的写操作，通过 `@Transactional` 保证要么全部成功，要么全部回滚，避免超卖或积分扣减后库存未减的问题。

### 8.3 多角色门户隔离

通过用户类型字段在路由层、菜单层、视图层实现三端完全隔离：
- 管理员看不到教练/会员菜单
- 教练访问管理端路径自动重定向到教练工作台
- 会员访问管理端路径自动重定向到会员首页

### 8.4 连续签到积分激励机制

在基础积分制上引入连续天数递增奖励（10→15→20分），通过"损失厌恶"心理提高用户粘性和回访率。

### 8.5 自动体测评分

无需人工计算，录入原始数据（身高、体重、体脂等）后，系统自动计算BMI和健康评分，并将评分规则可配置化存储在代码中。

### 8.6 积分商城双模式

支持**实物兑换**（扣库存扣积分）和**课程兑换**（预约课程扣积分）两种模式，通过 `type` 字段区分，后端统一处理，扩展性强。

---

## 9. 部署说明

### 9.1 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | 推荐 OpenJDK 17 或 Oracle JDK 17 |
| Maven | 3.6+ | 构建工具 |
| Node.js | 14+ | 前端构建 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.x | 缓存（可选） |

### 9.2 后端部署

```bash
# 1. 导入数据库
mysql -u root -p < src/main/resources/sql/init.sql

# 2. 修改 application.yml 中的数据库连接密码
#    spring.datasource.password: 123456  ← 改为你的密码

# 3. 编译打包
cd D:/gym
mvn clean package -DskipTests

# 4. 启动
java -jar target/gym-0.0.1-SNAPSHOT.jar
# 或开发模式
mvn spring-boot:run -DskipTests
```

服务启动后访问：`http://localhost:8080`

### 9.3 前端部署

```bash
cd frontend

# 1. 安装依赖
npm install

# 2. 开发模式（热重载）
npm run serve
# 访问 http://localhost:3000

# 3. 生产构建
npm run build
# 产物在 dist/ 目录，可部署到任意静态服务器
```

### 9.4 前后端联调

开发时前端默认代理到 `http://localhost:8080`（后端），配置文件为 `vue.config.js`。

### 9.5 默认账号

| 角色 | 用户名 | 密码 | userType |
|------|--------|------|----------|
| 管理员 | admin | admin123 | 1 |
| 教练 | zhao_coach | 123456 | 2 |
| 会员 | zhangsan | 123456 | 3 |

---

## 10. 使用指南

### 10.1 管理员操作流程

**日常管理流程**：

```
登录（admin / admin123）
  ↓
首页看板（查看会员/教练/课程/器材总量）
  ↓
会员管理 → 新增会员 → 开会员卡 → 会员等级管理
  ↓
教练管理 → 添加教练 → 分配证书/专长 → 教练排班
  ↓
课程管理 → 新增课程 → 设置分类 → 预约审核
  ↓
器材管理 → 器材登记 → 借用管理
  ↓
积分商城 → 新增商品 → 上下架管理 → 查看兑换记录
```

### 10.2 教练操作流程

```
登录教练账号（zhao_coach / 123456）
  ↓
工作台（查看今日课程、待处理预约、学员数量）
  ↓
我的排课 → 开始上课 → 结束上课
  ↓
我的学员 → 查看预约学员信息
  ↓
录入体测 → 填写体测数据（自动计算BMI和健康评分）
  ↓
我的评价 → 查看会员评价和回复
```

### 10.3 会员操作流程

```
登录会员账号（zhangsan / 123456）
  ↓
会员首页（查看积分余额、近期课程）
  ↓
课程预约 → 选择课程 → 点击预约 → 预约成功
  ↓
积分商城 → 查看商品/课程 → 兑换商品（扣积分扣库存）
          → 兑换课程（预约+扣积分）
  ↓
每日签到 → 获取积分奖励（连续签到递增）
  ↓
体测记录 → 查看历史体测数据
  ↓
联系教练 → 发起咨询
```

---

## 系统截图说明

> （演示时可依次展示以下页面）

1. **登录页**：三种角色登录入口，JWT认证流程
2. **管理后台首页**：数据看板，会员/教练/课程/器材总量统计
3. **会员管理**：分页列表、新增编辑、积分调整
4. **课程预约**：可预约课程列表、容量显示、预约成功
5. **积分商城（会员端）**：商品列表、课程列表、兑换记录
6. **积分商品管理（管理端）**：新增商品、上下架、库存调整
7. **教练工作台**：今日课程、学员统计
8. **签到记录**：连续签到天数、积分奖励记录
9. **体测数据**：BMI自动计算、健康评分

---

*文档版本：v1.0 | 更新日期：2026年3月*
