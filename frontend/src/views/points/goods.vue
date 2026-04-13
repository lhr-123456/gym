<template>
  <div class="points-goods-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="商品名称">
            <el-input v-model="queryForm.name" placeholder="请输入商品名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="商品类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 140px">
              <el-option label="实物商品" value="goods"></el-option>
              <el-option label="课程" value="course"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px">
              <el-option label="上架" :value="1"></el-option>
              <el-option label="下架" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作栏 -->
      <div class="table-toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增商品</el-button>
        <el-button type="success" icon="el-icon-bottom" @click="handleBatchOn">批量上架</el-button>
        <el-button type="warning" icon="el-icon-top" @click="handleBatchOff">批量下架</el-button>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <el-table
          :data="tableData"
          border
          :style="{ width: '100%' }"
          v-loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40"></el-table-column>
          <el-table-column label="商品图片" width="100">
            <template slot-scope="scope">
              <img
                v-if="scope.row.image"
                :src="'/gympicture/' + getImageName(scope.row.image)"
                :alt="scope.row.name"
                class="goods-thumb"
                @error="handleImgError"
              />
              <span v-else class="no-image">无图</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" min-width="150"></el-table-column>
          <el-table-column prop="description" label="商品描述" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column label="类型" width="90">
            <template slot-scope="scope">
              <el-tag :type="scope.row.type === 'goods' ? 'primary' : 'warning'" size="small">
                {{ scope.row.type === 'goods' ? '实物商品' : '课程' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="points" label="所需积分" width="90">
            <template slot-scope="scope">
              <span class="points-text">{{ scope.row.points || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="90">
            <template slot-scope="scope">
              <template v-if="scope.row.type === 'goods'">
                <span :class="{ 'stock-low': scope.row.stock <= 5 }">{{ scope.row.stock || 0 }}</span>
                <el-button type="text" size="mini" icon="el-icon-edit" @click="handleAdjustStock(scope.row)">调整</el-button>
              </template>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template slot-scope="scope">
              <el-switch
                :value="scope.row.status === 1"
                active-color="#67C23A"
                inactive-color="#909399"
                @change="handleToggleStatus(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="兑换次数" width="90">
            <template slot-scope="scope">
              <span>{{ exchangeCountMap[scope.row.id] || 0 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="620px" @close="handleDialogClose">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入商品名称" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="商品类型" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange">
            <el-radio label="goods">实物商品</el-radio>
            <el-radio label="course">课程</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所需积分" prop="points">
          <el-input-number v-model="formData.points" :min="1" :max="999999" style="width: 200px"></el-input-number>
        </el-form-item>
        <el-form-item v-if="formData.type === 'goods'" label="库存数量" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" :max="9999" style="width: 200px"></el-input-number>
          <span style="margin-left: 12px; color: #909399; font-size: 12px">实物商品需填写库存</span>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品描述（选填）" maxlength="500" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="formData.image" placeholder="图片文件名，如 gymshoutao.jpeg" style="width: 300px">
            <template slot="append">
              <el-tooltip content="将图片放入 frontend/public/gympicture/ 目录后填入文件名">
                <i class="el-icon-question"></i>
              </el-tooltip>
            </template>
          </el-input>
          <div v-if="formData.image" style="margin-top: 8px">
            <img :src="'/gympicture/' + getImageName(formData.image)" alt="预览" class="img-preview" @error="handleImgError" />
          </div>
          <div class="image-tip">
            可用图片：gymshoutao.jpeg、gymshuihu.jpeg、gymyujiadian.jpeg、gymmaojin.jpeg、gymjianshenbao.jpeg、gymtiaosheng.jpeg
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 库存调整对话框 -->
    <el-dialog title="调整库存" :visible.sync="stockDialogVisible" width="400px">
      <el-form :model="stockForm" label-width="80px">
        <el-form-item :label="'商品：' + stockForm.name"></el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整方式">
          <el-radio-group v-model="stockForm.adjustType">
            <el-radio label="set">设为</el-radio>
            <el-radio label="add">增加</el-radio>
            <el-radio label="sub">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="stockForm.adjustValue" :min="0" :max="9999"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockForm.remark" placeholder="调整原因（选填）" maxlength="100"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStockSubmit">确定调整</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getGoodsPage, saveGoods, updateGoods, deleteGoods, getExchangeListByGoods } from '@/api/points'

export default {
  name: 'PointsGoodsManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      queryForm: {
        name: '',
        type: '',
        status: ''
      },
      selectedRows: [],
      dialogVisible: false,
      dialogTitle: '新增商品',
      formData: {
        id: null,
        name: '',
        description: '',
        points: 100,
        stock: 0,
        image: '',
        type: 'goods',
        status: 1
      },
      rules: {
        name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
        points: [{ required: true, message: '请输入所需积分', trigger: 'blur' }]
      },
      stockDialogVisible: false,
      stockForm: {
        id: null,
        name: '',
        currentStock: 0,
        adjustType: 'set',
        adjustValue: 0,
        remark: ''
      },
      exchangeCountMap: {}
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }
      // 去掉空值
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === null) delete params[key]
      })
      getGoodsPage(params)
        .then(res => {
          if (res.code === 200 || res.code === '200') {
            const page = res.data || {}
            this.tableData = page.records || []
            this.total = page.total || 0
            this.fetchExchangeCounts()
          }
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    fetchExchangeCounts() {
      // 汇总各商品兑换次数
      const map = {}
      const requests = this.tableData.map(item => {
        return getExchangeListByGoods(item.id).then(res => {
          if (res.code === 200 || res.code === '200') {
            const list = Array.isArray(res.data) ? res.data : []
            map[item.id] = list.filter(e => e.goodsId === item.id && e.status !== 2).length
          }
        }).catch(() => {})
      })
      Promise.all(requests).then(() => {
        this.exchangeCountMap = map
      })
    },
    handleQuery() {
      this.pageNum = 1
      this.fetchData()
    },
    handleReset() {
      this.queryForm = { name: '', type: '', status: '' }
      this.pageNum = 1
      this.fetchData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增商品'
      this.formData = {
        id: null,
        name: '',
        description: '',
        points: 100,
        stock: 0,
        image: '',
        type: 'goods',
        status: 1
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑商品'
      this.formData = {
        id: row.id,
        name: row.name,
        description: row.description || '',
        points: row.points || 0,
        stock: row.stock || 0,
        image: row.image || '',
        type: row.type || 'goods',
        status: row.status
      }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = { ...this.formData }
        const action = data.id ? updateGoods(data) : saveGoods(data)
        action.then(res => {
          this.submitLoading = false
          if (res.code === 200 || res.code === '200') {
            this.$message.success(this.dialogTitle + '成功')
            this.dialogVisible = false
            this.fetchData()
          } else {
            this.$message.error(res.message || '操作失败')
          }
        }).catch(() => {
          this.submitLoading = false
        })
      })
    },
    handleDialogClose() {
      this.$refs.dataForm && this.$refs.dataForm.resetFields()
    },
    handleDelete(row) {
      this.$confirm(`确定要删除商品「${row.name}」吗？删除后不可恢复。`, '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGoods(row.id).then(res => {
          if (res.code === 200 || res.code === '200') {
            this.$message.success('删除成功')
            this.fetchData()
          } else {
            this.$message.error(res.message || '删除失败')
          }
        })
      }).catch(() => {})
    },
    handleToggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1
      const actionText = newStatus === 1 ? '上架' : '下架'
      this.$confirm(`确定要${actionText}「${row.name}」吗？`, '状态切换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        updateGoods({ id: row.id, status: newStatus }).then(res => {
          if (res.code === 200 || res.code === '200') {
            this.$message.success(`${actionText}成功`)
            this.fetchData()
          } else {
            this.$message.error(res.message || '操作失败')
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(val) {
      this.selectedRows = val
    },
    handleBatchOn() {
      if (!this.selectedRows.length) {
        this.$message.warning('请先选择要上架的商品')
        return
      }
      const ids = this.selectedRows.filter(r => r.status !== 1).map(r => r.id)
      if (!ids.length) {
        this.$message.warning('所选商品已全部上架')
        return
      }
      Promise.all(ids.map(id => updateGoods({ id, status: 1 }))).then(results => {
        const failed = results.filter(r => r.code !== 200 && r.code !== '200')
        if (failed.length === 0) {
          this.$message.success(`成功上架 ${ids.length} 个商品`)
          this.fetchData()
        } else {
          this.$message.error(`${failed.length} 个商品上架失败`)
        }
      })
    },
    handleBatchOff() {
      if (!this.selectedRows.length) {
        this.$message.warning('请先选择要下架的商品')
        return
      }
      const ids = this.selectedRows.filter(r => r.status !== 0).map(r => r.id)
      if (!ids.length) {
        this.$message.warning('所选商品已全部下架')
        return
      }
      Promise.all(ids.map(id => updateGoods({ id, status: 0 }))).then(results => {
        const failed = results.filter(r => r.code !== 200 && r.code !== '200')
        if (failed.length === 0) {
          this.$message.success(`成功下架 ${ids.length} 个商品`)
          this.fetchData()
        } else {
          this.$message.error(`${failed.length} 个商品下架失败`)
        }
      })
    },
    handleAdjustStock(row) {
      this.stockForm = {
        id: row.id,
        name: row.name,
        currentStock: row.stock || 0,
        adjustType: 'set',
        adjustValue: row.stock || 0,
        remark: ''
      }
      this.stockDialogVisible = true
    },
    handleStockSubmit() {
      let newStock = 0
      if (this.stockForm.adjustType === 'set') {
        newStock = this.stockForm.adjustValue
      } else if (this.stockForm.adjustType === 'add') {
        newStock = this.stockForm.currentStock + this.stockForm.adjustValue
      } else {
        newStock = Math.max(0, this.stockForm.currentStock - this.stockForm.adjustValue)
      }
      updateGoods({ id: this.stockForm.id, stock: newStock }).then(res => {
        if (res.code === 200 || res.code === '200') {
          this.$message.success('库存调整成功')
          this.stockDialogVisible = false
          this.fetchData()
        } else {
          this.$message.error(res.message || '库存调整失败')
        }
      })
    },
    getImageName(path) {
      if (!path) return ''
      return path.replace(/^.*[\\/]/, '')
    },
    handleImgError(e) {
      e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2YzZjRmNiIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTAzOTk5IiBmb250LXNpemU9IjEyIj5JbWFnZTwvdGV4dD48L3N2Zz4='
    },
    handleTypeChange(val) {
      if (val === 'course') {
        this.formData.stock = 0
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.points-goods-container {
  .toolbar {
    margin-bottom: 0;
  }
  .table-toolbar {
    margin-bottom: 14px;
    display: flex;
    gap: 10px;
  }
  .table-wrapper {
    margin-top: 0;
  }
  .pagination {
    margin-top: 16px;
    text-align: right;
  }
  .goods-thumb {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
    border: 1px solid #ebeef5;
  }
  .no-image {
    color: #c0c4cc;
    font-size: 12px;
  }
  .points-text {
    color: #e6a23c;
    font-weight: bold;
  }
  .stock-low {
    color: #f56c6c;
    font-weight: bold;
  }
  .img-preview {
    width: 120px;
    height: 120px;
    object-fit: cover;
    border-radius: 4px;
    border: 1px solid #ebeef5;
  }
  .image-tip {
    margin-top: 6px;
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
  }
}
</style>
