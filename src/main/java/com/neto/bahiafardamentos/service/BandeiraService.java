package com.neto.bahiafardamentos.service;


import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.repository.BandeiraRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/bandeira")
public class BandeiraService {
    private final BandeiraRepository bandeiraRepository;

    public BandeiraService(BandeiraRepository bandeiraRepository){
        this.bandeiraRepository = bandeiraRepository;
    }

    public static void main(String[] args){SpringApplication.run(BandeiraService.class,args);}

    @GetMapping
    public List<Bandeira> getBandeira(){return bandeiraRepository.findAll();}

    record NewBandeiraRequest(
            String nome
    ){}

    @PostMapping
    public void addBandeira(@RequestBody NewBandeiraRequest request){
        Bandeira bandeira = new Bandeira();
        bandeira.setNome(request.nome());
        bandeiraRepository.save(bandeira);

    }

    @DeleteMapping("{bandeiraId}")
    public void deleteBandeira(@PathVariable("bandeiraId") Integer id){bandeiraRepository.deleteById(id);}

    @PostMapping("{bandeiraId}")
    public void updateBandeira(@PathVariable("bandeiraId") Integer bandeiraId, @RequestBody Bandeira updateBandeira){
       //Busca a Bandeira existente pelo ID
        Bandeira existingBandeira = bandeiraRepository.findById(bandeiraId)
               .orElseThrow(() -> new RuntimeException("Bandeira não encontrada"));
        //Atualiza os campos da Bandeira existente com os dados da updateBandeira
       existingBandeira.setNome(updateBandeira.getNome());
        //Salva a bandeira atualizada no repositório
       bandeiraRepository.save(existingBandeira);
    }

}
