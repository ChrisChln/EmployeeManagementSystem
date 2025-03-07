package com.example.hello.service;

import com.example.hello.entity.Emp;
import com.example.hello.entity.PageBean;
import java.time.LocalDate;
import java.util.List;

public interface EmpService {
    PageBean<Emp> list(String name, Integer gender, LocalDate begin, LocalDate end, 
                      Integer page, Integer pageSize);
    
    void delete(List<Integer> ids);
    
    void save(Emp emp);
    
    Emp getById(Integer id);
    
    void update(Emp emp);
    
    List<Emp> list();
    
    int count();
    
    /**
     * 根据用户名查询员工
     */
    Emp getByUsername(String username);
} 