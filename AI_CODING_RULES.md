# AI Coding Rules — HR 管理系统

> AI 辅助编程的强制行为规范，用于 vibe coding 场景下防止 AI 乱改导致代码失效或混乱。
>
> 适用环境：Cursor / Trae / Copilot Chat 等 AI IDE 的系统提示词。

---

## 一、项目基本信息

| 属性 | 值 |
|------|-----|
| 项目 | HR 人力资源管理系统 |
| 仓库 | 前后端 Monorepo |
| 前端 | Vue 3 + TypeScript + Element Plus |
| 后端 | Java 17 + Spring Boot 3.3 + MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0，使用 Flyway 管理迁移 |
| 部署 | Docker Compose（Nginx + 后端 + MySQL + Redis + MinIO） |
| 团队 | 2 人，各自负责不同模块 |

---

## 二、模块归属（CRITICAL）

> **AI 严禁跨人员模块修改。修改前必须先确认模块归属。**

### 后端模块（hr-backend 多模块 Maven 项目）

| Maven 模块 | 对应业务 | 路径前缀 |
|------------|----------|----------|
| `hr-module-auth` | 认证授权 | `/api/auth` |
| `hr-module-system` | 系统管理（公司/部门/职位/用户/角色/菜单/字段配置） | `/api/companies`, `/api/depts`, `/api/positions`, `/api/users`, `/api/roles`, `/api/menus`, `/api/field-configs` |
| `hr-module-employee` | 员工管理（花名册/异动/账号） | `/api/employees`, `/api/transfers`, `/api/accounts` |
| `hr-module-recruitment` | 招聘管理（简历/面试/题库/通知/黑名单/报表） | `/api/resumes`, `/api/interviews`, `/api/questions`, `/api/notify-templates`, `/api/blacklists`, `/api/recruitment-reports` |
| `hr-module-attendance` | 考勤管理（打卡/异常/OA/规则/班次/假期/报表/扣款） | `/api/attendance-records`, `/api/attendance-exceptions`, `/api/oa-flows`, `/api/card-rules`, `/api/shifts`, `/api/holidays`, `/api/leave-quotas`, `/api/leave-types`, `/api/attendance-reports`, `/api/attendance-deductions` |
| `hr-module-performance` | 绩效管理（等级/工资/计划/记录/报表） | `/api/perf-levels`, `/api/perf-salaries`, `/api/perf-plans`, `/api/perf-records`, `/api/perf-reports` |
| `hr-module-salary` | 薪酬管理（规则/字段/调薪/工资表） | `/api/salary-rules`, `/api/salary-fields`, `/api/salary-adjusts`, `/api/salary-sheets` |
| `hr-module-crm` | CRM（客户/商机/订单/回款/退款/费用） | `/api/customers`, `/api/opportunities`, `/api/orders`, `/api/payments`, `/api/refunds`, `/api/expenses` |
| `hr-module-office` | 办公协同（公告/文档/任务/日程/消息） | `/api/notices`, `/api/documents`, `/api/tasks`, `/api/schedules`, `/api/messages` |
| `hr-module-workflow` | 工作流审批（报销/请假/借款/出差） | `/api/workflow/expenses`, `/api/workflow/leaves`, `/api/workflow/loans`, `/api/workflow/travels` |
| `hr-common` | 公共模块（全局异常/结果封装/工具类） | — |
| `hr-framework` | 框架层（Security/JWT/跨域/MyBatis-Plus 配置） | — |
| `hr-server` | 启动模块 + Flyway 迁移脚本 | — |

### 前端模块

| 目录/路由 | 对应业务 |
|------------|----------|
| `src/views/system/` | 系统管理 |
| `src/views/employee/` | 员工管理 |
| `src/views/recruitment/` | 招聘管理 |
| `src/views/attendance/` | 考勤管理 |
| `src/views/performance/` | 绩效管理 |
| `src/views/salary/` | 薪酬管理 |
| `src/views/crm/` | CRM |
| `src/views/office/` | 办公协同 |
| `src/views/workflow/` | 工作流审批 |
| `src/api/` | 对应后端每个模块的 API 封装 |
| `src/router/` | 路由配置 |
| `src/layout/` | 系统框架布局 |
| `src/components/` | 公共组件 |
| `src/stores/` | Pinia 状态管理 |

### 模块隔离规则

1. **修改前必须明确：这个修改属于哪个模块？**
2. **只修改该模块对应的后端 Controller/Service/Mapper/Entity/DTO 和前端 views/api 文件**
3. **严禁跨模块修改**，例如：修改招聘模块时不得改动员工模块的代码
4. 如果需求确实涉及多个模块，必须明确列出所有涉及模块并逐一说明修改原因

---

## 三、共享基础设施保护规则

> 以下文件/目录是项目基础设施，**非经明确授权严禁修改**：

### 后端受保护文件（只读区）

| 文件/目录 | 原因 |
|-----------|------|
| `hr-common/` 整个模块 | 全局异常处理、统一返回体、工具类 |
| `hr-framework/` 整个模块 | Security 配置、JWT 认证、跨域、MyBatis-Plus 配置 |
| `hr-server/src/main/resources/application*.yml` | 全局配置文件 |
| `hr-server/src/main/resources/db/migration/` 中已执行的迁移脚本 | **Flyway 迁移脚本只能新增，不能修改已有文件** |
| `pom.xml`（父 POM） | Maven 依赖管理 |
| `docker-compose.yml` | 容器编排 |
| `Dockerfile` | 容器构建 |
| `hr-server/src/main/java/com/hr/HrApplication.java` | 启动类 |

### 前端受保护文件（只读区）

| 文件/目录 | 原因 |
|-----------|------|
| `src/layout/` | 系统框架布局 |
| `src/router/index.ts`（顶层路由配置） | 全局路由守卫、登录跳转 |
| `src/api/request.ts`（或 axios 实例） | 全局请求拦截器（Token 注入、错误处理） |
| `src/stores/` 中的全局 store | 用户信息、权限等 |
| `vite.config.ts` | Vite 构建配置 |
| `package.json` | 依赖管理 |
| `.env.*` | 环境变量 |

### 基础设施修改审批流程

1. 如果修改确实需要触及上述文件，**必须先告知所有开发人员**
2. 在 PR/MR 中明确标注基础设施变更
3. 基础设施变更必须单独提交，不与业务代码混在一起

---

## 四、API 接口契约规则

> **`json设计.md`（即 `e:\PROTOTYPE\json设计.md`）是前后端接口的唯一权威来源。**

### 后端修改接口时

1. **必须先查阅 `json设计.md`**，确认接口定义（路径、参数、响应格式）
2. **Controller 路径必须与文档一致**，不得自行发明路径
3. **请求参数和响应字段必须与文档一致**，字段名使用 camelCase
4. **统一响应格式**必须为：
   ```json
   { "code": 200, "message": "成功", "data": {} }
   ```
5. 分页响应必须使用：
   ```json
   { "code": 200, "message": "成功", "data": { "total": 100, "page": 1, "pageSize": 10, "records": [] } }
   ```
6. **接口变更后必须同步更新 `json设计.md`**，并告知前端

### 前端调用接口时

1. **必须先查阅 `json设计.md`**，确认字段名和类型
2. **API 封装必须放在 `src/api/` 对应模块文件中**
3. **字段名使用 camelCase**，不使用 snake_case
4. **日期字段格式**：`yyyy-MM-dd`（日期）、`yyyy-MM-dd HH:mm:ss`（时间）
5. **金额字段以元为单位**，前后端均为 number 类型

### 枚举值对照表（关键）

| 业务 | 字段 | 可选值 |
|------|------|--------|
| 性别 | `gender` | `1`=男, `2`=女 |
| 员工状态 | `status` | `1`=在职, `2`=离职, `3`=试用 |
| 简历状态 | `status` | `new`/`screening`/`interview`/`offer`/`hired`/`eliminated` |
| 学历 | `education` | `high_school`/`junior_college`/`bachelor`/`master`/`doctor` |
| 面试结果 | `result` | `pending`/`pass`/`fail`/`offer`/`hired` |
| 异动类型 | `transferType` | `regular`/`transfer`/`promote`/`dimission`/`rehire` |
| 打卡状态 | `status` | `normal`/`late`/`early`/`absent`/`overtime` |
| 菜单类型 | `type` | `1`=目录, `2`=菜单, `3`=按钮/权限 |

---

## 五、数据库规则

### Flyway 迁移规则（MUST）

1. **严格禁止修改已有的 Flyway 迁移脚本**（`V1__init.sql`、`V2__employee.sql` 等）
2. **新增表/字段必须创建新的迁移脚本**，命名规则：`V{n}__{description}.sql`，序号递增
3. **迁移脚本放在** `hr-server/src/main/resources/db/migration/`
4. 所有表必须包含 `deleted TINYINT DEFAULT 0` 字段（MyBatis-Plus 逻辑删除）
5. 所有表必须包含 `create_time DATETIME DEFAULT CURRENT_TIMESTAMP` 和 `update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

### Entity 映射规则

1. **表名与 Entity 类名**：`sys_user` → `SysUser`，`hr_employee` → `HrEmployee`
2. **字段命名**：数据库使用 snake_case，Java Entity 使用 camelCase（MyBatis-Plus 自动转换）
3. **使用 `@TableLogic`** 注解 `deleted` 字段
4. **使用 `@TableField(fill = FieldFill.INSERT)`** 等注解自动填充时间字段

---

## 六、后端代码规范

### 分层结构（必须遵循）

```
Controller → Service（接口） → ServiceImpl → Mapper → Entity
     ↓
   DTO/VO（请求/响应对象，独立于 Entity）
```

1. **Controller** 只做参数校验和调用 Service，不写业务逻辑
2. **Service** 包含业务逻辑，使用 `@Transactional` 管理事务
3. **Mapper** 继承 MyBatis-Plus `BaseMapper<T>`，复杂查询写在 XML 中
4. **Entity** 对应数据库表，不暴露给前端
5. **DTO** 用于接收请求参数，**VO** 用于返回数据给前端

### 权限控制

1. **Controller 方法必须标注 `@PreAuthorize`**，权限标识格式：`{module}:{resource}:{action}`
   ```java
   @PreAuthorize("hasAuthority('employee:list')")
   @GetMapping
   public Result<PageResult<EmployeeVO>> list(...) { ... }
   ```
2. 后端权限标识必须与 `sys_menu` 表中的 `permission` 字段一致

### 已有代码模式（NEW：按需遵循）

> 当你需要在当前模块内新增功能时，**必须参照同模块内已有 Controller/Service 的写法**，保持一致性。不要自创模式。

参考模式（按模块查找对应代码）：
- 标准 CRUD → 参考 `hr-module-system` 下的任意 Controller
- 审批流 → 参考 `hr-module-workflow` 下的 `ExpenseApprovalController`
- 报表接口 → 参考 `RecruitmentReportController`

---

## 七、前端代码规范

### 技术栈

| 技术 | 版本 |
|------|------|
| Vue 3 + TypeScript | 3.4+ |
| Vite | 5.x |
| Element Plus | 2.x |
| Vue Router | 4.x |
| Pinia | 2.x |
| Axios | 1.x |
| ECharts | 5.x |

### 目录规范

```
src/
├── api/            # 每个模块一个文件，如 employee.ts、recruitment.ts
├── views/          # 每个模块一个目录，如 employee/、recruitment/
├── components/     # 公共组件
├── router/         # 路由配置
├── stores/         # Pinia store
├── utils/          # 工具函数
└── types/          # TypeScript 类型定义
```

### 组件/页面规范

1. **使用 `<script setup lang="ts">`** 语法
2. **使用 Composition API**（`ref`、`reactive`、`computed`、`onMounted`）
3. **表单校验使用 Element Plus 的 `el-form` + `rules`**
4. **权限按钮使用 `v-permission` 指令**
5. **表格使用 `el-table` + 分页使用 `el-pagination`**

### 新增页面时需要同步修改

| 文件 | 修改内容 |
|------|----------|
| `src/views/{module}/XxxPage.vue` | 新建页面组件 |
| `src/api/{module}.ts` | 新增 API 调用函数 |
| `src/router/index.ts` 或模块路由文件 | 新增路由配置 |
| 数据库 `sys_menu` 表（通过 Flyway 迁移） | 新增菜单记录（如果需要） |

---

## 八、AI 行为强制约束

### AI 禁止行为（红线）

| # | 禁止行为 | 原因 |
|---|----------|------|
| 1 | **在没有读取 `json设计.md` 的情况下修改 API 接口** | 接口定义必须与文档一致 |
| 2 | **修改不属于当前任务的模块代码** | 防止跨模块污染 |
| 3 | **修改受保护的基础设施文件**（见第三章） | 防止全局配置被破坏 |
| 4 | **修改已有的 Flyway 迁移脚本** | 数据库版本一致性 |
| 5 | **在 Controller 中写业务逻辑** | 分层规范 |
| 6 | **自行发明 API 路径** | 必须与文档一致 |
| 7 | **删除或重命名已有字段**（除非明确要求） | 防止前后端字段不匹配 |
| 8 | **引入未在 `pom.xml` / `package.json` 中声明的依赖** | 防止依赖混乱 |
| 9 | **修改全局 CSS/主题变量**（除非明确要求） | 防止样式污染 |
| 10 | **自行创建"优化"或"重构"** | vibe coding 场景下，保持改动范围最小化 |

### AI 必须行为

1. **每次修改代码前，先阅读相关模块的已有代码**，理解现有模式
2. **新增功能时，参照同模块已有代码的写法**，保持一致
3. **修改前后端接口时，查阅 `json设计.md`** 确认接口定义
4. **如果涉及多个文件的修改，先列出修改清单再执行**
5. **如果 API 有变更，提示用户需要同步更新文档**

---

## 九、模块冲突预防与 Git 整合流程

> 本章是防止两人协作中最常见的"代码冲突"和"功能互相破坏"问题的核心操作指南。

---

### 9.1 为什么会冲突？先理解冲突来源

在 Monorepo 中，即使两个人修改不同的业务模块，以下文件仍然可能发生冲突：

| 冲突点 | 典型场景 | 严重程度 |
|--------|----------|:--------:|
| **Flyway 版本号** | 两人同时创建了 V7__xxx.sql，版本号相同 | 高 |
| **`json设计.md`** | 两人都新增或修改了接口定义 | 高 |
| **路由配置** | 前端两人都添加了新路由 | 中 |
| **`pom.xml`** | 两人都新增了依赖 | 中 |
| **`sys_menu` 数据** | 两人都插入了菜单数据（INSERT 语句无冲突，但 SQL 文件有） | 中 |
| **`application.yml`** | 两人都改了配置 | 低（很少发生） |
| **业务模块代码** | 各自改各自的 Controller/Service，**理论上不会冲突** | 低 |

---

### 9.2 防冲突策略：事前预防

#### 策略一：模块分工宣言（每次开发前必做）

在开始一个新功能前，在团队群或项目文档中声明：

```
[模块登记]
负责人：[姓名]
模块：[hr-module-xxx]
功能描述：[简要说明]
预计涉及文件：[列出文件路径]
Flyway 脚本：V{n}__{description}.sql  （如果涉及数据库变更）
```

**好处**：另一个人看到后自动避开相同的 Flyway 版本号和路由位置。

#### 策略二：Flyway 版本号避让（关键！）

> Flyway 迁移脚本的版本号递增是 Monorepo 中最容易冲突的地方。

**规则**：

1. 使用**奇偶分区**避免冲突：
   - 开发人员 A 使用**奇数**版本号：V7、V9、V11、...
   - 开发人员 B 使用**偶数**版本号：V8、V10、V12、...

2. 创建迁移脚本前，先 `git pull` 确认当前最新版本号

3. 迁移脚本命名必须见名知义，格式：`V{n}__{module}_{description}.sql`
   - 好的命名：`V7__employee_add_bank_account.sql`
   - 坏的命名：`V7__update.sql`

#### 策略三：`json设计.md` 变更协调

> 这是两人最容易发生逻辑冲突的地方——一个人改了接口，另一个人不知道。

**规则**：

1. **谁先改，谁先在文档中占位**：新增接口时，在 `json设计.md` 中添加接口定义（标记 `状态：开发中`），然后告知对方
2. **对方修改前必须 `git pull`**，确认文档没有冲突变更
3. 如果两人的接口有依赖关系（A 的接口需要 B 的接口返回数据），**先让 B 完成接口定义，A 再开发**

#### 策略四：前端路由分块

> 前端路由文件也容易冲突。

**规则**：

1. 将路由拆分为模块文件（如 `src/router/modules/employee.ts`），不要都写在 `index.ts` 中
2. 每个人只修改自己模块的路由文件
3. 如果必须修改主路由文件（`index.ts`），先沟通确认

---

### 9.3 Git 整合工作流（每日操作）

#### 完整的一天开发流程

```
# ====== 上班第一件事：同步最新代码 ======
git checkout dev
git pull origin dev

# ====== 从 dev 创建自己的功能分支 ======
git checkout -b feature/employee-add-import

# ====== 开发中：频繁提交到本地 ======
git add src/views/employee/ImportDialog.vue
git add src/api/employee.ts
git add hr-server/src/main/resources/db/migration/V7__employee_add_import.sql
git add hr-module-employee/src/main/java/com/hr/modules/employee/controller/EmployeeController.java
git commit -m "feat(employee): 新增批量导入功能"

# ====== 下班前：合并 dev 最新代码到自己的分支 ======
git checkout dev
git pull origin dev
git checkout feature/employee-add-import
git merge dev          # 把自己的分支与 dev 最新代码合并
# 如有冲突，此时解决

# ====== 功能完成：推送并提 PR/MR ======
git push origin feature/employee-add-import
# 在 Git 平台上创建 Merge Request，目标分支 dev
```

#### 为什么每天要 `merge dev` 回自己的分支？

| 做法 | 结果 |
|------|------|
| 开发几天后才合并 dev | 累积大量冲突，一次性解决很痛苦 |
| **每天合并 dev** | 冲突很小，逐个解决，可控 |

---

### 9.4 合并冲突实战解决

#### 场景一：业务模块代码冲突

```
冲突文件：hr-module-employee/.../EmployeeController.java
```

这种情况**理论上不会发生**（因为两人分管不同模块）。
如果真的发生（可能是两人都改了同一个 Controller），说明分工不清晰，需要沟通。

**解决方式**：不要直接接受任何一方的代码，两人逐行对比确认。

#### 场景二：Flyway 脚本冲突（最常见）

```
冲突文件：hr-server/src/main/resources/db/migration/
A 的文件：V7__employee_add_field.sql
B 的文件：V7__recruitment_add_field.sql    ← 版本号冲突！
```

**解决方式**（按优先级）：
1. **最佳**：使用奇偶分区，提前避免（见 9.2 策略二）
2. **已发生时**：将 B 的脚本重命名为 V8，双方各自保留
3. **绝不**：合并成一个脚本（会导致回滚困难）

#### 场景三：`json设计.md` 冲突

```
A 在文档末尾新增了接口 2.6
B 在文档末尾新增了接口 2.6    ← 同一位置！
```

**解决方式**：
1. 两人沟通确认各自的接口定义
2. 手动合并，两个接口分别编号 2.6 和 2.7
3. 严禁一方直接覆盖另一方的内容

#### 场景四：`pom.xml` 或 `package.json` 冲突

```
A 新增了 EasyExcel 依赖
B 新增了 Redisson 依赖
```

这种情况不产生冲突（在不同的行），如果同区域冲突则手动合并双方依赖。

---

### 9.5 分支策略详解

```
main (生产)
│
├── dev (开发主线)
│   │
│   ├── feature/employee-import        ← 张三：员工导入功能
│   ├── feature/recruitment-interview  ← 李四：面试流程优化
│   ├── fix/employee-export-bug        ← 张三：修复导出 Bug
│   └── fix/recruitment-status-bug     ← 李四：修复状态 Bug
│
└── release/v1.0  (从 dev 创建，稳定后合并到 main)
```

**核心原则**：

| 分支 | 谁可以提交 | 何时合并 |
|------|-----------|----------|
| `main` | 没有人（只有 release 分支合并） | 版本发布时 |
| `dev` | 没有人直接提交（只接受 PR/MR 合并） | 随时（功能完成/修复完成） |
| `feature/*` | 各自开发者 | 功能完成且自测通过后 → PR 到 dev |
| `fix/*` | 各自开发者 | 修复完成且验证通过后 → PR 到 dev |
| `release/*` | 项目负责人 | 准备发布时从 dev 创建 |

---

### 9.6 防乱改保障：Git 层面的最后防线

#### 提交前自动检查

每次 `git commit` 前，AI 或开发者应确认：

```
[ ] git status 中是否只有目标模块的文件？  （没有误改其他模块）
[ ] 是否改动了 hr-common/ 或 hr-framework/ ？（如果是，是否已告知团队）
[ ] 是否修改了已有的 Flyway 脚本？        （如果是，撤销修改）
[ ] json设计.md 是否有冲突标记？           （如果有，已沟通解决）
```

#### PR/MR 审核要点

合并到 dev 前，另一个人审核时重点关注：

| 检查项 | 怎么检查 |
|--------|----------|
| 改动了哪些模块 | 看 PR 的 Files Changed 列表 |
| 是否误改了别人的模块 | 任何不在目标模块内的文件都是红色警报 |
| Flyway 版本号是否冲突 | 看新增的 V*.sql 版本号 |
| json设计.md 变更是否合理 | 逐行对比接口定义 |
| 是否动了基础设施文件 | `hr-common/`、`hr-framework/`、`application.yml` 等 |

---

### 9.7 日常协作节奏建议

| 时间 | 动作 | 目的 |
|------|------|------|
| 上午开工 | `git pull origin dev` | 同步最新代码 |
| 开发中 | 频繁本地 commit（每完成一个小功能点） | 减少单次变更范围 |
| 午饭前 | 自己的分支 `merge dev`，解决冲突 | 冲突早发现早解决 |
| 下午开发 | 继续在自己的分支开发 | — |
| 下班前 | 再次 `merge dev`，`push` 自己的分支 | 备份代码 + 解决今日冲突 |
| 功能完成 | 自测 → 提 PR → 另一个人审核 → 合并到 dev | 正式整合 |

---

### 9.8 冲突应急预案

当合并时出现你无法解决的冲突：

1. **不要强行接受"当前更改"或"传入更改"**，可能删除对方的工作
2. **截图冲突内容发到群里**，两个人一起看
3. **如果涉及公共文件**（如 `json设计.md`、`pom.xml`），两个人一起在线解决
4. **如果冲突太大无法手动解决**，撤销本次合并 `git merge --abort`，先沟通再重试

---

## 十、AI 对话提示词模板

> 以下模板可直接用于 AI 对话开头，带 AI 进入正确的上下文。

### 模板 A：修改后端模块

```
我要修改 [模块名称] 模块的 [功能描述]。
涉及的后端模块是 [hr-module-xxx]，API 路径前缀是 [/api/xxx]。
请只修改该模块内的 Controller/Service/Mapper/Entity 文件，不要改动其他模块。
修改前请先查阅 [json设计.md] 确认接口定义。
```

### 模板 B：修改前端页面

```
我要修改 [页面名称] 页面（位于 src/views/[module]/）。
涉及的 API 在 [json设计.md] 中定义。
请只修改该页面的 .vue 文件和对应的 src/api/[module].ts 文件。
```

### 模板 C：新增功能（端到端）

```
我要新增 [功能描述]，属于 [模块名称] 模块。
后端路径前缀 [/api/xxx]，前端页面目录 [src/views/xxx/]。
请遵守以下规则：
1. 所有接口定义必须与 [json设计.md] 一致
2. 数据库变更必须通过新的 Flyway 迁移脚本
3. 不要修改已有的迁移脚本
4. 不要改动其他模块的代码
```

### 模板 D：修复 Bug

```
我要修复 [模块名称] 模块的一个 Bug：[Bug 描述]。
当前表现：[xxx]
期望表现：[xxx]
请只排查和修改该模块的代码，不要动其他模块。
```

---

## 十一、变更前自查清单

每次提交代码前，逐项确认：

### 后端变更

- [ ] 是否只修改了目标模块的代码？
- [ ] API 路径是否与 `json设计.md` 一致？
- [ ] 请求参数/响应字段是否与文档一致？
- [ ] 是否添加了 `@PreAuthorize` 权限注解？
- [ ] 数据库变更是否通过新的 Flyway 迁移脚本？
- [ ] 是否修改了已有的迁移脚本？（如果是，**撤销**）
- [ ] 是否触动了 `hr-common`、`hr-framework`、`hr-server` 的基础设施代码？（如果是，**告知团队**）
- [ ] Controller 中是否有业务逻辑？（如果有，**移到 Service**）

### 前端变更

- [ ] 是否只修改了目标模块的 views/api 文件？
- [ ] API 调用的字段名是否与 `json设计.md` 一致？
- [ ] 是否触动了 `src/layout/`、`src/router/` 全局配置？（如果是，**告知团队**）
- [ ] 新增页面是否添加了路由配置？
- [ ] 是否使用了不存在的 Element Plus 组件或 API？

---

## 十二、常见错误模式（AI 最容易犯的）

| 错误模式 | 正确做法 |
|----------|----------|
| 修改 A 模块时顺手"优化"了 B 模块的代码 | 坚决不动 B 模块 |
| 觉得某个字段命名不好，自行重命名 | 字段名必须与 `json设计.md` 一致，除非文档也同步更新 |
| 觉得需要新增依赖，直接修改 `pom.xml` | 先询问用户，说明为什么需要这个依赖 |
| "重构"一个看着不顺眼的工具类 | 没有用户明确指令，不要重构已有代码 |
| 修改 API 路径让它"更 RESTful" | 路径必须与 `json设计.md` 一致 |
| 修改已有的 Flyway 脚本"修复"DDL | 只能新增迁移脚本 |
| 在 Controller 里写了一大段业务逻辑 | 业务逻辑放在 Service 层 |
| 给前端页面"美化"了一下样式 | 除非明确要求，不要改动样式 |

---

> 文档版本：v1.0 | 更新日期：2026-07-14
>
> **使用方式**：将此文件内容作为 AI 的系统提示词（System Prompt），或每次对话时引用相关章节。
