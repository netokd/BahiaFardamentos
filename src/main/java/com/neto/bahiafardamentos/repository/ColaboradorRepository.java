package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository
        extends JpaRepository<Colaborador, Integer> {
}
