package com.market.ecommerce.controller;

import com.market.ecommerce.dto.AddressRequest;
import com.market.ecommerce.entity.Address;
import com.market.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(request));
    }

    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses() {
        return ResponseEntity.ok(addressService.getUserAddresses());
    }
}