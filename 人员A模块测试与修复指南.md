# 人员 A 模块测试与修复指南

> 生成日期：2026-07-14 | 基于全量代码审查
> 最后更新：2026-07-14 | 三轮修复已全部完成 ✅

---

## 一、测试总览

| 模块 | 页面 | API | 后端架构 | 前端对齐 | 综合状态 |
|------|:--:|:--:|:--:|:--:|:--:|
| System（系统管理） | 7 | 36 | ✅ 完成 | ✅ 完成 | 🟢 就绪 |
| Employee（员工管理） | 5 | 15 | ✅ 完成 | ✅ 完成 | 🟢 已修复 |
| Attendance（考勤管理） | 11 | 34 | ✅ 完成 | ✅ 完成 | 🟢 已修复 |
| Performance（绩效管理） | 5 | 22 | ✅ 完成 | ✅ 完成 | 🟢 已修复 |

---

## 二、修复记录

### 第一轮（P0）：TODO 骨架修复 ✅

| # | 文件 | 修复内容 |
|:--:|------|------|
| 1 | [EmployeeController.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-employee/src/main/java/com/hr/module/employee/controller/EmployeeController.java) | `importExcel()` — 接收 `MultipartFile`，委托 Service 解析 Excel 逐行插入 |
| 2 | [EmployeeController.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-employee/src/main/java/com/hr/module/employee/controller/EmployeeController.java) | `exportExcel()` — 接收 `HttpServletResponse`，委托 Service 写 Excel 输出流 |
| 3 | [AttRecordController.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/controller/AttRecordController.java) | `importRecords()` — 接收 `MultipartFile`，委托 Service 解析并批量插入打卡记录 |
| 4 | [OaFlowController.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/controller/OaFlowController.java) | `importFlows()` — 接收 `MultipartFile`，委托 Service 解析并批量插入 OA 流程 |

**新增文件**：
- [EmployeeImportDTO.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-employee/src/main/java/com/hr/module/employee/dto/EmployeeImportDTO.java) — 员工 Excel 导入/导出模型
- [AttRecordImportDTO.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/dto/AttRecordImportDTO.java) — 打卡记录 Excel 导入模型
- [AttOaFlowImportDTO.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/dto/AttOaFlowImportDTO.java) — OA 流程 Excel 导入模型

**修改文件**：
- [HrEmployeeService.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-employee/src/main/java/com/hr/module/employee/service/HrEmployeeService.java) — 新增 `importExcel` / `exportExcel` 接口
- [HrEmployeeServiceImpl.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-employee/src/main/java/com/hr/module/employee/service/impl/HrEmployeeServiceImpl.java) — 实现导入/导出
- [AttRecordService.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/service/AttRecordService.java) — 新增 `importRecords` 接口
- [AttRecordServiceImpl.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/service/impl/AttRecordServiceImpl.java) — 实现导入
- [AttOaFlowService.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/service/AttOaFlowService.java) — 新增 `importFlows` 接口
- [AttOaFlowServiceImpl.java](file:///d:/PROJECT/hr-system/project/hr-backend/hr-module-attendance/src/main/java/com/hr/module/attendance/service/impl/AttOaFlowServiceImpl.java) — 实现导入

---

### 第二轮（P1）：前端 API 补齐 ✅

| # | 文件 | 修复内容 |
|:--:|------|------|
| 1 | [performance.ts](file:///d:/PROJECT/hr-system/project/hr-frontend/src/api/modules/performance.ts) | `perfLevelApi` 新增 `detail(id)` 函数 |
| 2 | [performance.ts](file:///d:/PROJECT/hr-system/project/hr-frontend/src/api/modules/performance.ts) | `perfSalaryApi` 新增 `detail(id)` 函数 |
| 3 | [employee.ts](file:///d:/PROJECT/hr-system/project/hr-frontend/src/api/modules/employee.ts) | 新增 `toggleAccount(id)` 函数 |

---

### 第三轮（P1）：路由权限标识统一 ✅

| # | 路由 | 修复前 | 修复后 |
|:--:|------|------|------|
| 1 | CardRuleSetting | `attendance:rule:list` | `attendance:card-rule:list` |
| 2 | OAFlowManage | `attendance:oa:list` | `attendance:oa-flow:list` |
| 3 | LevelSetting | `performance:level:list` | `perf:level:list` |
| 4 | SalarySetting | `performance:salary:list` | `perf:salary:list` |
| 5 | PlanManage | `performance:plan:list` | `perf:plan:list` |
| 6 | ExamRecord | `performance:record:list` | `perf:record:list` |
| 7 | PerfReport | `performance:report:list` | `perf:report:list` |
| 8 | OnboardingForm | `employee:onboarding` | `employee:create` |
| 9 | AccountOpen | `employee:account` | `employee:account:create` |
| 10 | TransferApply | `employee:transfer:apply` | `employee:transfer:create` |


