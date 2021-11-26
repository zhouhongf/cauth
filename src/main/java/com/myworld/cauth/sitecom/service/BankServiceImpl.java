package com.myworld.cauth.sitecom.service;

import com.myworld.cauth.sitecom.entity.Bank;
import com.myworld.cauth.sitecom.repository.BankRepository;
import com.myworld.cauth.common.model.ApiResult;
import com.myworld.cauth.common.util.ResultUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BankServiceImpl implements BankService {
    private static Logger log = LogManager.getRootLogger();

    private BankRepository bankRepository;

    @Autowired
    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public ApiResult getBankLocations(String cityname, String bankName) {
        Optional<List<Bank>> banks = bankRepository.findByCitynameStartsWithAndParentName(cityname, bankName);
        return banks.map(ResultUtil::success).orElseGet(() -> ResultUtil.error(-2, "未能找到相关银行网点"));
    }

    @Override
    public Set<String> getBankNamesByCityname(String cityname) {
        Set<String> bankNames = new HashSet<>();
        List<Bank> banks = bankRepository.findByCityname(cityname);
        if (banks.size() < 1) {
            banks = bankRepository.findByCitynameStartingWith(cityname);
        }
        if (banks.size() > 0) {
            for (Bank bank : banks) {
                String parentName = bank.getParentName();
                if (parentName != null && !parentName.equals("")) {
                    bankNames.add(parentName);
                }
            }
        }
        return bankNames;
    }
}
