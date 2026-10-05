package com.microservice.archchatmessagingservice.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StandardError {
    private LocalDate timestamp;
    private Integer status;
    private String error;
    private String path;
}
