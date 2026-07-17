-- 补充 admin 用户的角色关联
-- V1__init.sql 漏掉了 sys_user_role 的 INSERT

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);
