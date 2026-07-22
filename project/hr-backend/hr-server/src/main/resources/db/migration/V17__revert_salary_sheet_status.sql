-- 将已导出(status=2)的工资表记录恢复为已生成(status=1)
-- 导出功能不再改变记录状态，旧数据需回退
UPDATE sal_sheet SET status = 1 WHERE status = 2;
