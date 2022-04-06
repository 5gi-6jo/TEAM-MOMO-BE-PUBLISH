package com.sparta.team6.momo.repository;

import com.sparta.team6.momo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
