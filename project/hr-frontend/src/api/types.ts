export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T = any> {
  total: number
  page: number
  pageSize: number
  records: T[]
}

export interface PageParams {
  page?: number
  pageSize?: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  expiresIn: number
}

export interface UserInfo {
  userId: number
  username: string
  realName: string
  avatar: string
  deptId: number
  deptName: string
  email: string
  phone: string
  roles: string[]
  permissions: string[]
}

export interface Menu {
  id: number
  parentId: number
  name: string
  type: number
  path: string
  component: string
  permission: string
  icon: string
  sort: number
  status: number
  children?: Menu[]
}

export interface MenuForm {
  name: string
  parentId: number
  type: number
  path: string
  component: string
  permission: string
  icon: string
  sort: number
  status: number
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface Company {
  id: number
  name: string
  code: string
  parentId: number
  sort: number
  status: number
  children?: Company[]
}

export interface CompanyForm {
  name: string
  code: string
  parentId: number
  sort: number
  status: number
}

export interface Dept {
  id: number
  companyId: number
  companyName: string
  name: string
  parentId: number
  sort: number
  status: number
  children?: Dept[]
}

export interface DeptForm {
  companyId: number
  name: string
  parentId: number
  sort: number
  status: number
}

export interface Position {
  id: number
  name: string
  deptId: number
  deptName: string
  sort: number
  status: number
  createTime: string
}

export interface PositionForm {
  name: string
  deptId: number
  sort: number
  status: number
}

export interface User {
  id: number
  username: string
  realName: string
  deptId: number
  deptName: string
  phone: string
  email: string
  avatar: string
  status: number
  lastLoginTime: string
  createTime: string
  roles: { id: number; name: string; code: string }[]
}

export interface UserForm {
  username: string
  password?: string
  realName: string
  deptId: number
  phone?: string
  email?: string
  roleIds?: number[]
  status: number
}

export interface RoleForm {
  name: string
  code: string
  description: string
  status: number
  menuIds?: number[]
}

export interface FieldConfig {
  id: number
  module: string
  fieldKey: string
  fieldName: string
  visible: boolean
  required: boolean
  sort: number
}

export interface Field {
  id: number
  fieldName: string
  fieldCode: string
  entityType: string
  fieldType: string
  fieldLength: number
  defaultValue: string
  sort: number
  required: number
  visible: number
  status: number
  remark: string
  createTime: string
}

export interface FieldForm {
  fieldName: string
  fieldCode: string
  entityType: string
  fieldType: string
  fieldLength: number
  defaultValue: string
  sort: number
  required: number
  visible: number
  status: number
  remark: string
}

export interface Role {
  id: number
  name: string
  code: string
  description: string
  status: number
  menuIds: number[]
  createTime: string
  children?: Role[]
}

export interface Employee {
  id: number
  userId: number
  empNo: string
  name: string
  gender: number
  phone: string
  email: string
  deptId: number
  deptName: string
  positionId: number
  positionName: string
  companyId: number
  companyName: string
  entryDate: string
  status: number
  createTime: string
}

export interface EmployeeDetail extends Employee {
  idCard: string
  birthday: string
  updateTime: string
}

export interface EmployeeForm {
  empNo: string
  name: string
  gender: number
  idCard?: string
  birthday?: string
  phone: string
  email?: string
  deptId: number
  positionId: number
  companyId: number
  entryDate: string
  status: number
}

export interface EmployeeImportResponse {
  successCount: number
  failCount: number
  failDetails: { row: number; reason: string }[]
}

export interface Transfer {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  transferType: string
  transferTypeName: string
  beforeDeptId: number
  beforeDeptName: string
  afterDeptId: number
  afterDeptName: string
  beforePositionId: number
  beforePositionName: string
  afterPositionId: number
  afterPositionName: string
  effectiveDate: string
  reason: string
  status: number
  createTime: string
}

export interface TransferForm {
  employeeId: number
  transferType: string
  afterDeptId: number
  afterPositionId: number
  effectiveDate: string
  reason: string
}

export interface AccountOpenForm {
  employeeId: number
  username: string
  password: string
  roleIds: number[]
}

export interface Resume {
  id: number
  name: string
  phone: string
  email: string
  gender: number
  education: string
  school: string
  major: string
  workYears: number
  applyPosition: string
  source: string
  status: string
  resumeFile: string
  createTime: string
}

export interface ResumeForm {
  name: string
  phone: string
  email?: string
  gender?: number
  education?: string
  school?: string
  major?: string
  workYears?: number
  applyPosition?: string
  source?: string
  status?: string
}

export interface InviteRequest {
  interviewTime: string
  location: string
  templateId: number
  message: string
}

export interface Interview {
  id: number
  resumeId: number
  candidateName: string
  applyPosition: string
  interviewRound: number
  interviewerId: number
  interviewerName: string
  interviewTime: string
  location: string
  result: string
  score: number | null
  evaluation: string | null
  createTime: string
}

export interface InterviewForm {
  resumeId: number
  interviewRound: number
  interviewerId: number
  interviewTime: string
  location: string
}

export interface InterviewEvaluateForm {
  score: number
  evaluation: string
  result: string
}

export interface EliminateRequest {
  reason: string
}

export interface OfferRequest {
  offerSalary: string
  offerDate: string
  remark: string
}

export interface Question {
  id: number
  category: string
  categoryName: string
  difficulty: string
  difficultyName: string
  title: string
  answer: string
  createTime: string
}

export interface QuestionForm {
  category: string
  difficulty: string
  title: string
  answer: string
}

export interface NotifyTemplate {
  id: number
  name: string
  type: string
  typeName: string
  title: string
  content: string
  status: number
  createTime: string
}

export interface NotifyTemplateForm {
  name: string
  type: string
  title: string
  content: string
  status: number
}

export interface Blacklist {
  id: number
  name: string
  phone: string
  idCard: string
  reason: string
  createTime: string
}

export interface BlacklistForm {
  resumeId?: number
  name: string
  phone: string
  idCard?: string
  reason: string
}

export interface RecruitmentReport {
  totalResumes: number
  totalInterviews: number
  totalPassed: number
  totalHired: number
  conversionRate: number
  channelStats: { channel: string; count: number; rate: number }[]
  monthlyStats: { month: string; resumes: number; interviews: number; hired: number }[]
}

export interface AttendanceRecord {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  recordDate: string
  checkIn: string
  checkOut: string
  status: string
  source: string
}

export interface BatchFixRequest {
  ids: number[]
  checkIn: string
  checkOut: string
  remark: string
}

export interface AttendanceException {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  recordDate: string
  type: string
  typeName: string
  detail: string
  oaStatus: string
}

export interface OaFlow {
  id: number
  employeeId: number
  employeeName: string
  type: string
  typeName: string
  startDate: string
  endDate: string
  duration: number
  status: string
  statusName: string
  createTime: string
}

export interface CardRule {
  id: number
  name: string
  minCardCount: number
  allowOvertime: boolean
  status: number
}

export interface CardRuleForm {
  name: string
  minCardCount: number
  allowOvertime: boolean
  status: number
}

export interface Shift {
  id: number
  name: string
  startTime: string
  endTime: string
  lateBuffer: number
  earlyBuffer: number
  status: number
}

export interface ShiftForm {
  name: string
  startTime: string
  endTime: string
  lateBuffer: number
  earlyBuffer: number
  status: number
}

export interface Holiday {
  id: number
  name: string
  date: string
  days: number
}

export interface HolidayForm {
  year: number
  name: string
  date: string
  days: number
}

export interface HolidayListResponse {
  year: number
  items: Holiday[]
}

export interface LeaveQuota {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  leaveType: string
  leaveTypeName: string
  year: number
  totalDays: number
  usedDays: number
  remainDays: number
}

export interface LeaveType {
  id: number
  name: string
  code: string
  defaultDays: number
  enabled: boolean
}

export interface LeaveTypeForm {
  name: string
  code: string
  defaultDays: number
  enabled: boolean
}

export interface AttendanceSummaryItem {
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  shouldWorkDays: number
  actualWorkDays: number
  lateCount: number
  earlyCount: number
  absentCount: number
  leaveCount: number
  overtimeHours: number
}

export interface AttendanceSummaryReport {
  records: AttendanceSummaryItem[]
}

export interface AttendanceDeduction {
  id: number
  type: string
  typeName: string
  deduction: number
  unit: string
  remark: string
}

export interface LeaveApply {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  leaveTypeId: number
  leaveTypeName: string
  startTime: string
  endTime: string
  duration: number
  reason: string
  status: string
  createTime: string
}

export interface OvertimeApply {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  overtimeDate: string
  startTime: string
  endTime: string
  hours: number
  reason: string
  status: string
  createTime: string
}

export interface WorkShift {
  id: number
  name: string
  code: string
  startTime: string
  endTime: string
  workHours: number
  description: string
}

export interface AttendanceDevice {
  id: number
  name: string
  code: string
  type: string
  ipAddress: string
  location: string
  status: string
  lastSyncTime: string
}

export interface PerfLevel {
  id: number
  name: string
  scoreMin: number
  scoreMax: number
  coefficient: number
  sort: number
}

export interface PerfLevelForm {
  name: string
  scoreMin: number
  scoreMax: number
  coefficient: number
  sort: number
}

export interface PerfSalary {
  id: number
  levelId: number
  levelName: string
  salaryRange: string
  sort: number
}

export interface PerfSalaryForm {
  levelId: number
  salaryRange: string
  sort: number
}

export interface PerfPlan {
  id: number
  name: string
  deptId: number
  deptName: string
  startDate: string
  endDate: string
  status: number
  statusName: string
  createTime: string
}

export interface PerfPlanForm {
  name: string
  deptId: number
  startDate: string
  endDate: string
  employeeIds: number[]
  status: number
}

export interface PerfRecord {
  id: number
  planId: number
  planName: string
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  totalScore: number
  levelId: number
  levelName: string
  evaluatorName: string
  evaluateTime: string
}

export interface PerfRecordForm {
  planId: number
  employeeId: number
  items: { indicator: string; weight: number; score: number }[]
  totalScore: number
  evaluation: string
  levelId: number
}

export interface SalaryRule {
  id: number
  type: string
  typeName: string
  value: number
  unit: string
  remark: string
}

export interface SalaryField {
  id: number
  name: string
  code: string
  type: string
  typeName: string
  formula: string
  sort: number
  status: number
}

export interface SalaryFieldForm {
  name: string
  code: string
  type: string
  formula: string
  sort: number
  status: number
}

export interface SalaryAdjust {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  beforeSalary: number
  afterSalary: number
  adjustAmount: number
  adjustType: string
  adjustTypeName: string
  effectiveDate: string
  remark: string
  createTime: string
}

export interface SalaryAdjustForm {
  employeeId: number
  afterSalary: number
  adjustType: string
  effectiveDate: string
  remark: string
}

export interface SalarySheet {
  id: number
  yearMonth: string
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  baseSalary: number
  perfSalary: number
  subsidy: number
  overtimePay: number
  totalIncome: number
  socialInsurance: number
  tax: number
  totalDeduction: number
  netSalary: number
  status: string
}

export interface UploadResponse {
  fileName: string
  fileUrl: string
  fileSize: number
}

export interface OptionItem {
  value: string
  label: string
}

export interface Customer {
  id: number
  name: string
  contact: string
  phone: string
  email: string
  address: string
  industry: string
  source: string
  status: number
  createTime: string
  updateTime: string
}

export interface CustomerForm {
  name: string
  contact: string
  phone: string
  email?: string
  address?: string
  industry?: string
  source?: string
  status: number
}

export interface Opportunity {
  id: number
  customerId: number
  customerName: string
  name: string
  amount: number
  stage: string
  probability: number
  ownerId: number
  ownerName: string
  expectedCloseDate: string
  status: number
  createTime: string
  updateTime: string
}

export interface OpportunityForm {
  customerId: number
  name: string
  amount: number
  stage: string
  probability: number
  ownerId: number
  expectedCloseDate: string
  status: number
}

export interface Order {
  id: number
  customerId: number
  customerName: string
  opportunityId: number
  opportunityName: string
  orderNo: string
  amount: number
  status: string
  createTime: string
  updateTime: string
}

export interface OrderForm {
  customerId: number
  opportunityId?: number
  orderNo: string
  amount: number
  status: string
}

export interface Payment {
  id: number
  orderId: number
  orderNo: string
  amount: number
  paymentDate: string
  paymentMethod: string
  status: number
  createTime: string
}

export interface PaymentForm {
  orderId: number
  amount: number
  paymentDate: string
  paymentMethod: string
  status: number
}

export interface Refund {
  id: number
  orderId: number
  orderNo: string
  amount: number
  refundDate: string
  reason: string
  status: number
  createTime: string
}

export interface RefundForm {
  orderId: number
  amount: number
  refundDate: string
  reason: string
  status: number
}

export interface CrmExpense {
  id: number
  customerId: number
  customerName: string
  amount: number
  type: string
  description: string
  expenseDate: string
  status: number
  createTime: string
}

export interface CrmExpenseForm {
  customerId: number
  amount: number
  type: string
  description?: string
  expenseDate: string
  status: number
}

export interface Notice {
  id: number
  title: string
  content: string
  type: string
  status: number
  publishTime: string
  createTime: string
}

export interface NoticeForm {
  title: string
  content: string
  type: string
  status: number
}

export interface Document {
  id: number
  name: string
  type: string
  fileUrl: string
  fileSize: number
  description: string
  status: number
  createTime: string
}

export interface DocumentForm {
  name: string
  type: string
  fileUrl: string
  fileSize: number
  description?: string
  status: number
}

export interface Task {
  id: number
  title: string
  description: string
  assigneeId: number
  assigneeName: string
  priority: string
  status: string
  dueDate: string
  createTime: string
  updateTime: string
}

export interface TaskForm {
  title: string
  description?: string
  assigneeId: number
  priority: string
  status: string
  dueDate: string
}

export interface Schedule {
  id: number
  title: string
  description: string
  startTime: string
  endTime: string
  location: string
  status: number
  createTime: string
}

export interface ScheduleForm {
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  status: number
}

export interface Message {
  id: number
  title: string
  content: string
  senderId: number
  senderName: string
  receiverId: number
  receiverName: string
  type: string
  status: string
  sendTime: string
  readTime?: string
}

export interface MessageForm {
  title: string
  content: string
  receiverId: number
  type: string
}

export interface ExpenseApproval {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  amount: number
  type: string
  description: string
  applyDate: string
  status: string
  approveComment?: string
  approveTime?: string
  createTime: string
}

export interface ExpenseApprovalForm {
  amount: number
  type: string
  description: string
  applyDate: string
}

export interface LeaveApproval {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  leaveType: string
  leaveTypeName: string
  startTime: string
  endTime: string
  duration: number
  reason: string
  status: string
  approveComment?: string
  approveTime?: string
  createTime: string
}

export interface LeaveApprovalForm {
  leaveType: string
  startTime: string
  endTime: string
  reason: string
}

export interface LoanApproval {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  amount: number
  reason: string
  returnDate: string
  status: string
  approveComment?: string
  approveTime?: string
  createTime: string
}

export interface LoanApprovalForm {
  amount: number
  reason: string
  returnDate: string
}

export interface TravelApproval {
  id: number
  employeeId: number
  empNo: string
  employeeName: string
  deptName: string
  destination: string
  startTime: string
  endTime: string
  duration: number
  reason: string
  status: string
  approveComment?: string
  approveTime?: string
  createTime: string
}

export interface TravelApprovalForm {
  destination: string
  startTime: string
  endTime: string
  reason: string
}

export interface ApprovalRequest {
  approve: boolean
  comment?: string
}