package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.service.DeptService;
import com.example.hello.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatisticsController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private EmpService empService;

    /**
     * 获取首页统计数据
     * @return 包含各种统计数量的Map
     */
    @GetMapping("/statistics")
    public Result<Map<String, Integer>> getStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        
        // 由于没有班级和学员服务，这里使用模拟数据
        statistics.put("classCount", 12);
        statistics.put("studentCount", 256);
        
        // 获取部门数量
        int deptCount = deptService.list().size();
        statistics.put("deptCount", deptCount);
        
        // 获取员工数量
        int empCount = empService.list().size();
        statistics.put("empCount", empCount);
        
        return Result.success(statistics);
    }
} 