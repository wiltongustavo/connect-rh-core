package com.connectrh.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
