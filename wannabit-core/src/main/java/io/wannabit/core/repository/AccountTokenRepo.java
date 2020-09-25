package io.wannabit.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.wannabit.core.entity.AccountToken;

public interface AccountTokenRepo extends JpaRepository<AccountToken, Integer> {

}
