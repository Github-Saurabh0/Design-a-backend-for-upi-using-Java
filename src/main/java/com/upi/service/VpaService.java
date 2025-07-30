package com.upi.service;

import com.upi.dto.MessageResponse;
import com.upi.dto.vpa.VpaRequest;
import com.upi.dto.vpa.VpaResponse;
import com.upi.model.User;

import java.util.List;

public interface VpaService {

    List<VpaResponse> getAllVpas(User user);

    VpaResponse getVpaById(User user, Long id);

    VpaResponse getVpaByAddress(String address);

    VpaResponse createVpa(User user, VpaRequest vpaRequest);

    VpaResponse updateVpa(User user, Long id, VpaRequest vpaRequest);

    MessageResponse deleteVpa(User user, Long id);

    VpaResponse setPrimaryVpa(User user, Long id);

    List<VpaResponse> getVpasByBankAccount(User user, Long bankAccountId);

    boolean validateVpa(String vpaAddress);
}