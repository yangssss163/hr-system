import type { RouteRecordRaw } from 'vue-router'

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { hidden: true }
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/HomeView.vue'),
        meta: { title: '系统首页', icon: 'HomeFilled' }
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Grid' },
        children: [
          { path: 'company', name: 'CompanyManage', component: () => import('@/views/system/company/CompanyManage.vue'), meta: { title: '公司架构', permission: 'system:company:list' } },
          { path: 'dept', name: 'DeptManage', component: () => import('@/views/system/dept/DeptManage.vue'), meta: { title: '部门架构', permission: 'system:dept:list' } },
          { path: 'position', name: 'PositionManage', component: () => import('@/views/system/position/PositionManage.vue'), meta: { title: '职位管理', permission: 'system:position:list' } },
          { path: 'user', name: 'UserManage', component: () => import('@/views/system/user/UserManage.vue'), meta: { title: '用户管理', permission: 'system:user:list' } },
          { path: 'role', name: 'RoleManage', component: () => import('@/views/system/role/RoleManage.vue'), meta: { title: '角色权限', permission: 'system:role:list' } },
          { path: 'menu', name: 'MenuManage', component: () => import('@/views/system/menu/MenuManage.vue'), meta: { title: '菜单管理', permission: 'system:menu:list' } },
          { path: 'field', name: 'FieldConfig', component: () => import('@/views/system/field/FieldConfig.vue'), meta: { title: '字段配置', permission: 'system:field:list' } }
        ]
      },
      {
        path: 'employee',
        name: 'Employee',
        meta: { title: '员工管理', icon: 'User' },
        children: [
          { path: 'list', name: 'EmployeeList', component: () => import('@/views/employee/list/EmployeeList.vue'), meta: { title: '员工花名册', permission: 'employee:list' } },
          { path: 'onboarding', name: 'OnboardingForm', component: () => import('@/views/employee/onboarding/OnboardingForm.vue'), meta: { title: '创建入职', permission: 'employee:onboarding' } },
          { path: 'transfer', name: 'TransferApply', component: () => import('@/views/employee/transfer/TransferApply.vue'), meta: { title: '异动申请', permission: 'employee:transfer:apply' } },
          { path: 'transfer-detail', name: 'TransferDetail', component: () => import('@/views/employee/transfer/TransferDetail.vue'), meta: { title: '异动明细', permission: 'employee:transfer:list' } },
          { path: 'account', name: 'AccountOpen', component: () => import('@/views/employee/account/AccountOpen.vue'), meta: { title: '账号开通', permission: 'employee:account' } }
        ]
      },
      {
        path: 'recruitment',
        name: 'Recruitment',
        meta: { title: '招聘管理', icon: 'Search' },
        children: [
          { path: 'resume', name: 'ResumeList', component: () => import('@/views/recruitment/resume/ResumeList.vue'), meta: { title: '简历管理', permission: 'recruitment:resume:list' } },
          { path: 'interview', name: 'InterviewList', component: () => import('@/views/recruitment/interview/InterviewList.vue'), meta: { title: '面试管理', permission: 'recruitment:interview:list' } },
          { path: 'question', name: 'QuestionBank', component: () => import('@/views/recruitment/question/QuestionBank.vue'), meta: { title: '面试题库', permission: 'recruitment:question:list' } },
          { path: 'template', name: 'NotifyTemplate', component: () => import('@/views/recruitment/template/NotifyTemplate.vue'), meta: { title: '通知模板', permission: 'recruitment:template:list' } },
          { path: 'blacklist', name: 'BlacklistManage', component: () => import('@/views/recruitment/blacklist/BlacklistManage.vue'), meta: { title: '面试黑名单', permission: 'recruitment:blacklist:list' } },
          { path: 'report', name: 'RecruitmentReport', component: () => import('@/views/recruitment/report/RecruitmentReport.vue'), meta: { title: '招聘报表', permission: 'recruitment:report:list' } }
        ]
      },
      {
        path: 'attendance',
        name: 'Attendance',
        meta: { title: '考勤管理', icon: 'Clock' },
        children: [
          { path: 'record', name: 'AttendanceRecord', component: () => import('@/views/attendance/record/AttendanceRecord.vue'), meta: { title: '打卡记录', permission: 'attendance:record:list' } },
          { path: 'card-rule', name: 'CardRuleSetting', component: () => import('@/views/attendance/rule/CardRuleSetting.vue'), meta: { title: '取卡规则', permission: 'attendance:rule:list' } },
          { path: 'shift', name: 'ShiftSetting', component: () => import('@/views/attendance/shift/ShiftSetting.vue'), meta: { title: '班次设置', permission: 'attendance:shift:list' } },
          { path: 'holiday', name: 'HolidaySetting', component: () => import('@/views/attendance/holiday/HolidaySetting.vue'), meta: { title: '法定假期', permission: 'attendance:holiday:list' } },
          { path: 'leave-quota', name: 'LeaveQuota', component: () => import('@/views/attendance/leave/LeaveQuota.vue'), meta: { title: '假期额度', permission: 'attendance:leave-quota:list' } },
          { path: 'leave-type', name: 'LeaveTypeSetting', component: () => import('@/views/attendance/leave/LeaveTypeSetting.vue'), meta: { title: '假期设置', permission: 'attendance:leave-type:list' } },
          { path: 'exception', name: 'ExceptionManage', component: () => import('@/views/attendance/exception/ExceptionManage.vue'), meta: { title: '异常处理', permission: 'attendance:exception:list' } },
          { path: 'oa', name: 'OAFlowManage', component: () => import('@/views/attendance/oa/OAFlowManage.vue'), meta: { title: 'OA流程', permission: 'attendance:oa:list' } },
          { path: 'deduction', name: 'DeductionSetting', component: () => import('@/views/attendance/deduction/DeductionSetting.vue'), meta: { title: '考勤扣款', permission: 'attendance:deduction:list' } },
          { path: 'report-detail', name: 'AttendanceDetail', component: () => import('@/views/attendance/report/AttendanceDetail.vue'), meta: { title: '考勤明细表', permission: 'attendance:report:detail' } },
          { path: 'report-summary', name: 'AttendanceSummary', component: () => import('@/views/attendance/report/AttendanceSummary.vue'), meta: { title: '考勤汇总表', permission: 'attendance:report:summary' } }
        ]
      },
      {
        path: 'performance',
        name: 'Performance',
        meta: { title: '绩效管理', icon: 'TrendCharts' },
        children: [
          { path: 'level', name: 'LevelSetting', component: () => import('@/views/performance/level/LevelSetting.vue'), meta: { title: '绩效等级', permission: 'performance:level:list' } },
          { path: 'salary', name: 'SalarySetting', component: () => import('@/views/performance/salary/SalarySetting.vue'), meta: { title: '绩效工资', permission: 'performance:salary:list' } },
          { path: 'plan', name: 'PlanManage', component: () => import('@/views/performance/plan/PlanManage.vue'), meta: { title: '考核计划', permission: 'performance:plan:list' } },
          { path: 'record', name: 'ExamRecord', component: () => import('@/views/performance/record/ExamRecord.vue'), meta: { title: '考核记录', permission: 'performance:record:list' } },
          { path: 'report', name: 'PerfReport', component: () => import('@/views/performance/report/PerfReport.vue'), meta: { title: '绩效报表', permission: 'performance:report:list' } }
        ]
      },
      {
        path: 'salary',
        name: 'Salary',
        meta: { title: '薪酬管理', icon: 'Wallet' },
        children: [
          { path: 'rule', name: 'ComputeRule', component: () => import('@/views/salary/rule/ComputeRule.vue'), meta: { title: '核算规则', permission: 'salary:rule:list' } },
          { path: 'field', name: 'FieldManage', component: () => import('@/views/salary/field/FieldManage.vue'), meta: { title: '字段管理', permission: 'salary:field:list' } },
          { path: 'adjust', name: 'SalaryAdjust', component: () => import('@/views/salary/adjust/SalaryAdjust.vue'), meta: { title: '员工调薪', permission: 'salary:adjust:list' } },
          { path: 'sheet', name: 'SalarySheet', component: () => import('@/views/salary/sheet/SalarySheet.vue'), meta: { title: '工资单管理', permission: 'salary:sheet:list' } }
        ]
      },
      {
        path: 'crm',
        name: 'Crm',
        meta: { title: 'CRM管理', icon: 'UserFilled' },
        children: [
          { path: 'customer', name: 'CustomerManage', component: () => import('@/views/crm/customer/CustomerManage.vue'), meta: { title: '客户管理', permission: 'crm:customer:list' } },
          { path: 'opportunity', name: 'OpportunityManage', component: () => import('@/views/crm/opportunity/OpportunityManage.vue'), meta: { title: '商机管理', permission: 'crm:opportunity:list' } },
          { path: 'order', name: 'OrderManage', component: () => import('@/views/crm/order/OrderManage.vue'), meta: { title: '订单管理', permission: 'crm:order:list' } },
          { path: 'payment', name: 'PaymentManage', component: () => import('@/views/crm/payment/PaymentManage.vue'), meta: { title: '回款管理', permission: 'crm:payment:list' } },
          { path: 'refund', name: 'RefundManage', component: () => import('@/views/crm/refund/RefundManage.vue'), meta: { title: '退款管理', permission: 'crm:refund:list' } },
          { path: 'expense', name: 'ExpenseManage', component: () => import('@/views/crm/expense/ExpenseManage.vue'), meta: { title: '费用管理', permission: 'crm:expense:list' } }
        ]
      },
      {
        path: 'office',
        name: 'Office',
        meta: { title: '办公管理', icon: 'OfficeBuilding' },
        children: [
          { path: 'notice', name: 'NoticeManage', component: () => import('@/views/office/notice/NoticeManage.vue'), meta: { title: '公告管理', permission: 'office:notice:list' } },
          { path: 'document', name: 'DocumentManage', component: () => import('@/views/office/document/DocumentManage.vue'), meta: { title: '文档管理', permission: 'office:document:list' } },
          { path: 'task', name: 'TaskManage', component: () => import('@/views/office/task/TaskManage.vue'), meta: { title: '任务管理', permission: 'office:task:list' } },
          { path: 'schedule', name: 'ScheduleManage', component: () => import('@/views/office/schedule/ScheduleManage.vue'), meta: { title: '日程管理', permission: 'office:schedule:list' } },
          { path: 'message', name: 'MessageManage', component: () => import('@/views/office/message/MessageManage.vue'), meta: { title: '消息管理', permission: 'office:message:list' } }
        ]
      },
      {
        path: 'workflow',
        name: 'Workflow',
        meta: { title: '工作流审批', icon: 'Operation' },
        children: [
          { path: 'expense', name: 'ExpenseApproval', component: () => import('@/views/workflow/expense/ExpenseApproval.vue'), meta: { title: '报销审批', permission: 'workflow:expense:list' } },
          { path: 'leave', name: 'LeaveApproval', component: () => import('@/views/workflow/leave/LeaveApproval.vue'), meta: { title: '请假审批', permission: 'workflow:leave:list' } },
          { path: 'loan', name: 'LoanApproval', component: () => import('@/views/workflow/loan/LoanApproval.vue'), meta: { title: '借款审批', permission: 'workflow:loan:list' } },
          { path: 'travel', name: 'TravelApproval', component: () => import('@/views/workflow/travel/TravelApproval.vue'), meta: { title: '出差审批', permission: 'workflow:travel:list' } }
        ]
      }
    ]
  }
]

export const asyncRoutes: RouteRecordRaw[] = []