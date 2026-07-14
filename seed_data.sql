-- HR管理系统种子数据

-- 公司数据
INSERT INTO sys_company (id, name, code, parent_id, sort, status) VALUES
(1, '总公司', 'HQ', 0, 0, 1),
(2, '上海分公司', 'SH', 1, 1, 1),
(3, '北京分公司', 'BJ', 1, 2, 1);

-- 部门数据
INSERT INTO sys_dept (id, company_id, name, parent_id, sort, status) VALUES
(1, 1, '技术部', 0, 0, 1),
(2, 1, '人力资源部', 0, 1, 1),
(3, 1, '财务部', 0, 2, 1),
(4, 1, '市场部', 0, 3, 1),
(5, 1, '前端组', 1, 1, 1),
(6, 1, '后端组', 1, 2, 1),
(7, 2, '销售部', 0, 0, 1);

-- 职位数据
INSERT INTO sys_position (id, name, dept_id, sort, status) VALUES
(1, '高级前端工程师', 5, 1, 1),
(2, '前端工程师', 5, 2, 1),
(3, 'Java开发工程师', 6, 1, 1),
(4, 'HR专员', 2, 1, 1),
(5, '财务主管', 3, 1, 1),
(6, '市场经理', 4, 1, 1),
(7, '技术总监', 1, 1, 1),
(8, '销售代表', 7, 1, 1);

-- 更新admin用户关联到技术部
UPDATE sys_user SET dept_id = 1, phone = '13800138000', email = 'admin@company.com' WHERE username = 'admin';

-- 员工花名册
INSERT INTO hr_employee (id, emp_no, name, gender, phone, email, dept_id, position_id, company_id, entry_date, status) VALUES
(1, 'EMP001', '张三', 1, '13800001001', 'zhangsan@company.com', 5, 1, 1, '2025-03-01', 1),
(2, 'EMP002', '李四', 1, '13800001002', 'lisi@company.com', 5, 2, 1, '2025-06-15', 1),
(3, 'EMP003', '王五', 1, '13800001003', 'wangwu@company.com', 6, 3, 1, '2025-01-20', 1),
(4, 'EMP004', '赵六', 2, '13800001004', 'zhaoliu@company.com', 2, 4, 1, '2025-04-10', 1),
(5, 'EMP005', '孙七', 1, '13800001005', 'sunqi@company.com', 3, 5, 1, '2025-02-01', 1),
(6, 'EMP006', '周八', 1, '13800001006', 'zhouba@company.com', 7, 8, 2, '2025-05-01', 3);

-- 异动记录
INSERT INTO hr_transfer (id, employee_id, transfer_type, before_dept_id, after_dept_id, before_position_id, after_position_id, effective_date, reason, status) VALUES
(1, 3, 'promote', 6, 6, 3, 3, '2026-01-01', '年度绩效考核优秀，予以晋升', 1);

-- 简历数据
INSERT INTO rec_resume (id, name, phone, email, gender, education, school, major, work_years, apply_position, source, status) VALUES
(1, '候选人A', '13900000001', 'candidateA@mail.com', 1, 'bachelor', '清华大学', '计算机科学', 3, '前端工程师', 'BOSS直聘', 'new'),
(2, '候选人B', '13900000002', 'candidateB@mail.com', 2, 'master', '北京大学', '软件工程', 5, 'Java工程师', '拉勾', 'screening'),
(3, '候选人C', '13900000003', 'candidateC@mail.com', 1, 'bachelor', '上海交大', '信息管理', 2, 'HR专员', '智联招聘', 'interview'),
(4, '候选人D', '13900000004', 'candidateD@mail.com', 1, 'doctor', '浙江大学', '人工智能', 8, '技术总监', '猎聘', 'offer'),
(5, '候选人E', '13900000005', 'candidateE@mail.com', 2, 'junior_college', '南京大学', '市场营销', 1, '销售代表', '前程无忧', 'eliminated');

-- 面试记录
INSERT INTO rec_interview (id, resume_id, interview_round, interviewer_id, interview_time, location, result, score, evaluation) VALUES
(1, 3, 1, 1, '2026-07-15 10:00:00', '会议室A', 'pending', NULL, NULL),
(2, 2, 1, 1, '2026-07-10 14:00:00', '会议室B', 'pass', 8.5, '技术基础扎实，沟通良好'),
(3, 4, 1, 1, '2026-07-08 09:00:00', 'VIP会议室', 'pass', 9.2, '非常优秀，强烈推荐');

-- 通知模板
INSERT INTO rec_notify_template (id, name, type, title, content, status) VALUES
(1, '面试邀请模板', 'email', '面试邀请通知', '尊敬的{name}，恭喜您通过简历筛选，诚邀您参加面试。面试时间：{time}，地点：{location}。', 1),
(2, 'Offer模板', 'email', '录用通知书', '尊敬的{name}，我们很高兴地通知您已通过面试，正式向您发出录用通知。', 1);

-- 面试题库
INSERT INTO rec_question (id, category, difficulty, title, answer) VALUES
(1, 'technical', 'medium', '请解释Vue3的响应式原理', 'Vue3使用Proxy替代Object.defineProperty实现响应式'),
(2, 'technical', 'hard', '请描述微服务架构的优缺点', '微服务解耦独立部署，但增加运维复杂度'),
(3, 'behavioral', 'easy', '请描述你遇到的最大挑战', '考察候选人的问题解决能力'),
(4, 'hr', 'easy', '你为什么选择我们公司', '了解候选人的求职动机');

-- 黑名单
INSERT INTO rec_blacklist (id, name, phone, id_card, reason) VALUES
(1, '作弊者X', '13600000001', '310xxx199501019999', '简历严重造假'),
(2, '违纪者Y', '13600000002', '310xxx199602029999', '面试严重迟到且态度恶劣');

-- 打卡记录
INSERT INTO att_record (id, employee_id, record_date, check_in, check_out, status, source) VALUES
(1, 1, '2026-07-01', '2026-07-01 08:55:00', '2026-07-01 18:05:00', 'normal', 'card'),
(2, 1, '2026-07-02', '2026-07-01 09:10:00', '2026-07-01 18:00:00', 'late', 'card'),
(3, 2, '2026-07-01', '2026-07-01 08:50:00', '2026-07-01 17:30:00', 'early', 'card'),
(4, 3, '2026-07-01', '2026-07-01 09:00:00', '2026-07-01 21:00:00', 'overtime', 'card'),
(5, 1, '2026-07-03', NULL, NULL, 'absent', 'card');

-- 班次设置
INSERT INTO att_shift (id, name, start_time, end_time, late_buffer, early_buffer, status) VALUES
(1, '标准班', '09:00', '18:00', 10, 10, 1),
(2, '弹性班', '10:00', '19:00', 30, 30, 1);

-- 假期类型
INSERT INTO att_leave_type (id, name, code, default_days, enabled) VALUES
(1, '年假', 'annual', 5, 1),
(2, '病假', 'sick', 0, 1),
(3, '事假', 'personal', 0, 1),
(4, '婚假', 'marriage', 3, 1);

-- 假期额度
INSERT INTO att_leave_quota (id, employee_id, leave_type, year, total_days, used_days, remain_days) VALUES
(1, 1, 'annual', 2026, 5, 2, 3),
(2, 2, 'annual', 2026, 5, 0, 5),
(3, 3, 'annual', 2026, 10, 1, 9);

-- 取卡规则
INSERT INTO att_card_rule (id, name, min_card_count, allow_overtime, status) VALUES
(1, '标准取卡规则', 2, 1, 1);

-- 法定假期
INSERT INTO att_holiday (id, year, name, date, days) VALUES
(1, 2026, '元旦', '2026-01-01', 1),
(2, 2026, '春节', '2026-02-17', 7),
(3, 2026, '清明节', '2026-04-05', 1),
(4, 2026, '劳动节', '2026-05-01', 5),
(5, 2026, '端午节', '2026-06-19', 3),
(6, 2026, '中秋节', '2026-09-25', 3),
(7, 2026, '国庆节', '2026-10-01', 7);

-- OA流程
INSERT INTO att_oa_flow (id, employee_id, type, start_date, end_date, duration, status) VALUES
(1, 1, 'leave', '2026-07-05', '2026-07-06', 2, 'approved'),
(2, 2, 'leave', '2026-07-10', '2026-07-10', 1, 'pending');

-- 考勤扣款
INSERT INTO att_deduction (id, type, deduction, unit, remark) VALUES
(1, 'miss_card', 50, '次', '每次漏打卡扣款50元'),
(2, 'late', 30, '次', '每次迟到早退扣款30元');

-- 绩效等级
INSERT INTO perf_level (id, name, score_min, score_max, coefficient, sort) VALUES
(1, 'S', 90, 100, 1.5, 1),
(2, 'A', 80, 89, 1.2, 2),
(3, 'B', 70, 79, 1.0, 3),
(4, 'C', 60, 69, 0.8, 4),
(5, 'D', 0, 59, 0.5, 5);

-- 绩效工资
INSERT INTO perf_salary (id, level_id, salary_range, sort) VALUES
(1, 1, '5000-8000', 1),
(2, 2, '3000-5000', 2),
(3, 3, '1500-3000', 3),
(4, 4, '500-1500', 4),
(5, 5, '0-500', 5);

-- 考核计划
INSERT INTO perf_plan (id, name, dept_id, start_date, end_date, status) VALUES
(1, '2026年Q2绩效考核', 1, '2026-04-01', '2026-06-30', 2),
(2, '2026年Q3绩效考核', 1, '2026-07-01', '2026-09-30', 1);

-- 考核记录
INSERT INTO perf_record (id, plan_id, employee_id, total_score, evaluation, level_id, evaluator_id, evaluate_time) VALUES
(1, 1, 1, 88.5, '整体表现优秀，团队协作能力强', 2, 1, '2026-07-01 10:00:00'),
(2, 1, 2, 75.0, '工作认真负责，但技术深度有待加强', 3, 1, '2026-07-01 11:00:00'),
(3, 1, 3, 93.0, '项目完成质量高，创新能力突出', 1, 1, '2026-07-01 14:00:00');

-- 薪酬核算规则
INSERT INTO sal_rule (id, `type`, value, unit, remark) VALUES
(1, 'base_salary', 5000, '元', '上海地区基本工资标准');

-- 薪酬字段
INSERT INTO sal_field (id, name, code, type, formula, sort, status) VALUES
(1, '基本工资', 'base_salary', 'income', 'rule_base_salary', 1, 1),
(2, '绩效工资', 'perf_salary', 'income', 'base_salary * perf_coefficient', 2, 1),
(3, '加班费', 'overtime_pay', 'income', 'overtime_hours * hourly_rate * 1.5', 3, 1),
(4, '社保扣除', 'social_insurance', 'deduction', 'base_salary * 0.105', 10, 1),
(5, '公积金', 'housing_fund', 'deduction', 'base_salary * 0.07', 11, 1),
(6, '个税', 'income_tax', 'deduction', 'tax_calculation', 12, 1);

-- 调薪记录
INSERT INTO sal_adjust (id, employee_id, before_salary, after_salary, adjust_amount, adjust_type, effective_date, remark) VALUES
(1, 1, 15000, 18000, 3000, 'promote', '2026-07-01', '晋升为高级前端工程师');

-- 工资表
INSERT INTO sal_sheet (id, employee_id, month, base_salary, perf_salary, subsidy, overtime_pay, total_income, social_insurance, tax, total_deduction, net_salary) VALUES
(1, 1, '2026-06', 15000, 3000, 500, 800, 19300, 1575, 450, 2025, 17275),
(2, 2, '2026-06', 12000, 2000, 300, 0, 14300, 1260, 200, 1460, 12840),
(3, 3, '2026-06', 20000, 5000, 800, 1200, 27000, 2100, 800, 2900, 24100);

-- 为admin用户分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
