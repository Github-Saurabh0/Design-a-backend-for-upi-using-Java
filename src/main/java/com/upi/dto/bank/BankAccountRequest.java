package com.upi.dto.bank;

import com.upi.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {

    @NotBlank
    @Size(max = 100)
    private String bankName;

    @NotBlank
    @Size(max = 100)
    private String accountHolderName;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[0-9]{8,20}$")
    private String accountNumber;

    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$")
    private String ifscCode;

    @NotBlank
    private String accountType; // SAVINGS, CURRENT, CREDIT

    @NotBlank
    @Size(min = 4, max = 6)
    @Pattern(regexp = "^[0-9]{4,6}$")
    private String upiPin;

    private boolean primary;
}