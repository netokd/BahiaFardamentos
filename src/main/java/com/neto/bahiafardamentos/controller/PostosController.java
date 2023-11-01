package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.Posto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class PostosController {
    @Autowired
    private BandeiraController bandeiraController;
    private final RestTemplate restTemplate;
    @Autowired
    public PostosController(BandeiraController bandeiraController, RestTemplate restTemplate) {
        this.bandeiraController = bandeiraController;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/postos")
    public String postos(Model model){
        String apiEndPoint = "http://localhost:8050/api/v1/posto";

        try{
            List<Posto> postoList = restTemplate.exchange(
                    apiEndPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Posto>>(){
                    }).getBody();
            if(postoList.isEmpty()){
                model.addAttribute("mensagem", "Nenhum Posto encontrado");
            }else{
                model.addAttribute("postoList", postoList);
            }
        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma Posto neste momento.");
        }
        return "postos/postos";
    }

    public List<Posto> obterTodosPostos(){
        String apiEndpoint = "http://localhost:8050/api/v1/posto";
        try{
            List<Posto> postoList = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Posto>>() {
                    }).getBody();
            if(postoList.isEmpty()){
                return null;
            }else {
                return postoList;
            }
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }


    @GetMapping("/adicionar-posto")
    public String showAddPostoForm(Model model){
        List<Bandeira> bandeiras = bandeiraController.obteTodasBandeiras();

        if(bandeiras.isEmpty()){
            model.addAttribute("mensagem", "Erro, Bandeira não localizada");
            return "redirect:/postos";
        }else{
            model.addAttribute("posto", new Posto());
            model.addAttribute("bandeiras", bandeiras);
            return "postos/create-posto";
        }
    }

    @PostMapping("/adicionar-posto")
    public String addPosto(@ModelAttribute Posto posto, Model model) {
        String apiEndpoint = "http://localhost:8050/api/v1/posto";

        try {
            // Faça a chamada para adicionar a posto
            restTemplate.postForObject(apiEndpoint, posto, Posto.class);

            // Redireciona para a página de listagem após a adição
            return "redirect:/postos";
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao adicionar a posto.");
            return "postos/create-posto";
        }
    }
    @GetMapping("/atualizar-posto/{postoId}")
    public String showUpdateBandeiraForm(@PathVariable Integer postoId, Model model){
        ResponseEntity<Posto> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/posto/getPostoById/{postoId}",
                Posto.class,
                postoId
        );
        List<Bandeira> bandeiras = bandeiraController.obteTodasBandeiras();

        if(bandeiras.isEmpty()) {
            model.addAttribute("mensagem", "Erro, Bandeira não localizada");
        }else{
            model.addAttribute("bandeiras", bandeiras);
        }
        if(responseEntity.getStatusCode() ==HttpStatus.OK){
            Posto posto = responseEntity.getBody();
            model.addAttribute("posto", posto);

            return "postos/update-posto";
        }else{
            // Handle case where bandeira is not found
            return "redirect:/bandeira"; // Or show an error page
        }
    }
    @PostMapping("/atualizar-posto/{postoId}")
    public String updatePosto(@PathVariable Integer postoId, @ModelAttribute Posto posto, Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/posto/{postoId}";

        try{
            //Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Posto> requestEntity = new HttpEntity<>(posto, headers);

            //Faz a chamada para atualizar o posto usando o metodo HTTP POST

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    postoId
            );

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                return "redirect:/postos";
            }else{
                // Trate os erros adequadamente
                System.out.println("Erro ao atualizar a Posto. Status: " + responseEntity.getStatusCodeValue());
                model.addAttribute("mensagem", "Erro ao atualizar a Posto.");
                return "redirect:/atualizar-posto/{postoId}";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            System.out.println("CATCH Erro ao atualizar a Posto. Exceção: " + e.getMessage());
            e.printStackTrace();  // Adicione esta linha para imprimir o stack trace completo

            if (e instanceof HttpStatusCodeException) {
                System.out.println("Resposta do servidor: " + ((HttpStatusCodeException) e).getResponseBodyAsString());
            }

            model.addAttribute("mensagem", "Erro ao atualizar a Posto.");
            return "redirect:/atualizar-posto/{postoId}";
        }
    }

    @GetMapping("/excluir-posto/{postoId}")
    public String deletePosto(@PathVariable Integer postoId, Model model){
        ResponseEntity<Posto> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/posto/getPostoById/{postoId}",
                Posto.class,
                postoId
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // Faz a chamada para excluir o Posto usando o método HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/posto/{postoId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,  // O tipo de retorno é Void, pois o DELETE não retorna um corpo
                    postoId
            );

            return "redirect:/postos";
        } else {

            // Handle case where posto is not found
            return "redirect:/postos"; // Or show an error page
        }
    }

}
