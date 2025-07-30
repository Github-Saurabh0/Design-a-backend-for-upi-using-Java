package com.upi.service.impl;

import com.upi.dto.MessageResponse;
import com.upi.dto.bank.BankAccountRequest;
import com.upi.dto.bank.BankAccountResponse;
import com.upi.model.BankAccount;
import com.upi.model.User;
import com.upi.repository.BankAccountRepository;
import com.upi.service.BankAccountService;
import com.upi.util.MaskingUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<BankAccountResponse> getAllBankAccounts(User user) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser(user);
        return bankAccounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountResponse getBankAccountById(User user, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
        return convertToDto(bankAccount);
    }

    @Override
    @Transactional
    public BankAccountResponse addBankAccount(User user, BankAccountRequest bankAccountRequest) {
        // Check if account already exists
        if (bankAccountRepository.existsByAccountNumberAndIfscCode(
                bankAccountRequest.getAccountNumber(), bankAccountRequest.getIfscCode())) {
            throw new RuntimeException("This bank account is already linked with UPI");
        }

        // Create new bank account
        BankAccount bankAccount = BankAccount.builder()
                .user(user)
                .bankName(bankAccountRequest.getBankName())
                .accountHolderName(bankAccountRequest.getAccountHolderName())
                .accountNumber(bankAccountRequest.getAccountNumber())
                .ifscCode(bankAccountRequest.getIfscCode())
                .accountType(BankAccount.AccountType.valueOf(bankAccountRequest.getAccountType()))
                .balance(new BigDecimal("10000.00")) // Mock balance for demo
                .upiPin(passwordEncoder.encode(bankAccountRequest.getUpiPin()))
                .primary(bankAccountRequest.isPrimary())
                .build();

        // If this is the first account or marked as primary, ensure it's set as primary
        if (bankAccountRequest.isPrimary() || bankAccountRepository.findByUser(user).isEmpty()) {
            // Reset primary flag on all other accounts
            bankAccountRepository.findByUserAndPrimaryIsTrue(user)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        bankAccountRepository.save(existingPrimary);
                    });
            bankAccount.setPrimary(true);
        }

        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        return convertToDto(savedBankAccount);
    }

    @Override
    @Transactional
    public BankAccountResponse updateBankAccount(User user, Long id, BankAccountRequest bankAccountRequest) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        // Update fields
        bankAccount.setBankName(bankAccountRequest.getBankName());
        bankAccount.setAccountHolderName(bankAccountRequest.getAccountHolderName());
        bankAccount.setAccountType(BankAccount.AccountType.valueOf(bankAccountRequest.getAccountType()));

        // Update UPI PIN if provided
        if (bankAccountRequest.getUpiPin() != null && !bankAccountRequest.getUpiPin().isEmpty()) {
            bankAccount.setUpiPin(passwordEncoder.encode(bankAccountRequest.getUpiPin()));
        }

        // Handle primary flag
        if (bankAccountRequest.isPrimary() && !bankAccount.isPrimary()) {
            // Reset primary flag on all other accounts
            bankAccountRepository.findByUserAndPrimaryIsTrue(user)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        bankAccountRepository.save(existingPrimary);
                    });
            bankAccount.setPrimary(true);
        }

        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);
        return convertToDto(updatedBankAccount);
    }

    @Override
    @Transactional
    public MessageResponse deleteBankAccount(User user, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        // Check if this is the primary account
        if (bankAccount.isPrimary()) {
            throw new RuntimeException("Cannot delete primary bank account. Please set another account as primary first.");
        }

        // Check if there are any active VPAs linked to this account
        if (!bankAccount.getVpas().isEmpty()) {
            throw new RuntimeException("Cannot delete bank account with active VPAs. Please delete the VPAs first.");
        }

        bankAccountRepository.delete(bankAccount);
        return new MessageResponse("Bank account deleted successfully", true);
    }

    @Override
    @Transactional
    public BankAccountResponse setPrimaryBankAccount(User user, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        // Reset primary flag on all other accounts
        bankAccountRepository.findByUserAndPrimaryIsTrue(user)
                .ifPresent(existingPrimary -> {
                    existingPrimary.setPrimary(false);
                    bankAccountRepository.save(existingPrimary);
                });

        // Set this account as primary
        bankAccount.setPrimary(true);
        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);

        return convertToDto(updatedBankAccount);
    }

    @Override
    @Transactional
    public MessageResponse verifyBankAccount(User user, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        // In a real implementation, this would involve a verification process with the bank
        // For demo purposes, we'll just mark it as verified
        bankAccount.setVerified(true);
        bankAccountRepository.save(bankAccount);

        return new MessageResponse("Bank account verified successfully", true);
    }

    @Override
    public BigDecimal getBankBalance(User user, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        // In a real implementation, this would fetch the balance from the bank's API
        return bankAccount.getBalance();
    }

    @Override
    public MessageResponse validateUpiPin(User user, Long bankAccountId, String upiPin) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, bankAccountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (passwordEncoder.matches(upiPin, bankAccount.getUpiPin())) {
            return new MessageResponse("UPI PIN validated successfully", true);
        } else {
            return new MessageResponse("Invalid UPI PIN", false);
        }
    }

    private BankAccountResponse convertToDto(BankAccount bankAccount) {
        BankAccountResponse response = modelMapper.map(bankAccount, BankAccountResponse.class);
        // Mask account number for security
        response.setAccountNumber(MaskingUtil.maskAccountNumber(bankAccount.getAccountNumber()));
        return response;
    }
}