package com.example.hello.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageBean<T> {
    private Long total;
    private List<T> rows;
} 