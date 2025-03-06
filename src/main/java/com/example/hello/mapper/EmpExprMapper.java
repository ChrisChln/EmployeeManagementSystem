package com.example.hello.mapper;

import com.example.hello.entity.EmpExpr;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmpExprMapper {
    List<EmpExpr> getByEmpId(Integer empId);
    
    void insert(EmpExpr empExpr);
    
    void deleteByEmpId(Integer empId);
} 