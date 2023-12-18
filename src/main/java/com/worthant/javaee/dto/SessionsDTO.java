package com.worthant.javaee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionsDTO {
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
}

