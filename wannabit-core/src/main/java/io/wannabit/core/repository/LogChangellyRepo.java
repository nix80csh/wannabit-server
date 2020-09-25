package io.wannabit.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.wannabit.core.entity.LogChangelly;
import io.wannabit.core.entity.LogChangellyPK;

public interface LogChangellyRepo extends JpaRepository<LogChangelly, LogChangellyPK> {
  List<LogChangelly> findByIdIdfAccountOrderByCreatedAtDesc(int idfAccount);
}
