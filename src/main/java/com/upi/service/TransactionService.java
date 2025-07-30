package com.upi.service;

import com.upi.dto.transaction.TransactionRequest;
import com.upi.dto.transaction.TransactionResponse;
import com.upi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    /**
     * Initiates a new transaction
     */
    TransactionResponse initiateTransaction(User user, TransactionRequest transactionRequest);

    /**
     * Gets a transaction by UTR number
     */
    TransactionResponse getTransactionByUtr(String utr);

    /**
     * Gets all transactions for a user's VPA (sent or received)
     */
    Page<TransactionResponse> getUserTransactions(User user, Pageable pageable);

    /**
     * Gets transactions sent from a user's VPA
     */
    Page<TransactionResponse> getSentTransactions(User user, Pageable pageable);

    /**
     * Gets transactions received by a user's VPA
     */
    Page<TransactionResponse> getReceivedTransactions(User user, Pageable pageable);

    /**
     * Gets recent transactions for a user's VPA (sent or received)
     */
    List<TransactionResponse> getRecentTransactions(User user, int limit);

    /**
     * Gets transactions for a specific VPA (sent or received)
     */
    Page<TransactionResponse> getTransactionsByVpa(User user, String vpaAddress, Pageable pageable);
}