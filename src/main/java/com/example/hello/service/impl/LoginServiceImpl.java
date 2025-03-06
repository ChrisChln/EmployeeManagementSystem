package com.example.hello.service.impl;

import com.example.hello.dto.LoginDTO;
import com.example.hello.dto.LoginResponseDTO;
import com.example.hello.entity.Emp;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.service.LoginService;
import com.example.hello.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 根据用户名查询员工
        Emp emp = empMapper.getByUsername(loginDTO.getUsername());
        
        // 如果员工不存在或密码不匹配，返回null
        if (emp == null || !emp.getPassword().equals(loginDTO.getPassword())) {
            return null;
        }
        
        // 生成JWT令牌
        String token = jwtUtils.generateToken(emp.getId(), emp.getUsername());
        
        // 封装并返回结果
        return new LoginResponseDTO(emp.getId(), emp.getUsername(), emp.getName(), token);
    }
} 