package com.example.hello.service;

import com.example.hello.dto.LoginDTO;
import com.example.hello.dto.LoginResponseDTO;

public interface LoginService {
    /**
     * 员工登录
     */
    LoginResponseDTO login(LoginDTO loginDTO);
} 