package com.upi.repository;

import com.upi.model.BankAccount;
import com.upi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByUser(User user);

    Optional<BankAccount> findByUserAndId(User user, Long id);

    Optional<BankAccount> findByUserAndPrimaryIsTrue(User user);

    Optional<BankAccount> findByAccountNumberAndIfscCode(String accountNumber, String ifscCode);

    boolean existsByAccountNumberAndIfscCode(String accountNumber, String ifscCode);
}