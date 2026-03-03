<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>ShortURL Pro</h1>
          <div v-if="!isLoggedIn">
            <el-form :model="loginForm" inline @submit.prevent="handleLogin">
              <el-form-item label="用户名">
                <el-input v-model="loginForm.username" placeholder="请输入用户名" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleLogin" :loading="isLoggingIn">登录</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div v-else class="user-info">
            <span>欢迎，{{ loginForm.username }}</span>
            <el-button @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <div v-if="!isLoggedIn" class="login-prompt">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>请先登录</span>
              </div>
            </template>
            <p>请输入账号密码登录后使用短链接服务</p>
          </el-card>
        </div>
        <div v-else>
          <el-menu :default-active="activeTab" class="el-menu-demo" mode="horizontal" @select="handleTabSelect">
            <el-menu-item index="demo">功能演示</el-menu-item>
            <el-menu-item index="admin">管控后台</el-menu-item>
          </el-menu>
          <!-- 功能演示页 -->
          <div v-if="activeTab === 'demo'" class="demo-page">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>URL 缩短工具</span>
                </div>
              </template>
              <el-form :model="demoForm" label-width="80px" @submit.prevent="handleShorten">
                <el-form-item label="原始URL">
                  <el-input
                    v-model="demoForm.url"
                    placeholder="请输入需要缩短的URL"
                    style="width: 100%"
                    :disabled="isSubmitting"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleShorten" :loading="isSubmitting">
                    生成短链接
                  </el-button>
                </el-form-item>
                <el-form-item v-if="shortUrl" label="短链接">
                  <el-input
                    v-model="shortUrl"
                    style="width: 100%"
                    readonly
                  >
                    <template #append>
                      <el-button @click="copyShortUrl">复制</el-button>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 管控后台 -->
          <div v-else-if="activeTab === 'admin'" class="admin-page">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>短链接管理</span>
                  <el-button type="primary" @click="handleAdd">新增短链接</el-button>
                </div>
              </template>
              <el-input
                v-model="searchQuery"
                placeholder="按短链接名称搜索"
                style="width: 300px; margin-bottom: 20px"
                @input="handleSearch"
              >
                <template #append>
                  <el-button @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
              <el-table :data="filteredUrls" style="width: 100%">
                <el-table-column prop="short_code" label="短码" width="120" />
                <el-table-column prop="original_url" label="原始URL" show-overflow-tooltip />
                <el-table-column prop="short_url" label="短链接" show-overflow-tooltip />
                <el-table-column prop="created_at" label="创建时间" width="180" />
                <el-table-column prop="clicks" label="点击次数" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-switch
                      v-model="scope.row.status"
                      active-text="启用"
                      inactive-text="禁用"
                      @change="handleStatusChange(scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </div>
      </el-main>
    </el-container>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增短链接' : '编辑短链接'"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="原始URL">
          <el-input
            v-model="form.original_url"
            placeholder="请输入URL"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="dialogType === 'edit'" label="短码">
          <el-input v-model="form.short_code" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDialogConfirm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      activeTab: 'demo',
      isLoggedIn: false,
      loginForm: {
        username: '',
        password: ''
      },
      isLoggingIn: false,
      demoForm: {
        url: ''
      },
      shortUrl: '',
      isSubmitting: false,
      searchQuery: '',
      urls: [
        {
          short_code: 'abc123',
          original_url: 'https://www.example.com/very/long/url/1',
          short_url: 'http://localhost:8080/abc123',
          created_at: '2026-03-01T10:00:00',
          clicks: 10,
          status: true
        },
        {
          short_code: 'def456',
          original_url: 'https://www.example.com/very/long/url/2',
          short_url: 'http://localhost:8080/def456',
          created_at: '2026-03-01T11:00:00',
          clicks: 5,
          status: true
        },
        {
          short_code: 'ghi789',
          original_url: 'https://www.example.com/very/long/url/3',
          short_url: 'http://localhost:8080/ghi789',
          created_at: '2026-03-01T12:00:00',
          clicks: 0,
          status: false
        }
      ],
      dialogVisible: false,
      dialogType: 'add',
      form: {
        original_url: '',
        short_code: ''
      }
    }
  },
  computed: {
    filteredUrls() {
      if (!this.searchQuery) {
        return this.urls
      }
      return this.urls.filter(url => 
        url.short_code.includes(this.searchQuery)
      )
    }
  },
  methods: {
    handleTabSelect(key) {
      this.activeTab = key
    },
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        this.$message.warning('请输入用户名和密码')
        return
      }
      
      this.isLoggingIn = true
      try {
        const response = await this.$axios.post('http://localhost:8080/api/login', this.loginForm)
        this.isLoggedIn = true
        this.$message.success('登录成功')
      } catch (error) {
        console.error('登录错误:', error)
        if (error.response) {
          this.$message.error(error.response.data || '登录失败')
        } else {
          this.$message.error('登录失败，请检查网络连接')
        }
      } finally {
        this.isLoggingIn = false
      }
    },
    handleLogout() {
      this.isLoggedIn = false
      this.loginForm = { username: '', password: '' }
      this.$message.success('退出登录成功')
    },
    async handleShorten() {
      if (!this.demoForm.url) {
        this.$message.warning('请输入URL')
        return
      }
      
      this.isSubmitting = true
      try {
        // 真实API调用
        console.log('发送请求到:', 'http://localhost:8080/api/shorten')
        console.log('请求数据:', { url: this.demoForm.url })
        const response = await this.$axios.post('http://localhost:8080/api/shorten', { url: this.demoForm.url })
        console.log('响应数据:', response.data)
        this.shortUrl = response.data.short_url
        
        // 将新生成的短链接添加到 urls 数组中，以便在管控后台显示
        const newUrl = {
          short_code: response.data.short_code,
          original_url: response.data.original_url,
          short_url: response.data.short_url,
          created_at: new Date().toISOString(),
          clicks: 0,
          status: true
        }
        this.urls.unshift(newUrl)
        
        this.$message.success('短链接生成成功')
      } catch (error) {
        console.error('错误信息:', error)
        if (error.response) {
          console.error('响应错误:', error.response)
        } else if (error.request) {
          console.error('请求错误:', error.request)
        } else {
          console.error('其他错误:', error.message)
        }
        this.$message.error('生成失败，请稍后重试')
      } finally {
        this.isSubmitting = false
      }
    },
    copyShortUrl() {
      navigator.clipboard.writeText(this.shortUrl)
        .then(() => {
          this.$message.success('复制成功')
        })
        .catch(() => {
          this.$message.error('复制失败')
        })
    },
    handleSearch() {
      // 搜索逻辑已在computed中实现
    },
    handleAdd() {
      this.dialogType = 'add'
      this.form = {
        original_url: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleDialogConfirm() {
      if (!this.form.original_url) {
        this.$message.warning('请输入URL')
        return
      }
      
      try {
        if (this.dialogType === 'add') {
          // 真实API调用
          const response = await this.$axios.post('http://localhost:8080/api/shorten', { url: this.form.original_url })
          const newUrl = {
            short_code: response.data.short_code,
            original_url: response.data.original_url,
            short_url: response.data.short_url,
            created_at: new Date().toISOString(),
            clicks: 0,
            status: true
          }
          this.urls.unshift(newUrl)
          this.$message.success('新增成功')
        } else {
          // 真实API调用
          await this.$axios.put(`http://localhost:8080/api/update/${this.form.short_code}`, { url: this.form.original_url })
          const index = this.urls.findIndex(url => url.short_code === this.form.short_code)
          if (index !== -1) {
            this.urls[index] = { ...this.form }
            this.$message.success('编辑成功')
          }
        }
        this.dialogVisible = false
      } catch (error) {
        this.$message.error('操作失败，请稍后重试')
        console.error(error)
      }
    },
    handleDelete(row) {
      this.$confirm('确定要删除这条短链接吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 真实API调用
          await this.$axios.delete(`http://localhost:8080/api/delete/${row.short_code}`)
          const index = this.urls.findIndex(url => url.short_code === row.short_code)
          if (index !== -1) {
            this.urls.splice(index, 1)
            this.$message.success('删除成功')
          }
        } catch (error) {
          this.$message.error('删除失败，请稍后重试')
          console.error(error)
        }
      }).catch(() => {
        // 取消删除
      })
    },
    handleStatusChange(row) {
      // 真实API调用
      this.$axios.put(`http://localhost:8080/api/status/${row.short_code}`, { status: row.status })
      this.$message.success(`状态已${row.status ? '启用' : '禁用'}`)
    }
  }
}
</script>

<style>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.demo-page,
.admin-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>