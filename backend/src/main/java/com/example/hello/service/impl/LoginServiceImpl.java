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
        // username -> emp
        Emp emp = empMapper.getByUsername(loginDTO.getUsername());

        // if emp is null or password is incorrect, return null
        if (emp == null || !emp.getPassword().equals(loginDTO.getPassword())) {
            return null;
        }

        // create token
        String token = jwtUtils.generateToken(emp.getId(), emp.getUsername());

        // return LoginResponseDTO
        return new LoginResponseDTO(emp.getId(), emp.getUsername(), emp.getName(), token);
    }
}