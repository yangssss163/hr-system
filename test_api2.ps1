param([int]$Module=0)

$base = "http://localhost:8080/api"
$global:token = ""
$global:passCount = 0
$global:failCount = 0

function Test-Route {
    param([string]$Label, [string]$Method, [string]$Path, $Body, [bool]$Auth=$true)
    try {
        $h = @{"Content-Type"="application/json"}
        if ($Auth -and $global:token) { $h["Authorization"] = "Bearer $global:token" }
        $prm = @{Uri="$base$Path";Method=$Method;Headers=$h;ErrorAction="Stop"}
        if ($Body) { $prm["Body"] = ($Body | ConvertTo-Json -Compress -Depth 10) }
        $r = Invoke-RestMethod @prm
        if ($r.code -eq 200) { $global:passCount++; Write-Host "[PASS]" -NoNewline -ForegroundColor Green }
        else { $global:failCount++; Write-Host "[FAIL:$($r.code)]" -NoNewline -ForegroundColor Yellow }
        Write-Host " $Method $Path" -NoNewline
        Write-Host " ($Label)" -ForegroundColor Gray
        return $r
    } catch {
        $global:failCount++
        $err = if($_.Exception.Message.Length -gt 150){$_.Exception.Message.Substring(0,150)}else{$_.Exception.Message}
        Write-Host "[ERR ] $Method $Path ($Label) -- $err" -ForegroundColor Red
        return $null
    }
}

function Test-Module {
    param([string]$Title)
    Write-Host "`n>>> $Title" -ForegroundColor White
}

Write-Host ("="*60) -ForegroundColor Cyan
Write-Host " HR System API Test - Port 8080" -ForegroundColor Cyan
Write-Host ("="*60) -ForegroundColor Cyan

# LOGIN (only once)
Write-Host "`n>>> Login as admin/admin123" -ForegroundColor White
$lr = Test-Route "login-ok" "POST" "/auth/login" @{username="admin";password="admin123"} $false
if ($lr -and $lr.code -eq 200) { $global:token = $lr.data.token; Write-Host " Token: $($token.Length) chars" -ForegroundColor DarkGray }
Test-Route "login-fail" "POST" "/auth/login" @{username="admin";password="bad"} $false
Test-Route "userinfo" "GET" "/auth/userinfo"
Test-Route "menus" "GET" "/auth/menus"
Test-Route "pwd-change" "PUT" "/auth/password" @{oldPassword="admin123";newPassword="admin123"}

# === SYSTEM ===
Test-Module "System Management"
Test-Route "company-tree" "GET" "/companies/tree"
$co = Test-Route "company-create" "POST" "/companies" @{name="TestCo";code="TC";parentId=0;sort=99;status=1}
if ($co -and $co.data) { $cid=$co.data.id; Test-Route "company-get" "GET" "/companies/$cid"; Test-Route "company-edit" "PUT" "/companies/$cid" @{name="TestCo2";code="TC";parentId=0;sort=99;status=1}; Test-Route "company-del" "DELETE" "/companies/$cid" }

Test-Route "dept-tree" "GET" "/depts/tree"
$dp = Test-Route "dept-create" "POST" "/depts" @{companyId=1;name="TestDept";parentId=0;sort=99;status=1}
if ($dp -and $dp.data) { $did=$dp.data.id; Test-Route "dept-get" "GET" "/depts/$did"; Test-Route "dept-edit" "PUT" "/depts/$did" @{companyId=1;name="TestDept2";parentId=0;sort=99;status=1}; Test-Route "dept-del" "DELETE" "/depts/$did" }

Test-Route "pos-list" "GET" "/positions?page=1&pageSize=5"
$po = Test-Route "pos-create" "POST" "/positions" @{name="TestPos";deptId=1;sort=99;status=1}
if ($po -and $po.data) { $pid=$po.data.id; Test-Route "pos-get" "GET" "/positions/$pid"; Test-Route "pos-edit" "PUT" "/positions/$pid" @{name="TestPos2";deptId=1;sort=99;status=1}; Test-Route "pos-del" "DELETE" "/positions/$pid" }

Test-Route "user-list" "GET" "/users?page=1&pageSize=5"
$us = Test-Route "user-create" "POST" "/users" @{username="testu1";password="123456";realName="TestU";deptId=1;phone="13800000000";email="t@t.com";roleIds=@(1);status=1}
if ($us -and $us.data) { $uid=$us.data.id; Test-Route "user-get" "GET" "/users/$uid"; Test-Route "user-assign" "PUT" "/users/$uid/roles" @{roleIds=@(1)}; Test-Route "user-del" "DELETE" "/users/$uid" }

Test-Route "role-list" "GET" "/roles?page=1&pageSize=5"
Test-Route "role-all" "GET" "/roles/all"
$rl = Test-Route "role-create" "POST" "/roles" @{name="TestRole";code="tr";description="test";status=1}
if ($rl -and $rl.data) { $rid=$rl.data.id; Test-Route "role-get" "GET" "/roles/$rid"; Test-Route "role-menus" "PUT" "/roles/$rid/menus" @{menuIds=@(1,2)}; Test-Route "role-del" "DELETE" "/roles/$rid" }

Test-Route "menu-tree" "GET" "/menus/tree"
$mn = Test-Route "menu-create" "POST" "/menus" @{parentId=0;name="TestMenu";type=1;path="/tm";icon="Test";sort=99;visible=$true}
if ($mn -and $mn.data) { $mid=$mn.data.id; Test-Route "menu-get" "GET" "/menus/$mid"; Test-Route "menu-del" "DELETE" "/menus/$mid" }

Test-Route "fieldcfg-list" "GET" "/field-configs?module=employee"

# === EMPLOYEE ===
Test-Module "Employee Management"
Test-Route "emp-list" "GET" "/employees?page=1&pageSize=5"
$em = Test-Route "emp-create" "POST" "/employees" @{empNo="ET001";name="EmpT1";gender=1;phone="13900000001";email="e1@t.com";deptId=1;positionId=1;companyId=1;entryDate="2026-07-01";status=1}
if ($em -and $em.data) { $eid=$em.data.id; Test-Route "emp-get" "GET" "/employees/$eid"; Test-Route "emp-edit" "PUT" "/employees/$eid" @{empNo="ET001";name="EmpT1u";gender=1;phone="13900000001";email="e1@t.com";deptId=1;positionId=1;companyId=1;entryDate="2026-07-01";status=1}; Test-Route "emp-del" "DELETE" "/employees/$eid" }
Test-Route "emp-batch-del" "DELETE" "/employees/batch" @{ids=@(99999)}

$em2 = Test-Route "emp-create2" "POST" "/employees" @{empNo="ET002";name="TransT";gender=1;phone="13900000002";email="e2@t.com";deptId=1;positionId=1;companyId=1;entryDate="2026-06-01";status=1}
if ($em2 -and $em2.data) { $eid2=$em2.data.id; $tr = Test-Route "transfer-create" "POST" "/transfers" @{employeeId=$eid2;transferType="promote";afterDeptId=1;afterPositionId=1;effectiveDate="2026-07-15";reason="test"}; Test-Route "transfer-list" "GET" "/transfers?page=1&pageSize=5"; if ($tr -and $tr.data) { Test-Route "transfer-get" "GET" "/transfers/$($tr.data.id)"; Test-Route "transfer-revoke" "PUT" "/transfers/$($tr.data.id)/revoke" }; Test-Route "emp-del2" "DELETE" "/employees/$eid2" }
Test-Route "account-open" "POST" "/accounts/open" @{employeeId=99999;username="nx";password="123";roleIds=@(1)}

# === RECRUITMENT ===
Test-Module "Recruitment"
Test-Route "resume-list" "GET" "/resumes?page=1&pageSize=5"
$rs = Test-Route "resume-create" "POST" "/resumes" @{name="RS1";phone="13800000001";email="r1@t.com";gender=1;education="bachelor";school="Tsinghua";major="CS";workYears=3;applyPosition="FE";source="Boss";status="new"}
if ($rs -and $rs.data) { $rid=$rs.data.id; Test-Route "resume-get" "GET" "/resumes/$rid"; Test-Route "resume-edit" "PUT" "/resumes/$rid" @{name="RS1u";phone="13800000001";email="r1@t.com";gender=1;education="bachelor";school="Tsinghua";major="CS";workYears=4;applyPosition="SFE";source="Boss";status="new"}; Test-Route "resume-invite" "POST" "/resumes/$rid/invite" @{interviewTime="2026-07-20 14:00:00";location="RoomA";templateId=1;message="Hi"}; Test-Route "resume-del" "DELETE" "/resumes/$rid" }

Test-Route "interview-list" "GET" "/interviews?page=1&pageSize=5"
$rs2 = Test-Route "resume-create2" "POST" "/resumes" @{name="RS2";phone="13800000002";email="r2@t.com";status="new"}
if ($rs2 -and $rs2.data) { $rid2=$rs2.data.id; $iv = Test-Route "interview-create" "POST" "/interviews" @{resumeId=$rid2;interviewRound=1;interviewerId=1;interviewTime="2026-07-20 10:00:00";location="RoomB"}; if ($iv -and $iv.data) { Test-Route "interview-checkin" "POST" "/interviews/$($iv.data.id)/checkin"; Test-Route "interview-eval" "PUT" "/interviews/$($iv.data.id)/evaluate" @{score=8.0;evaluation="Good";result="pass"}; Test-Route "interview-pass" "PUT" "/interviews/$($iv.data.id)/pass" } }

Test-Route "question-list" "GET" "/questions?page=1&pageSize=5"
$qs = Test-Route "question-create" "POST" "/questions" @{category="technical";difficulty="medium";title="What is Vue3 reactivity?";answer="Proxy-based..."}
if ($qs -and $qs.data) { $qid=$qs.data.id; Test-Route "question-get" "GET" "/questions/$qid"; Test-Route "question-edit" "PUT" "/questions/$qid" @{category="technical";difficulty="hard";title="Deep dive Vue3";answer="Detail..."}; Test-Route "question-del" "DELETE" "/questions/$qid" }

Test-Route "tmpl-list" "GET" "/notify-templates?page=1&pageSize=5"
$tp = Test-Route "tmpl-create" "POST" "/notify-templates" @{name="TPL1";type="email";title="Invite";content="Dear {name}...";status=1}
if ($tp -and $tp.data) { $tpid=$tp.data.id; Test-Route "tmpl-get" "GET" "/notify-templates/$tpid"; Test-Route "tmpl-del" "DELETE" "/notify-templates/$tpid" }

Test-Route "bl-list" "GET" "/blacklists?page=1&pageSize=5"
$bl = Test-Route "bl-create" "POST" "/blacklists" @{name="BL1";phone="13700000000";idCard="310xxx";reason="Fake"}
if ($bl -and $bl.data) { Test-Route "bl-del" "DELETE" "/blacklists/$($bl.data.id)" }

Test-Route "recruit-report" "GET" "/recruitment-reports/summary"

# === ATTENDANCE ===
Test-Module "Attendance"
Test-Route "att-records" "GET" "/attendance-records?page=1&pageSize=5"
Test-Route "att-exceptions" "GET" "/attendance-exceptions?page=1&pageSize=5"
Test-Route "oa-flows" "GET" "/oa-flows?page=1&pageSize=5"

Test-Route "cardrule-list" "GET" "/card-rules"
$cr = Test-Route "cardrule-create" "POST" "/card-rules" @{name="Rule1";minCardCount=2;allowOvertime=$true;status=1}
if ($cr -and $cr.data) { $crid=$cr.data.id; Test-Route "cardrule-get" "GET" "/card-rules/$crid"; Test-Route "cardrule-del" "DELETE" "/card-rules/$crid" }

Test-Route "shift-list" "GET" "/shifts"
$sh = Test-Route "shift-create" "POST" "/shifts" @{name="Shift1";startTime="09:00";endTime="18:00";lateBuffer=10;earlyBuffer=10;status=1}
if ($sh -and $sh.data) { $shid=$sh.data.id; Test-Route "shift-get" "GET" "/shifts/$shid"; Test-Route "shift-del" "DELETE" "/shifts/$shid" }

Test-Route "holiday-list" "GET" "/holidays?year=2026"
$ho = Test-Route "holiday-create" "POST" "/holidays" @{year=2026;name="Hol1";date="2026-12-25";days=1}
if ($ho -and $ho.data) { $hoid=$ho.data.id; Test-Route "holiday-edit" "PUT" "/holidays/$hoid" @{year=2026;name="Hol1u";date="2026-12-25";days=2}; Test-Route "holiday-del" "DELETE" "/holidays/$hoid" }

Test-Route "leave-quota-list" "GET" "/leave-quotas?year=2026"
Test-Route "leave-type-list" "GET" "/leave-types"
$lt = Test-Route "leave-type-create" "POST" "/leave-types" @{name="TestL";code="tl";defaultDays=3;enabled=$true}
if ($lt -and $lt.data) { $ltid=$lt.data.id; Test-Route "leave-type-edit" "PUT" "/leave-types/$ltid" @{name="TestL2";code="tl";defaultDays=5;enabled=$true}; Test-Route "leave-type-del" "DELETE" "/leave-types/$ltid" }

Test-Route "att-report-detail" "GET" "/attendance-reports/detail?dateStart=2026-07-01&dateEnd=2026-07-31"
Test-Route "att-report-summary" "GET" "/attendance-reports/summary?month=2026-07"
Test-Route "att-deductions" "GET" "/attendance-deductions"

# === PERFORMANCE ===
Test-Module "Performance"
Test-Route "pf-level-list" "GET" "/perf-levels"
$pl = Test-Route "pf-level-create" "POST" "/perf-levels" @{name="B+";scoreMin=70;scoreMax=79;coefficient=1.0;sort=3}
if ($pl -and $pl.data) { $plid=$pl.data.id; Test-Route "pf-level-edit" "PUT" "/perf-levels/$plid" @{name="B++";scoreMin=75;scoreMax=84;coefficient=1.1;sort=3}; Test-Route "pf-level-del" "DELETE" "/perf-levels/$plid" }

Test-Route "pf-sal-list" "GET" "/perf-salaries"
$ps = Test-Route "pf-sal-create" "POST" "/perf-salaries" @{levelId=1;salaryRange="3K-5K";sort=1}
if ($ps -and $ps.data) { $psid=$ps.data.id; Test-Route "pf-sal-edit" "PUT" "/perf-salaries/$psid" @{levelId=1;salaryRange="4K-6K";sort=1}; Test-Route "pf-sal-del" "DELETE" "/perf-salaries/$psid" }

Test-Route "pf-plan-list" "GET" "/perf-plans?page=1&pageSize=5"
$pp = Test-Route "pf-plan-create" "POST" "/perf-plans" @{name="Q3Plan";deptId=1;startDate="2026-07-01";endDate="2026-09-30";employeeIds=@(1);status=1}
if ($pp -and $pp.data) { $ppid=$pp.data.id; Test-Route "pf-plan-get" "GET" "/perf-plans/$ppid"; Test-Route "pf-plan-edit" "PUT" "/perf-plans/$ppid" @{name="Q3Plan2";deptId=1;startDate="2026-07-01";endDate="2026-09-30";employeeIds=@(1);status=1}; Test-Route "pf-plan-del" "DELETE" "/perf-plans/$ppid" }

$pp2 = Test-Route "pf-plan-create2" "POST" "/perf-plans" @{name="Q4Plan";deptId=1;startDate="2026-10-01";endDate="2026-12-31";employeeIds=@(1);status=1}
if ($pp2 -and $pp2.data) { $ppid2=$pp2.data.id; $pr = Test-Route "pf-rec-create" "POST" "/perf-records" @{planId=$ppid2;employeeId=1;items=@(@{indicator="Eff";weight=50;score=90},@{indicator="Team";weight=50;score=85});totalScore=87.5;evaluation="Good";levelId=2}; Test-Route "pf-rec-list" "GET" "/perf-records?page=1&pageSize=5"; if ($pr -and $pr.data) { Test-Route "pf-rec-get" "GET" "/perf-records/$($pr.data.id)"; Test-Route "pf-rec-edit" "PUT" "/perf-records/$($pr.data.id)" @{planId=$ppid2;employeeId=1;items=@(@{indicator="Eff";weight=50;score=92},@{indicator="Team";weight=50;score=88});totalScore=90.0;evaluation="Great";levelId=1} }; Test-Route "pf-plan-del2" "DELETE" "/perf-plans/$ppid2" }

Test-Route "pf-rep-detail" "GET" "/perf-reports/detail"
Test-Route "pf-rep-dept" "GET" "/perf-reports/dept-summary"
Test-Route "pf-rep-emp" "GET" "/perf-reports/employee-summary"

# === SALARY ===
Test-Module "Salary"
Test-Route "sal-rules" "GET" "/salary-rules"
Test-Route "sal-fields" "GET" "/salary-fields"
$sf = Test-Route "sal-field-create" "POST" "/salary-fields" @{name="BonusT";code="bt";type="income";formula="base*0.1";sort=99;status=1}
if ($sf -and $sf.data) { $sfid=$sf.data.id; Test-Route "sal-field-edit" "PUT" "/salary-fields/$sfid" @{name="BonusT2";code="bt";type="income";formula="base*0.15";sort=99;status=1}; Test-Route "sal-field-del" "DELETE" "/salary-fields/$sfid" }
Test-Route "sal-adj-list" "GET" "/salary-adjusts?page=1&pageSize=5"
Test-Route "sal-adj-create" "POST" "/salary-adjusts" @{employeeId=1;afterSalary=18000;adjustType="promote";effectiveDate="2026-07-01";remark="test"}
Test-Route "sal-sheet-list" "GET" "/salary-sheets?month=2026-07"
Test-Route "sal-sync" "POST" "/salary-sheets/sync" @{month="2026-07";deptId=1}
Test-Route "sal-gen" "POST" "/salary-sheets/generate" @{month="2026-07";employeeIds=@(1)}

# === COMMON (no auth needed) ===
Test-Module "Common"
Test-Route "opt-education" "GET" "/common/options?type=education" $null $false
Test-Route "opt-gender" "GET" "/common/options?type=gender" $null $false
Test-Route "opt-resume-source" "GET" "/common/options?type=resume_source" $null $false
Test-Route "opt-leave-type" "GET" "/common/options?type=leave_type" $null $false
Test-Route "opt-transfer-type" "GET" "/common/options?type=transfer_type" $null $false

# === SUMMARY ===
$total = $passCount + $failCount
Write-Host ("`n"+"="*60) -ForegroundColor Cyan
Write-Host "  TEST SUMMARY" -ForegroundColor Cyan
Write-Host ("="*60) -ForegroundColor Cyan
Write-Host "  Total : $total" -ForegroundColor White
Write-Host "  PASS  : $passCount" -ForegroundColor Green
Write-Host "  FAIL  : $failCount" -ForegroundColor $(if($failCount -gt 0){"Red"}else{"Green"})
$rate = if($total -gt 0){[math]::Round($passCount/$total*100,1)}else{0}
Write-Host "  Rate  : $rate%" -ForegroundColor Cyan
Write-Host ("="*60) -ForegroundColor Cyan
