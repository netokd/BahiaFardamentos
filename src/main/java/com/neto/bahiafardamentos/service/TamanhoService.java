package com.neto.bahiafardamentos.service;

import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.CategoriaFardamentoRepository;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
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
    private final CategoriaFardamentoRepository categoriaFardamentoRepository;

    public TamanhoService(TamanhoRepository tamanhoRepository, CategoriaFardamentoRepository categoriaFardamentoRepository) {
        this.tamanhoRepository = tamanhoRepository;
        this.categoriaFardamentoRepository = categoriaFardamentoRepository;
    }

    public static void main(String[] args){
        SpringApplication.run(TamanhoService.class,args);}

    @GetMapping("/getTamanhoById/{tamanhoId}")
    public Optional<Tamanho> getTamanhoById(@PathVariable Integer tamanhoId){return tamanhoRepository.findById(tamanhoId);}

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

    @PutMapping("{tamanhoId}")
    public ResponseEntity<Object> updateTamanho(@PathVariable("tamanhoId")Integer tamanhoId, @RequestBody Tamanho updateTamanho){
        try{
            System.out.println("Entrou na API");
            Optional<Tamanho> optionalTamanho = tamanhoRepository.findById(tamanhoId);
            if(optionalTamanho.isPresent()){
                System.out.println("Tamanho presente");
                Tamanho existingTamanho = optionalTamanho.get();
                existingTamanho.setNome(updateTamanho.getNome());

                tamanhoRepository.save(existingTamanho);

                return ResponseEntity.ok().build();
            }else{
                System.out.println("Tamanho ano ta presente");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Tamanho não encontrado"));
            }
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados:" + ex.getMessage()));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));

        }
    }


    @DeleteMapping("{tamanhoId}")
    public ResponseEntity<String> deleteTamanho(@PathVariable("tamanhoId") Integer tamanhoId){
        try{
            Optional<Tamanho> tamanhoOptional = tamanhoRepository.findById(tamanhoId);

            if(tamanhoOptional.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tamanho não encontrado.");

            } else{
                Tamanho tamanho = tamanhoOptional.get();
                for(CategoriaFardamento categoria : tamanho.getCategorias()){
                    categoria.getTamanhos().remove(tamanho);
                }
                tamanhoRepository.deleteById(tamanhoId);
                return ResponseEntity.ok("Tamanho removido com sucesso.");
            }

        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }



}
