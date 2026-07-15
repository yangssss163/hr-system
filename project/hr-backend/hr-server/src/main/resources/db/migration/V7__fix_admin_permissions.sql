-- =============================================
-- 修复管理员权限：确保 admin 用户关联超级管理员角色
-- 并确保超级管理员角色拥有所有菜单权限
-- 使用 INSERT IGNORE 避免重复插入报错
-- =============================================

-- 1. 确保 admin 用户关联超级管理员角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 2. 确保超级管理员角色拥有系统管理菜单权限 (ID 1-27)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 1 AND 27;

-- 3. 确保拥有员工管理菜单权限 (ID 28-40)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 28 AND 40;

-- 4. 确保拥有招聘管理菜单权限 (ID 41-66)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 41 AND 66;

-- 5. 确保拥有考勤/绩效/薪酬菜单权限 (ID 67-133)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 67 AND 133;

-- 6. 确保拥有办公/CRM/工作流菜单权限 (ID 134-199)
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id BETWEEN 134 AND 199;
