package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.CategoriaFardamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaFardamentoRepository
        extends JpaRepository<CategoriaFardamento, Integer> {
}
