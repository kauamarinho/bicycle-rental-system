package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.AddressResponse;
import org.example.service.AddressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/{cep}")
    public AddressResponse get(@PathVariable String cep) {
        return AddressResponse.from(addressService.findByCep(cep));
    }
}
