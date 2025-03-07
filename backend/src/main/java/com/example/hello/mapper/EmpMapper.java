package com.example.hello.mapper;

import com.example.hello.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EmpMapper {
    List<Emp> list(@Param("name") String name, 
                   @Param("gender") Integer gender,
                   @Param("begin") LocalDate begin,
                   @Param("end") LocalDate end);
    
    void delete(@Param("ids") List<Integer> ids);
    
    void insert(Emp emp);
    
    Emp getById(Integer id);
    
    int update(Emp emp);
    
    List<Emp> listAll();
    
    Emp getByUsername(String username);
} 