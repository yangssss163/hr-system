# HR 管理系统 — 前后端 JSON 接口文档

> 版本：v1.0 | 更新日期：2026-07-12

---

## 一、通用规范

### 1.1 基础路径

```
开发环境: http://localhost:8080/api
生产环境: /api（Nginx 反向代理）
```

### 1.2 请求头

| 键 | 值 | 说明 |
|---|---|---|
| `Content-Type` | `application/json` | JSON 请求 |
| `Authorization` | `Bearer {token}` | JWT 认证（除登录外必传） |
| `Content-Type` | `multipart/form-data` | 文件上传时使用 |

### 1.3 统一响应格式

```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```

### 1.4 业务状态码

| code | 说明 | 处理方式 |
|---|---|---|
| 200 | 操作成功 | 正常处理 data |
| 400 | 参数校验失败 | 检查请求参数 |
| 401 | 未登录 / Token 过期 | 跳转登录页 |
| 403 | 无权限访问 | 提示无权限 |
| 404 | 资源不存在 | 提示资源不存在 |
| 500 | 服务器内部错误 | 提示系统繁忙 |
| 1001 | 用户名或密码错误 | 提示重新输入 |
| 1002 | 用户已禁用 | 提示联系管理员 |
| 1003 | Token 无效或已过期 | 跳转登录页 |
| 1004 | 旧密码不正确 | 提示重新输入 |
| 2001 | 数据已存在（工号/用户名重复等） | 提示已存在 |
| 2002 | 存在关联数据，无法删除 | 提示先删除关联 |
| 2003 | 导入文件格式错误 | 提示检查文件 |
| 3001 | 考勤记录不存在 | 提示记录不存在 |

### 1.5 分页请求参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `page` | int | 否 | 当前页码，默认 1 |
| `pageSize` | int | 否 | 每页条数，默认 10 |

### 1.6 分页响应格式

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "records": []
  }
}
```

---

## 二、认证模块 `/api/auth`

### 2.1 登录

```
POST /api/auth/login
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | String | 是 | 用户名/工号 |
| `password` | String | 是 | 密码（明文传输，后端 BCrypt 校验） |

**请求体：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应 `data`：**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400000
}
```

### 2.2 登出

```
POST /api/auth/logout
```

**响应：** `data: null`

### 2.3 获取当前用户信息

```
GET /api/auth/userinfo
```

**响应字段：**

| 字段 | 类型 | 说明 |
|---|---|---|
| `userId` | Long | 用户ID |
| `username` | String | 登录用户名 |
| `realName` | String | 真实姓名 |
| `avatar` | String | 头像URL |
| `deptId` | Long | 所属部门ID |
| `deptName` | String | 所属部门名称 |
| `email` | String | 邮箱 |
| `phone` | String | 手机号 |
| `roles` | String[] | 角色编码列表 |
| `permissions` | String[] | 权限标识列表 |

**响应 `data`：**

```json
{
  "userId": 1,
  "username": "admin",
  "realName": "管理员",
  "avatar": "https://xxx.com/avatar.png",
  "deptId": 1,
  "deptName": "技术部",
  "email": "admin@company.com",
  "phone": "13800138000",
  "roles": ["admin"],
  "permissions": ["system:user:list", "system:user:create", "employee:list"]
}
```

### 2.4 获取当前用户菜单

```
GET /api/auth/menus
```

**响应 `data`：**

```json
[
  {
    "id": 1,
    "parentId": 0,
    "name": "系统管理",
    "type": 1,
    "path": "/system",
    "icon": "Setting",
    "sort": 1,
    "children": [
      {
        "id": 2,
        "parentId": 1,
        "name": "用户管理",
        "type": 2,
        "path": "/system/user",
        "component": "system/user/UserManage",
        "permission": "system:user:list",
        "icon": "User",
        "sort": 1,
        "children": [
          {
            "id": 3,
            "parentId": 2,
            "name": "新增",
            "type": 3,
            "permission": "system:user:create",
            "sort": 1
          }
        ]
      }
    ]
  }
]
```

### 2.5 修改密码

```
PUT /api/auth/password
```

**请求体：**

```json
{
  "oldPassword": "old123",
  "newPassword": "new456"
}
```

---

## 三、系统管理模块

### 3.1 公司架构 `/api/companies`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/companies/tree` | 获取公司树 |
| GET | `/api/companies/{id}` | 获取公司详情 |
| POST | `/api/companies` | 创建公司 |
| PUT | `/api/companies/{id}` | 编辑公司 |
| DELETE | `/api/companies/{id}` | 删除公司 |

#### 公司树响应

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "name": "总公司",
      "code": "HQ",
      "parentId": 0,
      "sort": 0,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "上海分公司",
          "code": "SH",
          "parentId": 1,
          "sort": 1,
          "status": 1
        }
      ]
    }
  ]
}
```

#### 创建/编辑公司请求体

```json
{
  "name": "上海分公司",
  "code": "SH",
  "parentId": 1,
  "sort": 1,
  "status": 1
}
```

### 3.2 部门架构 `/api/depts`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/depts/tree` | 获取部门树（支持 `?companyId=1` 筛选） |
| GET | `/api/depts/{id}` | 获取部门详情 |
| POST | `/api/depts` | 创建部门 |
| PUT | `/api/depts/{id}` | 编辑部门 |
| DELETE | `/api/depts/{id}` | 删除部门 |

#### 部门树响应

```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "companyId": 1,
      "companyName": "总公司",
      "name": "技术部",
      "parentId": 0,
      "sort": 0,
      "status": 1,
      "children": [
        {
          "id": 2,
          "companyId": 1,
          "name": "前端组",
          "parentId": 1,
          "sort": 1,
          "status": 1
        }
      ]
    }
  ]
}
```

#### 创建/编辑部门请求体

```json
{
  "companyId": 1,
  "name": "前端组",
  "parentId": 1,
  "sort": 1,
  "status": 1
}
```

### 3.3 职位管理 `/api/positions`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/positions` | 职位列表（分页，支持 `?deptId=&keyword=` 搜索） |
| GET | `/api/positions/{id}` | 职位详情 |
| POST | `/api/positions` | 创建职位 |
| PUT | `/api/positions/{id}` | 编辑职位 |
| DELETE | `/api/positions/{id}` | 删除职位 |

#### 职位列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "高级前端工程师",
    "deptId": 1,
    "deptName": "技术部",
    "sort": 1,
    "status": 1,
    "createTime": "2026-07-01 10:00:00"
  }
]
```

#### 创建/编辑职位请求体

```json
{
  "name": "高级前端工程师",
  "deptId": 1,
  "sort": 1,
  "status": 1
}
```

### 3.4 用户管理 `/api/users`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/users` | 用户列表（分页，`?keyword=&deptId=&status=`） |
| GET | `/api/users/{id}` | 用户详情 |
| POST | `/api/users` | 创建用户 |
| PUT | `/api/users/{id}` | 编辑用户 |
| DELETE | `/api/users/{id}` | 删除用户 |
| PUT | `/api/users/{id}/roles` | 分配角色 |

#### 用户列表响应 `records`

```json
[
  {
    "id": 1,
    "username": "zhangsan",
    "realName": "张三",
    "deptId": 1,
    "deptName": "技术部",
    "phone": "13800138000",
    "email": "zhangsan@company.com",
    "avatar": "https://xxx.com/avatar.png",
    "status": 1,
    "lastLoginTime": "2026-07-12 09:00:00",
    "createTime": "2026-06-01 10:00:00",
    "roles": [
      { "id": 1, "name": "普通用户", "code": "user" }
    ]
  }
]
```

#### 创建/编辑用户请求体

**请求字段：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | String | 是 | 登录用户名 |
| `password` | String | 创建时必填 | 密码（明文，后端 BCrypt 加密）；编辑时不传则不修改密码 |
| `realName` | String | 是 | 真实姓名 |
| `deptId` | Long | 是 | 部门ID |
| `phone` | String | 否 | 手机号 |
| `email` | String | 否 | 邮箱 |
| `roleIds` | Long[] | 是 | 角色ID列表 |
| `status` | Integer | 是 | 状态：1启用 0禁用 |

```json
{
  "username": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "deptId": 1,
  "phone": "13800138000",
  "email": "zhangsan@company.com",
  "roleIds": [1, 2],
  "status": 1
}
```

> 注意：编辑时 `password` 可选，不传则不修改密码。

#### 分配角色请求体

```json
{
  "roleIds": [1, 2]
}
```

### 3.5 角色权限管理 `/api/roles`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/roles` | 角色列表（分页） |
| GET | `/api/roles/all` | 全部角色（不分页，用于下拉选择） |
| GET | `/api/roles/{id}` | 角色详情（含菜单权限） |
| POST | `/api/roles` | 创建角色 |
| PUT | `/api/roles/{id}` | 编辑角色 |
| DELETE | `/api/roles/{id}` | 删除角色 |
| PUT | `/api/roles/{id}/menus` | 分配菜单权限 |

#### 全部角色响应

```json
[
  { "id": 1, "name": "超级管理员", "code": "admin" },
  { "id": 2, "name": "HR 专员", "code": "hr" }
]
```

#### 角色详情响应

```json
{
  "id": 1,
  "name": "超级管理员",
  "code": "admin",
  "description": "拥有所有权限",
  "status": 1,
  "menuIds": [1, 2, 3, 4, 5],
  "createTime": "2026-06-01 10:00:00"
}
```

#### 创建/编辑角色请求体

```json
{
  "name": "HR 专员",
  "code": "hr",
  "description": "负责人力资源管理",
  "status": 1
}
```

#### 分配菜单权限请求体

```json
{
  "menuIds": [1, 2, 3, 10, 11, 12]
}
```

### 3.6 菜单管理 `/api/menus`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/menus/tree` | 菜单树（全部） |
| GET | `/api/menus/{id}` | 菜单详情 |
| POST | `/api/menus` | 创建菜单 |
| PUT | `/api/menus/{id}` | 编辑菜单 |
| DELETE | `/api/menus/{id}` | 删除菜单 |

#### 菜单树响应

```json
[
  {
    "id": 1,
    "parentId": 0,
    "name": "系统管理",
    "type": 1,
    "path": "/system",
    "component": "",
    "permission": "",
    "icon": "Setting",
    "sort": 1,
    "visible": true,
    "children": [
      {
        "id": 2,
        "parentId": 1,
        "name": "用户管理",
        "type": 2,
        "path": "/system/user",
        "component": "system/user/UserManage",
        "permission": "system:user:list",
        "icon": "User",
        "sort": 1,
        "visible": true,
        "children": [
          {
            "id": 3,
            "parentId": 2,
            "name": "新增用户",
            "type": 3,
            "permission": "system:user:create",
            "sort": 1,
            "visible": true
          }
        ]
      }
    ]
  }
]
```

#### 创建/编辑菜单请求体

```json
{
  "parentId": 1,
  "name": "用户管理",
  "type": 2,
  "path": "/system/user",
  "component": "system/user/UserManage",
  "permission": "system:user:list",
  "icon": "User",
  "sort": 1,
  "visible": true
}
```

### 3.7 字段配置 `/api/field-configs`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/field-configs` | 字段配置列表（`?module=` 按模块筛选） |
| PUT | `/api/field-configs` | 批量保存字段配置 |

#### 字段配置列表响应

```json
[
  {
    "id": 1,
    "module": "employee",
    "fieldKey": "phone",
    "fieldName": "手机号",
    "visible": true,
    "required": true,
    "sort": 3
  }
]
```

#### 批量保存请求体

```json
[
  {
    "id": 1,
    "module": "employee",
    "fieldKey": "phone",
    "fieldName": "手机号",
    "visible": true,
    "required": true,
    "sort": 3
  }
]
```

---

## 四、员工管理模块

### 4.1 员工花名册 `/api/employees`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/employees` | 花名册列表（分页） |
| GET | `/api/employees/{id}` | 员工详情 |
| POST | `/api/employees` | 新增员工 |
| PUT | `/api/employees/{id}` | 编辑员工 |
| DELETE | `/api/employees/{id}` | 删除员工 |
| POST | `/api/employees/import` | 批量导入（Excel） |
| GET | `/api/employees/export` | 导出 Excel |
| DELETE | `/api/employees/batch` | 批量删除 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `keyword` | string | 姓名/工号搜索 |
| `deptId` | int | 部门筛选 |
| `positionId` | int | 职位筛选 |
| `companyId` | int | 公司筛选 |
| `status` | int | 状态：1在职 2离职 3试用 |
| `entryDateStart` | date | 入职日期起 |
| `entryDateEnd` | date | 入职日期止 |

#### 列表响应 `records`

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | Long | 员工ID |
| `userId` | Long | 关联系统用户ID |
| `empNo` | String | 工号 |
| `name` | String | 姓名 |
| `gender` | Integer | 性别：1男 2女 |
| `phone` | String | 手机号 |
| `email` | String | 邮箱 |
| `deptId` | Long | 部门ID |
| `deptName` | String | 部门名称 |
| `positionId` | Long | 职位ID |
| `positionName` | String | 职位名称 |
| `companyId` | Long | 公司ID |
| `companyName` | String | 公司名称 |
| `entryDate` | String | 入职日期（yyyy-MM-dd） |
| `status` | Integer | 状态：1在职 2离职 3试用 |
| `createTime` | String | 创建时间（yyyy-MM-dd HH:mm:ss） |

```json
[
  {
    "id": 1,
    "userId": 10,
    "empNo": "EMP001",
    "name": "张三",
    "gender": 1,
    "phone": "13800138000",
    "email": "zhangsan@company.com",
    "deptId": 1,
    "deptName": "技术部",
    "positionId": 1,
    "positionName": "高级前端工程师",
    "companyId": 1,
    "companyName": "总公司",
    "entryDate": "2025-03-01",
    "status": 1,
    "createTime": "2025-03-01 09:00:00"
  }
]
```

#### 员工详情响应

```json
{
  "id": 1,
  "userId": 10,
  "empNo": "EMP001",
  "name": "张三",
  "gender": 1,
  "idCard": "310xxx199001011234",
  "birthday": "1990-01-01",
  "phone": "13800138000",
  "email": "zhangsan@company.com",
  "deptId": 1,
  "deptName": "技术部",
  "positionId": 1,
  "positionName": "高级前端工程师",
  "companyId": 1,
  "companyName": "总公司",
  "entryDate": "2025-03-01",
  "status": 1,
  "createTime": "2025-03-01 09:00:00",
  "updateTime": "2026-06-15 14:00:00"
}
```

#### 创建/编辑员工请求体

**请求字段：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `empNo` | String | 是 | 工号 |
| `name` | String | 是 | 姓名 |
| `gender` | Integer | 是 | 性别：1男 2女 |
| `idCard` | String | 否 | 身份证号 |
| `birthday` | String | 否 | 出生日期（yyyy-MM-dd） |
| `phone` | String | 是 | 手机号 |
| `email` | String | 否 | 邮箱 |
| `deptId` | Long | 是 | 部门ID |
| `positionId` | Long | 是 | 职位ID |
| `companyId` | Long | 是 | 公司ID |
| `entryDate` | String | 是 | 入职日期（yyyy-MM-dd） |
| `status` | Integer | 是 | 状态：1在职 2离职 3试用 |

```json
{
  "empNo": "EMP001",
  "name": "张三",
  "gender": 1,
  "idCard": "310xxx199001011234",
  "birthday": "1990-01-01",
  "phone": "13800138000",
  "email": "zhangsan@company.com",
  "deptId": 1,
  "positionId": 1,
  "companyId": 1,
  "entryDate": "2025-03-01",
  "status": 1
}
```

#### 批量导入（multipart/form-data）

```
POST /api/employees/import
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | File | 是 | Excel 文件 |

**响应 `data`：**

```json
{
  "successCount": 50,
  "failCount": 2,
  "failDetails": [
    { "row": 5, "reason": "工号已存在" }
  ]
}
```

#### 批量删除请求体

```json
{
  "ids": [1, 2, 3]
}
```

### 4.2 异动管理 `/api/transfers`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/transfers` | 异动列表（分页） |
| GET | `/api/transfers/{id}` | 异动详情 |
| POST | `/api/transfers` | 创建异动 |
| PUT | `/api/transfers/{id}/revoke` | 撤销异动 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `employeeId` | int | 员工ID |
| `transferType` | string | 异动类型：`regular`转正/`transfer`调岗/`promote`晋升/`dimission`离职/`rehire`返聘 |
| `startDate` | date | 生效日期起 |
| `endDate` | date | 生效日期止 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "empNo": "EMP001",
    "employeeName": "张三",
    "transferType": "promote",
    "transferTypeName": "晋升",
    "beforeDeptId": 2,
    "beforeDeptName": "前端组",
    "afterDeptId": 1,
    "afterDeptName": "技术部",
    "beforePositionId": 2,
    "beforePositionName": "前端工程师",
    "afterPositionId": 1,
    "afterPositionName": "高级前端工程师",
    "effectiveDate": "2026-07-01",
    "reason": "表现优秀，予以晋升",
    "status": 1,
    "createTime": "2026-06-20 10:00:00"
  }
]
```

#### 创建异动请求体

```json
{
  "employeeId": 1,
  "transferType": "promote",
  "afterDeptId": 1,
  "afterPositionId": 1,
  "effectiveDate": "2026-07-01",
  "reason": "表现优秀，予以晋升"
}
```

### 4.3 账号开通 `/api/accounts`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/accounts/open` | 为员工开通系统账号 |
| PUT | `/api/accounts/{id}/toggle` | 启用/禁用账号 |

#### 开通账号请求体

```json
{
  "employeeId": 1,
  "username": "zhangsan",
  "password": "123456",
  "roleIds": [2]
}
```

---

## 五、招聘管理模块

### 5.1 简历管理 `/api/resumes`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/resumes` | 简历列表（分页） |
| GET | `/api/resumes/{id}` | 简历详情 |
| POST | `/api/resumes` | 创建简历 |
| PUT | `/api/resumes/{id}` | 编辑简历 |
| DELETE | `/api/resumes/{id}` | 删除简历 |
| POST | `/api/resumes/import` | 批量导入 |
| DELETE | `/api/resumes/batch` | 批量删除 |
| POST | `/api/resumes/{id}/invite` | 发送面试邀请 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `keyword` | string | 姓名/电话搜索 |
| `status` | string | `new`新简历/`screening`筛选中/`interview`面试中/`offer`已发offer/`hired`已入职/`eliminated`已淘汰 |
| `education` | string | 学历 |
| `source` | string | 来源渠道 |
| `applyPosition` | string | 应聘职位 |
| `createTimeStart` | date | 创建时间起 |
| `createTimeEnd` | date | 创建时间止 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "李四",
    "phone": "13900139000",
    "email": "lisi@mail.com",
    "gender": 1,
    "education": "本科",
    "school": "清华大学",
    "major": "计算机科学与技术",
    "workYears": 5,
    "applyPosition": "前端工程师",
    "source": "BOSS直聘",
    "status": "interview",
    "resumeFile": "https://minio.xxx.com/resumes/xxx.pdf",
    "createTime": "2026-07-01 10:00:00"
  }
]
```

#### 创建/编辑简历请求体

**请求字段：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | String | 是 | 姓名 |
| `phone` | String | 是 | 手机号 |
| `email` | String | 否 | 邮箱 |
| `gender` | Integer | 否 | 性别：1男 2女 |
| `education` | String | 否 | 学历：`high_school`高中 `junior_college`大专 `bachelor`本科 `master`硕士 `doctor`博士 |
| `school` | String | 否 | 毕业学校 |
| `major` | String | 否 | 专业 |
| `workYears` | Integer | 否 | 工作年限 |
| `applyPosition` | String | 否 | 应聘职位 |
| `source` | String | 否 | 来源渠道 |
| `status` | String | 否 | 状态（默认 `new`） |

```json
{
  "name": "李四",
  "phone": "13900139000",
  "email": "lisi@mail.com",
  "gender": 1,
  "education": "本科",
  "school": "清华大学",
  "major": "计算机科学与技术",
  "workYears": 5,
  "applyPosition": "前端工程师",
  "source": "BOSS直聘",
  "status": "new"
}
```

#### 发送面试邀请请求体

```json
{
  "interviewTime": "2026-07-15 14:00:00",
  "location": "上海市浦东新区XX大厦3楼",
  "templateId": 1,
  "message": "恭喜您通过简历筛选，邀请您参加面试..."
}
```

### 5.2 面试管理 `/api/interviews`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/interviews` | 面试列表（分页） |
| GET | `/api/interviews/{id}` | 面试详情 |
| POST | `/api/interviews` | 安排面试 |
| PUT | `/api/interviews/{id}` | 更改面试时间 |
| POST | `/api/interviews/{id}/checkin` | 面试签到 |
| PUT | `/api/interviews/{id}/evaluate` | 面试评价 |
| PUT | `/api/interviews/{id}/pass` | 通过面试 |
| PUT | `/api/interviews/{id}/eliminate` | 淘汰 |
| PUT | `/api/interviews/{id}/offer` | 发送 Offer |
| PUT | `/api/interviews/{id}/confirm-hire` | 确认入职 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `keyword` | string | 候选人姓名搜索 |
| `result` | string | `pending`待面试/`pass`通过/`fail`淘汰/`offer`已发offer/`hired`已入职 |
| `interviewerId` | int | 面试官ID |
| `interviewDateStart` | date | 面试日期起 |
| `interviewDateEnd` | date | 面试日期止 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "resumeId": 1,
    "candidateName": "李四",
    "applyPosition": "前端工程师",
    "interviewRound": 1,
    "interviewerId": 10,
    "interviewerName": "王五",
    "interviewTime": "2026-07-15 14:00:00",
    "location": "上海市浦东新区XX大厦3楼",
    "result": "pending",
    "score": null,
    "evaluation": null,
    "createTime": "2026-07-10 09:00:00"
  }
]
```

#### 安排面试请求体

```json
{
  "resumeId": 1,
  "interviewRound": 1,
  "interviewerId": 10,
  "interviewTime": "2026-07-15 14:00:00",
  "location": "上海市浦东新区XX大厦3楼"
}
```

#### 面试评价请求体

```json
{
  "score": 8.5,
  "evaluation": "技术基础扎实，沟通能力良好，建议通过。",
  "result": "pass"
}
```

#### 淘汰请求体

```json
{
  "reason": "技术能力不匹配"
}
```

#### 发送 Offer 请求体

```json
{
  "offerSalary": "15K-20K",
  "offerDate": "2026-07-01",
  "remark": "试用期3个月"
}
```

### 5.3 面试题库 `/api/questions`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/questions` | 题库列表（分页） |
| GET | `/api/questions/{id}` | 试题详情 |
| POST | `/api/questions` | 创建试题 |
| PUT | `/api/questions/{id}` | 编辑试题 |
| DELETE | `/api/questions/{id}` | 删除试题 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `keyword` | string | 题目内容搜索 |
| `category` | string | 分类：`technical`技术/`behavioral`行为/`hr`综合 |
| `difficulty` | string | 难度：`easy`简单/`medium`中等/`hard`困难 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "category": "technical",
    "categoryName": "技术",
    "difficulty": "medium",
    "difficultyName": "中等",
    "title": "请解释 Vue 3 的响应式原理",
    "answer": "Vue 3 使用 Proxy 实现响应式...",
    "createTime": "2026-06-01 10:00:00"
  }
]
```

#### 创建/编辑试题请求体

```json
{
  "category": "technical",
  "difficulty": "medium",
  "title": "请解释 Vue 3 的响应式原理",
  "answer": "Vue 3 使用 Proxy 实现响应式..."
}
```

### 5.4 通知模板 `/api/notify-templates`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/notify-templates` | 模板列表（分页） |
| GET | `/api/notify-templates/{id}` | 模板详情 |
| POST | `/api/notify-templates` | 创建模板 |
| PUT | `/api/notify-templates/{id}` | 编辑模板 |
| DELETE | `/api/notify-templates/{id}` | 删除模板 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "面试邀请模板",
    "type": "email",
    "typeName": "邮件",
    "title": "面试邀请通知",
    "content": "尊敬的{name}，恭喜您通过简历筛选...",
    "status": 1,
    "createTime": "2026-06-01 10:00:00"
  }
]
```

#### 创建/编辑模板请求体

```json
{
  "name": "面试邀请模板",
  "type": "email",
  "title": "面试邀请通知",
  "content": "尊敬的{name}，恭喜您通过简历筛选...",
  "status": 1
}
```

### 5.5 面试黑名单 `/api/blacklists`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/blacklists` | 黑名单列表（分页） |
| POST | `/api/blacklists` | 加入黑名单 |
| DELETE | `/api/blacklists/{id}` | 移出黑名单 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "赵六",
    "phone": "13700137000",
    "idCard": "320xxx199201018888",
    "reason": "简历造假",
    "createTime": "2026-06-15 14:00:00"
  }
]
```

#### 加入黑名单请求体

```json
{
  "resumeId": 10,
  "name": "赵六",
  "phone": "13700137000",
  "idCard": "320xxx199201018888",
  "reason": "简历造假"
}
```

### 5.6 招聘报表 `/api/recruitment-reports`

```
GET /api/recruitment-reports/summary
```

**响应 `data`：**

```json
{
  "totalResumes": 500,
  "totalInterviews": 200,
  "totalPassed": 80,
  "totalHired": 30,
  "conversionRate": 15.0,
  "channelStats": [
    { "channel": "BOSS直聘", "count": 150, "rate": 30.0 },
    { "channel": "拉勾", "count": 100, "rate": 20.0 }
  ],
  "monthlyStats": [
    { "month": "2026-01", "resumes": 40, "interviews": 15, "hired": 3 },
    { "month": "2026-02", "resumes": 50, "interviews": 20, "hired": 5 }
  ]
}
```

---

## 六、考勤管理模块

### 6.1 打卡记录 `/api/attendance-records`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/attendance-records` | 打卡记录列表（分页） |
| POST | `/api/attendance-records/import` | 导入打卡记录 |
| PUT | `/api/attendance-records/batch-fix` | 批量修正 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `employeeId` | int | 员工ID |
| `deptId` | int | 部门ID |
| `dateStart` | date | 考勤日期起 |
| `dateEnd` | date | 考勤日期止 |
| `status` | string | `normal`正常/`late`迟到/`early`早退/`absent`缺勤/`overtime`加班 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "empNo": "EMP001",
    "employeeName": "张三",
    "deptName": "技术部",
    "recordDate": "2026-07-01",
    "checkIn": "2026-07-01 08:55:00",
    "checkOut": "2026-07-01 18:05:00",
    "status": "normal",
    "source": "card"
  }
]
```

#### 批量修正请求体

```json
{
  "ids": [1, 2],
  "checkIn": "2026-07-01 09:00:00",
  "checkOut": "2026-07-01 18:00:00",
  "remark": "OA流程审批通过，修正打卡时间"
}
```

### 6.2 异常统计 `/api/attendance-exceptions`

```
GET /api/attendance-exceptions
```

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `deptId` | int | 部门ID |
| `dateStart` | date | 日期起 |
| `dateEnd` | date | 日期止 |

#### 响应 `data`

```json
{
  "total": 100,
  "page": 1,
  "pageSize": 10,
  "records": [
    {
      "id": 1,
      "employeeId": 1,
      "empNo": "EMP001",
      "employeeName": "张三",
      "deptName": "技术部",
      "recordDate": "2026-07-01",
      "type": "late",
      "typeName": "迟到",
      "detail": "迟到30分钟",
      "oaStatus": "已审批"
    }
  ]
}
```

### 6.3 OA 流程管理 `/api/oa-flows`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/oa-flows` | OA流程列表（分页） |
| POST | `/api/oa-flows/import` | 导入OA单据 |
| GET | `/api/oa-flows/{id}` | 查看OA流程详情 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "employeeName": "张三",
    "type": "leave",
    "typeName": "请假",
    "startDate": "2026-07-01",
    "endDate": "2026-07-02",
    "duration": 2,
    "status": "approved",
    "statusName": "已通过",
    "createTime": "2026-06-28 10:00:00"
  }
]
```

### 6.4 取卡规则 `/api/card-rules`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/card-rules` | 规则列表 |
| GET | `/api/card-rules/{id}` | 规则详情 |
| POST | `/api/card-rules` | 创建规则 |
| PUT | `/api/card-rules/{id}` | 编辑规则 |
| DELETE | `/api/card-rules/{id}` | 删除规则 |

#### 列表响应

```json
[
  {
    "id": 1,
    "name": "标准取卡规则",
    "minCardCount": 2,
    "allowOvertime": true,
    "status": 1
  }
]
```

#### 创建/编辑规则请求体

```json
{
  "name": "标准取卡规则",
  "minCardCount": 2,
  "allowOvertime": true,
  "status": 1
}
```

### 6.5 班次设置 `/api/shifts`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/shifts` | 班次列表 |
| GET | `/api/shifts/{id}` | 班次详情 |
| POST | `/api/shifts` | 创建班次 |
| PUT | `/api/shifts/{id}` | 编辑班次 |
| DELETE | `/api/shifts/{id}` | 删除班次 |

#### 列表响应

```json
[
  {
    "id": 1,
    "name": "标准班",
    "startTime": "09:00",
    "endTime": "18:00",
    "lateBuffer": 10,
    "earlyBuffer": 10,
    "status": 1
  }
]
```

#### 创建/编辑班次请求体

```json
{
  "name": "标准班",
  "startTime": "09:00",
  "endTime": "18:00",
  "lateBuffer": 10,
  "earlyBuffer": 10,
  "status": 1
}
```

### 6.6 法定假期 `/api/holidays`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/holidays` | 法定假期列表（`?year=2026`） |
| POST | `/api/holidays` | 创建假期计划 |
| PUT | `/api/holidays/{id}` | 修改计划 |
| POST | `/api/holidays/{id}/copy` | 复制计划到其他年份 |
| DELETE | `/api/holidays/{id}` | 删除计划 |

#### 列表响应

```json
{
  "year": 2026,
  "items": [
    { "id": 1, "name": "元旦", "date": "2026-01-01", "days": 1 },
    { "id": 2, "name": "春节", "date": "2026-02-17", "days": 7 }
  ]
}
```

#### 创建/编辑请求体

```json
{
  "year": 2026,
  "name": "元旦",
  "date": "2026-01-01",
  "days": 1
}
```

### 6.7 假期额度 `/api/leave-quotas`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/leave-quotas` | 假期额度列表（`?employeeId=&year=`） |
| PUT | `/api/leave-quotas/{id}` | 调整额度 |

#### 列表响应

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "empNo": "EMP001",
    "employeeName": "张三",
    "leaveType": "annual",
    "leaveTypeName": "年假",
    "year": 2026,
    "totalDays": 5,
    "usedDays": 2,
    "remainDays": 3
  }
]
```

#### 调整额度请求体

```json
{
  "totalDays": 10
}
```

### 6.8 假期设置 `/api/leave-types`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/leave-types` | 假期类型列表 |
| POST | `/api/leave-types` | 创建假期类型 |
| PUT | `/api/leave-types/{id}` | 编辑假期类型 |
| DELETE | `/api/leave-types/{id}` | 删除假期类型 |

#### 列表响应

```json
[
  {
    "id": 1,
    "name": "年假",
    "code": "annual",
    "defaultDays": 5,
    "enabled": true
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "年假",
  "code": "annual",
  "defaultDays": 5,
  "enabled": true
}
```

### 6.9 考勤报表 `/api/attendance-reports`

#### 考勤明细表

```
GET /api/attendance-reports/detail
```

参数：`deptId`, `dateStart`, `dateEnd`, `employeeId`

#### 考勤汇总表

```
GET /api/attendance-reports/summary
```

参数：`deptId`, `month`（如 `2026-07`）

**响应 `data`：**

```json
{
  "records": [
    {
      "employeeId": 1,
      "empNo": "EMP001",
      "employeeName": "张三",
      "deptName": "技术部",
      "shouldWorkDays": 22,
      "actualWorkDays": 21,
      "lateCount": 1,
      "earlyCount": 0,
      "absentCount": 0,
      "leaveCount": 1,
      "overtimeHours": 5.5
    }
  ]
}
```

### 6.10 考勤扣款 `/api/attendance-deductions`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/attendance-deductions` | 扣款规则列表 |
| PUT | `/api/attendance-deductions/{id}` | 编辑扣款规则 |

#### 列表响应

```json
[
  {
    "id": 1,
    "type": "miss_card",
    "typeName": "漏打卡",
    "deduction": 50,
    "unit": "次",
    "remark": "每次漏打卡扣款50元"
  },
  {
    "id": 2,
    "type": "late",
    "typeName": "迟到/早退",
    "deduction": 30,
    "unit": "次",
    "remark": "每次迟到早退扣款30元"
  }
]
```

---

## 七、绩效管理模块

### 7.1 绩效等级 `/api/perf-levels`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/perf-levels` | 等级列表 |
| POST | `/api/perf-levels` | 创建等级 |
| PUT | `/api/perf-levels/{id}` | 编辑等级 |
| DELETE | `/api/perf-levels/{id}` | 删除等级 |

#### 列表响应

```json
[
  {
    "id": 1,
    "name": "S",
    "scoreMin": 90,
    "scoreMax": 100,
    "coefficient": 1.5,
    "sort": 1
  },
  {
    "id": 2,
    "name": "A",
    "scoreMin": 80,
    "scoreMax": 89,
    "coefficient": 1.2,
    "sort": 2
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "S",
  "scoreMin": 90,
  "scoreMax": 100,
  "coefficient": 1.5,
  "sort": 1
}
```

### 7.2 绩效工资 `/api/perf-salaries`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/perf-salaries` | 绩效工资设置列表 |
| POST | `/api/perf-salaries` | 创建设置 |
| PUT | `/api/perf-salaries/{id}` | 编辑设置 |
| DELETE | `/api/perf-salaries/{id}` | 删除设置 |

#### 列表响应

```json
[
  {
    "id": 1,
    "levelId": 1,
    "levelName": "S",
    "salaryRange": "5000-8000",
    "sort": 1
  }
]
```

#### 创建/编辑请求体

```json
{
  "levelId": 1,
  "salaryRange": "5000-8000",
  "sort": 1
}
```

### 7.3 绩效考核计划 `/api/perf-plans`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/perf-plans` | 计划列表（分页） |
| GET | `/api/perf-plans/{id}` | 计划详情 |
| POST | `/api/perf-plans` | 创建计划 |
| PUT | `/api/perf-plans/{id}` | 修改计划 |
| DELETE | `/api/perf-plans/{id}` | 删除计划 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "2026年Q3绩效考核",
    "deptId": 1,
    "deptName": "技术部",
    "startDate": "2026-07-01",
    "endDate": "2026-09-30",
    "status": 1,
    "statusName": "进行中",
    "createTime": "2026-06-20 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "2026年Q3绩效考核",
  "deptId": 1,
  "startDate": "2026-07-01",
  "endDate": "2026-09-30",
  "employeeIds": [1, 2, 3],
  "status": 1
}
```

### 7.4 绩效考核记录 `/api/perf-records`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/perf-records` | 考核记录列表（分页） |
| GET | `/api/perf-records/{id}` | 考核详情 |
| POST | `/api/perf-records` | 创建考核记录 |
| PUT | `/api/perf-records/{id}` | 编辑考核记录 |

#### 查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `planId` | int | 考核计划ID |
| `employeeId` | int | 员工ID |
| `deptId` | int | 部门ID |
| `levelId` | int | 等级筛选 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "planId": 1,
    "planName": "2026年Q3绩效考核",
    "employeeId": 1,
    "empNo": "EMP001",
    "employeeName": "张三",
    "deptName": "技术部",
    "totalScore": 88.5,
    "levelId": 2,
    "levelName": "A",
    "evaluatorName": "王五",
    "evaluateTime": "2026-10-05 10:00:00"
  }
]
```

#### 创建/编辑考核记录请求体

```json
{
  "planId": 1,
  "employeeId": 1,
  "items": [
    { "indicator": "工作效率", "weight": 30, "score": 85 },
    { "indicator": "代码质量", "weight": 25, "score": 90 },
    { "indicator": "团队协作", "weight": 25, "score": 92 },
    { "indicator": "创新能力", "weight": 20, "score": 88 }
  ],
  "totalScore": 88.5,
  "evaluation": "整体表现优秀",
  "levelId": 2
}
```

### 7.5 绩效报表 `/api/perf-reports`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/perf-reports/detail` | 绩效明细表（`?planId=&deptId=`） |
| GET | `/api/perf-reports/dept-summary` | 部门绩效汇总表 |
| GET | `/api/perf-reports/employee-summary` | 职员绩效汇总表 |

---

## 八、薪酬管理模块

### 8.1 核算规则 `/api/salary-rules`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/salary-rules` | 规则列表 |
| PUT | `/api/salary-rules/{id}` | 编辑规则 |

#### 列表响应

```json
[
  {
    "id": 1,
    "type": "base_salary",
    "typeName": "地区基本工资",
    "value": 5000,
    "unit": "元",
    "remark": "上海地区基本工资标准"
  }
]
```

#### 编辑请求体

```json
{
  "value": 5500,
  "remark": "上海地区基本工资调整"
}
```

### 8.2 薪酬字段 `/api/salary-fields`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/salary-fields` | 字段列表 |
| POST | `/api/salary-fields` | 创建字段 |
| PUT | `/api/salary-fields/{id}` | 编辑字段 |
| DELETE | `/api/salary-fields/{id}` | 删除字段 |

#### 列表响应

```json
[
  {
    "id": 1,
    "name": "基本工资",
    "code": "base_salary",
    "type": "income",
    "typeName": "应发",
    "formula": "rule_base_salary",
    "sort": 1,
    "status": 1
  },
  {
    "id": 2,
    "name": "社保扣除",
    "code": "social_insurance",
    "type": "deduction",
    "typeName": "应扣",
    "formula": "base_salary * 0.105",
    "sort": 10,
    "status": 1
  }
]
```

### 8.3 员工调薪 `/api/salary-adjusts`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/salary-adjusts` | 调薪记录列表（分页） |
| POST | `/api/salary-adjusts` | 员工调薪 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "employeeId": 1,
    "empNo": "EMP001",
    "employeeName": "张三",
    "deptName": "技术部",
    "beforeSalary": 15000,
    "afterSalary": 18000,
    "adjustAmount": 3000,
    "adjustType": "promote",
    "adjustTypeName": "晋升调薪",
    "effectiveDate": "2026-07-01",
    "remark": "晋升为高级工程师",
    "createTime": "2026-06-25 10:00:00"
  }
]
```

#### 调薪请求体

```json
{
  "employeeId": 1,
  "afterSalary": 18000,
  "adjustType": "promote",
  "effectiveDate": "2026-07-01",
  "remark": "晋升为高级工程师"
}
```

### 8.4 工资表 `/api/salary-sheets`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/salary-sheets` | 工资表列表（`?month=2026-07&deptId=`） |
| POST | `/api/salary-sheets/sync` | 同步绩效考勤数据 |
| POST | `/api/salary-sheets/generate` | 生成工资表 |
| POST | `/api/salary-sheets/import` | 导入数据 |
| GET | `/api/salary-sheets/export` | 导出工资表 |

#### 工资表响应

```json
{
  "month": "2026-07",
  "records": [
    {
      "id": 1,
      "employeeId": 1,
      "empNo": "EMP001",
      "employeeName": "张三",
      "deptName": "技术部",
      "baseSalary": 15000,
      "perfSalary": 3000,
      "subsidy": 500,
      "overtimePay": 800,
      "totalIncome": 19300,
      "socialInsurance": 1575,
      "tax": 450,
      "totalDeduction": 2025,
      "netSalary": 17275
    }
  ]
}
```

#### 同步请求体

```json
{
  "month": "2026-07",
  "deptId": 1
}
```

#### 生成请求体

```json
{
  "month": "2026-07",
  "employeeIds": [1, 2, 3, 4, 5]
}
```

---

## 九、公共接口

### 9.1 文件上传

```
POST /api/common/upload
```

**Content-Type:** `multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | File | 是 | 文件 |
| `module` | string | 否 | 模块名，如 `resume` |

**响应 `data`：**

```json
{
  "fileName": "abc123.pdf",
  "fileUrl": "https://minio.xxx.com/hr-files/resume/abc123.pdf",
  "fileSize": 102400
}
```

### 9.2 文件下载

```
GET /api/common/download/{fileKey}
```

返回文件流。

### 9.3 选项数据 `/api/common/options`

获取下拉选项数据。

```
GET /api/common/options?type=education
```

| type | 说明 |
|---|---|
| `education` | 学历 |
| `gender` | 性别 |
| `resume_source` | 简历来源 |
| `leave_type` | 假期类型 |
| `transfer_type` | 异动类型 |

**响应 `data`：**

```json
[
  { "value": "bachelor", "label": "本科" },
  { "value": "master", "label": "硕士" },
  { "value": "doctor", "label": "博士" }
]
```

---

## 十、CRM 管理模块

### 10.1 客户管理 `/api/customers`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/customers` | 客户列表（分页，`?keyword=&status=&ownerId=`） |
| GET | `/api/customers/{id}` | 客户详情 |
| POST | `/api/customers` | 新增客户 |
| PUT | `/api/customers/{id}` | 编辑客户 |
| DELETE | `/api/customers/{id}` | 删除客户 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "上海科技有限公司",
    "phone": "021-12345678",
    "email": "contact@sh-tech.com",
    "industry": "信息技术",
    "source": "官网",
    "level": "A",
    "status": "active",
    "ownerId": 10,
    "ownerName": "张三",
    "province": "上海",
    "city": "上海市",
    "address": "浦东新区XX路100号",
    "contactName": "李经理",
    "contactPhone": "13800138000",
    "remark": "重要客户",
    "createTime": "2026-06-01 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "上海科技有限公司",
  "phone": "021-12345678",
  "email": "contact@sh-tech.com",
  "industry": "信息技术",
  "source": "官网",
  "level": "A",
  "status": "active",
  "ownerId": 10,
  "province": "上海",
  "city": "上海市",
  "address": "浦东新区XX路100号",
  "contactName": "李经理",
  "contactPhone": "13800138000",
  "remark": "重要客户"
}
```

### 10.2 商机管理 `/api/opportunities`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/opportunities` | 商机列表（分页，`?keyword=&stage=&ownerId=`） |
| GET | `/api/opportunities/{id}` | 商机详情 |
| POST | `/api/opportunities` | 新增商机 |
| PUT | `/api/opportunities/{id}` | 编辑商机 |
| DELETE | `/api/opportunities/{id}` | 删除商机 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "ERP系统采购项目",
    "customerId": 1,
    "customerName": "上海科技有限公司",
    "amount": 500000,
    "stage": "negotiation",
    "probability": 70,
    "expectedCloseDate": "2026-09-30",
    "ownerId": 10,
    "ownerName": "张三",
    "contactName": "李经理",
    "contactPhone": "13800138000",
    "remark": "预计Q3签约",
    "createTime": "2026-06-15 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "ERP系统采购项目",
  "customerId": 1,
  "amount": 500000,
  "stage": "negotiation",
  "probability": 70,
  "expectedCloseDate": "2026-09-30",
  "ownerId": 10,
  "contactName": "李经理",
  "contactPhone": "13800138000",
  "remark": "预计Q3签约"
}
```

### 10.3 订单管理 `/api/orders`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/orders` | 订单列表（分页，`?keyword=&status=&customerId=&ownerId=`） |
| GET | `/api/orders/{id}` | 订单详情 |
| POST | `/api/orders` | 新增订单 |
| PUT | `/api/orders/{id}` | 编辑订单 |
| DELETE | `/api/orders/{id}` | 删除订单 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "opportunityId": 1,
    "customerId": 1,
    "customerName": "上海科技有限公司",
    "orderNo": "ORD-2026-0001",
    "amount": 500000,
    "status": "signed",
    "signDate": "2026-08-15",
    "ownerId": 10,
    "ownerName": "张三",
    "remark": "已签约",
    "createTime": "2026-08-15 14:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "orderNo": "ORD-2026-0001",
  "opportunityId": 1,
  "customerId": 1,
  "amount": 500000,
  "status": "signed",
  "signDate": "2026-08-15",
  "ownerId": 10,
  "remark": "已签约"
}
```

### 10.4 回款管理 `/api/payments`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/payments` | 回款列表（分页，`?keyword=&status=&orderId=&customerId=&ownerId=`） |
| GET | `/api/payments/{id}` | 回款详情 |
| POST | `/api/payments` | 新增回款 |
| PUT | `/api/payments/{id}` | 编辑回款 |
| DELETE | `/api/payments/{id}` | 删除回款 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "orderId": 1,
    "customerId": 1,
    "customerName": "上海科技有限公司",
    "paymentNo": "PAY-2026-0001",
    "amount": 300000,
    "paymentDate": "2026-09-01",
    "paymentMethod": "bank_transfer",
    "status": "received",
    "ownerId": 10,
    "ownerName": "张三",
    "remark": "首期款",
    "createTime": "2026-09-01 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "paymentNo": "PAY-2026-0001",
  "orderId": 1,
  "customerId": 1,
  "amount": 300000,
  "paymentDate": "2026-09-01",
  "paymentMethod": "bank_transfer",
  "status": "received",
  "ownerId": 10,
  "remark": "首期款"
}
```

### 10.5 退款管理 `/api/refunds`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/refunds` | 退款列表（分页，`?keyword=&status=&orderId=&customerId=&ownerId=`） |
| GET | `/api/refunds/{id}` | 退款详情 |
| POST | `/api/refunds` | 新增退款 |
| PUT | `/api/refunds/{id}` | 编辑退款 |
| DELETE | `/api/refunds/{id}` | 删除退款 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "orderId": 1,
    "customerId": 1,
    "customerName": "上海科技有限公司",
    "refundNo": "REF-2026-0001",
    "amount": 50000,
    "refundDate": "2026-10-10",
    "reason": "合同变更",
    "status": "processed",
    "ownerId": 10,
    "ownerName": "张三",
    "createTime": "2026-10-10 09:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "refundNo": "REF-2026-0001",
  "orderId": 1,
  "customerId": 1,
  "amount": 50000,
  "refundDate": "2026-10-10",
  "reason": "合同变更",
  "status": "processed",
  "ownerId": 10
}
```

### 10.6 费用管理 `/api/expenses`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/expenses` | 费用列表（分页，`?keyword=&status=&category=&applicantId=`） |
| GET | `/api/expenses/{id}` | 费用详情 |
| POST | `/api/expenses` | 新增费用 |
| PUT | `/api/expenses/{id}` | 编辑费用 |
| DELETE | `/api/expenses/{id}` | 删除费用 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "name": "差旅费报销",
    "amount": 3500,
    "expenseDate": "2026-07-05",
    "category": "travel",
    "status": "approved",
    "applicantId": 10,
    "applicantName": "张三",
    "remark": "深圳出差费用",
    "createTime": "2026-07-05 16:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "name": "差旅费报销",
  "amount": 3500,
  "expenseDate": "2026-07-05",
  "category": "travel",
  "status": "approved",
  "applicantId": 10,
  "remark": "深圳出差费用"
}
```

---

## 十一、办公管理模块

### 11.1 公告管理 `/api/notices`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/notices` | 公告列表（分页，`?keyword=&type=&status=`） |
| GET | `/api/notices/{id}` | 公告详情 |
| POST | `/api/notices` | 创建公告 |
| PUT | `/api/notices/{id}` | 编辑公告 |
| DELETE | `/api/notices/{id}` | 删除公告 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "title": "2026年Q3绩效考核通知",
    "content": "<p>各位同事：Q3绩效考核将于7月15日开始...</p>",
    "type": 1,
    "status": 1,
    "publisherId": 1,
    "publisherName": "管理员",
    "publishTime": "2026-07-01 09:00:00",
    "createTime": "2026-07-01 09:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "title": "2026年Q3绩效考核通知",
  "content": "<p>各位同事：Q3绩效考核将于7月15日开始...</p>",
  "type": 1,
  "status": 1,
  "publisherId": 1,
  "publishTime": "2026-07-01 09:00:00"
}
```

### 11.2 文档管理 `/api/documents`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/documents` | 文档列表（分页，`?keyword=&category=&isPublic=`） |
| GET | `/api/documents/{id}` | 文档详情 |
| POST | `/api/documents` | 创建文档 |
| PUT | `/api/documents/{id}` | 编辑文档 |
| DELETE | `/api/documents/{id}` | 删除文档 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "title": "员工手册 v3.0",
    "content": "<p>公司员工手册...</p>",
    "category": "制度",
    "parentId": 0,
    "creatorId": 1,
    "creatorName": "管理员",
    "isPublic": 1,
    "fileUrl": "https://minio.xxx.com/docs/handbook-v3.pdf",
    "createTime": "2026-06-01 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "title": "员工手册 v3.0",
  "content": "<p>公司员工手册...</p>",
  "category": "制度",
  "parentId": 0,
  "creatorId": 1,
  "isPublic": 1,
  "fileUrl": ""
}
```

### 11.3 任务管理 `/api/tasks`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/tasks` | 任务列表（分页，`?keyword=&priority=&status=&assigneeId=`） |
| GET | `/api/tasks/{id}` | 任务详情 |
| POST | `/api/tasks` | 创建任务 |
| PUT | `/api/tasks/{id}` | 编辑任务 |
| DELETE | `/api/tasks/{id}` | 删除任务 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "creatorId": 1,
    "assigneeId": 10,
    "title": "完成Q3绩效考核方案",
    "content": "制定各部门Q3绩效考核指标...",
    "priority": "high",
    "priorityName": "高",
    "status": "in_progress",
    "statusName": "进行中",
    "startDate": "2026-07-01",
    "dueDate": "2026-07-10",
    "completeTime": null,
    "createTime": "2026-06-28 10:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "creatorId": 1,
  "assigneeId": 10,
  "title": "完成Q3绩效考核方案",
  "content": "制定各部门Q3绩效考核指标...",
  "priority": "high",
  "status": "in_progress",
  "startDate": "2026-07-01",
  "dueDate": "2026-07-10"
}
```

### 11.4 日程管理 `/api/schedules`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/schedules` | 日程列表（分页，`?keyword=&userId=&startTime=&endTime=`） |
| GET | `/api/schedules/{id}` | 日程详情 |
| POST | `/api/schedules` | 创建日程 |
| PUT | `/api/schedules/{id}` | 编辑日程 |
| DELETE | `/api/schedules/{id}` | 删除日程 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "userId": 1,
    "userName": "管理员",
    "title": "Q3绩效考核启动会议",
    "content": "与各部门负责人讨论Q3考核方案",
    "startTime": "2026-07-15 09:00:00",
    "endTime": "2026-07-15 11:00:00",
    "allDay": 0,
    "color": "#409EFF",
    "createTime": "2026-07-10 14:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "userId": 1,
  "title": "Q3绩效考核启动会议",
  "content": "与各部门负责人讨论Q3考核方案",
  "startTime": "2026-07-15 09:00:00",
  "endTime": "2026-07-15 11:00:00",
  "allDay": 0,
  "color": "#409EFF"
}
```

### 11.5 消息管理 `/api/messages`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/messages` | 消息列表（分页，`?keyword=&isRead=&receiverId=`） |
| GET | `/api/messages/{id}` | 消息详情 |
| POST | `/api/messages` | 发送消息 |
| PUT | `/api/messages/{id}` | 编辑消息 |
| DELETE | `/api/messages/{id}` | 删除消息 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "senderId": 1,
    "senderName": "管理员",
    "receiverId": 10,
    "receiverName": "张三",
    "title": "Q3考核提醒",
    "content": "请于7月15日前完成自评...",
    "isRead": 0,
    "sendTime": "2026-07-12 09:00:00",
    "createTime": "2026-07-12 09:00:00"
  }
]
```

#### 创建/编辑请求体

```json
{
  "senderId": 1,
  "receiverId": 10,
  "title": "Q3考核提醒",
  "content": "请于7月15日前完成自评...",
  "isRead": 0,
  "sendTime": "2026-07-12 09:00:00"
}
```

---

## 十二、工作流模块

### 12.1 报销审批 `/api/workflow/expenses`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/workflow/expenses` | 报销列表（分页，`?applicantId=&status=`） |
| GET | `/api/workflow/expenses/{id}` | 报销详情 |
| POST | `/api/workflow/expenses` | 发起报销 |
| PUT | `/api/workflow/expenses/{id}` | 编辑报销 |
| DELETE | `/api/workflow/expenses/{id}` | 删除报销 |
| PUT | `/api/workflow/expenses/{id}/approve` | 审批报销 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "applicantId": 10,
    "applicantName": "张三",
    "amount": 3500,
    "expenseDate": "2026-07-05",
    "category": "travel",
    "description": "深圳出差差旅费",
    "status": "pending",
    "statusName": "待审批",
    "approverId": 1,
    "approverName": "管理员",
    "createTime": "2026-07-05 16:00:00"
  }
]
```

#### 发起/编辑请求体

```json
{
  "applicantId": 10,
  "amount": 3500,
  "expenseDate": "2026-07-05",
  "category": "travel",
  "description": "深圳出差差旅费"
}
```

#### 审批请求体

```json
{
  "result": "approved",
  "comment": "同意报销"
}
```

### 12.2 请假审批 `/api/workflow/leaves`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/workflow/leaves` | 请假列表（分页，`?applicantId=&status=`） |
| GET | `/api/workflow/leaves/{id}` | 请假详情 |
| POST | `/api/workflow/leaves` | 发起请假 |
| PUT | `/api/workflow/leaves/{id}` | 编辑请假 |
| DELETE | `/api/workflow/leaves/{id}` | 删除请假 |
| PUT | `/api/workflow/leaves/{id}/approve` | 审批请假 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "applicantId": 10,
    "applicantName": "张三",
    "leaveType": "annual",
    "startDate": "2026-08-01",
    "endDate": "2026-08-03",
    "days": 3,
    "reason": "年假休假",
    "status": "pending",
    "statusName": "待审批",
    "approverId": 1,
    "approverName": "管理员",
    "createTime": "2026-07-20 10:00:00"
  }
]
```

#### 发起/编辑请求体

```json
{
  "applicantId": 10,
  "leaveType": "annual",
  "startDate": "2026-08-01",
  "endDate": "2026-08-03",
  "days": 3,
  "reason": "年假休假"
}
```

#### 审批请求体

```json
{
  "result": "approved",
  "comment": "同意请假"
}
```

### 12.3 借款审批 `/api/workflow/loans`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/workflow/loans` | 借款列表（分页，`?applicantId=&status=`） |
| GET | `/api/workflow/loans/{id}` | 借款详情 |
| POST | `/api/workflow/loans` | 发起借款 |
| PUT | `/api/workflow/loans/{id}` | 编辑借款 |
| DELETE | `/api/workflow/loans/{id}` | 删除借款 |
| PUT | `/api/workflow/loans/{id}/approve` | 审批借款 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "applicantId": 10,
    "applicantName": "张三",
    "amount": 10000,
    "loanDate": "2026-08-01",
    "reason": "出差备用金",
    "repaymentDate": "2026-09-01",
    "status": "pending",
    "statusName": "待审批",
    "approverId": 1,
    "approverName": "管理员",
    "createTime": "2026-07-25 10:00:00"
  }
]
```

#### 发起/编辑请求体

```json
{
  "applicantId": 10,
  "amount": 10000,
  "loanDate": "2026-08-01",
  "reason": "出差备用金",
  "repaymentDate": "2026-09-01"
}
```

#### 审批请求体

```json
{
  "result": "approved",
  "comment": "同意借款"
}
```

### 12.4 出差审批 `/api/workflow/travels`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/workflow/travels` | 出差列表（分页，`?applicantId=&status=`） |
| GET | `/api/workflow/travels/{id}` | 出差详情 |
| POST | `/api/workflow/travels` | 发起出差 |
| PUT | `/api/workflow/travels/{id}` | 编辑出差 |
| DELETE | `/api/workflow/travels/{id}` | 删除出差 |
| PUT | `/api/workflow/travels/{id}/approve` | 审批出差 |

#### 列表响应 `records`

```json
[
  {
    "id": 1,
    "applicantId": 10,
    "applicantName": "张三",
    "destination": "深圳",
    "startDate": "2026-08-10",
    "endDate": "2026-08-12",
    "days": 3,
    "reason": "客户拜访",
    "companions": "李四",
    "budget": 5000,
    "status": "pending",
    "statusName": "待审批",
    "approverId": 1,
    "approverName": "管理员",
    "createTime": "2026-07-28 10:00:00"
  }
]
```

#### 发起/编辑请求体

```json
{
  "applicantId": 10,
  "destination": "深圳",
  "startDate": "2026-08-10",
  "endDate": "2026-08-12",
  "days": 3,
  "reason": "客户拜访",
  "companions": "李四",
  "budget": 5000
}
```

#### 审批请求体

```json
{
  "result": "approved",
  "comment": "同意出差"
}
```

---

## 十三、接口汇总

| 模块 | 基础路径 | 接口数 |
|---|---|---|
| 认证 | `/api/auth` | 5 |
| 公司架构 | `/api/companies` | 5 |
| 部门架构 | `/api/depts` | 5 |
| 职位管理 | `/api/positions` | 5 |
| 用户管理 | `/api/users` | 7 |
| 角色权限 | `/api/roles` | 7 |
| 菜单管理 | `/api/menus` | 5 |
| 字段配置 | `/api/field-configs` | 2 |
| 员工花名册 | `/api/employees` | 9 |
| 异动管理 | `/api/transfers` | 4 |
| 账号开通 | `/api/accounts` | 2 |
| 简历管理 | `/api/resumes` | 8 |
| 面试管理 | `/api/interviews` | 9 |
| 面试题库 | `/api/questions` | 5 |
| 通知模板 | `/api/notify-templates` | 5 |
| 黑名单 | `/api/blacklists` | 3 |
| 招聘报表 | `/api/recruitment-reports` | 1 |
| 打卡记录 | `/api/attendance-records` | 3 |
| 异常统计 | `/api/attendance-exceptions` | 1 |
| OA流程 | `/api/oa-flows` | 3 |
| 取卡规则 | `/api/card-rules` | 5 |
| 班次设置 | `/api/shifts` | 5 |
| 法定假期 | `/api/holidays` | 5 |
| 假期额度 | `/api/leave-quotas` | 2 |
| 假期设置 | `/api/leave-types` | 5 |
| 考勤报表 | `/api/attendance-reports` | 2 |
| 考勤扣款 | `/api/attendance-deductions` | 2 |
| 绩效等级 | `/api/perf-levels` | 5 |
| 绩效工资 | `/api/perf-salaries` | 5 |
| 考核计划 | `/api/perf-plans` | 5 |
| 考核记录 | `/api/perf-records` | 4 |
| 绩效报表 | `/api/perf-reports` | 3 |
| 核算规则 | `/api/salary-rules` | 2 |
| 薪酬字段 | `/api/salary-fields` | 5 |
| 员工调薪 | `/api/salary-adjusts` | 2 |
| 工资表 | `/api/salary-sheets` | 5 |
| **客户管理** | **`/api/customers`** | **5** |
| **商机管理** | **`/api/opportunities`** | **5** |
| **订单管理** | **`/api/orders`** | **5** |
| **回款管理** | **`/api/payments`** | **5** |
| **退款管理** | **`/api/refunds`** | **5** |
| **费用管理** | **`/api/expenses`** | **5** |
| **公告管理** | **`/api/notices`** | **5** |
| **文档管理** | **`/api/documents`** | **5** |
| **任务管理** | **`/api/tasks`** | **5** |
| **日程管理** | **`/api/schedules`** | **5** |
| **消息管理** | **`/api/messages`** | **5** |
| **报销审批** | **`/api/workflow/expenses`** | **6** |
| **请假审批** | **`/api/workflow/leaves`** | **6** |
| **借款审批** | **`/api/workflow/loans`** | **6** |
| **出差审批** | **`/api/workflow/travels`** | **6** |
| 公共接口 | `/api/common` | 3 |

---

> 完整接口总数：**约 199 个 API 端点**（原 120 + 新增 79），覆盖 HR 管理系统的 **12 大功能模块**。

---

## 十四、枚举值定义

### 14.1 性别

| 值 | 说明 |
|---|---|
| 1 | 男 |
| 2 | 女 |

### 14.2 员工状态

| 值 | 说明 |
|---|---|
| 1 | 在职 |
| 2 | 离职 |
| 3 | 试用 |

### 11.3 简历状态

| 值 | 说明 |
|---|---|
| `new` | 新简历 |
| `screening` | 筛选中 |
| `interview` | 面试中 |
| `offer` | 已发 Offer |
| `hired` | 已入职 |
| `eliminated` | 已淘汰 |

### 14.4 学历

| 值 | 说明 |
|---|---|
| `high_school` | 高中 |
| `junior_college` | 大专 |
| `bachelor` | 本科 |
| `master` | 硕士 |
| `doctor` | 博士 |

### 14.5 面试结果

| 值 | 说明 |
|---|---|
| `pending` | 待面试 |
| `pass` | 通过 |
| `fail` | 淘汰 |
| `offer` | 已发 Offer |
| `hired` | 已入职 |

### 11.6 异动类型

| 值 | 说明 |
|---|---|
| `regular` | 转正 |
| `transfer` | 调岗 |
| `promote` | 晋升 |
| `dimission` | 离职 |
| `rehire` | 返聘 |

### 11.7 打卡状态

| 值 | 说明 |
|---|---|
| `normal` | 正常 |
| `late` | 迟到 |
| `early` | 早退 |
| `absent` | 缺勤 |
| `overtime` | 加班 |

### 14.8 菜单类型

| 值 | 说明 |
|---|---|
| 1 | 目录 |
| 2 | 菜单 |
| 3 | 按钮/权限 |

### 11.9 薪酬字段类型

| 值 | 说明 |
|---|---|
| `income` | 应发 |
| `deduction` | 应扣 |

### 11.10 调薪类型

| 值 | 说明 |
|---|---|
| `promote` | 晋升调薪 |
| `transfer` | 调岗调薪 |
| `annual` | 年度调薪 |
| `special` | 特殊调薪 |

---

## 十五、前后端协作约定

### 15.1 字段命名规范

- 字段名统一使用 **camelCase**（驼峰命名）
- 日期字段统一以 `Date` 结尾，如 `entryDate`、`effectiveDate`
- 时间字段统一以 `Time` 结尾，如 `createTime`、`updateTime`
- ID 关联字段以 `Id` 结尾，如 `deptId`、`positionId`
- 关联名称字段以 `Name` 结尾，如 `deptName`、`positionName`

### 12.2 日期时间格式

| 类型 | 格式 | 示例 |
|---|---|---|
| 日期 | `yyyy-MM-dd` | `2026-07-12` |
| 时间 | `yyyy-MM-dd HH:mm:ss` | `2026-07-12 14:30:00` |

### 12.3 金额格式

- 金额字段统一用 Java `BigDecimal` / JS `number`，以**元**为单位
- 后端序列化时保留 2 位小数：`15000.00`

### 15.4 文件上传

- 使用 `multipart/form-data` 格式
- 文件大小上限：10MB
- 支持格式：`.pdf` `.doc` `.docx` `.xls` `.xlsx` `.png` `.jpg` `.jpeg`
- 文件存储：MinIO（开发）/ OSS（生产）

### 12.5 接口对接流程

1. 前端根据此文档编写 API 层代码
2. 后端根据此文档编写 Controller + Service
3. 使用 Apifox / Postman 管理接口和自动化测试
4. 变更接口必须同步更新此文档

### 12.6 权限控制

- 菜单/按钮级权限通过 `permission` 标识控制，如 `system:user:create`
- 前端使用 `v-permission` 指令控制按钮显隐
- 后端使用 `@PreAuthorize` 注解校验接口权限
- 所有非公开接口必须携带 `Authorization: Bearer {token}` 请求头
