package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Colaborador;
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
        private final Integer quantidade;
        private final Integer categoriaId;
        private final Integer bandeiraId;
        public NewFardamentoRequest(String nome, Integer quantidade, Integer categoriaId, Integer bandeiraId) {
            this.nome = nome;
            this.quantidade = quantidade;
            this.categoriaId = categoriaId;
            this.bandeiraId = bandeiraId;
        }
        public String getNome() {return nome;}

        public Integer getQuantidade() {return quantidade;}

        public Integer getCategoriaId() {return categoriaId;}

        public Integer getBandeiraId() {return bandeiraId;}
    }

    @PostMapping
    public ResponseEntity<?> addFardamento(@RequestBody NewFardamentoRequest request){
        try{
            Integer categoriaId = request.getCategoriaId();
            Optional<CategoriaFardamento> optionalcategoria = categoriaFardamentoRepository.findById(categoriaId);
            Integer bandeiraId = request.getBandeiraId();
            Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);

            if(optionalcategoria.isPresent() && optionalBandeira.isPresent()){
                CategoriaFardamento categoria = optionalcategoria.get();
                Bandeira bandeira = optionalBandeira.get();

                Fardamento fardamento = new Fardamento();

                fardamento.setNome(request.getNome());
                fardamento.setQuantidade(request.getQuantidade());
                fardamento.setCategoria(categoria);
                fardamento.setBandeira(bandeira);

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
            @RequestBody NewFardamentoRequest updateFardamento
    ){
        try{
            Optional<Fardamento> optionalFardamento = fardamentoRepository.findById(fardamentoId);
            Integer categoriaId = updateFardamento.getCategoriaId();
            Integer bandeiraId = updateFardamento.getBandeiraId();

            if(optionalFardamento.isPresent()){
                Fardamento existingFardamento = optionalFardamento.get();
                existingFardamento.setNome(updateFardamento.getNome());
                existingFardamento.setQuantidade(updateFardamento.getQuantidade());

                Optional<CategoriaFardamento> optionalCategoria = categoriaFardamentoRepository.findById(categoriaId);
                if(optionalCategoria.isPresent()){
                    CategoriaFardamento categoria = optionalCategoria.get();
                    existingFardamento.setCategoria(categoria);
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiError(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
                }

                Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);
                if (optionalBandeira.isPresent()){
                    Bandeira bandeira = optionalBandeira.get();
                    existingFardamento.setBandeira(bandeira);
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
    @PostMapping("/qtd/{fardamentoId}")
    public ResponseEntity<?> updateQtdFardamento(
            @PathVariable("fardamentoId") Integer fardamentoId,
            @RequestBody NewQuantidadeRequest request
    ){
        try{
            Optional<Fardamento> optionalFardamento = fardamentoRepository.findById(fardamentoId);
            if(optionalFardamento.isPresent()){
                Fardamento existingFardamento = optionalFardamento.get();
                existingFardamento.setQuantidade(request.quantidade);

                fardamentoRepository.save(existingFardamento);

                return ResponseEntity.ok("Quantidade do Fardamento atualizado");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Fardamento não encontrado"));
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
