-- HR 管理系统 - 系统管理初始化脚本
-- ==========================================

-- 公司表
CREATE TABLE IF NOT EXISTS sys_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '公司名称',
    code VARCHAR(50) NOT NULL COMMENT '公司编码',
    parent_id BIGINT DEFAULT 0 COMMENT '上级公司ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '公司表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL COMMENT '所属公司ID',
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '上级部门ID',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/工号',
    password VARCHAR(255) NOT NULL COMMENT '密码(bcrypt)',
    real_name VARCHAR(50) COMMENT '姓名',
    dept_id BIGINT COMMENT '部门ID',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '用户表';

-- 职位表
CREATE TABLE IF NOT EXISTS sys_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '职位名称',
    dept_id BIGINT NOT NULL COMMENT '所属部门ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '职位表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '角色表';

-- 菜单/权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0 COMMENT '上级菜单ID',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    type TINYINT NOT NULL COMMENT '类型 1目录 2菜单 3按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    permission VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否可见 1可见 0隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) COMMENT '菜单权限表';

-- 角色-菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
) COMMENT '角色菜单关联';

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT '用户角色关联';

-- 字段配置表
CREATE TABLE IF NOT EXISTS sys_field_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module VARCHAR(50) NOT NULL COMMENT '模块名',
    field_key VARCHAR(50) NOT NULL COMMENT '字段键',
    field_name VARCHAR(50) NOT NULL COMMENT '字段名称',
    visible TINYINT DEFAULT 1 COMMENT '是否可见 1可见 0隐藏',
    required TINYINT DEFAULT 0 COMMENT '是否必填 1必填 0非必填',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '字段配置表';

-- 初始化管理员 (密码: admin123)
-- BCrypt hash 由 BCryptPasswordEncoder 生成，若需更换密码请在应用启动后调用修改密码接口
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$hadyUsFC7YFotydBtZhz9ObOd9mS/cPVJYz2FXW1/rrcT0DfDo9y.', '系统管理员', 1);

-- 初始化角色
INSERT INTO sys_role (name, code, description, sort, status) VALUES
('超级管理员', 'admin', '拥有所有权限', 0, 1);

-- 初始化菜单（系统管理）
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(1, 0, '系统管理', 1, '/system', '', '', 'Setting', 1, 1),
(2, 1, '公司架构', 2, '/system/company', 'system/company/CompanyManage', 'system:company:list', 'OfficeBuilding', 1, 1),
(3, 2, '新增公司', 3, '', '', 'system:company:create', '', 1, 1),
(4, 2, '编辑公司', 3, '', '', 'system:company:edit', '', 2, 1),
(5, 2, '删除公司', 3, '', '', 'system:company:delete', '', 3, 1),
(6, 1, '部门架构', 2, '/system/dept', 'system/dept/DeptManage', 'system:dept:list', 'Menu', 2, 1),
(7, 6, '新增部门', 3, '', '', 'system:dept:create', '', 1, 1),
(8, 6, '编辑部门', 3, '', '', 'system:dept:edit', '', 2, 1),
(9, 6, '删除部门', 3, '', '', 'system:dept:delete', '', 3, 1),
(10, 1, '职位管理', 2, '/system/position', 'system/position/PositionManage', 'system:position:list', 'Medal', 3, 1),
(11, 10, '新增职位', 3, '', '', 'system:position:create', '', 1, 1),
(12, 10, '编辑职位', 3, '', '', 'system:position:edit', '', 2, 1),
(13, 10, '删除职位', 3, '', '', 'system:position:delete', '', 3, 1),
(14, 1, '用户管理', 2, '/system/user', 'system/user/UserManage', 'system:user:list', 'User', 4, 1),
(15, 14, '新增用户', 3, '', '', 'system:user:create', '', 1, 1),
(16, 14, '编辑用户', 3, '', '', 'system:user:edit', '', 2, 1),
(17, 14, '删除用户', 3, '', '', 'system:user:delete', '', 3, 1),
(18, 14, '分配角色', 3, '', '', 'system:user:assign-role', '', 4, 1),
(19, 1, '角色权限', 2, '/system/role', 'system/role/RoleManage', 'system:role:list', 'Key', 5, 1),
(20, 19, '新增角色', 3, '', '', 'system:role:create', '', 1, 1),
(21, 19, '编辑角色', 3, '', '', 'system:role:edit', '', 2, 1),
(22, 19, '删除角色', 3, '', '', 'system:role:delete', '', 3, 1),
(23, 19, '分配权限', 3, '', '', 'system:role:assign-menu', '', 4, 1),
(24, 1, '菜单管理', 2, '/system/menu', 'system/menu/MenuManage', 'system:menu:list', 'List', 6, 1),
(25, 24, '新增菜单', 3, '', '', 'system:menu:create', '', 1, 1),
(26, 24, '编辑菜单', 3, '', '', 'system:menu:edit', '', 2, 1),
(27, 24, '删除菜单', 3, '', '', 'system:menu:delete', '', 3, 1);

-- 为管理员分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;
