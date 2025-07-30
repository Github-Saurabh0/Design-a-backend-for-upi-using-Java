package com.upi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String utrNumber; // Unique Transaction Reference Number

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_vpa_id")
    private VirtualPaymentAddress senderVpa;
    
    @Column(name = "sender_vpa_address")
    private String senderVpaAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_vpa_id")
    private VirtualPaymentAddress receiverVpa;
    
    @Column(name = "receiver_vpa_address")
    private String receiverVpaAddress;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String failureReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum TransactionType {
        P2P, // Person to Person
        P2M, // Person to Merchant
        BILL_PAYMENT,
        REFUND
    }

    public enum TransactionStatus {
        INITIATED,
        PROCESSING,
        COMPLETED,
        FAILED,
        REVERSED
    }
}