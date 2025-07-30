package com.upi.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$")
    private String senderVpa;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$")
    private String receiverVpa;

    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4,6}$")
    private String upiPin;

    private String description;

    @NotBlank
    private String transactionType; // P2P, P2M, BILL_PAYMENT, REFUND
}