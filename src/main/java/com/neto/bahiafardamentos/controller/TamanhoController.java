package com.neto.bahiafardamentos.controller;


import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.model.Tamanho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class TamanhoController {


    private final RestTemplate restTemplate;

    @Autowired
    public TamanhoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/tamanho")
    public String tamanhos(Model model){
        String apiEndPoint = "http://localhost:8050/api/v1/tamanho";

        try{
            List<Tamanho> tamanhoList = restTemplate.exchange(
                    apiEndPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Tamanho>>(){
                    }).getBody();
            if(tamanhoList.isEmpty()){
                model.addAttribute("mensagem", "Nenhum Tamanho encontrado");
            }else{
                model.addAttribute("tamanhoList", tamanhoList);
            }
        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma Tamanho neste momento.");
        }
        return "tamanho/tamanho";
    }

    public List<Tamanho> obterTodosTamanhos(){
        String apiEndpoint = "http://localhost:8050/api/v1/tamanho";
        try{
            List<Tamanho> tamanhoList = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Tamanho>>(){
                    }).getBody();
            if(tamanhoList.isEmpty()){
                return null;
            }else {
                return tamanhoList;
            }
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }

    @GetMapping("/adicionar-tamanho")
    public String showAddTamanhoForm(Model model){
        model.addAttribute("tamanho", new Tamanho());
        return "tamanho/create-tamanho";
    }
    @PostMapping("/adicionar-tamanho")
    public String addTamanho(@ModelAttribute Tamanho tamanho, Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/tamanho";

        try{
            restTemplate.postForObject(apiEndpoint, tamanho, Tamanho.class);

            return "redirect:/tamanho";
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao adicionar a tamanho.");
            return "erros/deuruim3";
        }
    }

    @GetMapping("/atualizar-tamanho/{tamanhoId}")
    public String showUpdateTamanhoForm(@PathVariable Integer tamanhoId, Model model){
        ResponseEntity<Tamanho> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/tamanho/getTamanhoById/{tamanhoId}",
                Tamanho.class,
                tamanhoId
        );

        if(responseEntity.getStatusCode() == HttpStatus.OK){
            Tamanho tamanho = responseEntity.getBody();
            model.addAttribute("tamanho", tamanho);

            return "tamanho/update-tamanho";
        }else{
            // Handle case where bandeira is not found
            model.addAttribute("mensagem", "Erro ao atualizar o Tamanho Else.");
            return "erros/deuruim3"; // Or show an error page
        }
    }

    @PostMapping("/atualizar-tamanho/{tamanhoId}")
    public String updateTamanho(@PathVariable Integer tamanhoId, @ModelAttribute Tamanho tamanho, Model model) {
        String apiEndpoint = "http://localhost:8050/api/v1/tamanho/{tamanhoId}";
        try {
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Tamanho> requestEntity = new HttpEntity<>(tamanho, headers);

            // Faz a chamada para atualizar a bandeira usando o método HTTP POST
            ResponseEntity<Tamanho> responseEntity = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.PUT,
                    requestEntity,
                    Tamanho.class,
                    tamanhoId
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // Redireciona para a página de listagem após a atualização
                System.out.println("Status da resposta: " + responseEntity.getStatusCodeValue());
                System.out.println("Corpo da resposta: " + responseEntity.getBody());
                return "redirect:/tamanho";
            } else {
                System.out.println("Status da resposta: " + responseEntity.getStatusCodeValue());
                System.out.println("Corpo da resposta: " + responseEntity.getBody());
                // Trate os erros adequadamente
                model.addAttribute("mensagem", "Erro ao atualizar o Tamanho Else.");
                return "erros/deuruim3";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            e.printStackTrace();
            model.addAttribute("mensagem", "Erro ao atualizar o Tamanho Catch.");
            return "erros/deuruim3";
        }
    }

    @GetMapping("/excluir-tamanho/{tamanhoId}")
    public String deleteTamanho(@PathVariable Integer tamanhoId, Model model) {
        // Fetch the bandeira object based on the ID and add it to the model
        ResponseEntity<Tamanho> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/tamanho/getTamanhoById/{tamanhoId}",
                Tamanho.class,
                tamanhoId
        );
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // Faz a chamada para excluir a bandeira usando o método HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/tamanho/{tamanhoId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,  // O tipo de retorno é Void, pois o DELETE não retorna um corpo
                    tamanhoId
            );
            return "redirect:/tamanho";
        } else {
            // Handle case where bandeira is not found
            model.addAttribute("mensagem", "Erro ao excluir Tamnho.");
            return "erros/deuruim3"; // Or show an error page
        }
    }












}
