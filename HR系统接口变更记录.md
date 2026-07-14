# HR 管理系统 — 接口变更记录

> 版本：v1.1 | 更新日期：2026-07-12 | 变更人：开发团队

---

## 一、变更概述

本次变更新增了 **CRM 客户关系管理**、**办公管理**、**工作流审批** 三大功能模块，共计 **79 个 API 端点**，使系统总接口数从约 **120 个** 增至约 **199 个**。

同时修复了 3 个关键问题：

| # | 问题 | 影响 | 修复方式 |
|---|------|------|----------|
| 1 | 15 张数据库表缺失，所有新增端点返回 500 | 高危 | 创建 V5 Flyway 迁移脚本建表 |
| 2 | 系统管理菜单被删除 + 新模块菜单缺失，权限校验全部失败 | 高危 | 创建 V6 迁移脚本重建 199 条菜单 + 权限分配 |
| 3 | `@PreAuthorize` 权限不足时返回 500 而非 403 | 中危 | `GlobalExceptionHandler` 新增 `AccessDeniedException` 处理 |

---

## 二、新增 API 端点清单

### 2.1 CRM 管理模块（30 个端点）

#### 客户管理 — `/api/customers`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/customers` | 客户列表（分页+搜索） | `crm:customer:list` |
| GET | `/api/customers/{id}` | 客户详情 | `crm:customer:list` |
| POST | `/api/customers` | 新增客户 | `crm:customer:create` |
| PUT | `/api/customers/{id}` | 编辑客户 | `crm:customer:update` |
| DELETE | `/api/customers/{id}` | 删除客户 | `crm:customer:delete` |

#### 商机管理 — `/api/opportunities`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/opportunities` | 商机列表 | `crm:opportunity:list` |
| GET | `/api/opportunities/{id}` | 商机详情 | `crm:opportunity:list` |
| POST | `/api/opportunities` | 新增商机 | `crm:opportunity:create` |
| PUT | `/api/opportunities/{id}` | 编辑商机 | `crm:opportunity:update` |
| DELETE | `/api/opportunities/{id}` | 删除商机 | `crm:opportunity:delete` |

#### 订单管理 — `/api/orders`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/orders` | 订单列表 | `crm:order:list` |
| GET | `/api/orders/{id}` | 订单详情 | `crm:order:list` |
| POST | `/api/orders` | 新增订单 | `crm:order:create` |
| PUT | `/api/orders/{id}` | 编辑订单 | `crm:order:update` |
| DELETE | `/api/orders/{id}` | 删除订单 | `crm:order:delete` |

#### 回款管理 — `/api/payments`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/payments` | 回款列表 | `crm:payment:list` |
| GET | `/api/payments/{id}` | 回款详情 | `crm:payment:list` |
| POST | `/api/payments` | 新增回款 | `crm:payment:create` |
| PUT | `/api/payments/{id}` | 编辑回款 | `crm:payment:update` |
| DELETE | `/api/payments/{id}` | 删除回款 | `crm:payment:delete` |

#### 退款管理 — `/api/refunds`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/refunds` | 退款列表 | `crm:refund:list` |
| GET | `/api/refunds/{id}` | 退款详情 | `crm:refund:list` |
| POST | `/api/refunds` | 新增退款 | `crm:refund:create` |
| PUT | `/api/refunds/{id}` | 编辑退款 | `crm:refund:update` |
| DELETE | `/api/refunds/{id}` | 删除退款 | `crm:refund:delete` |

#### 费用管理 — `/api/expenses`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/expenses` | 费用列表 | `crm:expense:list` |
| GET | `/api/expenses/{id}` | 费用详情 | `crm:expense:list` |
| POST | `/api/expenses` | 新增费用 | `crm:expense:create` |
| PUT | `/api/expenses/{id}` | 编辑费用 | `crm:expense:update` |
| DELETE | `/api/expenses/{id}` | 删除费用 | `crm:expense:delete` |

### 2.2 办公管理模块（25 个端点）

#### 公告管理 — `/api/notices`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/notices` | 公告列表 | `office:notice:list` |
| GET | `/api/notices/{id}` | 公告详情 | `office:notice:list` |
| POST | `/api/notices` | 新增公告 | `office:notice:create` |
| PUT | `/api/notices/{id}` | 编辑公告 | `office:notice:update` |
| DELETE | `/api/notices/{id}` | 删除公告 | `office:notice:delete` |

#### 文档管理 — `/api/documents`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/documents` | 文档列表 | `office:document:list` |
| GET | `/api/documents/{id}` | 文档详情 | `office:document:list` |
| POST | `/api/documents` | 新增文档 | `office:document:create` |
| PUT | `/api/documents/{id}` | 编辑文档 | `office:document:update` |
| DELETE | `/api/documents/{id}` | 删除文档 | `office:document:delete` |

#### 任务管理 — `/api/tasks`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/tasks` | 任务列表 | `office:task:list` |
| GET | `/api/tasks/{id}` | 任务详情 | `office:task:list` |
| POST | `/api/tasks` | 新增任务 | `office:task:create` |
| PUT | `/api/tasks/{id}` | 编辑任务 | `office:task:update` |
| DELETE | `/api/tasks/{id}` | 删除任务 | `office:task:delete` |

#### 日程管理 — `/api/schedules`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/schedules` | 日程列表 | `office:schedule:list` |
| GET | `/api/schedules/{id}` | 日程详情 | `office:schedule:list` |
| POST | `/api/schedules` | 新增日程 | `office:schedule:create` |
| PUT | `/api/schedules/{id}` | 编辑日程 | `office:schedule:update` |
| DELETE | `/api/schedules/{id}` | 删除日程 | `office:schedule:delete` |

#### 消息管理 — `/api/messages`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/messages` | 消息列表 | `office:message:list` |
| GET | `/api/messages/{id}` | 消息详情 | `office:message:list` |
| POST | `/api/messages` | 新增消息 | `office:message:create` |
| PUT | `/api/messages/{id}` | 编辑消息 | `office:message:update` |
| DELETE | `/api/messages/{id}` | 删除消息 | `office:message:delete` |

### 2.3 工作流模块（24 个端点）

#### 报销审批 — `/api/workflow/expenses`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/workflow/expenses` | 报销列表 | `workflow:expense:list` |
| GET | `/api/workflow/expenses/{id}` | 报销详情 | `workflow:expense:list` |
| POST | `/api/workflow/expenses` | 新建报销 | `workflow:expense:create` |
| PUT | `/api/workflow/expenses/{id}` | 编辑报销 | `workflow:expense:update` |
| DELETE | `/api/workflow/expenses/{id}` | 删除报销 | `workflow:expense:delete` |
| PUT | `/api/workflow/expenses/{id}/approve` | 审批报销 | `workflow:expense:approve` |

#### 请假审批 — `/api/workflow/leaves`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/workflow/leaves` | 请假列表 | `workflow:leave:list` |
| GET | `/api/workflow/leaves/{id}` | 请假详情 | `workflow:leave:list` |
| POST | `/api/workflow/leaves` | 新建请假 | `workflow:leave:create` |
| PUT | `/api/workflow/leaves/{id}` | 编辑请假 | `workflow:leave:update` |
| DELETE | `/api/workflow/leaves/{id}` | 删除请假 | `workflow:leave:delete` |
| PUT | `/api/workflow/leaves/{id}/approve` | 审批请假 | `workflow:leave:approve` |

#### 借款审批 — `/api/workflow/loans`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/workflow/loans` | 借款列表 | `workflow:loan:list` |
| GET | `/api/workflow/loans/{id}` | 借款详情 | `workflow:loan:list` |
| POST | `/api/workflow/loans` | 新建借款 | `workflow:loan:create` |
| PUT | `/api/workflow/loans/{id}` | 编辑借款 | `workflow:loan:update` |
| DELETE | `/api/workflow/loans/{id}` | 删除借款 | `workflow:loan:delete` |
| PUT | `/api/workflow/loans/{id}/approve` | 审批借款 | `workflow:loan:approve` |

#### 出差审批 — `/api/workflow/travels`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/workflow/travels` | 出差列表 | `workflow:travel:list` |
| GET | `/api/workflow/travels/{id}` | 出差详情 | `workflow:travel:list` |
| POST | `/api/workflow/travels` | 新建出差 | `workflow:travel:create` |
| PUT | `/api/workflow/travels/{id}` | 编辑出差 | `workflow:travel:update` |
| DELETE | `/api/workflow/travels/{id}` | 删除出差 | `workflow:travel:delete` |
| PUT | `/api/workflow/travels/{id}/approve` | 审批出差 | `workflow:travel:approve` |

### 2.4 端点统计

| 模块 | 子模块数 | 端点总数 | 模式 |
|------|:---:|:---:|------|
| CRM 管理 | 6 | 30 | 标准 CRUD（每模块 5 端点） |
| 办公管理 | 5 | 25 | 标准 CRUD（每模块 5 端点） |
| 工作流 | 4 | 24 | CRUD + 审批（每模块 6 端点） |
| **合计** | **15** | **79** | |

---

## 三、数据库变更

### 3.1 V5 — 创建 CRM/办公/工作流数据表

**文件**: `hr-server/src/main/resources/db/migration/V5__crm_office_workflow.sql`

**新建 15 张表**，每张表均包含 `deleted TINYINT DEFAULT 0` 列以兼容 MyBatis-Plus `@TableLogic` 逻辑删除：

| 模块 | 表名 | 说明 |
|------|------|------|
| CRM | `crm_customer` | 客户 |
| CRM | `crm_opportunity` | 商机 |
| CRM | `crm_order` | 订单 |
| CRM | `crm_payment` | 回款 |
| CRM | `crm_refund` | 退款 |
| CRM | `crm_expense` | 费用 |
| 办公 | `sys_notice` | 系统公告 |
| 办公 | `sys_document` | 文档管理 |
| 办公 | `sys_task` | 任务管理 |
| 办公 | `sys_schedule` | 日程管理 |
| 办公 | `sys_message` | 消息管理 |
| 工作流 | `flow_expense` | 报销审批 |
| 工作流 | `flow_loan` | 借款审批 |
| 工作流 | `flow_travel` | 出差审批 |
| 工作流 | `flow_leave` | 请假审批 |

### 3.2 V6 — 重建菜单与权限数据

**文件**: `hr-server/src/main/resources/db/migration/V6__fix_menus_permissions.sql`

**变更内容**：

1. **重新插入系统管理菜单**（ID 1-27），覆盖用户/角色/菜单/部门/公司/职位/字段配置的管理功能
2. **新增 CRM 菜单**（ID 134-163）：客户/商机/订单/回款/退款/费用，每个子模块含 list/create/update/delete 权限按钮
3. **新增办公菜单**（ID 164-188）：公告/文档/任务/日程/消息管理
4. **新增工作流菜单**（ID 189-199）：报销/请假/借款/出差审批，含 approve 审批权限
5. **为超级管理员（role_id=1）分配全部 199 条菜单权限**（`sys_role_menu`）

**权限标识命名规范**：

- CRM: `crm:{resource}:{action}`（如 `crm:customer:list`）
- 办公: `office:{resource}:{action}`（如 `office:notice:create`）
- 工作流: `workflow:{resource}:{action}`（如 `workflow:leave:approve`）

---

## 四、代码变更

### 4.1 GlobalExceptionHandler — 修复 500 → 403

**文件**: `hr-framework/src/main/java/com/hr/framework/web/GlobalExceptionHandler.java`

**变更**：新增 `AccessDeniedException` 异常处理器，当 `@PreAuthorize` 权限校验失败时正确返回 403 而非 500。

```java
@ExceptionHandler(AccessDeniedException.class)
public Result<Void> handleAccessDenied(AccessDeniedException e) {
    log.warn("权限不足: {}", e.getMessage());
    return Result.error(403, "权限不足");
}
```

**影响范围**：所有带 `@PreAuthorize` 注解的端点，权限不足时响应码从 `500` 修正为 `403`。

### 4.2 新增文件清单

| 文件 | 模块 | 类型 |
|------|------|------|
| `hr-module-crm/.../CustomerController.java` | CRM | Controller |
| `hr-module-crm/.../OpportunityController.java` | CRM | Controller |
| `hr-module-crm/.../OrderController.java` | CRM | Controller |
| `hr-module-crm/.../PaymentController.java` | CRM | Controller |
| `hr-module-crm/.../RefundController.java` | CRM | Controller |
| `hr-module-crm/.../ExpenseController.java` | CRM | Controller |
| `hr-module-office/.../NoticeController.java` | 办公 | Controller |
| `hr-module-office/.../DocumentController.java` | 办公 | Controller |
| `hr-module-office/.../TaskController.java` | 办公 | Controller |
| `hr-module-office/.../ScheduleController.java` | 办公 | Controller |
| `hr-module-office/.../MessageController.java` | 办公 | Controller |
| `hr-module-workflow/.../ExpenseApprovalController.java` | 工作流 | Controller |
| `hr-module-workflow/.../LoanApprovalController.java` | 工作流 | Controller |
| `hr-module-workflow/.../TravelApprovalController.java` | 工作流 | Controller |
| `hr-module-workflow/.../LeaveApprovalController.java` | 工作流 | Controller |
| `V5__crm_office_workflow.sql` | 数据库 | Flyway 迁移 |
| `V6__fix_menus_permissions.sql` | 数据库 | Flyway 迁移 |

### 4.3 文档变更

| 文件 | 变更说明 |
|------|----------|
| [json设计.md](./json设计.md) | 新增第十/十一/十二章（CRM/办公/工作流）；旧章编号调整（十一→十四，十二→十五）；接口汇总表扩充至 199 端点 |

---

## 五、前端对接注意事项

1. **新模块必须配置路由**：CRM（`/crm/*`）、办公（`/office/*`）、工作流（`/workflow/*`）
2. **权限指令需扩展**：新增 `crm:*`、`office:*`、`workflow:*` 三组权限标识，前端 `v-permission` 指令需同步更新
3. **工作流审批路径**：审批操作是通过 `PUT /api/workflow/{type}/{id}/approve` 而非独立的审批端点
4. **CRM 费用 vs 工作流报销**：`/api/expenses` 是 CRM 的费用记录（无需审批），`/api/workflow/expenses` 是报销审批流程，两者路径不同
5. **所有新增端点均需 `Authorization: Bearer {token}` 请求头**

---

## 六、测试验证

全部 199 个端点已通过接口测试，覆盖：

- 认证模块（5 端点）
- 系统管理（36 端点）
- 员工管理（15 端点）
- 招聘管理（31 端点）
- 考勤管理（26 端点）
- 绩效管理（22 端点）
- 薪酬管理（14 端点）
- **CRM 管理（30 端点）** ← 新增
- **办公管理（25 端点）** ← 新增
- **工作流（24 端点）** ← 新增
- 公共接口（3 端点）

---

> 对接入完整接口文档请参见 [json设计.md](./json设计.md)
