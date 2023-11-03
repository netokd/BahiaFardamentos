package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Fardamento;
import com.neto.bahiafardamentos.repository.BandeiraRepository;
import com.neto.bahiafardamentos.repository.CategoriaFardamentoRepository;
import com.neto.bahiafardamentos.repository.FardamentoRepository;
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
@RequestMapping("api/v1/fardamento")
public class FardamentoService {


    private final FardamentoRepository fardamentoRepository;
    private final CategoriaFardamentoRepository categoriaFardamentoRepository;
    private final BandeiraRepository bandeiraRepository;

    public FardamentoService(FardamentoRepository fardamentoRepository, CategoriaFardamentoRepository categoriaFardamentoRepository, BandeiraRepository bandeiraRepository) {
        this.fardamentoRepository = fardamentoRepository;
        this.categoriaFardamentoRepository = categoriaFardamentoRepository;
        this.bandeiraRepository = bandeiraRepository;
    }

    public static void main(String[] args){SpringApplication.run(FardamentoService.class, args);}

    @GetMapping
    public ResponseEntity<?> getFardamento(){
        try{

            List<Fardamento> fardamentos = fardamentoRepository.findAll();

            if(fardamentos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum Fardamento encontrado"));
            }else{
                return ResponseEntity.ok(fardamentos);
            }


        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    record NewQuantidadeRequest(
            Integer quantidade
    ){}

    public static class NewFardamentoRequest{
        private final String nome;
        private final Integer categoriaId;
        private final Integer bandeiraId;
        public NewFardamentoRequest(String nome, Integer categoriaId, Integer bandeiraId) {
            this.nome = nome;
            this.categoriaId = categoriaId;
            this.bandeiraId = bandeiraId;
        }
        public String getNome() {return nome;}


        public Integer getCategoriaId() {return categoriaId;}

        public Integer getBandeiraId() {return bandeiraId;}
    }

    public static class NewFardamentoObjRequest{
        private final String nome;
        private final CategoriaFardamento categoria;
        private final Bandeira bandeira;

        public NewFardamentoObjRequest(String nome, CategoriaFardamento categoria, Bandeira bandeira) {
            this.nome = nome;
            this.categoria = categoria;
            this.bandeira = bandeira;
        }

        public String getNome() {
            return nome;
        }

        public CategoriaFardamento getCategoria() {
            return categoria;
        }

        public Bandeira getBandeira() {
            return bandeira;
        }
    }

    @GetMapping("/getFardamentoById/{fardamentoId}")
    public Optional<Fardamento> getFardamentoById(@PathVariable Integer fardamentoId) {
        return fardamentoRepository.findById(fardamentoId);
    }

    @PostMapping
    public ResponseEntity<?> addFardamento(@RequestBody NewFardamentoObjRequest request){
        try{

            if(request != null){

                Fardamento fardamento = new Fardamento();

                fardamento.setNome(request.getNome());
                fardamento.setCategoria(request.getCategoria());
                fardamento.setBandeira(request.getBandeira());

                fardamentoRepository.save(fardamento);
                return ResponseEntity.status((HttpStatus.CREATED))
                        .body(new ApiResponse("Fardamento adicionado com sucesso"));

            }else{
                return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Categoria ou Bandeira não encontrada"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }
    @PostMapping("{fardamentoId}")
    public ResponseEntity<?> updateFardamento(
            @PathVariable("fardamentoId")Integer fardamentoId,
            @RequestBody NewFardamentoObjRequest updateFardamento
    ){
        try{
            Optional<Fardamento> optionalFardamento = fardamentoRepository.findById(fardamentoId);
            if(optionalFardamento.isPresent()){
                Fardamento existingFardamento = optionalFardamento.get();
                existingFardamento.setNome(updateFardamento.getNome());


                if(updateFardamento.getCategoria() != null){
                    existingFardamento.setCategoria(updateFardamento.getCategoria());
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiError(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
                }


                if (updateFardamento.getBandeira() != null){
                    existingFardamento.setBandeira(updateFardamento.getBandeira());
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiError(HttpStatus.NOT_FOUND, "Bandeira não encontrada"));
                }

                fardamentoRepository.save(existingFardamento);
                return ResponseEntity.ok("Fardamento atualizado com sucesso");

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Fardamento não encontrada"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @DeleteMapping("{fardamentoId}")
    public ResponseEntity<?> deleteFardamento(@PathVariable("fardamentoId")Integer fardamentoId){
        try{
            Optional<Fardamento> optionalFardamento = fardamentoRepository.findById(fardamentoId);
            if(optionalFardamento.isPresent()){
                fardamentoRepository.deleteById(fardamentoId);
                return ResponseEntity.ok("Fardamento deletado com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fardamento  não encontrado.");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }




}
