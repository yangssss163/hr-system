# 人员B任务文档

> 项目：HR 管理系统 | 技术栈：Vue 3 + TypeScript + Element Plus + Vite
> 接口规范：以 [json设计.md](./json设计.md) 为准 | 禁止自行发明接口
> 协作规则：参考 [AI\_CODING\_RULES.md](./AI_CODING_RULES.md) 第 9 章

***

## 一、当前状态概览

### 1.1 已有页面（56 个 Vue 文件，全部路由已配置）

| 模块    | 页面数 | 页面列表                                                     |
| ----- | :-: | -------------------------------------------------------- |
| 系统管理  |  7  | 公司架构、部门架构、职位管理、用户管理、角色权限、菜单管理、字段配置                       |
| 员工管理  |  5  | 员工花名册、创建入职、异动申请、异动明细、账号开通                                |
| 招聘管理  |  6  | 简历管理、面试管理、面试题库、通知模板、面试黑名单、招聘报表                           |
| 考勤管理  |  11 | 打卡记录、取卡规则、班次设置、法定假期、假期额度、假期设置、异常处理、OA流程、考勤扣款、考勤明细表、考勤汇总表 |
| 绩效管理  |  5  | 绩效等级、绩效工资、考核计划、考核记录、绩效报表                                 |
| 薪酬管理  |  4  | 核算规则、字段管理、员工调薪、工资单管理                                     |
| CRM管理 |  6  | 客户管理、商机管理、订单管理、回款管理、退款管理、费用管理                            |
| 办公管理  |  5  | 公告管理、文档管理、任务管理、日程管理、消息管理                                 |
| 工作流审批 |  4  | 报销审批、请假审批、借款审批、出差审批                                      |
| 系统首页  |  1  | HomeView                                                 |
| 登录    |  1  | LoginView                                                |

### 1.2 已有 API 封装

| 文件          | 路径                  |             状态            |
| ----------- | ------------------- | :-----------------------: |
| `http.ts`   | `src/api/http.ts`   |       Axios 实例 + 拦截器      |
| `types.ts`  | `src/api/types.ts`  |       所有 TS 类型定义（完整）      |
| `auth.ts`   | `src/api/auth.ts`   |      登录/登出/用户信息/菜单/密码     |
| `common.ts` | `src/api/common.ts` |        文件上传/下载/下拉选项       |
| `system/`   | `src/api/system/`   |    公司/部门/职位/用户/角色/菜单/字段   |
| `modules/`  | `src/api/modules/`  | 员工/招聘/考勤/绩效/薪酬/CRM/办公/工作流 |

### 1.3 基础设施（已完成）

- 路由守卫（白名单只放 `/login`，Token 过期自动跳转登录页）
- Pinia 状态管理（user store、app store）
- 全局布局（侧边栏 + 顶栏 + 面包屑 + 页脚）
- Element Plus 按需引入
- 前后端一体化（构建产物输出到后端 static 目录，Spring Boot 统一服务）

***

## 二、待处理任务（按优先级排列）

### 🔴 P0 — 阻塞性问题

| #    | 问题                                                            | 文件                                                                           | 修复方式                                |
| ---- | ------------------------------------------------------------- | ---------------------------------------------------------------------------- | ----------------------------------- |
| P0-1 | `src/api/index.ts` 第 4 行 `export * from './system'` 引用了不存在的路径 | [api/index.ts](file:///e:/PROTOTYPE/project/hr-frontend/src/api/index.ts#L4) | 改为 `export * from './system/index'` |

### 🟡 P1 — 接口适配与功能完善

| #    | 任务                   | 涉及文件                                                               | 说明                                                       |
| ---- | -------------------- | ------------------------------------------------------------------ | -------------------------------------------------------- |
| P1-1 | **用户管理页面适配后端接口**     | `views/system/user/UserManage.vue` + `api/system/user.ts`          | 确认列表/新增/编辑/删除/分配角色接口与 [json设计.md](./json设计.md) 3.4 节完全一致 |
| P1-2 | **角色管理页面适配后端接口**     | `views/system/role/RoleManage.vue` + `api/system/role.ts`          | 确认分配菜单权限接口 `PUT /roles/{id}/menus`                       |
| P1-3 | **员工花名册 Excel 导入导出** | `views/employee/list/EmployeeList.vue` + `api/modules/employee.ts` | `POST /employees/import` + `GET /employees/export`       |
| P1-4 | **面试流程完整对接**         | `views/recruitment/interview/InterviewList.vue`                    | 签到→评价→通过→淘汰→Offer→确认入职 全流程                               |
| P1-5 | **工资表生成/导出**         | `views/salary/sheet/SalarySheet.vue` + `api/modules/salary.ts`     | 同步数据→生成→导出                                               |
| P1-6 | **工作流审批按钮对接**        | `views/workflow/*/` 4 个页面                                          | 审批按钮调用 `PUT /workflow/{type}/{id}/approve`               |

### 🟢 P2 — UI/UX 优化

| #    | 任务            | 说明                                   |
| ---- | ------------- | ------------------------------------ |
| P2-1 | **表格空状态占位**   | 各模块列表页的 `el-table` 空数据提示             |
| P2-2 | **表单二次确认**    | 删除/撤销等破坏性操作增加 `ElMessageBox.confirm` |
| P2-3 | **公司/部门树选择器** | 公司架构、部门架构页面使用 `el-tree-select`       |
| P2-4 | **面包屑实时更新**   | 页面切换后面包屑正确显示层级                       |

### ⚪ P3 — 非紧急

| #    | 任务                    | 说明                        |
| ---- | --------------------- | ------------------------- |
| P3-1 | **ECharts 图表数据对接**    | HomeView 首页统计图表、招聘报表、绩效报表 |
| P3-2 | **消息通知轮询**            | 顶栏的消息通知接入真实数据             |
| P3-3 | **v-permission 指令完善** | 确保所有按钮都添加了权限指令            |
| P3-4 | **响应式布局优化**           | 侧边栏折叠/展开状态持久化             |

***

## 三、任务执行规范

### 3.1 开发前必做

1. 阅读 [json设计.md](./json设计.md) 对应模块的接口定义
2. 确认字段名、类型、枚举值与文档一致
3. 确认前端 `src/api/types.ts` 中的类型定义与接口文档匹配

### 3.2 接口调用流程

```
前端页面 .vue
    → 调用 src/api/modules/xxx.ts 中的函数
    → 通过 src/api/http.ts 的 axios 实例发送请求
    → axios 拦截器自动注入 Authorization: Bearer {token}
    → 后端返回 { code: 200, message: "成功", data: {...} }
    → 前端处理响应，更新页面
```

### 3.3 枚举值对照（快速参考）

| 业务   | 字段             | 可选值                                                         |
| ---- | -------------- | ----------------------------------------------------------- |
| 性别   | `gender`       | `1`=男, `2`=女                                                |
| 员工状态 | `status`       | `1`=在职, `2`=离职, `3`=试用                                      |
| 简历状态 | `status`       | `new`/`screening`/`interview`/`offer`/`hired`/`eliminated`  |
| 学历   | `education`    | `high_school`/`junior_college`/`bachelor`/`master`/`doctor` |
| 面试结果 | `result`       | `pending`/`pass`/`fail`/`offer`/`hired`                     |
| 异动类型 | `transferType` | `regular`/`transfer`/`promote`/`dimission`/`rehire`         |
| 打卡状态 | `status`       | `normal`/`late`/`early`/`absent`/`overtime`                 |
| 菜单类型 | `type`         | `1`=目录, `2`=菜单, `3`=按钮/权限                                   |

### 3.4 日期时间格式

| 类型 | 格式                    | 示例                    |
| -- | --------------------- | --------------------- |
| 日期 | `yyyy-MM-dd`          | `2026-07-12`          |
| 时间 | `yyyy-MM-dd HH:mm:ss` | `2026-07-12 14:30:00` |

### 3.5 新增页面清单

新增页面时需要同步修改：

| 操作          | 文件                                                           |
| ----------- | ------------------------------------------------------------ |
| 新建页面组件      | `src/views/{module}/XxxPage.vue`                             |
| 新增 API 调用函数 | `src/api/modules/{module}.ts` 或 `src/api/system/{entity}.ts` |
| 注册路由        | `src/router/routes.ts` 对应模块的 children 数组                     |
| 新增 TS 类型    | `src/api/types.ts`（如需要新接口类型）                                 |

***

## 四、Git 协作规范

### 4.1 分支策略

- 人员A（后端）使用**奇数** Flyway 版本号：V7、V9、V11...
- 人员B（前端）使用**偶数** Flyway 版本号：V8、V10、V12...
- 前端人员**不直接修改** Flyway 迁移脚本（如需新增菜单或权限，告知后端人员A）

### 4.2 每日流程

```
git checkout dev
git pull origin dev
git checkout -b feature/frontend-xxx  # 创建自己的分支
# 开发中...
git add 目标文件
git commit -m "feat(frontend): 描述"
# 下班前合并 dev
git checkout dev
git pull origin dev
git checkout feature/frontend-xxx
git merge dev
```

### 4.3 冲突预防

| 前端易冲突文件                | 预防措施                              |
| ---------------------- | --------------------------------- |
| `src/router/routes.ts` | 每人只新增自己模块的路由，在同模块 children 数组末尾追加 |
| `src/api/index.ts`     | 新增 API 模块后，在 index.ts 末尾追加 export |
| `src/api/types.ts`     | 新增类型在文件末尾追加                       |

***

## 五、构建与测试

### 5.1 本地构建

```bash
cd project/hr-frontend
npm run build
# 构建产物输出到：../hr-backend/hr-server/src/main/resources/static/
```

### 5.2 启动后端（前后端一体）

```bash
cd project/hr-backend
mvn package -DskipTests -pl hr-server -am -q
java -jar hr-server/target/hr-server-1.0.0.jar
# 访问 http://localhost:8080/
```

### 5.3 接口文档

```
http://localhost:8080/doc.html    （Knife4j 在线文档）
```

***

## 六、模块文件索引

### 6.1 页面 → API 对应关系

| 页面目录                     | API 文件                       | 后端路径前缀                                                                                                                                                                                                                        |
| ------------------------ | ---------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `views/system/company/`  | `api/system/company.ts`      | `/api/companies`                                                                                                                                                                                                              |
| `views/system/dept/`     | `api/system/dept.ts`         | `/api/depts`                                                                                                                                                                                                                  |
| `views/system/position/` | `api/system/position.ts`     | `/api/positions`                                                                                                                                                                                                              |
| `views/system/user/`     | `api/system/user.ts`         | `/api/users`                                                                                                                                                                                                                  |
| `views/system/role/`     | `api/system/role.ts`         | `/api/roles`                                                                                                                                                                                                                  |
| `views/system/menu/`     | `api/system/menu.ts`         | `/api/menus`                                                                                                                                                                                                                  |
| `views/system/field/`    | `api/system/fieldConfig.ts`  | `/api/field-configs`                                                                                                                                                                                                          |
| `views/employee/`        | `api/modules/employee.ts`    | `/api/employees`, `/api/transfers`, `/api/accounts`                                                                                                                                                                           |
| `views/recruitment/`     | `api/modules/recruitment.ts` | `/api/resumes`, `/api/interviews`, `/api/questions`, `/api/notify-templates`, `/api/blacklists`, `/api/recruitment-reports`                                                                                                   |
| `views/attendance/`      | `api/modules/attendance.ts`  | `/api/attendance-records`, `/api/attendance-exceptions`, `/api/oa-flows`, `/api/card-rules`, `/api/shifts`, `/api/holidays`, `/api/leave-quotas`, `/api/leave-types`, `/api/attendance-reports`, `/api/attendance-deductions` |
| `views/performance/`     | `api/modules/performance.ts` | `/api/perf-levels`, `/api/perf-salaries`, `/api/perf-plans`, `/api/perf-records`, `/api/perf-reports`                                                                                                                         |
| `views/salary/`          | `api/modules/salary.ts`      | `/api/salary-rules`, `/api/salary-fields`, `/api/salary-adjusts`, `/api/salary-sheets`                                                                                                                                        |
| `views/crm/`             | `api/modules/crm.ts`         | `/api/customers`, `/api/opportunities`, `/api/orders`, `/api/payments`, `/api/refunds`, `/api/expenses`                                                                                                                       |
| `views/office/`          | `api/modules/office.ts`      | `/api/notices`, `/api/documents`, `/api/tasks`, `/api/schedules`, `/api/messages`                                                                                                                                             |
| `views/workflow/`        | `api/modules/workflow.ts`    | `/api/workflow/expenses`, `/api/workflow/leaves`, `/api/workflow/loans`, `/api/workflow/travels`                                                                                                                              |

***

> 文档版本：v1.0 | 更新日期：2026-07-14
> 如有疑问，参考 [项目方案.md](./项目方案.md) 和 [json设计.md](./json设计.md)

