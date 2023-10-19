package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Posto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostoRepository
        extends JpaRepository<Posto, Integer> {
    List<Posto> findByBandeira_Id(Integer bandeiraId);
}
