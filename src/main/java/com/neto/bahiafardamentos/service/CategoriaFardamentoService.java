package com.neto.bahiafardamentos.service;

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

    public static void main(String[] args){SpringApplication.run(CategoriaFardamentoService.class, args);}

    @GetMapping
    public List<CategoriaFardamento> getCategoria(){return categoriaFardamentoRepository.findAll();}

    record NewTamanhoRequest(
            Integer tamanhoId
    ){}
    public static class NewCategoriaRequest{
        private final Integer tamanhoId;
        private final String nome;

        public NewCategoriaRequest(Integer tamanhoId, String nome){
            this.tamanhoId = tamanhoId;
            this.nome = nome;
        }
        public Integer getTamanhoId(){return tamanhoId;}
        public String getNome(){return nome;}
    }

    @PostMapping
    public void addCategoriaFardamento(@RequestBody NewCategoriaRequest request){
        Integer tamanhoId = request.getTamanhoId();

        Optional<Tamanho> tamanhoOptional = tamanhoRepository.findById(tamanhoId);
        if(tamanhoOptional.isPresent()) {
            Tamanho tamanho = tamanhoOptional.get();
            CategoriaFardamento categoria = new CategoriaFardamento();
            categoria.setNome(request.getNome());
            categoria.getTamanhos().add(tamanho);



            categoriaFardamentoRepository.save(categoria);
        }
    }

    @PostMapping("/add-tam/{categoriaId}")
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
