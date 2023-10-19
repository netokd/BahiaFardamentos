package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaboradorRepository
        extends JpaRepository<Colaborador, Integer> {
    List<Colaborador> findByPostoId(Integer postoId);
}
