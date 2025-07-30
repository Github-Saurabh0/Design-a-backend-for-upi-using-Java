package com.upi.repository;

import com.upi.model.Transaction;
import com.upi.model.VirtualPaymentAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByUtrNumber(String utrNumber);

    Page<Transaction> findBySenderVpaAddressOrReceiverVpaAddress(String senderVpaAddress, String receiverVpaAddress, Pageable pageable);

    List<Transaction> findBySenderVpaAddressInOrReceiverVpaAddressInOrderByCreatedAtDesc(List<String> senderVpaAddresses, List<String> receiverVpaAddresses);

    Page<Transaction> findBySenderVpaAddress(String senderVpaAddress, Pageable pageable);

    Page<Transaction> findByReceiverVpaAddress(String receiverVpaAddress, Pageable pageable);
}