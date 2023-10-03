package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Gastos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastosRepository
        extends JpaRepository<Gastos, Integer> {

}
