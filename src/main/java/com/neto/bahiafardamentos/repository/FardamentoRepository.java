package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Fardamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FardamentoRepository
        extends JpaRepository<Fardamento, Integer> {
}
