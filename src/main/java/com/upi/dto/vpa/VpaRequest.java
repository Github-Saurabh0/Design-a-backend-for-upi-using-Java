package com.upi.dto.vpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VpaRequest {

    @NotNull
    private Long bankAccountId;

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    private String username; // Part before @ in VPA

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String handle; // Part after @ in VPA (bank handle)

    private boolean primary;
}