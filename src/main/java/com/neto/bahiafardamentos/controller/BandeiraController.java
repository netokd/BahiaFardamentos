package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.model.Bandeira;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class BandeiraController {
    private final RestTemplate restTemplate;
    @Autowired
    public BandeiraController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    @GetMapping("/bandeira")
    public String bandeira(Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/bandeira";

        try {
            List<Bandeira> bandeiraList = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Bandeira>>() {
                    }).getBody();

            if (bandeiraList.isEmpty()) {
                model.addAttribute("mensagem", "Nenhuma bandeira encontrada.");
            } else {
                model.addAttribute("bandeiraList", bandeiraList);
            }

        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma bandeira neste momento.");
        }
        return "bandeira/bandeira";

    }
    public List<Bandeira> obterTodasBandeiras(){
        String apiEndpoint = "http://localhost:8050/api/v1/bandeira";
        try{
            List<Bandeira> bandeirasList = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Bandeira>>() {
                    }).getBody();
            if (bandeirasList.isEmpty()) {
                return null;
            } else {
                return bandeirasList;
            }
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }
    @GetMapping("/adicionar-bandeira")
    public String showAddBandeiraForm(Model model
    ) {
        model.addAttribute("bandeira", new Bandeira());
        return "bandeira/create-bandeira";
    }
    @PostMapping("/adicionar-bandeira")
    public String addBandeira(@ModelAttribute Bandeira bandeira, Model model) {
        String apiEndpoint = "http://localhost:8050/api/v1/bandeira";
        try {
            // Faça a chamada para adicionar a bandeira
            restTemplate.postForObject(apiEndpoint, bandeira, Bandeira.class);
            // Redireciona para a página de listagem após a adição
            return "redirect:/bandeira";
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao adicionar a bandeira.");
            return "bandeira/create-bandeira";
        }
    }
    @GetMapping("/atualizar-bandeira/{bandeiraId}")
    public String showUpdateBandeiraForm(@PathVariable Integer bandeiraId, Model model) {
        // Fetch the bandeira object based on the ID and add it to the model
        ResponseEntity<Bandeira> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/bandeira/getBandeiraById/{bandeiraId}",
                Bandeira.class,
                bandeiraId
        );
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Bandeira bandeira = responseEntity.getBody();
            model.addAttribute("bandeira", bandeira);
            return "bandeira/update-bandeira";
        } else {
            // Handle case where bandeira is not found
            return "redirect:/bandeira"; // Or show an error page
        }
    }
    @PostMapping("/atualizar-bandeira/{bandeiraId}")
    public String updateBandeira(@PathVariable Integer bandeiraId, @ModelAttribute Bandeira bandeira, Model model) {
        String apiEndpoint = "http://localhost:8050/api/v1/bandeira/{bandeiraId}";
        try {
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Bandeira> requestEntity = new HttpEntity<>(bandeira, headers);

            // Faz a chamada para atualizar a bandeira usando o método HTTP POST
            ResponseEntity<Bandeira> responseEntity = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    Bandeira.class,
                    bandeiraId
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Redireciona para a página de listagem após a atualização
                return "redirect:/bandeira";
            } else {
                // Trate os erros adequadamente
                model.addAttribute("mensagem", "Erro ao atualizar a bandeira.");
                return "bandeira/update-bandeira/{bandeiraId}";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao atualizar a bandeira.");
            return "bandeira/update-bandeira/{bandeiraId}";
        }
    }
    @GetMapping("/excluir-bandeira/{bandeiraId}")
    public String deleteBandeira(@PathVariable Integer bandeiraId, Model model) {
        // Fetch the bandeira object based on the ID and add it to the model
        ResponseEntity<Bandeira> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/bandeira/getBandeiraById/{bandeiraId}",
                Bandeira.class,
                bandeiraId
        );
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Obtém a bandeira do response
            Bandeira bandeira = responseEntity.getBody();

            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // Faz a chamada para excluir a bandeira usando o método HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/bandeira/{bandeiraId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,  // O tipo de retorno é Void, pois o DELETE não retorna um corpo
                    bandeiraId
            );
            return "redirect:/bandeira";
        } else {
            // Handle case where bandeira is not found
            return "redirect:/bandeira"; // Or show an error page
        }
    }

}
