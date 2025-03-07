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
            // record log
            log.info("update employee info,ID: {}, data: {}", emp.getId(), emp);

            // verify employee ID
            if (emp.getId() == null) {
                log.error("update employee info failed: employee ID is required");
                throw new IllegalArgumentException("employee ID is required");
            }

            // check if the employee exists
            Emp existingEmp = empMapper.getById(emp.getId());
            if (existingEmp == null) {
                log.error("updata fail：ID{}employee does not exist", emp.getId());
                throw new IllegalArgumentException("employee does not exist");
            }

            // if the name is empty, use the original name
            if (emp.getPhone() == null) {
                emp.setPhone(existingEmp.getPhone());
            }

            // update employee basic information
            int rows = empMapper.update(emp);
            log.info("更新员工基本信息影响行数: {}", rows);

            // delete the original work experience
            empExprMapper.deleteByEmpId(emp.getId());

            // add new work experience
            if (emp.getExprList() != null && !emp.getExprList().isEmpty()) {
                for (EmpExpr expr : emp.getExprList()) {
                    expr.setEmpId(emp.getId());
                    empExprMapper.insert(expr);
                }
                log.info("update: {} record", emp.getExprList().size());
            }
        } catch (Exception e) {
            // record log
            log.error("fail", e);
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