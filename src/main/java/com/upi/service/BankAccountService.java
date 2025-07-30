package com.upi.service;

import com.upi.dto.MessageResponse;
import com.upi.dto.bank.BankAccountRequest;
import com.upi.dto.bank.BankAccountResponse;
import com.upi.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface BankAccountService {

    List<BankAccountResponse> getAllBankAccounts(User user);

    BankAccountResponse getBankAccountById(User user, Long id);

    BankAccountResponse addBankAccount(User user, BankAccountRequest bankAccountRequest);

    BankAccountResponse updateBankAccount(User user, Long id, BankAccountRequest bankAccountRequest);

    MessageResponse deleteBankAccount(User user, Long id);

    BankAccountResponse setPrimaryBankAccount(User user, Long id);

    MessageResponse verifyBankAccount(User user, Long id);

    BigDecimal getBankBalance(User user, Long id);

    MessageResponse validateUpiPin(User user, Long bankAccountId, String upiPin);
}