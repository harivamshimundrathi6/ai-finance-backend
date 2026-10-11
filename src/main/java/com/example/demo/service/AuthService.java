package com.example.demo.service;

import com.example.demo.entity.Merchant;
import com.example.demo.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final MerchantRepository merchantRepo;

    public AuthService(MerchantRepository merchantRepo) {
        this.merchantRepo = merchantRepo;
    }

    public Merchant register(String email, String password, String businessName) {
        if (merchantRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Merchant with this email already exists");
        }
        return merchantRepo.save(new Merchant(email, password, businessName));
    }

    public Merchant login(String email, String password) {
        Optional<Merchant> merchantOpt = merchantRepo.findByEmail(email);
        if (merchantOpt.isPresent() && merchantOpt.get().getPassword().equals(password)) {
            return merchantOpt.get();
        }
        throw new RuntimeException("Invalid email or password");
    }
}