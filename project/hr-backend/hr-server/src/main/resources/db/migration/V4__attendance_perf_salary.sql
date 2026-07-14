-- HR 管理系统 - 考勤/绩效/薪酬模块建表
-- ==========================================

-- ============ 考勤管理 ============

-- 打卡记录表
CREATE TABLE IF NOT EXISTS att_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    record_date DATE NOT NULL COMMENT '考勤日期',
    check_in DATETIME COMMENT '签到时间',
    check_out DATETIME COMMENT '签退时间',
    status VARCHAR(20) COMMENT '状态: normal/late/early/absent/overtime',
    source VARCHAR(20) COMMENT '数据来源: import/card/manual',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_emp_date (employee_id, record_date)
) COMMENT '打卡记录表';

-- 取卡规则表
CREATE TABLE IF NOT EXISTS att_card_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '规则名称',
    min_card_count INT DEFAULT 2 COMMENT '最少打卡次数',
    allow_overtime TINYINT DEFAULT 0 COMMENT '是否允许加班 1是 0否',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '取卡规则表';

-- 班次设置表
CREATE TABLE IF NOT EXISTS att_shift (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '班次名称',
    start_time TIME NOT NULL COMMENT '上班时间',
    end_time TIME NOT NULL COMMENT '下班时间',
    late_buffer INT DEFAULT 0 COMMENT '迟到缓冲(分钟)',
    early_buffer INT DEFAULT 0 COMMENT '早退缓冲(分钟)',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '班次设置表';

-- 法定假期表
CREATE TABLE IF NOT EXISTS att_holiday (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    year INT NOT NULL COMMENT '年份',
    name VARCHAR(100) NOT NULL COMMENT '假期名称',
    date DATE NOT NULL COMMENT '假期日期',
    days INT DEFAULT 1 COMMENT '天数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '法定假期表';

-- 假期类型设置表
CREATE TABLE IF NOT EXISTS att_leave_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '假期类型名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '假期类型编码: annual/sick/personal/marriage',
    default_days DECIMAL(4,1) DEFAULT 0 COMMENT '默认天数',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '假期类型设置表';

-- 假期额度表
CREATE TABLE IF NOT EXISTS att_leave_quota (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '假期类型',
    year INT NOT NULL COMMENT '年份',
    total_days DECIMAL(4,1) DEFAULT 0 COMMENT '总天数',
    used_days DECIMAL(4,1) DEFAULT 0 COMMENT '已用天数',
    remain_days DECIMAL(4,1) DEFAULT 0 COMMENT '剩余天数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_emp_type_year (employee_id, leave_type, year)
) COMMENT '假期额度表';

-- OA流程管理表
CREATE TABLE IF NOT EXISTS att_oa_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    type VARCHAR(20) NOT NULL COMMENT '流程类型: leave请假/travel出差/overtime加班',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    duration DECIMAL(4,1) COMMENT '时长(天)',
    status VARCHAR(20) COMMENT '状态: approved已通过/pending待审批/rejected已驳回',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT 'OA流程表';

-- 考勤扣款规则表
CREATE TABLE IF NOT EXISTS att_deduction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL COMMENT '扣款类型: miss_card/late/early/absent',
    deduction DECIMAL(10,2) NOT NULL COMMENT '扣款金额',
    unit VARCHAR(20) COMMENT '单位: 次/天/小时',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '考勤扣款规则表';

-- ============ 绩效管理 ============

-- 绩效等级表
CREATE TABLE IF NOT EXISTS perf_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL COMMENT '等级名称: S/A/B/C/D',
    score_min DECIMAL(5,1) NOT NULL COMMENT '最低分',
    score_max DECIMAL(5,1) NOT NULL COMMENT '最高分',
    coefficient DECIMAL(3,2) NOT NULL COMMENT '绩效系数',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '绩效等级表';

-- 绩效工资表
CREATE TABLE IF NOT EXISTS perf_salary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level_id BIGINT NOT NULL COMMENT '绩效等级ID',
    salary_range VARCHAR(50) COMMENT '工资范围',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '绩效工资表';

-- 绩效考核计划表
CREATE TABLE IF NOT EXISTS perf_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '计划名称',
    dept_id BIGINT COMMENT '部门ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '状态 1进行中 2已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '绩效考核计划表';

-- 考核计划-员工关联表
CREATE TABLE IF NOT EXISTS perf_plan_employee (
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    PRIMARY KEY (plan_id, employee_id)
) COMMENT '考核计划员工关联';

-- 绩效考核记录表
CREATE TABLE IF NOT EXISTS perf_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    plan_id BIGINT NOT NULL COMMENT '考核计划ID',
    evaluator_id BIGINT COMMENT '评估人ID',
    total_score DECIMAL(5,2) COMMENT '总分',
    level_id BIGINT COMMENT '绩效等级ID',
    evaluation TEXT COMMENT '评价',
    evaluate_time DATETIME COMMENT '评估时间',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '绩效考核记录表';

-- 考核指标明细表
CREATE TABLE IF NOT EXISTS perf_record_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_id BIGINT NOT NULL COMMENT '考核记录ID',
    indicator VARCHAR(200) NOT NULL COMMENT '指标名称',
    weight INT DEFAULT 0 COMMENT '权重',
    score DECIMAL(5,2) COMMENT '得分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '考核指标明细表';

-- ============ 薪酬管理 ============

-- 核算规则表
CREATE TABLE IF NOT EXISTS sal_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL COMMENT '规则类型: base_salary/social_insurance/tax',
    value DECIMAL(10,2) COMMENT '规则值/基数',
    unit VARCHAR(20) COMMENT '单位: 元/%',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '核算规则表';

-- 薪酬字段表
CREATE TABLE IF NOT EXISTS sal_field (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '字段名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '字段编码',
    type VARCHAR(20) NOT NULL COMMENT '类型: income应发/deduction应扣',
    formula VARCHAR(500) COMMENT '计算公式',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '薪酬字段表';

-- 员工调薪表
CREATE TABLE IF NOT EXISTS sal_adjust (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    before_salary DECIMAL(10,2) COMMENT '调前工资',
    after_salary DECIMAL(10,2) NOT NULL COMMENT '调后工资',
    adjust_amount DECIMAL(10,2) COMMENT '调薪金额',
    adjust_type VARCHAR(20) NOT NULL COMMENT '调薪类型: promote/transfer/annual/special',
    effective_date DATE COMMENT '生效日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '员工调薪表';

-- 工资表
CREATE TABLE IF NOT EXISTS sal_sheet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    month VARCHAR(7) NOT NULL COMMENT '月份: yyyy-MM',
    base_salary DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    perf_salary DECIMAL(10,2) DEFAULT 0 COMMENT '绩效工资',
    subsidy DECIMAL(10,2) DEFAULT 0 COMMENT '补贴',
    overtime_pay DECIMAL(10,2) DEFAULT 0 COMMENT '加班费',
    total_income DECIMAL(10,2) DEFAULT 0 COMMENT '应发合计',
    social_insurance DECIMAL(10,2) DEFAULT 0 COMMENT '社保扣除',
    tax DECIMAL(10,2) DEFAULT 0 COMMENT '个税',
    total_deduction DECIMAL(10,2) DEFAULT 0 COMMENT '应扣合计',
    net_salary DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
    status TINYINT DEFAULT 1 COMMENT '状态 1已生成 0草稿',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_emp_month (employee_id, month)
) COMMENT '工资表';

-- ============ 菜单权限 ============

-- 考勤管理菜单
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(67, 0, '考勤管理', 1, '/attendance', '', '', 'Clock', 4, 1),
(68, 67, '打卡记录', 2, '/attendance/record', 'attendance/record/RecordManage', 'attendance:record:list', 'Timer', 1, 1),
(69, 68, '导入打卡', 3, '', '', 'attendance:record:import', '', 1, 1),
(70, 68, '批量修正', 3, '', '', 'attendance:record:fix', '', 2, 1),
(71, 67, '异常统计', 2, '/attendance/exception', 'attendance/exception/ExceptionManage', 'attendance:exception:list', 'WarningFilled', 2, 1),
(72, 67, 'OA流程管理', 2, '/attendance/oa-flow', 'attendance/oa/OAFlowManage', 'attendance:oa-flow:list', 'Document', 3, 1),
(73, 72, '导入单据', 3, '', '', 'attendance:oa-flow:import', '', 1, 1),
(74, 72, '查看详情', 3, '', '', 'attendance:oa-flow:view', '', 2, 1),
(75, 67, '取卡规则', 2, '/attendance/card-rule', 'attendance/rule/CardRuleManage', 'attendance:card-rule:list', 'Setting', 4, 1),
(76, 75, '创建规则', 3, '', '', 'attendance:card-rule:create', '', 1, 1),
(77, 75, '编辑规则', 3, '', '', 'attendance:card-rule:edit', '', 2, 1),
(78, 75, '删除规则', 3, '', '', 'attendance:card-rule:delete', '', 3, 1),
(79, 67, '班次设置', 2, '/attendance/shift', 'attendance/shift/ShiftManage', 'attendance:shift:list', 'Watch', 5, 1),
(80, 79, '创建班次', 3, '', '', 'attendance:shift:create', '', 1, 1),
(81, 79, '编辑班次', 3, '', '', 'attendance:shift:edit', '', 2, 1),
(82, 79, '删除班次', 3, '', '', 'attendance:shift:delete', '', 3, 1),
(83, 67, '法定假期', 2, '/attendance/holiday', 'attendance/holiday/HolidayManage', 'attendance:holiday:list', 'Sunny', 6, 1),
(84, 83, '创建计划', 3, '', '', 'attendance:holiday:create', '', 1, 1),
(85, 83, '修改计划', 3, '', '', 'attendance:holiday:edit', '', 2, 1),
(86, 83, '复制计划', 3, '', '', 'attendance:holiday:copy', '', 3, 1),
(87, 83, '删除计划', 3, '', '', 'attendance:holiday:delete', '', 4, 1),
(88, 67, '假期额度', 2, '/attendance/leave-quota', 'attendance/leave/LeaveQuotaManage', 'attendance:leave-quota:list', 'Calendar', 7, 1),
(89, 88, '调整额度', 3, '', '', 'attendance:leave-quota:adjust', '', 1, 1),
(90, 67, '假期设置', 2, '/attendance/leave-type', 'attendance/leave/LeaveTypeManage', 'attendance:leave-type:list', 'Sunrise', 8, 1),
(91, 90, '创建假期', 3, '', '', 'attendance:leave-type:create', '', 1, 1),
(92, 90, '编辑假期', 3, '', '', 'attendance:leave-type:edit', '', 2, 1),
(93, 90, '删除假期', 3, '', '', 'attendance:leave-type:delete', '', 3, 1),
(94, 67, '考勤报表', 2, '/attendance/report', 'attendance/report/ReportManage', 'attendance:report:list', 'TrendCharts', 9, 1),
(95, 94, '考勤明细表', 3, '', '', 'attendance:report:detail', '', 1, 1),
(96, 94, '考勤汇总表', 3, '', '', 'attendance:report:summary', '', 2, 1),
(97, 67, '考勤扣款', 2, '/attendance/deduction', 'attendance/deduction/DeductionManage', 'attendance:deduction:list', 'Money', 10, 1),
(98, 97, '编辑扣款', 3, '', '', 'attendance:deduction:edit', '', 1, 1);

-- 绩效管理菜单
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(99, 0, '绩效管理', 1, '/performance', '', '', 'Rank', 5, 1),
(100, 99, '绩效等级', 2, '/performance/level', 'performance/level/LevelManage', 'perf:level:list', 'Medal', 1, 1),
(101, 100, '创建等级', 3, '', '', 'perf:level:create', '', 1, 1),
(102, 100, '编辑等级', 3, '', '', 'perf:level:edit', '', 2, 1),
(103, 100, '删除等级', 3, '', '', 'perf:level:delete', '', 3, 1),
(104, 99, '绩效工资', 2, '/performance/salary', 'performance/salary/SalaryManage', 'perf:salary:list', 'Money', 2, 1),
(105, 104, '创建设置', 3, '', '', 'perf:salary:create', '', 1, 1),
(106, 104, '编辑设置', 3, '', '', 'perf:salary:edit', '', 2, 1),
(107, 104, '删除设置', 3, '', '', 'perf:salary:delete', '', 3, 1),
(108, 99, '考核计划', 2, '/performance/plan', 'performance/plan/PlanManage', 'perf:plan:list', 'Tickets', 3, 1),
(109, 108, '创建计划', 3, '', '', 'perf:plan:create', '', 1, 1),
(110, 108, '编辑计划', 3, '', '', 'perf:plan:edit', '', 2, 1),
(111, 108, '删除计划', 3, '', '', 'perf:plan:delete', '', 3, 1),
(112, 99, '考核记录', 2, '/performance/record', 'performance/record/RecordManage', 'perf:record:list', 'Edit', 4, 1),
(113, 112, '创建记录', 3, '', '', 'perf:record:create', '', 1, 1),
(114, 112, '编辑记录', 3, '', '', 'perf:record:edit', '', 2, 1),
(115, 99, '绩效报表', 2, '/performance/report', 'performance/report/ReportManage', 'perf:report:list', 'TrendCharts', 5, 1),
(116, 115, '绩效明细表', 3, '', '', 'perf:report:detail', '', 1, 1),
(117, 115, '部门绩效汇总', 3, '', '', 'perf:report:dept-summary', '', 2, 1),
(118, 115, '职员绩效汇总', 3, '', '', 'perf:report:employee-summary', '', 3, 1);

-- 薪酬管理菜单
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(119, 0, '薪酬管理', 1, '/salary', '', '', 'Wallet', 6, 1),
(120, 119, '核算规则', 2, '/salary/rule', 'salary/rule/RuleManage', 'salary:rule:list', 'Setting', 1, 1),
(121, 120, '编辑规则', 3, '', '', 'salary:rule:edit', '', 1, 1),
(122, 119, '薪酬字段', 2, '/salary/field', 'salary/field/FieldManage', 'salary:field:list', 'Grid', 2, 1),
(123, 122, '创建字段', 3, '', '', 'salary:field:create', '', 1, 1),
(124, 122, '编辑字段', 3, '', '', 'salary:field:edit', '', 2, 1),
(125, 122, '删除字段', 3, '', '', 'salary:field:delete', '', 3, 1),
(126, 119, '员工调薪', 2, '/salary/adjust', 'salary/adjust/AdjustManage', 'salary:adjust:list', 'Switch', 3, 1),
(127, 126, '调薪记录', 3, '', '', 'salary:adjust:list', '', 1, 1),
(128, 126, '员工调薪', 3, '', '', 'salary:adjust:create', '', 2, 1),
(129, 119, '工资表', 2, '/salary/sheet', 'salary/sheet/SheetManage', 'salary:sheet:list', 'Postcard', 4, 1),
(130, 129, '导入数据', 3, '', '', 'salary:sheet:import', '', 1, 1),
(131, 129, '导出数据', 3, '', '', 'salary:sheet:export', '', 2, 1),
(132, 129, '同步数据', 3, '', '', 'salary:sheet:sync', '', 3, 1),
(133, 129, '生成工资表', 3, '', '', 'salary:sheet:generate', '', 4, 1);

-- 为超级管理员分配所有新菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 67 AND 133;
