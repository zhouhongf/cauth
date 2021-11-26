package com.myworld.cauth.sitecom.controller;

import com.myworld.cauth.sitecom.service.BankService;
import com.myworld.cauth.common.model.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankController {

    private BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/locations")
    public ApiResult getBankLocations(@RequestParam String cityname, @RequestParam String bankName) {
        return bankService.getBankLocations(cityname, bankName);
    }
}
