package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.Colaborador;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.repository.ColaboradorRepository;
import com.neto.bahiafardamentos.repository.PostoRepository;
import org.hibernate.annotations.Parent;
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
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));
        }
    }

    public static class NewColaboradorRequest{
        private final String nome;
        private final String cargo;
        private final Date dataContratacao;
        private final Date dataUltimoKitEnviado;
        private final Integer postoId;

        public NewColaboradorRequest(String nome, String cargo, Date dataContratacao, Date dataUltimoKitEnviado, Integer postoId) {
            this.nome = nome;
            this.cargo = cargo;
            this.dataContratacao = dataContratacao;
            this.dataUltimoKitEnviado = dataUltimoKitEnviado;
            this.postoId = postoId;
        }
        public String getNome() {return nome;}
        public String getCargo() {return cargo;}
        public Date getDataContratacao() {return dataContratacao;}
        public Date getDataUltimoKitEnviado() {return dataUltimoKitEnviado;}
        public Integer getPostoId() {return postoId;}
    }

    @PostMapping
    public ResponseEntity<?> addColaborador(@RequestBody NewColaboradorRequest request){
        try{
            Integer postoId = request.getPostoId();
            Optional<Posto> optionalPosto = postoRepository.findById(postoId);

            if(optionalPosto.isPresent()){
                Posto posto = optionalPosto.get();
                Colaborador colaborador = new Colaborador();

                colaborador.setNome(request.getNome());
                colaborador.setCargo(request.getCargo());
                colaborador.setDataContratacao(request.getDataContratacao());
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




}
