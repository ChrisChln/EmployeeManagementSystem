package com.example.hello.mapper;

import com.example.hello.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DeptMapper {
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
} 