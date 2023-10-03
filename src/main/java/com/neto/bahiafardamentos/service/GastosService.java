package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.model.Gastos;
import com.neto.bahiafardamentos.repository.GastosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/fardamento/gastos")
public class GastosService {

    private final GastosRepository gastosRepository;
    @Autowired
    public GastosService(GastosRepository gastosRepository) {
        this.gastosRepository = gastosRepository;
    }
    public static void main(String[] args){SpringApplication.run(GastosService.class, args);}

    record NewGastoRequest(
            Double valor,
            String descricao,
            LocalDate dataEntrada

    ){}
    @GetMapping
    public ResponseEntity<?> getGastos(){
        try{
            List<Gastos> gastos = gastosRepository.findAll();

            if(gastos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum Gasto encontrado"));
            }else {
                return ResponseEntity.ok(gastos);
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addGastos(@RequestBody NewGastoRequest request){
        try{
            Gastos gastos = new Gastos();
            gastos.setValor(request.valor);
            gastos.setDescricao(request.descricao);
            gastos.setDataEntrada(request.dataEntrada);

            gastosRepository.save(gastos);
            return ResponseEntity.status(HttpStatus.CREATED).body("Gasto adicionado com sucesso");

        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @PostMapping("{gastoId}")
    public ResponseEntity<?> updateGastos(@PathVariable Integer gastoId,@RequestBody NewGastoRequest request){
        try {
            Optional<Gastos> optionalGastos = gastosRepository.findById(gastoId);
            if(optionalGastos.isPresent()){
                Gastos existingGasto = optionalGastos.get();

                existingGasto.setValor(request.valor);
                existingGasto.setDescricao(request.descricao);
                existingGasto.setDataEntrada(request.dataEntrada);

                gastosRepository.save(existingGasto);
                return ResponseEntity.ok("Gasto atualizado com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Gasto não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }
    @DeleteMapping("{gastoId}")
    public ResponseEntity<?> deleteEstoque(@PathVariable Integer gastoId){
        try{
            Optional<Gastos> optionalGastos = gastosRepository.findById(gastoId);
            if(optionalGastos.isPresent()){
                gastosRepository.deleteById(gastoId);
                return ResponseEntity.ok("Gasto excluído com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Gasto não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }




}
