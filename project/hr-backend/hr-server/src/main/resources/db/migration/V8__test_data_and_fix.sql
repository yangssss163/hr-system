-- ================================================================
-- V8: 测试数据 + 绩效考核记录删除权限
-- ================================================================

-- =============================================
-- 第一部分: 添加考核记录删除权限
-- 新增 perf:record:delete 权限，挂载到考核记录菜单下
-- 自动授权给超级管理员角色
-- =============================================

-- 1. 添加考核记录-删除按钮权限（父菜单 ID=112 考核记录）
INSERT IGNORE INTO sys_menu (parent_id, name, type, path, component, permission, icon, sort, visible)
VALUES (112, '删除记录', 3, '', '', 'perf:record:delete', '', 3, 1);

-- 2. 授予超级管理员角色该权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE permission = 'perf:record:delete';

-- ================================================================
-- 第二部分: 全模块测试数据
-- ================================================================

-- ================================================================
-- 一、系统管理 - 基础数据
-- ================================================================

-- 公司
INSERT IGNORE INTO sys_company (id, name, code, parent_id, status) VALUES
(1, 'XX科技有限公司', 'XX-KJ', 0, 1);

-- 部门
INSERT IGNORE INTO sys_dept (id, company_id, name, sort, status) VALUES
(1, 1, '技术部', 1, 1),
(2, 1, '产品部', 2, 1),
(3, 1, '人事部', 3, 1),
(4, 1, '市场部', 4, 1),
(5, 1, '财务部', 5, 1);

-- 职位
INSERT IGNORE INTO sys_position (id, dept_id, name, sort, status) VALUES
(1, 1, 'Java工程师', 1, 1),
(2, 1, '前端工程师', 2, 1),
(3, 2, '产品经理',   1, 1),
(4, 3, 'HR专员',     1, 1),
(5, 4, '市场专员',   1, 1),
(6, 5, '会计',       1, 1);

-- 用户（密码统一 admin123）
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, dept_id, status) VALUES
(2, 'zhangsan', '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '张三', '13800000011', 'zhangsan@test.com', 1, 1),
(3, 'lisi',     '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '李四', '13800000012', 'lisi@test.com',     1, 1),
(4, 'wangwu',   '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '王五', '13800000013', 'wangwu@test.com',   2, 1),
(5, 'zhaoliu',  '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '赵六', '13800000014', 'zhaoliu@test.com',  3, 1),
(6, 'sunqi',    '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '孙七', '13800000015', 'sunqi@test.com',    4, 1),
(7, 'zhouba',   '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '周八', '13800000016', 'zhouba@test.com',   5, 1);

-- 为除 admin 外的用户分配普通角色
INSERT IGNORE INTO sys_role (id, name, code, description, sort, status) VALUES
(2, '普通员工', 'user', '基础权限', 1, 1),
(3, '部门主管', 'manager', '部门管理权限', 2, 1);

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
(2, 2), (3, 2), (4, 2), (5, 3), (6, 2), (7, 2);

-- ================================================================
-- 二、员工管理
-- ================================================================

INSERT INTO hr_employee (id, user_id, emp_no, name, gender, phone, email, dept_id, position_id, company_id, entry_date, status) VALUES
(1, 2, 'EMP001', '张三', 1, '13800000011', 'zhangsan@test.com', 1, 1, 1, '2020-03-15', 1),
(2, 3, 'EMP002', '李四', 1, '13800000012', 'lisi@test.com',     1, 2, 1, '2021-07-01', 1),
(3, 4, 'EMP003', '王五', 2, '13800000013', 'wangwu@test.com',   2, 3, 1, '2019-01-10', 1),
(4, 5, 'EMP004', '赵六', 2, '13800000014', 'zhaoliu@test.com',  3, 4, 1, '2022-06-20', 1),
(5, 6, 'EMP005', '孙七', 1, '13800000015', 'sunqi@test.com',    4, 5, 1, '2023-01-05', 3),
(6, 7, 'EMP006', '周八', 2, '13800000016', 'zhouba@test.com',   5, 6, 1, '2020-09-01', 1);

-- 异动记录
INSERT INTO hr_transfer (id, employee_id, transfer_type, before_dept_id, after_dept_id, before_position_id, after_position_id, effective_date, reason) VALUES
(1, 2, 'promote',  NULL,     1, NULL,     1, '2021-07-01', '晋升为高级工程师'),
(2, 5, 'regular',  NULL,     NULL, NULL,    NULL, '2023-04-01', '试用期转正'),
(3, 3, 'transfer', 1,        1, 2,         1, '2023-01-01', '内部调岗');

-- ================================================================
-- 三、考勤管理
-- ================================================================

-- 班次设置
INSERT INTO att_shift (id, name, start_time, end_time, late_buffer, early_buffer, status) VALUES
(1, '标准班',   '09:00', '18:00', 15, 15, 1),
(2, '弹性班',   '10:00', '19:00', 30, 15, 1);

-- 取卡规则
INSERT INTO att_card_rule (id, name, min_card_count, allow_overtime, status) VALUES
(1, '标准打卡规则', 2, 1, 1);

-- 法定假期（2026年）
INSERT INTO att_holiday (id, year, name, date, days) VALUES
(1, 2026, '元旦',   '2026-01-01', 1),
(2, 2026, '春节',   '2026-02-17', 1),
(3, 2026, '春节',   '2026-02-18', 1),
(4, 2026, '清明节', '2026-04-05', 1),
(5, 2026, '劳动节', '2026-05-01', 1),
(6, 2026, '端午节', '2026-06-19', 1);

-- 假期类型
INSERT INTO att_leave_type (id, name, code, default_days, enabled) VALUES
(1, '年假',     'annual',    5,  1),
(2, '病假',     'sick',      12, 1),
(3, '事假',     'personal',  5,  1),
(4, '婚假',     'marriage',  3,  1),
(5, '产假',     'maternity', 14, 1);

-- 假期额度（2026年）
INSERT INTO att_leave_quota (employee_id, leave_type, year, total_days, used_days, remain_days) VALUES
(1, 'annual',  2026, 5, 1.0, 4.0),
(1, 'sick',    2026, 12, 0, 12),
(2, 'annual',  2026, 5, 2.0, 3.0),
(2, 'personal', 2026, 5, 0.5, 4.5),
(3, 'annual',  2026, 5, 3.0, 2.0);

-- 打卡记录（最近一周）
INSERT INTO att_record (employee_id, record_date, check_in, check_out, status) VALUES
(1, '2026-07-08', '2026-07-08 08:55:00', '2026-07-08 18:05:00', 'normal'),
(2, '2026-07-08', '2026-07-08 09:10:00', '2026-07-08 18:00:00', 'normal'),
(3, '2026-07-08', '2026-07-08 09:20:00', '2026-07-08 17:45:00', 'late'),
(4, '2026-07-08', '2026-07-08 08:50:00', '2026-07-08 18:10:00', 'normal'),
(1, '2026-07-09', '2026-07-09 08:58:00', '2026-07-09 20:30:00', 'overtime'),
(2, '2026-07-09', '2026-07-09 09:05:00', '2026-07-09 18:00:00', 'normal'),
(3, '2026-07-09', '2026-07-09 09:00:00', '2026-07-09 16:00:00', 'early'),
(1, '2026-07-10', '2026-07-10 09:02:00', '2026-07-10 18:00:00', 'normal'),
(2, '2026-07-10', '2026-07-10 00:00:00', '2026-07-10 00:00:00', 'absent'),
(3, '2026-07-10', '2026-07-10 08:55:00', '2026-07-10 18:05:00', 'normal'),
(1, '2026-07-11', '2026-07-11 08:50:00', '2026-07-11 18:00:00', 'normal'),
(2, '2026-07-11', '2026-07-11 09:15:00', '2026-07-11 18:00:00', 'normal'),
(3, '2026-07-11', '2026-07-11 09:00:00', '2026-07-11 18:00:00', 'normal');

-- OA流程
INSERT INTO att_oa_flow (id, employee_id, type, start_date, end_date, duration, status) VALUES
(1, 1, 'leave',  '2026-07-05', '2026-07-05', 1.0, 'approved'),
(2, 2, 'travel', '2026-07-12', '2026-07-14', 3.0, 'pending'),
(3, 4, 'overtime', '2026-07-10', '2026-07-10', 0.5, 'approved');

-- 扣款规则
INSERT INTO att_deduction (id, type, deduction, unit, remark) VALUES
(1, 'miss_card', 20.00, '次', '漏打卡每次扣20元'),
(2, 'late',      10.00, '次', '迟到每次扣10元（超缓冲）'),
(3, 'absent',    200.00, '天', '旷工每天扣200元');

-- ================================================================
-- 四、绩效管理
-- ================================================================

-- 绩效等级
INSERT INTO perf_level (id, name, score_min, score_max, coefficient, sort) VALUES
(1, 'S', 90.0, 100.0, 1.50, 1),
(2, 'A', 80.0,  89.9,  1.20, 2),
(3, 'B', 70.0,  79.9,  1.00, 3),
(4, 'C', 60.0,  69.9,  0.80, 4),
(5, 'D',  0.0,  59.9,  0.50, 5);

-- 绩效工资
INSERT INTO perf_salary (id, level_id, salary_range, sort) VALUES
(1, 1, '20K-35K', 1),
(2, 2, '15K-25K', 2),
(3, 3, '10K-18K', 3),
(4, 4, '8K-12K',  4),
(5, 5, '5K-8K',   5);

-- 考核计划
INSERT INTO perf_plan (id, name, dept_id, start_date, end_date, status) VALUES
(1, '2026年Q3技术部绩效考核', 1, '2026-07-01', '2026-09-30', 1),
(2, '2026年Q2全员绩效考核',   NULL, '2026-04-01', '2026-06-30', 2);

-- 考核记录
INSERT INTO perf_record (id, employee_id, plan_id, evaluator_id, total_score, level_id, evaluation, evaluate_time) VALUES
(1, 1, 2, 5, 92.50, 1, '技术贡献突出，超额完成任务',    '2026-07-01 10:00:00'),
(2, 2, 2, 5, 85.00, 2, '前端架构优化明显，进步快',      '2026-07-01 10:30:00'),
(3, 3, 2, 5, 72.00, 3, '基本达标，需加强跨部门协作',    '2026-07-01 11:00:00'),
(4, 4, 2, 5, 65.00, 4, '招聘指标完成率偏低，需改进',    '2026-07-01 11:30:00');

-- 考核指标明细
INSERT INTO perf_record_item (record_id, indicator, weight, score) VALUES
(1, '代码质量',    30, 28),
(1, '项目交付',    30, 28),
(1, '技术分享',    20, 18),
(1, '团队协作',    20, 18.5),
(2, '功能开发',    35, 30),
(2, 'Bug修复率',   25, 20),
(2, '代码Review',  20, 17),
(2, '学习成长',    20, 18),
(3, '产品交付',    40, 28),
(3, '用户满意度',  30, 22),
(3, '创新提案',    30, 22);

-- ================================================================
-- 五、薪酬管理
-- ================================================================

-- 核算规则
INSERT INTO sal_rule (id, type, value, unit, remark) VALUES
(1, 'base_salary',      8000,  '元',  '基本工资基数'),
(2, 'social_insurance', 0.105, '%',   '社保个人缴纳比例'),
(3, 'tax_threshold',    5000,  '元',  '个税起征点');

-- 薪酬字段
INSERT INTO sal_field (id, name, code, type, sort, status) VALUES
(1, '基本工资', 'base_salary',  'income',    1, 1),
(2, '绩效工资', 'perf_salary',  'income',    2, 1),
(3, '岗位补贴', 'subsidy',      'income',    3, 1),
(4, '加班费',   'overtime_pay', 'income',    4, 1),
(5, '社保扣除', 'social_insurance', 'deduction', 1, 1),
(6, '个税',     'tax',          'deduction', 2, 1);

-- 员工调薪
INSERT INTO sal_adjust (id, employee_id, before_salary, after_salary, adjust_amount, adjust_type, effective_date, remark) VALUES
(1, 1, 15000.00, 18000.00, 3000.00, 'promote',  '2025-07-01', '晋升调薪'),
(2, 2, 12000.00, 14000.00, 2000.00, 'annual',   '2026-01-01', '年度调薪'),
(3, 3, 10000.00, 11000.00, 1000.00, 'transfer', '2023-01-01', '调岗调薪');

-- 工资表（2026年6月 & 7月）
INSERT INTO sal_sheet (employee_id, month, base_salary, perf_salary, subsidy, overtime_pay, total_income, social_insurance, tax, total_deduction, net_salary, status) VALUES
(1, '2026-06', 18000, 3000, 500, 200, 21700, 1890, 1230, 3120, 18580, 1),
(2, '2026-06', 14000, 2000, 300, 0,   16300, 1470, 780,  2250, 14050, 1),
(3, '2026-06', 11000, 1000, 300, 0,   12300, 1155, 450,  1605, 10695, 1),
(4, '2026-06',  9000,  800,  200, 100, 10100,  945, 210,  1155,  8945, 1),
(1, '2026-07', 18000, 3000, 500, 500, 22000, 1890, 1280, 3170, 18830, 1),
(2, '2026-07', 14000, 2000, 300, 200, 16500, 1470, 810,  2280, 14220, 1),
(3, '2026-07', 11000, 1000, 300, 0,   12300, 1155, 450,  1605, 10695, 1);

-- ================================================================
-- 六、CRM
-- ================================================================

-- 客户
INSERT INTO crm_customer (id, name, phone, email, industry, source, level, status, owner_id, province, city, address, contact_name, contact_phone) VALUES
(1, '深圳创新科技有限公司', '0755-88880001', 'cx@cx-tech.cn', '互联网',    '展会',       'A', 'active', 6, '广东省',  '深圳市', '南山区科技园',    '刘总', '13900000001'),
(2, '广州大数据集团',      '020-88880002',  'ds@bigdata.cn', '大数据',    '电话销售',   'B', 'active', 6, '广东省',  '广州市', '天河区天河路',    '陈经理', '13900000002'),
(3, '杭州电商平台有限公司', '0571-88880003', 'ds@hz-ec.com',  '电子商务',  '网络推广',   'A', 'active', 7, '浙江省',  '杭州市', '余杭区未来科技城', '王总监', '13900000003'),
(4, '北京智能制造公司',    '010-88880004',  'zn@bj-zn.com',  '制造业',    '渠道推荐',   'C', 'inactive', 7, '北京市', '北京市', '海淀区中关村',    '赵经理', '13900000004');

-- 商机
INSERT INTO crm_opportunity (id, name, customer_id, amount, stage, probability, expected_close_date, owner_id) VALUES
(1, '企业HR系统采购项目',     1, 500000.00, '谈判',  70, '2026-08-30', 6),
(2, '大数据分析平台建设',     2, 300000.00, '初期接触', 20, '2026-10-15', 6),
(3, '移动办公APP开发',       3, 200000.00, '方案提交', 50, '2026-09-20', 7);

-- 订单
INSERT INTO crm_order (id, opportunity_id, customer_id, order_no, amount, status, sign_date, owner_id) VALUES
(1, 1, 1, 'ORD-2026-001', 500000.00, '已签约', '2026-06-15', 6),
(2, 3, 3, 'ORD-2026-002', 200000.00, '待审核', '2026-07-01', 7);

-- 回款
INSERT INTO crm_payment (id, order_id, customer_id, payment_no, amount, payment_date, payment_method, status, owner_id) VALUES
(1, 1, 1, 'PYT-001', 300000.00, '2026-06-20', '银行转账', '已到账', 6),
(2, 1, 1, 'PYT-002', 200000.00, '2026-07-10', '银行转账', '待确认', 6);

-- 退款
INSERT INTO crm_refund (id, order_id, customer_id, refund_no, amount, refund_date, reason, status, owner_id) VALUES
(1, 2, 3, 'RFD-001', 10000.00, '2026-07-05', '功能需求变更，部分退款', '已退款', 7);

-- 费用
INSERT INTO crm_expense (id, name, amount, expense_date, category, status, applicant_id, remark) VALUES
(1, '客户拜访差旅费',   3500.00,  '2026-07-03', '差旅',   'approved', 6, '拜访深圳创新科技'),
(2, '市场推广-百度广告', 15000.00, '2026-07-05', '推广',   'pending',  7, 'Q3线上推广投放'),
(3, '办公设备采购',      8000.00,  '2026-07-08', '采购',   'approved', 6, '新员工办公电脑');

-- ================================================================
-- 七、办公协同
-- ================================================================

-- 公告
INSERT INTO sys_notice (id, title, content, type, status, publisher_id, publish_time) VALUES
(1, '关于2026年Q3绩效考核安排的通知',
    '各位同事：\n2026年Q3绩效考核将于7月1日正式启动，请各部门主管在7月5日前提交考核计划。\n考核周期：2026年7月1日 - 2026年9月30日',
    1, 1, 1, '2026-07-01 09:00:00'),
(2, '公司团建活动通知',
    '各部门：\n为加强团队凝聚力，公司定于7月20日（周六）组织户外团建活动，地点：深圳梧桐山。\n请大家提前安排工作，准时参加。',
    1, 1, 5, '2026-07-05 10:00:00');

-- 文档
INSERT INTO sys_document (id, title, content, category, creator_id, is_public) VALUES
(1, '新员工入职指南',
    '# 新员工入职指南\n\n## 一、入职流程\n1. 提交入职材料\n2. 签订劳动合同\n3. 领取办公设备\n\n## 二、公司制度\n详见公司钉钉文档',
    '制度文档', 5, 1),
(2, 'Java开发规范V3.0',
    '# Java开发规范\n\n## 命名规范\n- 类名：大驼峰\n- 方法名：小驼峰\n- 常量：全大写下划线分隔\n\n## 最佳实践\n参考阿里巴巴Java开发手册',
    '技术文档', 1, 1),
(3, '2026年H1述职报告模板',
    '## 述职报告\n\n姓名：\n部门：\n岗位：\n\n### 上半年工作总结\n...\n\n### 关键成果\n...\n\n### 下半年计划\n...',
    '模板', 5, 1);

-- 任务
INSERT INTO sys_task (id, creator_id, assignee_id, title, content, priority, status, start_date, due_date) VALUES
(1, 1, 2, '完成员工导入功能开发',
    '实现Excel批量导入员工信息的功能，支持模板下载、数据校验、错误提示', 'high', 'done', '2026-07-01', '2026-07-05'),
(2, 5, 1, '面试管理打通Offer流程',
    '面试通过后支持发放Offer、确认入职全流程', 'high', 'done', '2026-06-25', '2026-07-10'),
(3, 1, 3, '前端页面性能优化',
    '优化首屏加载速度，目标LCP < 2.5s', 'medium', 'in_progress', '2026-07-10', '2026-07-20'),
(4, 5, 5, '汇总Q2招聘数据',
    '整理Q2各部门招聘数据并生成报表', 'low', 'pending', '2026-07-15', '2026-07-20');

-- 日程
INSERT INTO sys_schedule (id, user_id, title, content, start_time, end_time, all_day, color) VALUES
(1, 1, '技术部周会',       '评审本周开发进度',  '2026-07-15 10:00:00', '2026-07-15 11:00:00', 0, '#409EFF'),
(2, 2, '面试-陈小明',      'Java工程师初试',    '2026-07-16 10:00:00', '2026-07-16 11:00:00', 0, '#67C23A'),
(3, 5, '全员月度会',       '7月月度总结',       '2026-07-16 14:00:00', '2026-07-16 16:00:00', 0, '#E6A23C'),
(4, 1, '团建日',           '公司户外团建',      '2026-07-20 08:00:00', '2026-07-20 18:00:00', 1, '#F56C6C');

-- 消息
INSERT INTO sys_message (id, sender_id, receiver_id, title, content, is_read, send_time) VALUES
(1, 1, 2, '新任务分配',
    '张三，请尽快完成员工导入功能的开发，截止日期7月5日。', 1, '2026-07-01 09:00:00'),
(2, 2, 1, '任务完成通知',
    '管理员，员工导入功能已开发完成，请检查。', 1, '2026-07-05 17:00:00'),
(3, 1, 2, '代码Review通知',
    '你的MR #45 有3条Review意见，请查看并修改。', 0, '2026-07-14 15:00:00'),
(4, 5, 1, '团建报名提醒',
    '管理员，团建报名截止今天下午6点，技术部还有2人未填。', 0, '2026-07-14 09:00:00');

-- ================================================================
-- 八、工作流审批
-- ================================================================

-- 报销审批
INSERT INTO flow_expense (id, applicant_id, amount, expense_date, category, description, status, approver_id, approve_time, approve_comment) VALUES
(1, 2, 3500.00,  '2026-07-03', '差旅',   '拜访客户出差-往返机票+住宿',        'approved', 5, '2026-07-04 10:00:00', '同意报销'),
(2, 3,  150.00,  '2026-07-08', '办公用品', '购买键盘鼠标',                     'approved', 5, '2026-07-09 09:00:00', '同意'),
(3, 4,  800.00,  '2026-07-10', '培训',    '参加HR数字化管理培训报名费',        'pending',  5, NULL, NULL);

-- 借款审批
INSERT INTO flow_loan (id, applicant_id, amount, loan_date, reason, repayment_date, status, approver_id) VALUES
(1, 2, 5000.00, '2026-07-05', '出差备用金', '2026-07-20', 'approved', 5),
(2, 4, 2000.00, '2026-07-12', '团建活动预支', '2026-07-25', 'pending', 5);

-- 出差审批
INSERT INTO flow_travel (id, applicant_id, destination, start_date, end_date, days, reason, budget, status, approver_id, approve_time, approve_comment) VALUES
(1, 2, '深圳-北京', '2026-07-12', '2026-07-14', 3, '拜访北京智能制造公司，洽谈合作', 5000.00, 'approved', 5, '2026-07-10 10:00:00', '批准'),
(2, 6, '深圳-杭州', '2026-07-20', '2026-07-22', 3, '参加杭州电子商务峰会',           3000.00, 'pending',  5, NULL, NULL);

-- 请假审批
INSERT INTO flow_leave (id, applicant_id, leave_type, start_date, end_date, days, reason, status, approver_id, approve_time, approve_comment) VALUES
(1, 2, '年假',    '2026-07-05', '2026-07-05', 1, '个人事务处理',  'approved', 5, '2026-07-04 14:00:00', '同意'),
(2, 4, '病假',    '2026-07-08', '2026-07-08', 1, '身体不适就医',  'approved', 5, '2026-07-08 09:00:00', '同意'),
(3, 3, '事假',    '2026-07-18', '2026-07-18', 1, '搬家',           'pending',  5, NULL, NULL);

-- ================================================================
-- 九、招聘管理 - 补充
-- ================================================================

-- 候选人简历
INSERT INTO rec_resume (id, name, phone, email, gender, education, school, major, work_years, apply_position, source, status) VALUES
(1, '陈小明', '13800000001', 'chenxm@test.com', 1, 'bachelor', '清华大学', '计算机科学', 3, 'Java工程师', '猎头推荐', 'interview'),
(2, '林小红', '13800000002', 'linxh@test.com', 2, 'master',   '北京大学', '软件工程', 5, '前端工程师', 'BOSS直聘', 'interview'),
(3, '吴大伟', '13800000003', 'wudw@test.com',  1, 'bachelor', '浙江大学', '计算机科学', 2, 'Java工程师', '内推',     'interview'),
(4, '黄小芳', '13800000004', 'huangxf@test.com', 2, 'bachelor', '复旦大学', '产品设计', 4, '产品经理',   '拉勾网',   'offer'),
(5, '周建国', '13800000005', 'zhoujg@test.com',  1, 'master',   '上海交大', '工商管理', 6, 'HR专员',     '智联招聘', 'eliminated'),
(6, '刘小雨', '13800000006', 'liuxy@test.com', 2, 'bachelor', '武汉大学', '市场营销', 2, '市场专员',   '猎聘网',   'new');

-- 面试记录（全状态覆盖）
INSERT INTO rec_interview (id, resume_id, interview_round, interviewer_id, interview_time, location, result, score, evaluation, checkin_time, reason, offer_salary, offer_date, offer_remark) VALUES
-- pending: 待签到
(1, 1, 1, 2, '2026-07-16 10:00:00', '3F 会议室A', 'pending', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
-- pending: 已签到待评价
(2, 2, 1, 3, '2026-07-16 14:00:00', '3F 会议室B', 'pending', NULL, NULL, '2026-07-16 13:55:00', NULL, NULL, NULL, NULL),
-- pass: 已通过 → 可发Offer
(3, 3, 1, 2, '2026-07-14 09:00:00', '3F 会议室A', 'pass', 8.5, '技术扎实，沟通良好，建议录用', '2026-07-14 08:55:00', NULL, NULL, NULL, NULL),
-- offer: 已发Offer → 可确认入职
(4, 4, 2, 4, '2026-07-13 10:00:00', '4F 会议室C', 'offer', 9.0, '产品思维优秀，经验匹配', '2026-07-13 09:50:00', NULL, '20K-30K', '2026-07-22', '试用期3个月，达标可提前转正'),
-- fail: 已淘汰
(5, 5, 1, 5, '2026-07-12 15:00:00', '2F 小会议室', 'fail', 5.0, '经验不足，与岗位要求不匹配', '2026-07-12 14:50:00', '缺乏HR系统相关经验', NULL, NULL, NULL),
-- hired: 已入职
(6, 1, 0, 2, '2026-07-01 10:00:00', '3F 会议室A', 'hired', 9.2, '综合素质优秀', '2026-07-01 09:50:00', NULL, '25K-35K', '2026-07-10', '已入职');

-- 面试题库
INSERT INTO rec_question (id, category, difficulty, title, answer) VALUES
(1, 'technical', 'medium', '请简述Java中HashMap的实现原理', 'HashMap基于数组+链表+红黑树实现，JDK1.8后当链表长度超过8且数组长度>=64时转为红黑树，默认负载因子0.75，扩容为2的幂次方'),
(2, 'technical', 'hard',   '如何排查和解决线上OOM问题',      '1. 通过jmap导出dump文件 2. 使用MAT/JProfiler分析 3. 检查是否存在内存泄漏 4. 调整JVM参数如Xmx'),
(3, 'technical', 'easy',   'Vue3 Composition API 和 Options API 的区别', 'Composition API使用setup函数组织逻辑，更好的逻辑复用和类型推导；Options API按data/methods/computed等选项组织'),
(4, 'behavioral', 'medium', '请描述一次你带领团队解决技术难关的经历', '关注点：领导力、问题解决能力、技术方案设计、沟通协作'),
(5, 'hr', 'easy',           '你未来3年的职业规划是什么', '考察求职动机、自我认知、与公司发展的匹配度');

-- 通知模板
INSERT INTO rec_notify_template (id, name, type, title, content, status) VALUES
(1, '面试邀请-邮件', 'email', '面试邀请 - {position}', '亲爱的{name}：\n感谢您投递{position}岗位。我们已安排好面试，详情如下：\n面试时间：{interview_time}\n面试地点：{location}\n联系人：{contact}\n\n请携带简历准时参加。\n\nHR团队', 1),
(2, 'Offer通知-邮件', 'email', '录用通知 - {company}', '亲爱的{name}：\n恭喜您通过面试！我们决定录用您为{position}，薪资范围：{salary}，入职日期：{date}。\n请在3个工作日内回复确认。\n\n{company} HR团队', 1),
(3, '面试提醒-短信', 'sms',   '【面试提醒】', '{name}您好，您的面试安排在{time}，地点：{location}，请准时参加。如有疑问请回复T退订。', 1);

-- 黑名单
INSERT INTO rec_blacklist (id, resume_id, name, phone, reason) VALUES
(1, 5, '周建国', '13800000005', '面试迟到30分钟以上，态度恶劣');
