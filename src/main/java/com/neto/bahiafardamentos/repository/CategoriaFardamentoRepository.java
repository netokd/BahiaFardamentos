package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.CategoriaFardamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaFardamentoRepository
        extends JpaRepository<CategoriaFardamento, Integer> {
    List<CategoriaFardamento> findByTamanhos_Id(Integer tamanhoId);
}
