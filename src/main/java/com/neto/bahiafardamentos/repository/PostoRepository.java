package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Posto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostoRepository
        extends JpaRepository<Posto, Integer> {
}
