package com.neto.bahiafardamentos.service;

import com.neto.bahiafardamentos.dto.EstoqueSimplificadoDTO;
import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.EstoqueFardamentoTamanho;
import com.neto.bahiafardamentos.model.Fardamento;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.EstoqueFardamentoTamanhoRepository;
import com.neto.bahiafardamentos.repository.FardamentoRepository;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/fardamento/estoque")
public class EstoqueFardamentoTamanhoService {
    private final EstoqueFardamentoTamanhoRepository estoqueFardamentoTamanhoRepository;
    private final TamanhoRepository tamanhoRepository;
    private final FardamentoRepository fardamentoRepository;

    @Autowired
    public EstoqueFardamentoTamanhoService(EstoqueFardamentoTamanhoRepository estoqueFardamentoTamanhoRepository, TamanhoRepository tamanhoRepository, FardamentoRepository fardamentoRepository) {
        this.estoqueFardamentoTamanhoRepository = estoqueFardamentoTamanhoRepository;
        this.tamanhoRepository = tamanhoRepository;
        this.fardamentoRepository = fardamentoRepository;
    }

    public static void main(String[] args){SpringApplication.run(EstoqueFardamentoTamanhoService.class, args);}

    public static class NewEstoqueRequest{
        private final Integer tamanhoId;
        private final Integer fardamentoId;
        private final Integer quantidade;

        public NewEstoqueRequest(Integer tamanhoId, Integer fardamentoId, Integer quantidade) {
            this.tamanhoId = tamanhoId;
            this.fardamentoId = fardamentoId;
            this.quantidade = quantidade;
        }

        public Integer getTamanhoId() {
            return tamanhoId;
        }

        public Integer getFardamentoId() {
            return fardamentoId;
        }

        public Integer getQuantidade() {
            return quantidade;
        }
    }


    record NewQuantidadeRequest(
            Integer quantidade
    ){}

    record NewConsultaRequest(
            Integer fardamentoId,
            Integer tamanhoId
    ){}
    @GetMapping
    public ResponseEntity<?> getEstoque(){
        try{
            List<EstoqueFardamentoTamanho> estoque = estoqueFardamentoTamanhoRepository.findAll();

            if(estoque.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhum estoque encontrado"));
            }else{
                return  ResponseEntity.ok(estoque);
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addEstoque(@RequestBody NewEstoqueRequest request){
        try{
            Integer fardamentoId = request.fardamentoId;
            Optional<Fardamento> optionalFardamento = fardamentoRepository.findById(fardamentoId);
            Integer tamanhoId = request.tamanhoId;
            Optional<Tamanho> optionalTamanho = tamanhoRepository.findById(tamanhoId);

            if(optionalFardamento.isPresent() && optionalTamanho.isPresent()){
                Fardamento fardamento = optionalFardamento.get();
                Tamanho tamanho = optionalTamanho.get();
                Optional<EstoqueFardamentoTamanho> optionalEstoque = estoqueFardamentoTamanhoRepository.findByFardamentoIdAndTamanhoId(request.fardamentoId, request.tamanhoId);
                if(optionalEstoque.isEmpty()){
                    EstoqueFardamentoTamanho estoque = new EstoqueFardamentoTamanho();
                    estoque.setFardamento(fardamento);
                    estoque.setTamanho(tamanho);
                    estoque.setQuantidade(request.quantidade);
                    estoqueFardamentoTamanhoRepository.save(estoque);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ApiResponse("Estoque adicionado com sucesso"));
                }else{
                    return  ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiError(HttpStatus.CONFLICT, "O estoque para esse fardamento e tamanho ja existem"));
                }
            }else{
                return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Fardamento ou Tamanho não encontrada"));
            }

        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }

    }
    //Atualiza a quantidade em estoque associada ao fardamento e tamanho correspondentes aos ids passados na url.
    @PostMapping("/{fardamentoId}/{tamanhoId}")
    public ResponseEntity<?> updateEstoque(
            @PathVariable Integer fardamentoId,
            @PathVariable Integer tamanhoId,
            @RequestBody NewQuantidadeRequest request
    ){
        try{
            Optional<EstoqueFardamentoTamanho> optionalEstoque = estoqueFardamentoTamanhoRepository.findByFardamentoIdAndTamanhoId(fardamentoId,tamanhoId);
            if(optionalEstoque.isPresent()){
                EstoqueFardamentoTamanho estoque = optionalEstoque.get();
                estoque.setQuantidade(request.quantidade);
                estoqueFardamentoTamanhoRepository.save(estoque);

                return ResponseEntity.ok("Quantidade no estoque atualizado com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }
    @DeleteMapping("/{fardamentoId}/{tamanhoId}")
    public ResponseEntity<?> deleteEstoque(
            @PathVariable Integer fardamentoId,
            @PathVariable Integer tamanhoId){

        try{
            Optional<EstoqueFardamentoTamanho> optionalEstoque = estoqueFardamentoTamanhoRepository.findByFardamentoIdAndTamanhoId(fardamentoId,tamanhoId);
            if(optionalEstoque.isPresent()){
                estoqueFardamentoTamanhoRepository.delete(optionalEstoque.get());
                return ResponseEntity.ok("Estoque excluído com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Estoque não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @GetMapping("/quantidade/{fardamentoId}/{tamanhoId}")
    public ResponseEntity<?> getEstoqueByFardTam(
            @PathVariable Integer fardamentoId,
            @PathVariable Integer tamanhoId){
       try{
           Integer estoque = getEstoqueByFardamentoAndTamanho(fardamentoId, tamanhoId);
           if(estoque >=0 ){
               return new ResponseEntity<>(estoque, HttpStatus.OK);
           }else{
               return ResponseEntity.status(HttpStatus.NOT_FOUND)
                       .body("Estoque negativo ou não encontrado");
           }
       }catch(DataIntegrityViolationException ex){
           return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
       }catch (Exception ex){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
       }
    }

    @GetMapping("/simplificado/{fardamentoId}/{tamanhoId}")
    public ResponseEntity<?> getEstoqueSimplificado(
            @PathVariable Integer fardamentoId,
            @PathVariable Integer tamanhoId){
        try{
            Integer estoque = getEstoqueByFardamentoAndTamanho(fardamentoId, tamanhoId);
            if(estoque >=0 ){
                String tamanho = tamanhoRepository.findById(tamanhoId).map(Tamanho::getNome).orElse("");
                String fardamento = fardamentoRepository.findById(fardamentoId).map(Fardamento::getNome).orElse("");

                // Cria um DTO simplificado com a quantidade
                EstoqueSimplificadoDTO estoqueDTO = new EstoqueSimplificadoDTO(fardamento, fardamentoId,tamanho,tamanhoId, estoque);
                return new ResponseEntity<>(estoqueDTO, HttpStatus.OK);

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Estoque negativo ou não encontrado");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    public Integer getEstoqueByFardamentoAndTamanho(
             Integer fardamentoId,
            Integer tamanhoId){
        Optional<EstoqueFardamentoTamanho> optionalEstoque = estoqueFardamentoTamanhoRepository.findByFardamentoIdAndTamanhoId(fardamentoId, tamanhoId);
        if(optionalEstoque.isPresent()){
            EstoqueFardamentoTamanho estoque = optionalEstoque.get();
            return estoque.getQuantidade();
        }else{
            return -1;
        }
    }

}
