package com.sistemacontable.usuarios.repository;

import com.sistemacontable.usuarios.entity.UserRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolRepository extends JpaRepository<UserRol, Long> {
    List<UserRol> findByUser_Id(Long userId);
}
