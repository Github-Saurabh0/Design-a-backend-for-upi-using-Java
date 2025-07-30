package com.upi.service.impl;

import com.upi.dto.MessageResponse;
import com.upi.dto.vpa.VpaRequest;
import com.upi.dto.vpa.VpaResponse;
import com.upi.model.BankAccount;
import com.upi.model.User;
import com.upi.model.VirtualPaymentAddress;
import com.upi.repository.BankAccountRepository;
import com.upi.repository.VirtualPaymentAddressRepository;
import com.upi.service.VpaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VpaServiceImpl implements VpaService {

    @Autowired
    private VirtualPaymentAddressRepository vpaRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Pattern VPA_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$");

    @Override
    public List<VpaResponse> getAllVpas(User user) {
        List<VirtualPaymentAddress> vpas = vpaRepository.findByUser(user);
        return vpas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public VpaResponse getVpaById(User user, Long id) {
        VirtualPaymentAddress vpa = vpaRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("VPA not found"));
        return convertToDto(vpa);
    }

    @Override
    public VpaResponse getVpaByAddress(String address) {
        VirtualPaymentAddress vpa = vpaRepository.findByAddress(address)
                .orElseThrow(() -> new RuntimeException("VPA not found"));
        return convertToDto(vpa);
    }

    @Override
    @Transactional
    public VpaResponse createVpa(User user, VpaRequest vpaRequest) {
        // Validate bank account
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, vpaRequest.getBankAccountId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (!bankAccount.isVerified()) {
            throw new RuntimeException("Bank account is not verified. Please verify your bank account first.");
        }

        // Create VPA address
        String vpaAddress = vpaRequest.getUsername() + "@" + vpaRequest.getHandle();

        // Check if VPA already exists
        if (vpaRepository.existsByAddress(vpaAddress)) {
            throw new RuntimeException("This VPA is already taken. Please choose a different username or handle.");
        }

        // Create new VPA
        VirtualPaymentAddress vpa = VirtualPaymentAddress.builder()
                .user(user)
                .bankAccount(bankAccount)
                .address(vpaAddress)
                .primary(vpaRequest.isPrimary())
                .active(true)
                .build();

        // If this is the first VPA or marked as primary, ensure it's set as primary
        if (vpaRequest.isPrimary() || vpaRepository.findByUser(user).isEmpty()) {
            // Reset primary flag on all other VPAs
            vpaRepository.findByUserAndPrimaryIsTrue(user)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        vpaRepository.save(existingPrimary);
                    });
            vpa.setPrimary(true);
        }

        VirtualPaymentAddress savedVpa = vpaRepository.save(vpa);
        return convertToDto(savedVpa);
    }

    @Override
    @Transactional
    public VpaResponse updateVpa(User user, Long id, VpaRequest vpaRequest) {
        VirtualPaymentAddress vpa = vpaRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("VPA not found"));

        // Check if bank account is changing
        if (!vpa.getBankAccount().getId().equals(vpaRequest.getBankAccountId())) {
            BankAccount newBankAccount = bankAccountRepository.findByUserAndId(user, vpaRequest.getBankAccountId())
                    .orElseThrow(() -> new RuntimeException("Bank account not found"));

            if (!newBankAccount.isVerified()) {
                throw new RuntimeException("Bank account is not verified. Please verify your bank account first.");
            }

            vpa.setBankAccount(newBankAccount);
        }

        // Check if VPA address is changing
        String newVpaAddress = vpaRequest.getUsername() + "@" + vpaRequest.getHandle();
        if (!vpa.getAddress().equals(newVpaAddress)) {
            // Check if new VPA already exists
            if (vpaRepository.existsByAddress(newVpaAddress)) {
                throw new RuntimeException("This VPA is already taken. Please choose a different username or handle.");
            }
            vpa.setAddress(newVpaAddress);
        }

        // Handle primary flag
        if (vpaRequest.isPrimary() && !vpa.isPrimary()) {
            // Reset primary flag on all other VPAs
            vpaRepository.findByUserAndPrimaryIsTrue(user)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        vpaRepository.save(existingPrimary);
                    });
            vpa.setPrimary(true);
        }

        VirtualPaymentAddress updatedVpa = vpaRepository.save(vpa);
        return convertToDto(updatedVpa);
    }

    @Override
    @Transactional
    public MessageResponse deleteVpa(User user, Long id) {
        VirtualPaymentAddress vpa = vpaRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("VPA not found"));

        // Check if this is the primary VPA and user has other VPAs
        if (vpa.isPrimary() && vpaRepository.findByUser(user).size() > 1) {
            throw new RuntimeException("Cannot delete primary VPA. Please set another VPA as primary first.");
        }

        vpaRepository.delete(vpa);
        return new MessageResponse("VPA deleted successfully", true);
    }

    @Override
    @Transactional
    public VpaResponse setPrimaryVpa(User user, Long id) {
        VirtualPaymentAddress vpa = vpaRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new RuntimeException("VPA not found"));

        // Reset primary flag on all other VPAs
        vpaRepository.findByUserAndPrimaryIsTrue(user)
                .ifPresent(existingPrimary -> {
                    existingPrimary.setPrimary(false);
                    vpaRepository.save(existingPrimary);
                });

        // Set this VPA as primary
        vpa.setPrimary(true);
        VirtualPaymentAddress updatedVpa = vpaRepository.save(vpa);

        return convertToDto(updatedVpa);
    }

    @Override
    public List<VpaResponse> getVpasByBankAccount(User user, Long bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findByUserAndId(user, bankAccountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        List<VirtualPaymentAddress> vpas = vpaRepository.findByBankAccount(bankAccount);
        return vpas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateVpa(String vpaAddress) {
        if (!VPA_PATTERN.matcher(vpaAddress).matches()) {
            return false;
        }

        return vpaRepository.existsByAddress(vpaAddress);
    }

    private VpaResponse convertToDto(VirtualPaymentAddress vpa) {
        return modelMapper.map(vpa, VpaResponse.class);
    }
}