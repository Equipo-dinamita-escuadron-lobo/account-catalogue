package com.chartaccounts.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.chartaccounts.application.input.IChartAccountsCreateInput;

@Service
public class ChartAccountsCreateService implements IChartAccountsCreateInput  {

    @Autowired
    private IChartAccountsCreateInput chartAccountsCreateInput;

    
}
