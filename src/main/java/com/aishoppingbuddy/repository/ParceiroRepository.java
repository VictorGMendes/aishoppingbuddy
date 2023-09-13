package com.aishoppingbuddy.repository;

import com.aishoppingbuddy.model.Parceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParceiroRepository extends JpaRepository<Parceiro, Long> {
}
