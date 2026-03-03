# ShortURL Pro - 短链接服务系统

这是一个完整的短链接服务系统，包含前端和后端实现。

## 功能特性

### 前端
- 功能演示页：输入长链接 → 生成短码 → 可复制短链接
- 管控后台：短码列表页 + CRUD（新增/编辑/删除/启用/禁用）+ 搜索（按短链接名称）
- 按钮防重复提交
- 操作后自动刷新列表

### 后端
- URL缩短功能
- 短链接重定向
- 点击次数统计
- 短链接信息查询
- URL去重
- H2内存数据库存储

## 技术栈

### 前端
- Vue 3.4.0+
- Element Plus 2.4.0+
- Axios 1.6.0+
- Vite 5.0.0+

### 后端
- Java 17+
- Spring Boot 3.2.0+
- Spring Data JPA
- H2 Database

## 快速开始

### 1. 安装前端依赖

```bash
npm install
```

### 2. 构建前端项目

```bash
npm run build
```

### 3. 启动后端服务

```bash
# 进入后端目录
cd backend

# 构建后端项目
mvn clean package

# 运行后端服务
java -jar target/shortlink-backend-1.0.0.jar
```

### 4. 启动前端开发服务器（可选）

```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 上运行。

## API接口

### 1. 创建短链接

**请求**：
- 方法：POST
- 路径：`/api/shorten`
- 内容类型：application/json
- 请求体：
  ```json
  {
    "url": "https://www.example.com/very/long/url/that/needs/shortening"
  }
  ```

**响应**：
```json
{
  "original_url": "https://www.example.com/very/long/url/that/needs/shortening",
  "short_url": "http://localhost:8080/abc123",
  "short_code": "abc123"
}
```

### 2. 访问短链接

**请求**：
- 方法：GET
- 路径：`/{short_code}`

**响应**：
- 重定向到原始URL

### 3. 查询短链接信息

**请求**：
- 方法：GET
- 路径：`/api/info/{short_code}`

**响应**：
```json
{
  "id": 1,
  "short_code": "abc123",
  "original_url": "https://www.example.com/very/long/url/that/needs/shortening",
  "created_at": "2026-03-01T12:00:00",
  "clicks": 5,
  "status": true
}
```

### 4. 获取所有短链接

**请求**：
- 方法：GET
- 路径：`/api/urls`

**响应**：
- 短链接列表

### 5. 更新短链接

**请求**：
- 方法：PUT
- 路径：`/api/update/{short_code}`
- 内容类型：application/json
- 请求体：
  ```json
  {
    "url": "https://www.example.com/updated/url"
  }
  ```

**响应**：
- 更新后的短链接信息

### 6. 更新短链接状态

**请求**：
- 方法：PUT
- 路径：`/api/status/{short_code}`
- 内容类型：application/json
- 请求体：
  ```json
  {
    "status": false
  }
  ```

**响应**：
- 更新后的短链接信息

### 7. 删除短链接

**请求**：
- 方法：DELETE
- 路径：`/api/delete/{short_code}`

**响应**：
- 删除成功消息

## 项目结构

```
ShortLinkFinal/
├── src/             # 前端源码
│   ├── App.vue      # 主应用组件
│   └── main.js      # 前端入口文件
├── backend/         # 后端源码
│   ├── src/main/java/com/shortlink/    # 后端Java代码
│   │   ├── config/  # 配置类
│   │   ├── controller/  # 控制器
│   │   ├── entity/  # 实体类
│   │   ├── repository/  # 仓库接口
│   │   ├── utils/   # 工具类
│   │   └── ShortLinkApplication.java  # 应用主类
│   ├── src/main/resources/  # 资源文件
│   └── pom.xml      # Maven配置文件
├── index.html       # 前端HTML模板
├── package.json     # 前端依赖配置
├── vite.config.js   # Vite配置文件
└── README.md        # 项目说明
```

## 注意事项

1. 本项目使用H2内存数据库，重启服务后数据会丢失。
2. 生产环境部署时，建议使用持久化数据库。
3. 前端默认使用Mock数据进行开发，如需使用真实API，请修改 `App.vue` 中的API调用代码。

## 扩展建议

1. 添加用户认证，支持个人短链接管理
2. 添加自定义短码功能
3. 添加短链接过期时间设置
4. 添加访问统计和分析功能
5. 部署到云服务器，使用域名访问