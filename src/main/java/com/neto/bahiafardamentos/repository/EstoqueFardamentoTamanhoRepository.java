package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.EstoqueFardamentoTamanho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueFardamentoTamanhoRepository
        extends JpaRepository<EstoqueFardamentoTamanho, Integer> {

    Optional<EstoqueFardamentoTamanho> findByFardamentoIdAndTamanhoId(Integer fardamento_id, Integer tamanho_id);
}
