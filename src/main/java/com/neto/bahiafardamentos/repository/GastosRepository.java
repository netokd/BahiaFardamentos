package com.neto.bahiafardamentos.repository;

import com.neto.bahiafardamentos.model.Gastos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GastosRepository
        extends JpaRepository<Gastos, Integer> {
    @Query("SELECT g.categoriaFardamento, SUM(g.valor) FROM Gastos g GROUP BY g.categoriaFardamento")
    List<Object[]> calcularGastosPorCategoria();
}
