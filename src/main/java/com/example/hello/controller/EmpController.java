package com.example.hello.controller;

import com.example.hello.entity.Emp;
import com.example.hello.entity.PageBean;
import com.example.hello.service.EmpService;
import com.example.hello.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/emps")
public class EmpController {
    
    private static final Logger log = LoggerFactory.getLogger(EmpController.class);

    @Autowired
    private EmpService empService;

    @GetMapping
    public Result<PageBean<Emp>> list(
            String name, Integer gender,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean<Emp> pageBean = empService.list(name, gender, begin, end, page, pageSize);
        return Result.success(pageBean);
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam List<Integer> ids) {
        empService.delete(ids);
        return Result.success();
    }

    @PostMapping
    public Result<Void> save(@RequestBody Emp emp) {
        empService.save(emp);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Emp> getById(@PathVariable Integer id) {
        Emp emp = empService.getById(id);
        return Result.success(emp);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Emp emp) {
        log.info("接收到更新员工请求: {}", emp);
        
        try {
            // 如果ID为空，尝试通过用户名查找员工
            if (emp.getId() == null && emp.getUsername() != null) {
                Emp existingEmp = empService.getByUsername(emp.getUsername());
                if (existingEmp != null) {
                    emp.setId(existingEmp.getId());
                    log.info("通过用户名找到员工ID: {}", existingEmp.getId());
                } else {
                    return Result.error("更新失败: 找不到用户名为 " + emp.getUsername() + " 的员工");
                }
            }
            
            // 检查ID是否存在
            if (emp.getId() == null) {
                return Result.error("更新失败: 缺少员工ID");
            }
            
            empService.update(emp);
            return Result.success();
        } catch (Exception e) {
            log.error("更新员工失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Emp>> list() {
        List<Emp> list = empService.list();
        return Result.success(list);
    }
} 