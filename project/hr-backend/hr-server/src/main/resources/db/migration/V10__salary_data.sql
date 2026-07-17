-- ================================================================
-- V10 - 薪酬管理模块补充数据
-- ================================================================

-- ============ 核算规则补充 ============
INSERT INTO sal_rule (id, type, value, unit, remark) VALUES
(4, 'overtime_rate',    1.5,  '倍',  '工作日加班费率'),
(5, 'overtime_weekend', 2.0,  '倍',  '周末加班费率'),
(6, 'subsidy_default',  500,  '元',  '默认岗位补贴'),
(7, 'pension',          0.08, '%',   '养老保险个人比例'),
(8, 'medical',          0.02, '%',   '医疗保险个人比例'),
(9, 'unemployment',     0.005, '%',  '失业保险个人比例'),
(10, 'housing_fund',    0.07, '%',   '公积金个人比例');

-- ============ 薪酬字段补充 ============
INSERT INTO sal_field (id, name, code, type, formula, sort, status) VALUES
(7, '工龄工资',  'seniority_pay',   'income',    'base_salary * 0.02 * years', 5, 1),
(8, '全勤奖',    'attendance_bonus','income',    '200',                       6, 1),
(9, '养老保险',  'pension',         'deduction', 'base_salary * 0.08',         3, 1),
(10, '医疗保险', 'medical',         'deduction', 'base_salary * 0.02',         4, 1),
(11, '失业保险', 'unemployment',    'deduction', 'base_salary * 0.005',        5, 1),
(12, '公积金',   'housing_fund',    'deduction', 'base_salary * 0.07',         6, 1);

-- ============ 员工调薪记录补充 ============
INSERT INTO sal_adjust (id, employee_id, before_salary, after_salary, adjust_amount, adjust_type, effective_date, remark) VALUES
(4, 4,  9000.00,  9000.00,    0.00, 'transfer',  '2022-06-20', '入职定薪'),
(5, 4,  9000.00, 10000.00, 1000.00, 'annual',    '2025-01-01', '年度调薪'),
(6, 2, 14000.00, 15000.00, 1000.00, 'promote',   '2024-03-01', '晋升调薪'),
(7, 2, 15000.00, 14000.00, -1000.00,'transfer',  '2025-01-01', '岗位调整'),
(8, 1, 18000.00, 20000.00, 2000.00, 'annual',    '2026-01-01', '年度调薪'),
(9, 5,     0.00,  8000.00, 8000.00, 'transfer',  '2023-01-05', '入职定薪'),
(10, 6,    0.00, 12000.00,12000.00, 'transfer',  '2020-09-01', '入职定薪'),
(11, 6,12000.00, 13000.00, 1000.00, 'annual',    '2023-01-01', '年度调薪'),
(12, 3, 11000.00, 12000.00, 1000.00, 'special',   '2026-04-01', '特别调薪');

-- ============ 工资表补充（补齐缺失员工和月份） ============

-- 员工4（赵六）2026年7月
INSERT INTO sal_sheet (employee_id, month, base_salary, perf_salary, subsidy, overtime_pay, total_income, social_insurance, tax, total_deduction, net_salary, status) VALUES
(4, '2026-07', 10000, 1000, 500,  0, 11500, 1050, 310, 1360, 10140, 1);

-- 员工5（孙七）2026年6月 & 7月
INSERT INTO sal_sheet (employee_id, month, base_salary, perf_salary, subsidy, overtime_pay, total_income, social_insurance, tax, total_deduction, net_salary, status) VALUES
(5, '2026-06',  8000,  500, 300,  0,  8800,  840, 90,  930,  7870, 1),
(5, '2026-07',  8000,  600, 300, 200, 9100,  840, 120,  960,  8140, 1);

-- 员工6（周八）2026年6月 & 7月
INSERT INTO sal_sheet (employee_id, month, base_salary, perf_salary, subsidy, overtime_pay, total_income, social_insurance, tax, total_deduction, net_salary, status) VALUES
(6, '2026-06', 13000, 1500, 300,  0, 14800, 1365, 450, 1815, 12985, 1),
(6, '2026-07', 13000, 1800, 300, 300, 15400, 1365, 510, 1875, 13525, 1);

-- ============ 更新已有员工 base_salary（V9 新增字段） ============
UPDATE hr_employee SET base_salary = 20000 WHERE id = 1 AND base_salary = 0;
UPDATE hr_employee SET base_salary = 14000 WHERE id = 2 AND base_salary = 0;
UPDATE hr_employee SET base_salary = 12000 WHERE id = 3 AND base_salary = 0;
UPDATE hr_employee SET base_salary = 10000 WHERE id = 4 AND base_salary = 0;
UPDATE hr_employee SET base_salary =  8000 WHERE id = 5 AND base_salary = 0;
UPDATE hr_employee SET base_salary = 13000 WHERE id = 6 AND base_salary = 0;
