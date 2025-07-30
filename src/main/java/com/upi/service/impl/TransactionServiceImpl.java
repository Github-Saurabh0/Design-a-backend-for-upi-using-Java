package com.upi.service.impl;

import com.upi.dto.transaction.TransactionRequest;
import com.upi.dto.transaction.TransactionResponse;
import com.upi.model.BankAccount;
import com.upi.model.Transaction;
import com.upi.model.Transaction.TransactionStatus;
import com.upi.model.Transaction.TransactionType;
import com.upi.model.User;
import com.upi.model.VirtualPaymentAddress;
import com.upi.repository.BankAccountRepository;
import com.upi.repository.TransactionRepository;
import com.upi.repository.VirtualPaymentAddressRepository;
import com.upi.service.BankAccountService;
import com.upi.service.TransactionService;
import com.upi.service.VpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private VirtualPaymentAddressRepository vpaRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private VpaService vpaService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public TransactionResponse initiateTransaction(User user, TransactionRequest transactionRequest) {
        // Validate sender VPA belongs to the user
        VirtualPaymentAddress senderVpa = vpaRepository.findByAddress(transactionRequest.getSenderVpa())
                .orElseThrow(() -> new RuntimeException("Sender VPA not found"));

        if (!senderVpa.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to use this VPA");
        }

        // Validate receiver VPA exists
        if (!vpaService.validateVpa(transactionRequest.getReceiverVpa())) {
            throw new RuntimeException("Receiver VPA is invalid or does not exist");
        }

        VirtualPaymentAddress receiverVpa = vpaRepository.findByAddress(transactionRequest.getReceiverVpa())
                .orElseThrow(() -> new RuntimeException("Receiver VPA not found"));

        // Validate UPI PIN
        BankAccount senderBankAccount = senderVpa.getBankAccount();
        if (!bankAccountService.validateUpiPin(user, senderBankAccount.getId(), transactionRequest.getUpiPin()).isSuccess()) {
            throw new RuntimeException("Invalid UPI PIN");
        }

        // Check if sender has sufficient balance
        BigDecimal senderBalance = bankAccountService.getBankBalance(user, senderBankAccount.getId());
        if (senderBalance.compareTo(transactionRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Generate UTR number
        String utr = generateUtrNumber();

        // Create transaction
        Transaction transaction = Transaction.builder()
                .utrNumber(utr)
                .senderVpa(senderVpa)
                .senderVpaAddress(transactionRequest.getSenderVpa())
                .receiverVpa(receiverVpa)
                .receiverVpaAddress(transactionRequest.getReceiverVpa())
                .amount(transactionRequest.getAmount())
                .description(transactionRequest.getDescription())
                .type(transactionRequest.getTransactionType() != null ? TransactionType.valueOf(transactionRequest.getTransactionType()) : TransactionType.P2P)
                .status(TransactionStatus.INITIATED)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Process the transaction (in a real system, this would involve communication with banks)
        try {
            // Mock bank transfer
            // Deduct from sender's account
            senderBankAccount.setBalance(senderBankAccount.getBalance().subtract(transactionRequest.getAmount()));
            bankAccountRepository.save(senderBankAccount);

            // Add to receiver's account
            BankAccount receiverBankAccount = receiverVpa.getBankAccount();
            receiverBankAccount.setBalance(receiverBankAccount.getBalance().add(transactionRequest.getAmount()));
            bankAccountRepository.save(receiverBankAccount);

            // Update transaction status
            savedTransaction.setStatus(TransactionStatus.COMPLETED);
            savedTransaction.setCompletedAt(LocalDateTime.now());
            savedTransaction = transactionRepository.save(savedTransaction);
        } catch (Exception e) {
            // Handle failure
            savedTransaction.setStatus(TransactionStatus.FAILED);
            savedTransaction.setFailureReason(e.getMessage());
            savedTransaction = transactionRepository.save(savedTransaction);
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }

        return convertToDto(savedTransaction);
    }

    @Override
    public TransactionResponse getTransactionByUtr(String utr) {
        Transaction transaction = transactionRepository.findByUtrNumber(utr)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return convertToDto(transaction);
    }

    @Override
    public Page<TransactionResponse> getUserTransactions(User user, Pageable pageable) {
        // Get all VPAs belonging to the user
        List<VirtualPaymentAddress> userVpas = vpaRepository.findByUser(user);
        List<String> userVpaAddresses = userVpas.stream()
                .map(VirtualPaymentAddress::getAddress)
                .collect(Collectors.toList());

        // This method needs to be implemented in the repository
        // For now, we'll use a workaround by getting all transactions and filtering
        Page<Transaction> allTransactions = transactionRepository.findAll(pageable);
        
        // Filter transactions where user is either sender or receiver
        List<Transaction> filteredTransactions = allTransactions.getContent().stream()
                .filter(t -> userVpaAddresses.contains(t.getSenderVpaAddress()) || 
                             userVpaAddresses.contains(t.getReceiverVpaAddress()))
                .collect(Collectors.toList());
        
        // Create a new page with the filtered transactions
        Page<Transaction> transactions = new PageImpl<>(filteredTransactions, pageable, filteredTransactions.size());

        List<TransactionResponse> transactionResponses = transactions.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
    }

    @Override
    public Page<TransactionResponse> getSentTransactions(User user, Pageable pageable) {
        // Get all VPAs belonging to the user
        List<VirtualPaymentAddress> userVpas = vpaRepository.findByUser(user);
        List<String> userVpaAddresses = userVpas.stream()
                .map(VirtualPaymentAddress::getAddress)
                .collect(Collectors.toList());

        // This method needs to be implemented in the repository
        // For now, we'll use a workaround by getting all transactions and filtering
        Page<Transaction> allTransactions = transactionRepository.findAll(pageable);
        
        // Filter transactions where user is sender
        List<Transaction> filteredTransactions = allTransactions.getContent().stream()
                .filter(t -> userVpaAddresses.contains(t.getSenderVpaAddress()))
                .collect(Collectors.toList());
        
        // Create a new page with the filtered transactions
        Page<Transaction> transactions = new PageImpl<>(filteredTransactions, pageable, filteredTransactions.size());

        List<TransactionResponse> transactionResponses = transactions.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
    }

    @Override
    public Page<TransactionResponse> getReceivedTransactions(User user, Pageable pageable) {
        // Get all VPAs belonging to the user
        List<VirtualPaymentAddress> userVpas = vpaRepository.findByUser(user);
        List<String> userVpaAddresses = userVpas.stream()
                .map(VirtualPaymentAddress::getAddress)
                .collect(Collectors.toList());

        // This method needs to be implemented in the repository
        // For now, we'll use a workaround by getting all transactions and filtering
        Page<Transaction> allTransactions = transactionRepository.findAll(pageable);
        
        // Filter transactions where user is receiver
        List<Transaction> filteredTransactions = allTransactions.getContent().stream()
                .filter(t -> userVpaAddresses.contains(t.getReceiverVpaAddress()))
                .collect(Collectors.toList());
        
        // Create a new page with the filtered transactions
        Page<Transaction> transactions = new PageImpl<>(filteredTransactions, pageable, filteredTransactions.size());

        List<TransactionResponse> transactionResponses = transactions.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
    }

    @Override
    public List<TransactionResponse> getRecentTransactions(User user, int limit) {
        // Get all VPAs belonging to the user
        List<VirtualPaymentAddress> userVpas = vpaRepository.findByUser(user);
        List<String> userVpaAddresses = userVpas.stream()
                .map(VirtualPaymentAddress::getAddress)
                .collect(Collectors.toList());

        // Get recent transactions where user is either sender or receiver
        List<Transaction> transactions = transactionRepository.findBySenderVpaAddressInOrReceiverVpaAddressInOrderByCreatedAtDesc(
                userVpaAddresses, userVpaAddresses);

        // Limit the results
        transactions = transactions.stream().limit(limit).collect(Collectors.toList());

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> getTransactionsByVpa(User user, String vpaAddress, Pageable pageable) {
        // Validate VPA belongs to the user
        VirtualPaymentAddress vpa = vpaRepository.findByAddress(vpaAddress)
                .orElseThrow(() -> new RuntimeException("VPA not found"));

        if (!vpa.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view transactions for this VPA");
        }

        // Get transactions where the specified VPA is either sender or receiver
        Page<Transaction> transactions = transactionRepository.findBySenderVpaAddressOrReceiverVpaAddress(
                vpaAddress, vpaAddress, pageable);

        List<TransactionResponse> transactionResponses = transactions.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
    }

    private String generateUtrNumber() {
        // Generate a unique UTR number
        return "UPI" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16).toUpperCase();
    }

    private TransactionResponse convertToDto(Transaction transaction) {
        return modelMapper.map(transaction, TransactionResponse.class);
    }
}