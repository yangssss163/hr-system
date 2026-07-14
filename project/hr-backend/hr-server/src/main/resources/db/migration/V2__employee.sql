-- HR 管理系统 - 员工管理模块建表
-- ==========================================

-- 员工花名册
CREATE TABLE IF NOT EXISTS hr_employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联用户ID',
    emp_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别 1男 2女',
    id_card VARCHAR(20) COMMENT '身份证号',
    birthday DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    dept_id BIGINT COMMENT '部门ID',
    position_id BIGINT COMMENT '职位ID',
    company_id BIGINT COMMENT '公司ID',
    entry_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态 1在职 2离职 3试用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '员工花名册';

-- 异动记录
CREATE TABLE IF NOT EXISTS hr_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    transfer_type VARCHAR(20) NOT NULL COMMENT '异动类型: regular转正/transfer调岗/promote晋升/dimission离职/rehire返聘',
    before_dept_id BIGINT COMMENT '原部门',
    after_dept_id BIGINT COMMENT '新部门',
    before_position_id BIGINT COMMENT '原职位',
    after_position_id BIGINT COMMENT '新职位',
    effective_date DATE COMMENT '生效日期',
    reason VARCHAR(500) COMMENT '原因',
    status TINYINT DEFAULT 1 COMMENT '状态 1已生效 0已撤销',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '异动记录表';

-- 员工管理菜单权限
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(28, 0, '员工管理', 1, '/employee', '', '', 'UserFilled', 2, 1),
(29, 28, '员工花名册', 2, '/employee/list', 'employee/list/EmployeeList', 'employee:list', 'List', 1, 1),
(30, 29, '新增员工', 3, '', '', 'employee:create', '', 1, 1),
(31, 29, '编辑员工', 3, '', '', 'employee:edit', '', 2, 1),
(32, 29, '删除员工', 3, '', '', 'employee:delete', '', 3, 1),
(33, 29, '导入员工', 3, '', '', 'employee:import', '', 4, 1),
(34, 29, '导出员工', 3, '', '', 'employee:export', '', 5, 1),
(35, 28, '异动管理', 2, '/employee/transfer', 'employee/transfer/TransferManage', 'employee:transfer:list', 'Switch', 2, 1),
(36, 35, '新增异动', 3, '', '', 'employee:transfer:create', '', 1, 1),
(37, 35, '撤销异动', 3, '', '', 'employee:transfer:revoke', '', 2, 1),
(38, 28, '账号管理', 2, '/employee/account', 'employee/account/AccountManage', 'employee:account:list', 'Avatar', 3, 1),
(39, 38, '开通账号', 3, '', '', 'employee:account:create', '', 1, 1),
(40, 38, '启用禁用', 3, '', '', 'employee:account:toggle', '', 2, 1);

-- 为超级管理员分配员工管理菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 28 AND 40;
