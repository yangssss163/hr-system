package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.system.dto.PositionVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.entity.SysPosition;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.mapper.SysPositionMapper;
import com.hr.module.system.service.SysPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPositionServiceImpl implements SysPositionService {

    private final SysPositionMapper sysPositionMapper;
    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<PositionVO> list(Integer page, Integer pageSize, Long deptId, String keyword) {
        LambdaQueryWrapper<SysPosition> wrapper = buildQuery(deptId, keyword);

        Page<SysPosition> p = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        Page<SysPosition> result = sysPositionMapper.selectPage(p, wrapper);

        return toVOList(result.getRecords());
    }

    @Override
    public Long count(Integer page, Integer pageSize, Long deptId, String keyword) {
        LambdaQueryWrapper<SysPosition> wrapper = buildQuery(deptId, keyword);
        return sysPositionMapper.selectCount(wrapper);
    }

    private LambdaQueryWrapper<SysPosition> buildQuery(Long deptId, String keyword) {
        LambdaQueryWrapper<SysPosition> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(SysPosition::getDeptId, deptId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysPosition::getName, keyword);
        }
        wrapper.orderByAsc(SysPosition::getSort);
        return wrapper;
    }

    private List<PositionVO> toVOList(List<SysPosition> positions) {
        List<Long> deptIds = positions.stream()
                .map(SysPosition::getDeptId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));

        return positions.stream().map(pos -> {
            PositionVO vo = new PositionVO();
            vo.setId(pos.getId());
            vo.setName(pos.getName());
            vo.setDeptId(pos.getDeptId());
            vo.setDeptName(deptNameMap.getOrDefault(pos.getDeptId(), ""));
            vo.setSort(pos.getSort());
            vo.setStatus(pos.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SysPosition getById(Long id) {
        return sysPositionMapper.selectById(id);
    }

    @Override
    public void create(SysPosition position) {
        sysPositionMapper.insert(position);
    }

    @Override
    public void update(SysPosition position) {
        sysPositionMapper.updateById(position);
    }

    @Override
    public void delete(Long id) {
        sysPositionMapper.deleteById(id);
    }
}
