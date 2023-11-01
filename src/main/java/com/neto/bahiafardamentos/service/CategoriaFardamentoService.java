package com.neto.bahiafardamentos.service;

import com.neto.bahiafardamentos.exception.ApiError;
import com.neto.bahiafardamentos.exception.ApiResponse;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.CategoriaFardamentoRepository;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/categoria")
public class CategoriaFardamentoService {

    private final CategoriaFardamentoRepository categoriaFardamentoRepository;
    private final TamanhoRepository tamanhoRepository;

    @Autowired
    public CategoriaFardamentoService(CategoriaFardamentoRepository categoriaFardamentoRepository, TamanhoRepository tamanhoRepository){
        this.categoriaFardamentoRepository = categoriaFardamentoRepository;
        this.tamanhoRepository = tamanhoRepository;
    }
    @GetMapping("/getCategoriaById/{categoriaId}")
    public Optional<CategoriaFardamento> getCategoriaById(@PathVariable Integer categoriaId){return categoriaFardamentoRepository.findById(categoriaId);}

    public static void main(String[] args){SpringApplication.run(CategoriaFardamentoService.class, args);}

    @GetMapping
    public ResponseEntity<?> getCategoria(){
        try{
            List<CategoriaFardamento> categorias = categoriaFardamentoRepository.findAll();

            if(categorias.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Nenhuma categoria encontrada."));
            }
            return ResponseEntity.ok(categorias);

        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor:" + ex.getMessage()));
        }

    }

    record NewTamanhoRequest(
            Integer tamanhoId
    ){}
    public static class NewCategoriaRequest{
        private  List<Integer> tamanhoIds;
        private  String nome;

        public NewCategoriaRequest(List<Integer> tamanhoIds, String nome) {
            this.tamanhoIds = tamanhoIds;
            this.nome = nome;
        }

        public NewCategoriaRequest() {
        }

        public void setNome(String nome){
            this.nome = nome;
        }

        public void setTamanhoIds(List<Integer> tamanhoIds) {
            this.tamanhoIds = tamanhoIds;
        }

        public String getNome() {
            return nome;
        }


        public List<Integer> getTamanhoIds() {
            return tamanhoIds;
        }
    }

    public static class NewUpdateCatRequest{
        private List<Tamanho> tamanhos;
        private String nome;

        public NewUpdateCatRequest(List<Tamanho> tamanhos, String nome) {
            this.tamanhos = tamanhos;
            this.nome = nome;
        }

        public NewUpdateCatRequest() {
        }

        public List<Tamanho> getTamanhos() {
            return tamanhos;
        }

        public void setTamanhos(List<Tamanho> tamanhos) {
            this.tamanhos = tamanhos;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    @PostMapping
    public ResponseEntity<Object> addCategoriaFardamento(@RequestBody NewCategoriaRequest request){
       try{
           List<Integer> tamanhoIds = request.getTamanhoIds();
           List<Tamanho> tamanhos = tamanhoRepository.findAllById(tamanhoIds);
           if(!tamanhos.isEmpty()) {
               CategoriaFardamento categoria = new CategoriaFardamento();
               categoria.setNome(request.getNome());
               categoria.getTamanhos().addAll(tamanhos);
               categoriaFardamentoRepository.save(categoria);
               System.out.println("A lista de tamanhos não está vazia.");
               return ResponseEntity.status(HttpStatus.CREATED)
                       .body(new ApiResponse("Categoria adicionada com sucesso"));

           }else {
               System.out.println("A lista de tamanhos está vazia.");
               return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                       .body(new ApiError(HttpStatus.NOT_FOUND, "Tamanho não encontrado"));
           }


       }catch(Exception ex){

           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor: "+ ex.getMessage()));
       }

    }

    @PostMapping("/tam/{categoriaId}")
    public ResponseEntity<String> addTamanhoCategoria(@PathVariable("categoriaId") Integer categoriaId, @RequestBody NewTamanhoRequest request){
       try{
           Optional<CategoriaFardamento> categoriaOptional = categoriaFardamentoRepository.findById(categoriaId);
           Integer tamanhoId = request.tamanhoId;
           Optional<Tamanho> tamanhoOptional = tamanhoRepository.findById(tamanhoId);

           if(categoriaOptional.isPresent() && tamanhoOptional.isPresent()){
               CategoriaFardamento existingCategoria = categoriaOptional.get();
               Tamanho tamanho = tamanhoOptional.get();
               existingCategoria.getTamanhos().add(tamanho);

               categoriaFardamentoRepository.save(existingCategoria);

               return ResponseEntity.ok("Tamanho adicionado com sucesso à categoria.");
           }else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria ou tamanho não encontrado.");
           }
       }catch (DataIntegrityViolationException ex){
           return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
       }catch (Exception ex){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
       }

    }

    @PutMapping("{categoriaId}")
    public ResponseEntity<?> updateCategoria(@PathVariable("categoriaId") Integer categoriaId, @RequestBody NewUpdateCatRequest updateCategoria){
        try{

            Optional<CategoriaFardamento> optionalCategoria = categoriaFardamentoRepository.findById(categoriaId);

            if(optionalCategoria.isPresent()){
                CategoriaFardamento existingCategoria = optionalCategoria.get();
                existingCategoria.setNome(updateCategoria.getNome());
                existingCategoria.getTamanhos().clear();
                existingCategoria.getTamanhos().addAll(updateCategoria.getTamanhos());

                categoriaFardamentoRepository.save(existingCategoria);

                return ResponseEntity.ok("Categoria atualizada com sucesso");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Categoria não encontrado"));
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @DeleteMapping("/tam/{categoriaId}")
    public ResponseEntity<String> delTamanhoCategoria(@PathVariable("categoriaId") Integer categoriaId, @RequestBody NewTamanhoRequest request){
        try {
            Optional<CategoriaFardamento> optionalCategoria = categoriaFardamentoRepository.findById(categoriaId);
            Integer tamanhoId = request.tamanhoId;

            if(optionalCategoria.isPresent()){
                CategoriaFardamento existingCategoria = optionalCategoria.get();

                boolean tamanhoEncontrado = existingCategoria.getTamanhos().removeIf(tamanho -> tamanho.getId().equals(tamanhoId));

                if(tamanhoEncontrado){
                    categoriaFardamentoRepository.save(existingCategoria);
                    return ResponseEntity.ok("Tamanho removido com sucesso da categoria.");
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Tamanho não encontrado na categoria");
                }

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Categoria não encontrado");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }

    @DeleteMapping("{categoriaId}")
    public ResponseEntity<String> deleteCategoria(@PathVariable("categoriaId") Integer id){
        Optional<CategoriaFardamento> optionalCategoriaFardamento = categoriaFardamentoRepository.findById(id);
        try{
            if(optionalCategoriaFardamento.isPresent()){
                CategoriaFardamento categoria = optionalCategoriaFardamento.get();

                for(Tamanho tamanho : categoria.getTamanhos()){
                    tamanho.getCategorias().remove(categoria);
                }
                categoriaFardamentoRepository.deleteById(id);
                return ResponseEntity.ok("Categoria deletada com sucesso.");
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria  não encontrado.");
            }
        }catch(DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body("Erro de integridade de dados: " + ex.getMessage());
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + ex.getMessage());
        }
    }
}
