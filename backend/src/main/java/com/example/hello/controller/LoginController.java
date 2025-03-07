package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.dto.LoginDTO;
import com.example.hello.dto.LoginResponseDTO;
import com.example.hello.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginResponse = loginService.login(loginDTO);
        
        if (loginResponse != null) {
            return Result.success(loginResponse);
        } else {
            return Result.error("用户名或密码错误");
        }
    }
} 