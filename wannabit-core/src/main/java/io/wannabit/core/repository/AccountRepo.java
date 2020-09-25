package io.wannabit.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.wannabit.core.entity.Account;

public interface AccountRepo extends JpaRepository<Account, Integer> {

  Account findByEmail(String email);

}
