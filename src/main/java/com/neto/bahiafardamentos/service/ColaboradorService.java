package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.Colaborador;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.repository.ColaboradorRepository;
import com.neto.bahiafardamentos.repository.PostoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/colaborador")
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final PostoRepository postoRepository;

    public ColaboradorService(ColaboradorRepository colaboradorRepository, PostoRepository postoRepository){
        this.colaboradorRepository = colaboradorRepository;
        this.postoRepository = postoRepository;
    }
    public static void main(String[] args){SpringApplication.run(ColaboradorService.class, args);}

    @GetMapping
    public ResponseEntity<?> getColaborador(){
        try{
            List<Colaborador> colaborador = colaboradorRepository.findAll();

            if (colaborador.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum Colaborador encontrado"));
            }
            return ResponseEntity.ok(colaborador);
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    record NewKitRequest(
            Date dataUltimoKitEnviado
    ){}
    public static class NewColaboradorRequest{
        private final String nome;
        private final String cargo;
        private final Date dataContratacao;
        private final Date dataUltimoKitEnviado;
        private final Integer postoId;

        private final Posto posto;

        public NewColaboradorRequest(String nome, String cargo, Date dataContratacao, Date dataUltimoKitEnviado, Integer postoId, Posto posto) {
            this.nome = nome;
            this.cargo = cargo;
            this.dataContratacao = dataContratacao;
            this.dataUltimoKitEnviado = dataUltimoKitEnviado;
            this.postoId = postoId;
            this.posto = posto;
        }

        public String getNome() {return nome;}
        public String getCargo() {return cargo;}
        public Date getDataContratacao() {return dataContratacao;}
        public Date getDataUltimoKitEnviado() {return dataUltimoKitEnviado;}
        public Integer getPostoId() {return postoId;}

        public Posto getPosto() {return posto;}
    }

    @PostMapping
    public ResponseEntity<?> addColaborador(@RequestBody NewColaboradorRequest request){
        try{
            Integer postoId = request.getPostoId();
            Optional<Posto> optionalPosto = postoRepository.findById(postoId);

            if(optionalPosto.isPresent()){
                Posto posto = optionalPosto.get();
                Colaborador colaborador = new Colaborador();
                Date dataUltimoKit = request.getDataUltimoKitEnviado();

                colaborador.setNome(request.getNome());
                colaborador.setCargo(request.getCargo());
                colaborador.setDataContratacao(request.getDataContratacao());
                if(dataUltimoKit != null){
                    colaborador.setDataUltimoKitEnviado(dataUltimoKit);
                }
                colaborador.setPosto(posto);

                colaboradorRepository.save(colaborador);
                return ResponseEntity.status((HttpStatus.CREATED))
                        .body(new ApiResponse("Colaborador adicionado com sucesso"));
            }else{
                return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Posto não encontrado"));
            }

        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @PutMapping("{colaboradorId}")
    public ResponseEntity<?> updateColaborador(
            @PathVariable("colaboradorId") Integer colaboradorId,
            @RequestBody NewColaboradorRequest updateColaborador
    ){
        try{
            if (colaboradorId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiError(HttpStatus.BAD_REQUEST, "O ID do colaborador não pode ser nulo"));
            }
            Optional<Colaborador> optionalColaborador = colaboradorRepository.findById(colaboradorId);
            Integer postoId = updateColaborador.getPostoId();
            if(postoId != null){
                System.out.println("ok");
            }else{
                postoId = updateColaborador.getPosto().getId();
            }

            if(optionalColaborador.isPresent()){
                Colaborador existingColaborador = optionalColaborador.get();
                existingColaborador.setNome(updateColaborador.getNome());
                existingColaborador.setDataContratacao(updateColaborador.getDataContratacao());
                existingColaborador.setCargo(updateColaborador.getCargo());
                if(updateColaborador.getDataUltimoKitEnviado() != null){
                    existingColaborador.setDataUltimoKitEnviado(updateColaborador.getDataUltimoKitEnviado());
                }

                Optional<Posto> optionalPosto = postoRepository.findById(postoId);
                if(optionalPosto.isPresent()){
                    Posto posto = optionalPosto.get();
                    existingColaborador.setPosto(posto);
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiError(HttpStatus.NOT_FOUND, "Posto não encontrada"));
                }
                colaboradorRepository.save(existingColaborador);
                return ResponseEntity.ok("Posto atualizado com sucesso");
            }else{
                System.out.println("ID do Colaborador na API dentro do else : " + colaboradorId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @PostMapping("/kit/{colaboradorId}")
    public ResponseEntity<?> updateDataKit(
            @PathVariable("colaboradorId") Integer colaboradorId,
            @RequestBody NewKitRequest request
    ){
       try{
            Optional<Colaborador> optionalColaborador = colaboradorRepository.findById(colaboradorId);

            if(optionalColaborador.isPresent()){
                Colaborador existingColaborador = optionalColaborador.get();

                existingColaborador.setDataUltimoKitEnviado(request.dataUltimoKitEnviado);

                colaboradorRepository.save(existingColaborador);

                return ResponseEntity.ok("Data do ultimo Kit atualizado");

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
            }
       }catch(DataIntegrityViolationException ex){
           return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
       }catch (Exception ex){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
       }
    }

    @DeleteMapping("/{colaboradorId}")
    public ResponseEntity<?> deleteColaborador(@PathVariable("colaboradorId") Integer colaboradorId){
        try{
            Optional<Colaborador> optionalColaborador = colaboradorRepository.findById(colaboradorId);
            if(optionalColaborador.isPresent()){
                colaboradorRepository.deleteById(colaboradorId);
                return ResponseEntity.ok("Colaborador deletado com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Colaborador  não encontrado.");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }
    @GetMapping("/getColaboradorById/{colaboradorId}")
    public Optional<Colaborador> getColaboradorById(@PathVariable Integer colaboradorId) {
        return colaboradorRepository.findById(colaboradorId);
    }

}

