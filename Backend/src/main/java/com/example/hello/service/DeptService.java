package com.example.hello.service;

import com.example.hello.entity.Dept;
import java.util.List;

public interface DeptService {
    /**
     * Query all departments
     */
    List<Dept> list();
    
    /**
     * Query department by ID
     */
    Dept getById(Integer id);
    
    /**
     * Add a department
     */
    void add(Dept dept);
    
    /**
     * Modify a department
     */
    void update(Dept dept);
    
    /**
     * Delete a department
     */
    void delete(Integer id);

    /**
     * Count the number of departments
     */
    int count();
} 