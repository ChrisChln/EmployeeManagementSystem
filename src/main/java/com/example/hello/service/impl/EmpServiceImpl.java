package com.example.hello.service.impl;

import com.example.hello.entity.Emp;
import com.example.hello.entity.EmpExpr;
import com.example.hello.entity.PageBean;
import com.example.hello.mapper.EmpExprMapper;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.service.EmpService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {
    
    private static final Logger log = LoggerFactory.getLogger(EmpServiceImpl.class);
    
    @Autowired
    private EmpMapper empMapper;
    
    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public PageBean<Emp> list(String name, Integer gender, LocalDate begin, 
                             LocalDate end, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Emp> empList = empMapper.list(name, gender, begin, end);
        Page<Emp> p = (Page<Emp>) empList;
        
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        empMapper.delete(ids);
        for (Integer id : ids) {
            empExprMapper.deleteByEmpId(id);
        }
    }

    @Override
    @Transactional
    public void save(Emp emp) {
        // Save employee basic information
        empMapper.insert(emp);
        
        // Save work experience
        if (emp.getExprList() != null) {
            for (EmpExpr expr : emp.getExprList()) {
                expr.setEmpId(emp.getId());
                empExprMapper.insert(expr);
            }
        }
    }

    @Override
    public Emp getById(Integer id) {
        Emp emp = empMapper.getById(id);
        if (emp != null) {
            emp.setExprList(empExprMapper.getByEmpId(id));
        }
        return emp;
    }

    @Override
    @Transactional
    public void update(Emp emp) {
        try {
            // 记录更新前的数据
            log.info("更新员工信息，ID: {}, 数据: {}", emp.getId(), emp);
            
            // 验证ID是否存在
            if (emp.getId() == null) {
                log.error("更新员工信息失败：ID为空");
                throw new IllegalArgumentException("员工ID不能为空");
            }
            
            // 检查员工是否存在
            Emp existingEmp = empMapper.getById(emp.getId());
            if (existingEmp == null) {
                log.error("更新员工信息失败：ID为{}的员工不存在", emp.getId());
                throw new IllegalArgumentException("员工不存在");
            }
            
            // 如果没有提供某些字段，使用原来的值
            if (emp.getPhone() == null) {
                emp.setPhone(existingEmp.getPhone());
            }
            
            // 更新员工基本信息
            int rows = empMapper.update(emp);
            log.info("更新员工基本信息影响行数: {}", rows);
            
            // 删除原有工作经历
            empExprMapper.deleteByEmpId(emp.getId());
            
            // 添加新的工作经历
            if (emp.getExprList() != null && !emp.getExprList().isEmpty()) {
                for (EmpExpr expr : emp.getExprList()) {
                    expr.setEmpId(emp.getId());
                    empExprMapper.insert(expr);
                }
                log.info("更新员工工作经历: {} 条记录", emp.getExprList().size());
            }
        } catch (Exception e) {
            // 记录异常信息
            log.error("更新员工信息失败", e);
            throw e;
        }
    }

    @Override
    public List<Emp> list() {
        return empMapper.listAll();
    }

    @Override
    public int count() {
        return empMapper.listAll().size();
    }

    @Override
    public Emp getByUsername(String username) {
        return empMapper.getByUsername(username);
    }
} 