-- 修正工作流菜单的 component 路径，使其与实际前端文件匹配
-- V6 中使用了 ExpenseManage/LoanManage/TravelManage/LeaveManage，但实际文件为 ExpenseApproval/LoanApproval/TravelApproval/LeaveApproval

UPDATE sys_menu SET component = 'workflow/expense/ExpenseApproval' WHERE id = 180 AND component = 'workflow/expense/ExpenseManage';
UPDATE sys_menu SET component = 'workflow/loan/LoanApproval'       WHERE id = 185 AND component = 'workflow/loan/LoanManage';
UPDATE sys_menu SET component = 'workflow/travel/TravelApproval'   WHERE id = 190 AND component = 'workflow/travel/TravelManage';
UPDATE sys_menu SET component = 'workflow/leave/LeaveApproval'     WHERE id = 195 AND component = 'workflow/leave/LeaveManage';
