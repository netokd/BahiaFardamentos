package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.exception.BandeiraNotFoundException;
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
@RequestMapping("api/v1/bandeira")
public class BandeiraService {
    private final BandeiraRepository bandeiraRepository;
    private final PostoRepository postoRepository;

    public BandeiraService(BandeiraRepository bandeiraRepository, PostoRepository postoRepository) {
        this.bandeiraRepository = bandeiraRepository;
        this.postoRepository = postoRepository;
    }

    public static void main(String[] args){SpringApplication.run(BandeiraService.class,args);}

    @GetMapping
    public ResponseEntity<?> getBandeira(){
        try{
            List<Bandeira> bandeiras = bandeiraRepository.findAll();

            if(bandeiras.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhuma bandeira encontrada."));
            }
            return ResponseEntity.ok(bandeiras);
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: " + ex.getMessage()));
        }

    }

    @GetMapping("/getBandeiraById/{bandeiraId}")
    public Optional<Bandeira> getBandeiraById(@PathVariable Integer bandeiraId) {
        return bandeiraRepository.findById(bandeiraId);
    }

    record NewBandeiraRequest(
            String nome
    ){}

    @PostMapping
    public ResponseEntity<Object> addBandeira(@RequestBody NewBandeiraRequest request){
        try{
            Bandeira bandeira = new Bandeira();
            bandeira.setNome(request.nome());
            bandeiraRepository.save(bandeira);
            ApiResponse response = new ApiResponse("Bandeira adicionada com sucesso.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch(BandeiraNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(new ApiError(HttpStatus.NOT_FOUND,"Bandeira não encontrada: " + ex.getMessage()));

        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: " + ex.getMessage()));
        }


    }

    @DeleteMapping("{bandeiraId}")
    public ResponseEntity<Object> deleteBandeira(@PathVariable("bandeiraId") Integer bandeiraId){
        try{
            Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);
            if(optionalBandeira.isPresent()){
              //Remover futuras associações(implementado)
                // Verificar se há postos associados a bandeira
                List<Posto> postos = postoRepository.findByBandeira_Id(bandeiraId);
                if(!postos.isEmpty()){
                    //Remover associações de postos a bandeira
                    for(Posto posto : postos){
                        posto.setBandeira(null);
                        postoRepository.save(posto);
                    }
                }
               bandeiraRepository.deleteById(bandeiraId);
               ApiResponse response = new ApiResponse("Bandeira excluída com sucesso.");
               return ResponseEntity.ok(response);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND,"Bandeira não encontrada."));
            }
        }catch(DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest()
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados:"+ ex.getMessage()));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor" + ex.getMessage()));
        }
    }

    @PostMapping("{bandeiraId}")
    public ResponseEntity<Object> updateBandeira(@PathVariable("bandeiraId") Integer bandeiraId, @RequestBody Bandeira updateBandeira){
       //Busca a Bandeira existente pelo ID
        try{
            Optional<Bandeira> optionalBandeira = bandeiraRepository.findById(bandeiraId);
            if(optionalBandeira.isPresent()){
                Bandeira existingBandeira = optionalBandeira.get();
                //Atualiza os campos da Bandeira existente com os dados da updateBandeira
                existingBandeira.setNome(updateBandeira.getNome());
                //Salva a bandeira atualizada no repositório
                bandeiraRepository.save(existingBandeira);

                ApiResponse response = new ApiResponse("Bandeira atualizada com sucesso");
                return ResponseEntity.ok(response);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Bandeira não encontrada."));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Erro de integridade de dados:" + ex.getMessage()));
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));
        }


    }

}
