package com.neto.bahiafardamentos.service;

import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/tamanho")
public class TamanhoService {


    private final TamanhoRepository tamanhoRepository;

    public TamanhoService(TamanhoRepository tamanhoRepository){this.tamanhoRepository = tamanhoRepository;

    }

    public static void main(String[] args){
        SpringApplication.run(TamanhoService.class,args);}

    @GetMapping
    public List<Tamanho> getTamanho(){return tamanhoRepository.findAll();}

    record NewTamanhoRequest(
      String nome
    ){}

    @PostMapping
    public void addTamanho(@RequestBody NewTamanhoRequest request){
        Tamanho tamanho = new Tamanho();
        tamanho.setNome(request.nome());

        tamanhoRepository.save(tamanho);
    }


    @DeleteMapping("{tamanhoId}")
    public void deleteTamanho(@PathVariable("tamanhoId") Integer id){
        Optional<Tamanho> tamanhoOptional = tamanhoRepository.findById(id);

        if(tamanhoOptional.isPresent()){
            Tamanho tamanho = tamanhoOptional.get();

            for(CategoriaFardamento categoria : tamanho.getCategorias()){
                categoria.getTamanhos().remove(tamanho);
            }
        tamanhoRepository.deleteById(id);

        }


    }



}
