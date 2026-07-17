-- 补充员工管理模块下的"创建入职"按钮权限
-- ==============================================

INSERT IGNORE INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort, visible) VALUES
(200, 29, '创建入职', 3, '', '', 'employee:onboarding', '', 6, 1);

-- 为超级管理员分配权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 200);
