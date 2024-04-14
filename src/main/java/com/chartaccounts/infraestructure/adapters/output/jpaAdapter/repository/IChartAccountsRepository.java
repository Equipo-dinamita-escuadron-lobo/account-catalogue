package com.chartaccounts.infraestructure.adapters.output.jpaAdapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chartaccounts.infraestructure.adapters.output.jpaAdapter.entity.ChartAccountsEntity;

public interface IChartAccountsRepository extends JpaRepository<ChartAccountsEntity, Long> {
    
}
