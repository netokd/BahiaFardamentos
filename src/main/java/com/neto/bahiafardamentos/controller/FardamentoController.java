package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.model.*;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class FardamentoController {

    private final RestTemplate restTemplate;
    private final BandeiraController bandeiraController;
    private final CategoriaFardamentoController categoriaFardamentoController;
    @Autowired
    public FardamentoController(RestTemplate restTemplate, BandeiraController bandeiraController, CategoriaFardamentoController categoriaFardamentoController) {
        this.restTemplate = restTemplate;
        this.bandeiraController = bandeiraController;
        this.categoriaFardamentoController = categoriaFardamentoController;
    }




    @GetMapping("/fardamento")
    public String fardamento(Model model){
        String apiEndPoint = "http://localhost:8050/api/v1/fardamento";

        try{
            List<Fardamento> fardamentoList = restTemplate.exchange(
                    apiEndPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Fardamento>>(){
                    }).getBody();
            if(fardamentoList.isEmpty()){
                model.addAttribute("mensagem", "Nenhum Tamanho encontrado");
            }else{
                model.addAttribute("fardamentoList", fardamentoList);
            }
        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma Tamanho neste momento.");
        }
        return "fardamento/fardamento";
    }

    public List<Fardamento> obterTodosFardamentos(){
        String apiEndpoint = "http://localhost:8050/api/v1/fardamento";

        try{
            List<Fardamento> fardamentoList = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Fardamento>>() {
                    }).getBody();
            if(fardamentoList.isEmpty()){
                return null;
            }else {
                return fardamentoList;
            }
        }catch (HttpClientErrorException.NotFound ex) {
            return null;
        }
    }
    @GetMapping("/adicionar-fardamento")
    public String showAddFardamentoForm(Model model){
        List<Bandeira> bandeiras = bandeiraController.obterTodasBandeiras();
        List<CategoriaFardamento> categorias = categoriaFardamentoController.obterTodosCategorias();

        if(bandeiras.isEmpty() || categorias.isEmpty()){
            model.addAttribute("mensagem", "Erro, Bandeira ou Categoria não localizada");
            return "redirect:/fardamento";
        }else{
            model.addAttribute("fardamento", new Fardamento());
            model.addAttribute("bandeiras", bandeiras);
            model.addAttribute("categorias", categorias);
            return "fardamento/create-fardamento";
        }
    }
    @PostMapping("/adicionar-fardamento")
    public String addFardamento(@ModelAttribute Fardamento fardamento, Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/fardamento";

        try {
            // Faça a chamada para adicionar a posto
            restTemplate.postForObject(apiEndpoint, fardamento, Fardamento.class);

            // Redireciona para a página de listagem após a adição
            return "redirect:/fardamento";
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao adicionar a fardamento.");
            return "erros/deuruim3";
        }
    }

    @GetMapping("/atualizar-fardamento/{fardamentoId}")
    public String showUpdateFardamentoForm(@PathVariable Integer fardamentoId, Model model){
        ResponseEntity<Fardamento> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/fardamento/getFardamentoById/{fardamentoId}",
                Fardamento.class,
                fardamentoId
        );
        List<Bandeira> bandeiras = bandeiraController.obterTodasBandeiras();
        List<CategoriaFardamento> categorias = categoriaFardamentoController.obterTodosCategorias();

        if(bandeiras.isEmpty() || categorias.isEmpty()) {
            model.addAttribute("mensagem", "Erro, Bandeira ou Categoria não localizada");
        }else{
            model.addAttribute("bandeiras", bandeiras);
        }
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            Fardamento fardamento = responseEntity.getBody();
            model.addAttribute("fardamento", fardamento);
            model.addAttribute("categorias", categorias);

            return "fardamento/update-fardamento";
        }else{
            // Handle case where bandeira is not found
            return "erros/deuruim3"; // Or show an error page
        }
    }

    @PostMapping("/atualizar-fardamento/{fardamentoId}")
    public String updateFardamento(@PathVariable Integer fardamentoId, @ModelAttribute Fardamento fardamento, Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/fardamento/{fardamentoId}";

        try{
            //Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Fardamento> requestEntity = new HttpEntity<>(fardamento, headers);

            //Faz a chamada para atualizar o posto usando o metodo HTTP POST

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.POST,
                    requestEntity,
                    String.class,
                    fardamentoId
            );

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                return "redirect:/fardamento";
            }else{
                // Trate os erros adequadamente
                System.out.println("Erro ao atualizar a Farda. Status: " + responseEntity.getStatusCodeValue());
                model.addAttribute("mensagem", "Erro ao atualizar a Farda.");
                return "erros/deuruim3";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            System.out.println("CATCH Erro ao atualizar a Posto. Exceção: " + e.getMessage());
            e.printStackTrace();  // Adicione esta linha para imprimir o stack trace completo

            if (e instanceof HttpStatusCodeException) {
                System.out.println("Resposta do servidor: " + ((HttpStatusCodeException) e).getResponseBodyAsString());
            }

            model.addAttribute("mensagem", "Erro ao atualizar a Farda.");
            return "erros/deuruim3";
        }
    }
    @GetMapping("/excluir-fardamento/{fardamentoId}")
    public String deletePosto(@PathVariable Integer fardamentoId, Model model){
        ResponseEntity<Fardamento> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/fardamento/getFardamentoById/{fardamentoId}",
                Fardamento.class,
                fardamentoId
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            // Faz a chamada para excluir o Posto usando o método HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/fardamento/{fardamentoId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,  // O tipo de retorno é Void, pois o DELETE não retorna um corpo
                    fardamentoId
            );

            return "redirect:/fardamento";
        } else {

            // Handle case where posto is not found
            return "erros/deuruim3"; // Or show an error page
        }
    }





















}
