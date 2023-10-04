package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.repository.BandeiraRepository;
import com.neto.bahiafardamentos.repository.PostoRepository;
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
@RequestMapping("api/v1/posto")
public class PostoService {

    private final PostoRepository postoRepository;
    private final BandeiraRepository bandeiraRepository;

    public  PostoService(PostoRepository postoRepository, BandeiraRepository bandeiraRepository){
        this.postoRepository = postoRepository;
        this.bandeiraRepository = bandeiraRepository;
    }

    public static void main(String[] args){SpringApplication.run(PostoService.class, args);}

    @GetMapping
    public ResponseEntity<?> getPostos(){
        try{
            List<Posto> postos = postoRepository.findAll();

            if(postos.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum Posto encontrado."));
            }
            return ResponseEntity.ok(postos);

        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));
        }
    }

    @GetMapping("/getPostoById/{postoId}")
    public Optional<Posto> getPostoById(@PathVariable Integer postoId) {
        return postoRepository.findById(postoId);
    }

    record NewBandeiraRequest(
            Integer bandeiraId
    ){}

    public static class NewPostoRequest{
        private final Integer bandeiraId;
        private final String nome;

        private final String endereco;


        public NewPostoRequest(Integer bandeiraId, String nome, String endereco){
            this.bandeiraId = bandeiraId;
            this.nome = nome;
            this.endereco = endereco;
        }
        public Integer getBandeiraId(){return bandeiraId;}
        public String getNome(){return nome;}
        public String getEndereco(){return endereco;}
    }
    @PostMapping
    public ResponseEntity<?> addPosto(@RequestBody NewPostoRequest request){
        try{
            Integer bandeiraId = request.bandeiraId;
            Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);

            if(optionalBandeira.isPresent()){
                Bandeira bandeira = optionalBandeira.get();
                Posto posto = new Posto();
                posto.setNome(request.getNome());
                posto.setEndereco(request.getEndereco());
                posto.setBandeira(bandeira);
                postoRepository.save(posto);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse("Posto adicionado com sucesso"));
            }else{
                return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Bandeira não encontrado"));
            }

        }catch(Exception ex){

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: "+ ex.getMessage()));
        }
    }
    @PutMapping("{postoId}")
    public ResponseEntity<?> updatePosto(
            @PathVariable("postoId") Integer postoId,
            @RequestBody NewPostoRequest updatePosto
    ){
        try{
            Optional<Posto> optionalPosto = postoRepository.findById(postoId);
            Integer bandeiraId = updatePosto.getBandeiraId();
             if(optionalPosto.isPresent()){
                 Posto existingPosto = optionalPosto.get();
                 existingPosto.setNome(updatePosto.getNome());
                 existingPosto.setEndereco(updatePosto.getEndereco());

                 Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);
                 if(optionalBandeira.isPresent()){
                     Bandeira bandeira = optionalBandeira.get();
                     existingPosto.setBandeira(bandeira);
                 }else {
                     return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ApiError(HttpStatus.NOT_FOUND, "Bandeira não encontrada"));
                 }

                 postoRepository.save(existingPosto);

                 return ResponseEntity.ok("Posto atualizado com sucesso");
             }else{
                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
                         .body(new ApiError(HttpStatus.NOT_FOUND, "Posto não encontrado"));
             }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @DeleteMapping("{postoId}")
    public ResponseEntity<?> deletePosto(@PathVariable("postoId") Integer postoId){
        try{
            Optional<Posto> optionalPosto = postoRepository.findById(postoId);
            if(optionalPosto.isPresent()){
                postoRepository.deleteById(postoId);
                return ResponseEntity.ok("Posto deletado com sucesso.");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Posto  não encontrado.");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

}
