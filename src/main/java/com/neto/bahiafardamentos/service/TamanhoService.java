package com.neto.bahiafardamentos.service;

import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
import com.sun.net.httpserver.HttpsServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/tamanho")
public class TamanhoService {


    private final TamanhoRepository tamanhoRepository;

    public TamanhoService(TamanhoRepository tamanhoRepository){this.tamanhoRepository = tamanhoRepository;

    }

    public static void main(String[] args){
        SpringApplication.run(TamanhoService.class,args);}

    @GetMapping
    public ResponseEntity<?> getTamanho(){
        try{
            List<Tamanho> tamanhos = tamanhoRepository.findAll();

            if(tamanhos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum tamanho encontrado"));
            }
            return ResponseEntity.ok(tamanhos);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));
        }
    }

    record NewTamanhoRequest(
      String nome
    ){}

    @PostMapping
    public ResponseEntity<Object> addTamanho(@RequestBody NewTamanhoRequest request){
        try{
            Tamanho tamanho = new Tamanho();
            tamanho.setNome(request.nome());

            tamanhoRepository.save(tamanho);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Tamanho adicionado com sucesso"));
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: " + ex.getMessage()));
        }
    }


    @DeleteMapping("{tamanhoId}")
    public ResponseEntity<String> deleteTamanho(@PathVariable("tamanhoId") Integer id){
        try{
            Optional<Tamanho> tamanhoOptional = tamanhoRepository.findById(id);

            if(tamanhoOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tamanho não encontrado.");

            } else{
                Tamanho tamanho = tamanhoOptional.get();

                for(CategoriaFardamento categoria : tamanho.getCategorias()){
                    categoria.getTamanhos().remove(tamanho);
                }
                tamanhoRepository.deleteById(id);
                return ResponseEntity.ok("Tamanho removido com sucesso.");
            }

        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }



    }



}
