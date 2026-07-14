# HR管理系统 API 自动化测试脚本
# 端口: 8080, 基础路径: /api

$base = "http://localhost:8080/api"
$headers = @{"Content-Type" = "application/json"}
$token = ""
$pass = 0
$fail = 0
$results = @()

function Test-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        $Body = $null,
        [bool]$NeedAuth = $true
    )
    try {
        $h = @{"Content-Type" = "application/json"}
        if ($NeedAuth -and $token) { $h["Authorization"] = "Bearer $token" }
        
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $h
            ErrorAction = "Stop"
        }
        if ($Body) { $params["Body"] = ($Body | ConvertTo-Json -Compress -Depth 10) }
        
        $resp = Invoke-RestMethod @params
        $code = $resp.code
        if ($code -eq 200) {
            $script:pass++
            $status = "PASS"
        } else {
            $script:fail++
            $status = "FAIL($code)"
        }
        $msg = "$status | $Method $Name -> $($resp.message)"
        Write-Host $msg -ForegroundColor $(if($status -eq "PASS"){"Green"}else{"Yellow"})
        $script:results += [PSCustomObject]@{Name=$Name;Method=$Method;Url=$Url;Status=$status;Code=$code;Message=$resp.message}
        return $resp
    } catch {
        $script:fail++
        $errMsg = $_.Exception.Message
        if ($errMsg.Length -gt 100) { $errMsg = $errMsg.Substring(0,100) }
        Write-Host "FAIL | $Method $Name -> $errMsg" -ForegroundColor Red
        $script:results += [PSCustomObject]@{Name=$Name;Method=$Method;Url=$Url;Status="ERROR";Code=0;Message=$errMsg}
        return $null
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  HR管理系统 API 自动化测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ============================================
# 模块1: 认证 (Auth)
# ============================================
Write-Host "=== [1] 认证模块 /api/auth ===" -ForegroundColor Cyan

# 1.1 登录（正确）
$loginResp = Test-Api -Name "登录(正确)" -Method "POST" -Url "$base/auth/login" -Body @{username="admin";password="admin123"} -NeedAuth $false
if ($loginResp -and $loginResp.code -eq 200) {
    $token = $loginResp.data.token
    Write-Host "  Token已获取: $($token.Substring(0,30))..." -ForegroundColor Gray
}
# 1.2 登录（错误）
Test-Api -Name "登录(错误密码)" -Method "POST" -Url "$base/auth/login" -Body @{username="admin";password="wrong"} -NeedAuth $false

# 1.3 获取当前用户信息
Test-Api -Name "用户信息" -Method "GET" -Url "$base/auth/userinfo"

# 1.4 获取菜单
Test-Api -Name "用户菜单" -Method "GET" -Url "$base/auth/menus"

# 1.5 修改密码
Test-Api -Name "修改密码" -Method "PUT" -Url "$base/auth/password" -Body @{oldPassword="admin123";newPassword="admin123"}

# 1.6 登出
Test-Api -Name "登出" -Method "POST" -Url "$base/auth/logout"

# ============================================
# 模块2: 系统管理 (System)
# ============================================
Write-Host "`n=== [2] 系统管理模块 ===" -ForegroundColor Cyan

# --- 公司架构 ---
Test-Api -Name "公司树" -Method "GET" -Url "$base/companies/tree"
$createCompany = Test-Api -Name "创建公司" -Method "POST" -Url "$base/companies" -Body @{name="测试分公司";code="TEST";parentId=0;sort=99;status=1}
if ($createCompany -and $createCompany.data) {
    $companyId = $createCompany.data.id
    Test-Api -Name "公司详情" -Method "GET" -Url "$base/companies/$companyId"
    Test-Api -Name "编辑公司" -Method "PUT" -Url "$base/companies/$companyId" -Body @{name="测试分公司改";code="TEST";parentId=0;sort=99;status=1}
    Test-Api -Name "删除公司" -Method "DELETE" -Url "$base/companies/$companyId"
}

# --- 部门架构 ---
Test-Api -Name "部门树" -Method "GET" -Url "$base/depts/tree"
$createDept = Test-Api -Name "创建部门" -Method "POST" -Url "$base/depts" -Body @{companyId=1;name="测试部门";parentId=0;sort=99;status=1}
if ($createDept -and $createDept.data) {
    $deptId = $createDept.data.id
    Test-Api -Name "部门详情" -Method "GET" -Url "$base/depts/$deptId"
    Test-Api -Name "编辑部门" -Method "PUT" -Url "$base/depts/$deptId" -Body @{companyId=1;name="测试部门改";parentId=0;sort=99;status=1}
    Test-Api -Name "删除部门" -Method "DELETE" -Url "$base/depts/$deptId"
}

# --- 职位管理 ---
Test-Api -Name "职位列表" -Method "GET" -Url "$base/positions?page=1&pageSize=5"
$createPos = Test-Api -Name "创建职位" -Method "POST" -Url "$base/positions" -Body @{name="测试工程师";deptId=1;sort=99;status=1}
if ($createPos -and $createPos.data) {
    $posId = $createPos.data.id
    Test-Api -Name "职位详情" -Method "GET" -Url "$base/positions/$posId"
    Test-Api -Name "编辑职位" -Method "PUT" -Url "$base/positions/$posId" -Body @{name="高级测试工程师";deptId=1;sort=99;status=1}
    Test-Api -Name "删除职位" -Method "DELETE" -Url "$base/positions/$posId"
}

# --- 用户管理 ---
Test-Api -Name "用户列表" -Method "GET" -Url "$base/users?page=1&pageSize=5"
$createUser = Test-Api -Name "创建用户" -Method "POST" -Url "$base/users" -Body @{username="testuser";password="123456";realName="测试用户";deptId=1;phone="13800000000";email="test@test.com";roleIds=@(1);status=1}
if ($createUser -and $createUser.data) {
    $userId = $createUser.data.id
    Test-Api -Name "用户详情" -Method "GET" -Url "$base/users/$userId"
    Test-Api -Name "编辑用户" -Method "PUT" -Url "$base/users/$userId" -Body @{username="testuser";realName="测试用户改";deptId=1;phone="13800000001";email="test2@test.com";roleIds=@(1);status=1}
    Test-Api -Name "分配角色" -Method "PUT" -Url "$base/users/$userId/roles" -Body @{roleIds=@(1)}
    Test-Api -Name "删除用户" -Method "DELETE" -Url "$base/users/$userId"
}

# --- 角色管理 ---
Test-Api -Name "角色列表" -Method "GET" -Url "$base/roles?page=1&pageSize=5"
Test-Api -Name "全部角色" -Method "GET" -Url "$base/roles/all"
$createRole = Test-Api -Name "创建角色" -Method "POST" -Url "$base/roles" -Body @{name="测试角色";code="test_role";description="测试";status=1}
if ($createRole -and $createRole.data) {
    $roleId = $createRole.data.id
    Test-Api -Name "角色详情" -Method "GET" -Url "$base/roles/$roleId"
    Test-Api -Name "编辑角色" -Method "PUT" -Url "$base/roles/$roleId" -Body @{name="测试角色改";code="test_role";description="测试改";status=1}
    Test-Api -Name "分配菜单权限" -Method "PUT" -Url "$base/roles/$roleId/menus" -Body @{menuIds=@(1,2,3)}
    Test-Api -Name "删除角色" -Method "DELETE" -Url "$base/roles/$roleId"
}

# --- 菜单管理 ---
Test-Api -Name "菜单树" -Method "GET" -Url "$base/menus/tree"
$createMenu = Test-Api -Name "创建菜单" -Method "POST" -Url "$base/menus" -Body @{parentId=0;name="测试菜单";type=1;path="/test";icon="Test";sort=99;visible=$true}
if ($createMenu -and $createMenu.data) {
    $menuId = $createMenu.data.id
    Test-Api -Name "菜单详情" -Method "GET" -Url "$base/menus/$menuId"
    Test-Api -Name "编辑菜单" -Method "PUT" -Url "$base/menus/$menuId" -Body @{parentId=0;name="测试菜单改";type=1;path="/test2";icon="Test";sort=99;visible=$true}
    Test-Api -Name "删除菜单" -Method "DELETE" -Url "$base/menus/$menuId"
}

# --- 字段配置 ---
Test-Api -Name "字段配置列表" -Method "GET" -Url "$base/field-configs?module=employee"
Test-Api -Name "批量保存字段" -Method "PUT" -Url "$base/field-configs" -Body @(@{id=1;module="employee";fieldKey="phone";fieldName="手机号";visible=$true;required=$true;sort=3})

# ============================================
# 模块3: 员工管理 (Employee)
# ============================================
Write-Host "`n=== [3] 员工管理模块 ===" -ForegroundColor Cyan

# --- 花名册 ---
Test-Api -Name "花名册列表" -Method "GET" -Url "$base/employees?page=1&pageSize=5"
$createEmp = Test-Api -Name "新增员工" -Method "POST" -Url "$base/employees" -Body @{empNo="EMP_TEST";name="测试员工";gender=1;phone="13900000000";email="emp@test.com";deptId=1;positionId=1;companyId=1;entryDate="2026-07-01";status=1}
if ($createEmp -and $createEmp.data) {
    $empId = $createEmp.data.id
    Test-Api -Name "员工详情" -Method "GET" -Url "$base/employees/$empId"
    Test-Api -Name "编辑员工" -Method "PUT" -Url "$base/employees/$empId" -Body @{empNo="EMP_TEST";name="测试员工改";gender=1;phone="13900000001";email="emp2@test.com";deptId=1;positionId=1;companyId=1;entryDate="2026-07-01";status=1}
    Test-Api -Name "删除员工" -Method "DELETE" -Url "$base/employees/$empId"
}
Test-Api -Name "批量删除员工" -Method "DELETE" -Url "$base/employees/batch" -Body @{ids=@(99999)}

# --- 异动管理 ---
$createEmp2 = Test-Api -Name "创建员工(异动用)" -Method "POST" -Url "$base/employees" -Body @{empNo="EMP_TRANS";name="异动测试";gender=1;phone="13900000002";email="trans@test.com";deptId=1;positionId=1;companyId=1;entryDate="2026-06-01";status=1}
if ($createEmp2 -and $createEmp2.data) {
    $transEmpId = $createEmp2.data.id
    $createTrans = Test-Api -Name "创建异动" -Method "POST" -Url "$base/transfers" -Body @{employeeId=$transEmpId;transferType="promote";afterDeptId=1;afterPositionId=1;effectiveDate="2026-07-15";reason="测试异动"}
    Test-Api -Name "异动列表" -Method "GET" -Url "$base/transfers?page=1&pageSize=5"
    if ($createTrans -and $createTrans.data) {
        Test-Api -Name "异动详情" -Method "GET" -Url "$base/transfers/$($createTrans.data.id)"
        Test-Api -Name "撤销异动" -Method "PUT" -Url "$base/transfers/$($createTrans.data.id)/revoke"
    }
    Test-Api -Name "删除员工2" -Method "DELETE" -Url "$base/employees/$transEmpId"
}

# --- 账号开通 ---
Test-Api -Name "开通账号" -Method "POST" -Url "$base/accounts/open" -Body @{employeeId=99999;username="noexist";password="123456";roleIds=@(1)}
Test-Api -Name "启用禁用账号" -Method "PUT" -Url "$base/accounts/99999/toggle"

# ============================================
# 模块4: 招聘管理 (Recruitment)
# ============================================
Write-Host "`n=== [4] 招聘管理模块 ===" -ForegroundColor Cyan

# --- 简历管理 ---
Test-Api -Name "简历列表" -Method "GET" -Url "$base/resumes?page=1&pageSize=5"
$createResume = Test-Api -Name "创建简历" -Method "POST" -Url "$base/resumes" -Body @{name="李四";phone="13900139000";email="lisi@mail.com";gender=1;education="bachelor";school="清华大学";major="计算机";workYears=5;applyPosition="前端工程师";source="BOSS直聘";status="new"}
if ($createResume -and $createResume.data) {
    $resumeId = $createResume.data.id
    Test-Api -Name "简历详情" -Method "GET" -Url "$base/resumes/$resumeId"
    Test-Api -Name "编辑简历" -Method "PUT" -Url "$base/resumes/$resumeId" -Body @{name="李四改";phone="13900139000";email="lisi@mail.com";gender=1;education="bachelor";school="清华大学";major="计算机";workYears=6;applyPosition="高级前端";source="BOSS直聘";status="new"}
    Test-Api -Name "发送邀请" -Method "POST" -Url "$base/resumes/$resumeId/invite" -Body @{interviewTime="2026-07-20 14:00:00";location="上海浦东";templateId=1;message="邀请面试"}
    Test-Api -Name "删除简历" -Method "DELETE" -Url "$base/resumes/$resumeId"
}
Test-Api -Name "批量删除简历" -Method "DELETE" -Url "$base/resumes/batch" -Body @{ids=@(99999)}

# --- 面试管理 ---
Test-Api -Name "面试列表" -Method "GET" -Url "$base/interviews?page=1&pageSize=5"
$createResume2 = Test-Api -Name "创建简历2" -Method "POST" -Url "$base/resumes" -Body @{name="王五";phone="13800138000";email="wangwu@mail.com";status="new"}
if ($createResume2 -and $createResume2.data) {
    $resumeId2 = $createResume2.data.id
    $createInterview = Test-Api -Name "安排面试" -Method "POST" -Url "$base/interviews" -Body @{resumeId=$resumeId2;interviewRound=1;interviewerId=1;interviewTime="2026-07-20 10:00:00";location="会议室A"}
    if ($createInterview -and $createInterview.data) {
        $interviewId = $createInterview.data.id
        Test-Api -Name "面试签到" -Method "POST" -Url "$base/interviews/$interviewId/checkin"
        Test-Api -Name "面试评价" -Method "PUT" -Url "$base/interviews/$interviewId/evaluate" -Body @{score=8.5;evaluation="表现优秀";result="pass"}
        Test-Api -Name "通过面试" -Method "PUT" -Url "$base/interviews/$interviewId/pass"
    }
}

# --- 面试题库 ---
Test-Api -Name "题库列表" -Method "GET" -Url "$base/questions?page=1&pageSize=5"
$createQ = Test-Api -Name "创建试题" -Method "POST" -Url "$base/questions" -Body @{category="technical";difficulty="medium";title="请解释Vue3响应式原理";answer="Vue3使用Proxy..."}
if ($createQ -and $createQ.data) {
    $qId = $createQ.data.id
    Test-Api -Name "试题详情" -Method "GET" -Url "$base/questions/$qId"
    Test-Api -Name "编辑试题" -Method "PUT" -Url "$base/questions/$qId" -Body @{category="technical";difficulty="hard";title="请解释Vue3响应式原理(难)";answer="详细答案..."}
    Test-Api -Name "删除试题" -Method "DELETE" -Url "$base/questions/$qId"
}

# --- 通知模板 ---
Test-Api -Name "模板列表" -Method "GET" -Url "$base/notify-templates?page=1&pageSize=5"
$createTmpl = Test-Api -Name "创建模板" -Method "POST" -Url "$base/notify-templates" -Body @{name="测试模板";type="email";title="面试通知";content="尊敬的{name}...";status=1}
if ($createTmpl -and $createTmpl.data) {
    $tmplId = $createTmpl.data.id
    Test-Api -Name "模板详情" -Method "GET" -Url "$base/notify-templates/$tmplId"
    Test-Api -Name "编辑模板" -Method "PUT" -Url "$base/notify-templates/$tmplId" -Body @{name="测试模板改";type="email";title="面试通知改";content="您好{name}...";status=1}
    Test-Api -Name "删除模板" -Method "DELETE" -Url "$base/notify-templates/$tmplId"
}

# --- 黑名单 ---
Test-Api -Name "黑名单列表" -Method "GET" -Url "$base/blacklists?page=1&pageSize=5"
$createBl = Test-Api -Name "加入黑名单" -Method "POST" -Url "$base/blacklists" -Body @{name="赵六";phone="13700137000";idCard="320xxx199201018888";reason="简历造假"}
if ($createBl -and $createBl.data) {
    Test-Api -Name "移出黑名单" -Method "DELETE" -Url "$base/blacklists/$($createBl.data.id)"
}

# --- 招聘报表 ---
Test-Api -Name "招聘报表" -Method "GET" -Url "$base/recruitment-reports/summary"

# ============================================
# 模块5: 考勤管理 (Attendance)
# ============================================
Write-Host "`n=== [5] 考勤管理模块 ===" -ForegroundColor Cyan

# --- 打卡记录 ---
Test-Api -Name "打卡记录列表" -Method "GET" -Url "$base/attendance-records?page=1&pageSize=5"

# --- 异常统计 ---
Test-Api -Name "异常统计" -Method "GET" -Url "$base/attendance-exceptions?page=1&pageSize=5"

# --- OA流程 ---
Test-Api -Name "OA流程列表" -Method "GET" -Url "$base/oa-flows?page=1&pageSize=5"

# --- 取卡规则 ---
Test-Api -Name "取卡规则列表" -Method "GET" -Url "$base/card-rules"
$createCard = Test-Api -Name "创建取卡规则" -Method "POST" -Url "$base/card-rules" -Body @{name="测试规则";minCardCount=2;allowOvertime=$true;status=1}
if ($createCard -and $createCard.data) {
    $cardId = $createCard.data.id
    Test-Api -Name "规则详情" -Method "GET" -Url "$base/card-rules/$cardId"
    Test-Api -Name "编辑规则" -Method "PUT" -Url "$base/card-rules/$cardId" -Body @{name="测试规则改";minCardCount=2;allowOvertime=$true;status=1}
    Test-Api -Name "删除规则" -Method "DELETE" -Url "$base/card-rules/$cardId"
}

# --- 班次设置 ---
Test-Api -Name "班次列表" -Method "GET" -Url "$base/shifts"
$createShift = Test-Api -Name "创建班次" -Method "POST" -Url "$base/shifts" -Body @{name="测试班次";startTime="09:00";endTime="18:00";lateBuffer=10;earlyBuffer=10;status=1}
if ($createShift -and $createShift.data) {
    $shiftId = $createShift.data.id
    Test-Api -Name "班次详情" -Method "GET" -Url "$base/shifts/$shiftId"
    Test-Api -Name "编辑班次" -Method "PUT" -Url "$base/shifts/$shiftId" -Body @{name="测试班次改";startTime="09:30";endTime="18:30";lateBuffer=15;earlyBuffer=15;status=1}
    Test-Api -Name "删除班次" -Method "DELETE" -Url "$base/shifts/$shiftId"
}

# --- 法定假期 ---
Test-Api -Name "法定假期列表" -Method "GET" -Url "$base/holidays?year=2026"
$createHol = Test-Api -Name "创建假期" -Method "POST" -Url "$base/holidays" -Body @{year=2026;name="测试假期";date="2026-12-25";days=1}
if ($createHol -and $createHol.data) {
    $holId = $createHol.data.id
    Test-Api -Name "修改假期" -Method "PUT" -Url "$base/holidays/$holId" -Body @{year=2026;name="测试假期改";date="2026-12-25";days=2}
    Test-Api -Name "删除假期" -Method "DELETE" -Url "$base/holidays/$holId"
}

# --- 假期额度 ---
Test-Api -Name "假期额度列表" -Method "GET" -Url "$base/leave-quotas?year=2026"

# --- 假期类型 ---
Test-Api -Name "假期类型列表" -Method "GET" -Url "$base/leave-types"
$createLT = Test-Api -Name "创建假期类型" -Method "POST" -Url "$base/leave-types" -Body @{name="测试假";code="test_leave";defaultDays=3;enabled=$true}
if ($createLT -and $createLT.data) {
    $ltId = $createLT.data.id
    Test-Api -Name "编辑假期类型" -Method "PUT" -Url "$base/leave-types/$ltId" -Body @{name="测试假改";code="test_leave";defaultDays=5;enabled=$true}
    Test-Api -Name "删除假期类型" -Method "DELETE" -Url "$base/leave-types/$ltId"
}

# --- 考勤报表 ---
Test-Api -Name "考勤明细表" -Method "GET" -Url "$base/attendance-reports/detail?dateStart=2026-07-01&dateEnd=2026-07-31"
Test-Api -Name "考勤汇总表" -Method "GET" -Url "$base/attendance-reports/summary?month=2026-07"

# --- 考勤扣款 ---
Test-Api -Name "考勤扣款列表" -Method "GET" -Url "$base/attendance-deductions"

# ============================================
# 模块6: 绩效管理 (Performance)
# ============================================
Write-Host "`n=== [6] 绩效管理模块 ===" -ForegroundColor Cyan

# --- 绩效等级 ---
Test-Api -Name "绩效等级列表" -Method "GET" -Url "$base/perf-levels"
$createLevel = Test-Api -Name "创建等级" -Method "POST" -Url "$base/perf-levels" -Body @{name="B+";scoreMin=70;scoreMax=79;coefficient=1.0;sort=3}
if ($createLevel -and $createLevel.data) {
    $levelId = $createLevel.data.id
    Test-Api -Name "编辑等级" -Method "PUT" -Url "$base/perf-levels/$levelId" -Body @{name="B+改";scoreMin=75;scoreMax=84;coefficient=1.1;sort=3}
    Test-Api -Name "删除等级" -Method "DELETE" -Url "$base/perf-levels/$levelId"
}

# --- 绩效工资 ---
Test-Api -Name "绩效工资列表" -Method "GET" -Url "$base/perf-salaries"
$createSal = Test-Api -Name "创建绩效工资" -Method "POST" -Url "$base/perf-salaries" -Body @{levelId=1;salaryRange="3000-5000";sort=1}
if ($createSal -and $createSal.data) {
    $psId = $createSal.data.id
    Test-Api -Name "编辑绩效工资" -Method "PUT" -Url "$base/perf-salaries/$psId" -Body @{levelId=1;salaryRange="4000-6000";sort=1}
    Test-Api -Name "删除绩效工资" -Method "DELETE" -Url "$base/perf-salaries/$psId"
}

# --- 考核计划 ---
Test-Api -Name "考核计划列表" -Method "GET" -Url "$base/perf-plans?page=1&pageSize=5"
$createPlan = Test-Api -Name "创建计划" -Method "POST" -Url "$base/perf-plans" -Body @{name="2026Q3考核";deptId=1;startDate="2026-07-01";endDate="2026-09-30";employeeIds=@(1);status=1}
if ($createPlan -and $createPlan.data) {
    $planId = $createPlan.data.id
    Test-Api -Name "计划详情" -Method "GET" -Url "$base/perf-plans/$planId"
    Test-Api -Name "修改计划" -Method "PUT" -Url "$base/perf-plans/$planId" -Body @{name="2026Q3考核改";deptId=1;startDate="2026-07-01";endDate="2026-09-30";employeeIds=@(1);status=1}
    Test-Api -Name "删除计划" -Method "DELETE" -Url "$base/perf-plans/$planId"
}

# --- 考核记录 ---
Test-Api -Name "考核记录列表" -Method "GET" -Url "$base/perf-records?page=1&pageSize=5"
$createPlan2 = Test-Api -Name "创建计划2" -Method "POST" -Url "$base/perf-plans" -Body @{name="2026Q4考核";deptId=1;startDate="2026-10-01";endDate="2026-12-31";employeeIds=@(1);status=1}
if ($createPlan2 -and $createPlan2.data) {
    $plan2Id = $createPlan2.data.id
    $createRec = Test-Api -Name "创建考核记录" -Method "POST" -Url "$base/perf-records" -Body @{planId=$plan2Id;employeeId=1;items=@(@{indicator="工作效率";weight=50;score=90},@{indicator="团队协作";weight=50;score=85});totalScore=87.5;evaluation="良好";levelId=2}
    if ($createRec -and $createRec.data) {
        Test-Api -Name "考核详情" -Method "GET" -Url "$base/perf-records/$($createRec.data.id)"
        Test-Api -Name "编辑考核" -Method "PUT" -Url "$base/perf-records/$($createRec.data.id)" -Body @{planId=$plan2Id;employeeId=1;items=@(@{indicator="工作效率";weight=50;score=92},@{indicator="团队协作";weight=50;score=88});totalScore=90.0;evaluation="优秀";levelId=1}
    }
    Test-Api -Name "删除计划2" -Method "DELETE" -Url "$base/perf-plans/$plan2Id"
}

# --- 绩效报表 ---
Test-Api -Name "绩效明细表" -Method "GET" -Url "$base/perf-reports/detail"
Test-Api -Name "部门绩效汇总" -Method "GET" -Url "$base/perf-reports/dept-summary"
Test-Api -Name "职员绩效汇总" -Method "GET" -Url "$base/perf-reports/employee-summary"

# ============================================
# 模块7: 薪酬管理 (Salary)
# ============================================
Write-Host "`n=== [7] 薪酬管理模块 ===" -ForegroundColor Cyan

# --- 核算规则 ---
Test-Api -Name "核算规则列表" -Method "GET" -Url "$base/salary-rules"

# --- 薪酬字段 ---
Test-Api -Name "薪酬字段列表" -Method "GET" -Url "$base/salary-fields"
$createField = Test-Api -Name "创建字段" -Method "POST" -Url "$base/salary-fields" -Body @{name="测试奖金";code="test_bonus";type="income";formula="base_salary*0.1";sort=99;status=1}
if ($createField -and $createField.data) {
    $fieldId = $createField.data.id
    Test-Api -Name "编辑字段" -Method "PUT" -Url "$base/salary-fields/$fieldId" -Body @{name="测试奖金改";code="test_bonus";type="income";formula="base_salary*0.15";sort=99;status=1}
    Test-Api -Name "删除字段" -Method "DELETE" -Url "$base/salary-fields/$fieldId"
}

# --- 员工调薪 ---
Test-Api -Name "调薪记录列表" -Method "GET" -Url "$base/salary-adjusts?page=1&pageSize=5"
Test-Api -Name "员工调薪" -Method "POST" -Url "$base/salary-adjusts" -Body @{employeeId=1;afterSalary=18000;adjustType="promote";effectiveDate="2026-07-01";remark="测试调薪"}

# --- 工资表 ---
Test-Api -Name "工资表列表" -Method "GET" -Url "$base/salary-sheets?month=2026-07"
Test-Api -Name "同步绩效考勤" -Method "POST" -Url "$base/salary-sheets/sync" -Body @{month="2026-07";deptId=1}
Test-Api -Name "生成工资表" -Method "POST" -Url "$base/salary-sheets/generate" -Body @{month="2026-07";employeeIds=@(1)}

# ============================================
# 模块8: 公共接口 (Common)
# ============================================
Write-Host "`n=== [8] 公共接口模块 ===" -ForegroundColor Cyan

# --- 选项数据 ---
Test-Api -Name "选项(学历)" -Method "GET" -Url "$base/common/options?type=education" -NeedAuth $false
Test-Api -Name "选项(性别)" -Method "GET" -Url "$base/common/options?type=gender" -NeedAuth $false
Test-Api -Name "选项(简历来源)" -Method "GET" -Url "$base/common/options?type=resume_source" -NeedAuth $false
Test-Api -Name "选项(假期类型)" -Method "GET" -Url "$base/common/options?type=leave_type" -NeedAuth $false
Test-Api -Name "选项(异动类型)" -Method "GET" -Url "$base/common/options?type=transfer_type" -NeedAuth $false

# ============================================
# 汇总报告
# ============================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  测试汇总报告" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "总计: $($pass + $fail) 个测试" -ForegroundColor White
Write-Host "通过: $pass" -ForegroundColor Green
Write-Host "失败: $fail" -ForegroundColor $(if($fail -gt 0){"Red"}else{"Green"})

if ($fail -gt 0) {
    Write-Host "`n--- 失败列表 ---" -ForegroundColor Yellow
    $results | Where-Object { $_.Status -ne "PASS" } | ForEach-Object {
        Write-Host "  $($_.Status) | $($_.Method) $($_.Name)" -ForegroundColor $(if($_.Status -eq "FAIL(401)"){"Yellow"}else{"Red"})
    }
}

# 按状态分类统计
Write-Host "`n--- 按状态分类 ---" -ForegroundColor Cyan
$results | Group-Object Status | ForEach-Object {
    $color = if($_.Name -eq "PASS"){"Green"}elseif($_.Name -like "FAIL*"){"Yellow"}else{"Red"}
    Write-Host "  $($_.Name): $($_.Count)" -ForegroundColor $color
}
