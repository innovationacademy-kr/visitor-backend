package com.ftseoul.visitor.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FrontErrorResponseDto implements Serializable {
    private String message;
}