<template>
  <div class="equipment-list-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="toolbar">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="器材名称">
            <el-input v-model="queryForm.equipmentName" placeholder="请输入器材名称" clearable></el-input>
          </el-form-item>
          <el-form-item label="器材类型">
            <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 140px">
              <el-option label="有氧" value="有氧"></el-option>
              <el-option label="力量" value="力量"></el-option>
              <el-option label="其他" value="其他"></el-option>
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
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增器材</el-button>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <el-table :data="tableData" border :style="{ width: '100%' }" v-loading="loading">
          <el-table-column label="器材图片" width="110" align="center">
            <template slot-scope="scope">
              <div class="img-cell" @click="handlePreview(scope.row)">
                <img
                  v-if="scope.row.image"
                  :src="getImageUrl(scope.row.image)"
                  :alt="scope.row.equipmentName"
                  class="equipment-thumb"
                  @error="handleImgError"
                />
                <div v-else class="no-image-cell">
                  <i class="el-icon-picture-outline"></i>
                  <span>暂无图片</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="equipmentId" label="器材 ID" width="80" align="center"></el-table-column>
          <el-table-column prop="equipmentName" label="器材名称" min-width="140"></el-table-column>
          <el-table-column prop="type" label="器材类型" width="90" align="center">
            <template slot-scope="scope">
              <el-tag size="small" :type="getTypeTag(scope.row.type)">{{ scope.row.type || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="brand" label="品牌" width="110"></el-table-column>
          <el-table-column prop="model" label="型号" width="110"></el-table-column>
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ scope.row.status || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="位置" width="110"></el-table-column>
          <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip></el-table-column>
          <el-table-column prop="purchaseDate" label="购买日期" width="110" align="center"></el-table-column>
          <el-table-column label="操作" fixed="right" width="200" align="center">
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="640px" @close="handleDialogClose">
      <el-form ref="dataForm" :model="formData" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="器材名称" prop="equipmentName">
              <el-input v-model="formData.equipmentName" placeholder="请输入器材名称" maxlength="50"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="器材类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择" style="width: 100%">
                <el-option label="有氧" value="有氧"></el-option>
                <el-option label="力量" value="力量"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="formData.brand" placeholder="请输入品牌" maxlength="50"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="formData.model" placeholder="请输入型号" maxlength="50"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%">
                <el-option label="可用" value="可用"></el-option>
                <el-option label="维修中" value="维修中"></el-option>
                <el-option label="已报废" value="已报废"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-date-picker v-model="formData.purchaseDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="位置" prop="location">
          <el-input v-model="formData.location" placeholder="如：A区-01号" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="器材图片" prop="image">
          <div class="image-upload-row">
            <div v-if="formData.image" class="image-preview-box">
              <img :src="getImageUrl(formData.image)" alt="器材图片" class="upload-preview" @error="handleImgError" />
              <div class="image-overlay" @click="handleRemoveImage">
                <i class="el-icon-delete"></i>
              </div>
            </div>
            <el-upload
              v-else
              class="image-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :http-request="handleUpload"
              accept="image/jpeg,image/png,image/gif,image/webp"
            >
              <i class="el-icon-plus uploader-icon"></i>
              <div class="uploader-text">点击上传</div>
            </el-upload>
            <div class="upload-tip">
              <span>支持 JPG、PNG、GIF、WebP 格式，文件不超过 5MB</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入器材描述（选填）" maxlength="300" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 图片大图预览 -->
    <el-dialog title="器材图片" :visible.sync="previewVisible" width="600px" append-to-body>
      <div class="preview-container">
        <img v-if="previewImage" :src="getImageUrl(previewImage)" alt="器材图片" class="preview-img" @error="handleImgError" />
        <div v-else class="preview-empty">暂无图片</div>
      </div>
      <div slot="footer" class="preview-footer">
        <span class="preview-name">{{ previewName }}</span>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEquipmentPage, addEquipment, updateEquipment, deleteEquipment, uploadEquipmentImage } from '@/api/equipment'

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
      submitLoading: false,
      uploadLoading: false,
      formData: {
        equipmentId: null,
        equipmentName: '',
        type: '',
        brand: '',
        model: '',
        status: '可用',
        purchaseDate: '',
        location: '',
        description: '',
        image: ''
      },
      rules: {
        equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择器材类型', trigger: 'change' }]
      },
      previewVisible: false,
      previewImage: '',
      previewName: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        ...this.queryForm
      }
      Object.keys(params).forEach(key => {
        if (params[key] === '') delete params[key]
      })
      getEquipmentPage(params).then(response => {
        this.tableData = response.data.records || []
        this.total = response.data.total || 0
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
      this.queryForm = { equipmentName: '', type: '' }
      this.handleQuery()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getList()
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
        description: '',
        image: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm && this.$refs.dataForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑器材'
      this.formData = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm && this.$refs.dataForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除器材「${row.equipmentName}」吗？删除后不可恢复。`, '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteEquipment(row.equipmentId).then(res => {
          if (res.code === 200 || res.code === '200') {
            this.$message.success('删除成功')
            this.getList()
          } else {
            this.$message.error(res.message || '删除失败')
          }
        })
      }).catch(() => {})
    },
    handleSubmit() {
      this.$refs.dataForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.formData.equipmentId ? updateEquipment : addEquipment
        api(this.formData).then(res => {
          this.submitLoading = false
          if (res.code === 200 || res.code === '200') {
            this.$message.success(this.formData.equipmentId ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.getList()
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
    handleSizeChange2(val) {
      this.pageSize = val
      this.getList()
    },
    handleCurrentChange2(val) {
      this.pageNum = val
      this.getList()
    },
    getStatusType(status) {
      const map = { '可用': 'success', '维修中': 'warning', '已报废': 'danger' }
      return map[status] || 'info'
    },
    getTypeTag(type) {
      const map = { '有氧': '', '力量': 'warning', '其他': 'info' }
      return map[type] || 'info'
    },
    getImageUrl(path) {
      if (!path) return ''
      if (path.startsWith('http://') || path.startsWith('https://')) {
        return path
      }
      // 统一为绝对路径并对每段编码，避免「Smith Machine.jpeg」等含空格时请求失败
      const normalized = (path.startsWith('/') ? path : '/' + path).replace(/\/+/g, '/')
      const segs = normalized.split('/').filter(s => s.length > 0)
      return '/' + segs.map(s => encodeURIComponent(s)).join('/')
    },
    handleImgError(e) {
      e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iODAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjZjNmNGY2Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiM5MDM5OTkiIGZvbnQtc2l6ZT0iMTIiPkltYWdlPC90ZXh0Pjwvc3ZnPg=='
    },
    beforeUpload(file) {
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
      const isAllowed = allowedTypes.includes(file.type)
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isAllowed) {
        this.$message.error('仅支持 JPG、PNG、GIF、WebP 格式的图片')
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB')
      }
      return isAllowed && isLt5M
    },
    handleUpload(option) {
      uploadEquipmentImage(option.file).then(res => {
        if (res.code === 200 || res.code === '200') {
          this.formData.image = res.data
          this.$message.success('图片上传成功')
        } else {
          this.$message.error(res.message || '图片上传失败')
        }
      }).catch(err => {
        this.$message.error('图片上传失败：' + (err.message || '网络错误'))
      })
    },
    handleRemoveImage() {
      this.formData.image = ''
    },
    handlePreview(row) {
      this.previewImage = row.image || ''
      this.previewName = row.equipmentName || ''
      this.previewVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.equipment-list-container {
  width: 100%;

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

  // 图片相关样式
  .img-cell {
    width: 80px;
    height: 80px;
    margin: 0 auto;
    border-radius: 6px;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid #ebeef5;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409EFF;
    }
  }

  .equipment-thumb {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  .no-image-cell {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #c0c4cc;

    i {
      font-size: 24px;
      margin-bottom: 4px;
    }

    span {
      font-size: 10px;
    }
  }

  // 对话框内图片上传
  .image-upload-row {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .image-preview-box {
    position: relative;
    width: 140px;
    height: 140px;
    border-radius: 6px;
    overflow: hidden;
    border: 1px solid #dcdfe6;

    .upload-preview {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .image-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      opacity: 0;
      transition: opacity 0.3s;

      i {
        font-size: 24px;
        color: #fff;
      }
    }

    &:hover .image-overlay {
      opacity: 1;
    }
  }

  .image-uploader {
    width: 140px;
    height: 140px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409EFF;
    }

    .uploader-icon {
      font-size: 32px;
      color: #8c939d;
      display: block;
      margin: 28px auto 4px;
    }

    .uploader-text {
      font-size: 12px;
      color: #8c939d;
      text-align: center;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
  }

  // 大图预览
  .preview-container {
    width: 100%;
    min-height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    border-radius: 4px;
    overflow: hidden;
  }

  .preview-img {
    max-width: 100%;
    max-height: 400px;
    object-fit: contain;
    display: block;
  }

  .preview-empty {
    color: #c0c4cc;
    font-size: 14px;
    padding: 60px;
  }

  .preview-footer {
    text-align: center;

    .preview-name {
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>
