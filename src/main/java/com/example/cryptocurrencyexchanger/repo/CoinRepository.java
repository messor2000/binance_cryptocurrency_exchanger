package com.example.cryptocurrencyexchanger.repo;

import com.example.cryptocurrencyexchanger.entity.coin.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    Coin getCoinBySymbol(String symbol);
}
