package com.upi.repository;

import com.upi.model.BankAccount;
import com.upi.model.User;
import com.upi.model.VirtualPaymentAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualPaymentAddressRepository extends JpaRepository<VirtualPaymentAddress, Long> {

    List<VirtualPaymentAddress> findByUser(User user);

    List<VirtualPaymentAddress> findByBankAccount(BankAccount bankAccount);

    Optional<VirtualPaymentAddress> findByAddress(String address);

    Optional<VirtualPaymentAddress> findByUserAndId(User user, Long id);

    Optional<VirtualPaymentAddress> findByUserAndPrimaryIsTrue(User user);

    boolean existsByAddress(String address);
}