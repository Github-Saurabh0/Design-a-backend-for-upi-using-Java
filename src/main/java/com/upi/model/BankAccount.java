package com.upi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bank_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal balance;

    @Size(min = 4, max = 6)
    private String upiPin; // Encrypted UPI PIN

    private boolean primary = false;

    private boolean verified = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VirtualPaymentAddress> vpas = new HashSet<>();

    public enum AccountType {
        SAVINGS, CURRENT, CREDIT
    }
}