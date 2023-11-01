package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.model.Bandeira;
import com.neto.bahiafardamentos.model.CategoriaFardamento;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.model.Tamanho;
import com.neto.bahiafardamentos.repository.CategoriaFardamentoRepository;
import com.neto.bahiafardamentos.repository.TamanhoRepository;
import com.neto.bahiafardamentos.service.CategoriaFardamentoService;
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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CategoriaFardamentoController {

    private final RestTemplate restTemplate;
    private final TamanhoController tamanhoController;

    @Autowired
    public CategoriaFardamentoController(RestTemplate restTemplate, TamanhoController tamanhoController) {
        this.restTemplate = restTemplate;
        this.tamanhoController = tamanhoController;
    }

    @GetMapping("/categorias")
    public String categoria(Model model){
        String apiEndPoint = "http://localhost:8050/api/v1/categoria";

        try{
            List<CategoriaFardamento> categoriaList = restTemplate.exchange(
                    apiEndPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CategoriaFardamento>>(){
                    }
            ).getBody();
            if(categoriaList.isEmpty()){
                model.addAttribute("mensagem", "Nenhum Categoria encontrado");
            }else{
                model.addAttribute("categoriaList", categoriaList);
            }
        }catch (HttpClientErrorException.NotFound ex){
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma Categoria neste momento.");
        }
        return "categoria/categoria";
    }

    @GetMapping("/adicionar-categoria")
    public String showAddCategoriaForm(Model model){
        model.addAttribute("categoria", new CategoriaFardamento());
        List<Tamanho> tamanhoList = tamanhoController.obterTodosTamanhos();
        model.addAttribute("tamanhos", tamanhoList);
        return "categoria/create-categoria";
    }

    @PostMapping("/adicionar-categoria")
    public String addCategoria(@ModelAttribute CategoriaFardamento categoria, Model model){
        String apiEndpoint = "http://localhost:8050/api/v1/categoria";
        try{
            // Obtém os IDs dos tamanhos selecionados no formulário
            Set<Integer> tamanhoIds = categoria.getTamanhos().stream()
                            .map(Tamanho::getId)
                                    .collect(Collectors.toSet());
            CategoriaFardamentoService.NewCategoriaRequest request = new CategoriaFardamentoService.NewCategoriaRequest();
            request.setNome(categoria.getNome());
            request.setTamanhoIds(new ArrayList<>(tamanhoIds));
            restTemplate.postForObject(apiEndpoint, request, String.class);

            return "redirect:/categorias";
        } catch (Exception e) {
            // Trate os erros adequadamente
            model.addAttribute("mensagem", "Erro ao adicionar a tamanho.");
            return "erros/deuruim3";
        }
    }

    @GetMapping("/atualizar-categoria/{categoriaId}")
    public String showUpdateCategoriaForm(@PathVariable Integer categoriaId, Model model){
        ResponseEntity<CategoriaFardamento> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/categoria/getCategoriaById/{categoriaId}",
                CategoriaFardamento.class,
                categoriaId
        );
        List<Tamanho> tamanhos = tamanhoController.obterTodosTamanhos();

        if(tamanhos.isEmpty()) {
            model.addAttribute("mensagem", "Erro, Bandeira não localizada");
        }else{
            model.addAttribute("tamanhos", tamanhos);
        }
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            CategoriaFardamento categoria = responseEntity.getBody();
            model.addAttribute("categoria", categoria);

            // Obtém os IDs dos tamanhos associados à categoria
            Set<Integer> tamanhosAssociados = categoria.getTamanhos()
                    .stream()
                    .map(Tamanho::getId)
                    .collect(Collectors.toSet());

            model.addAttribute("tamanhosAssociados", tamanhosAssociados);
            System.out.println( "Ids, Tamanhos Associados" + tamanhosAssociados);

            return "categoria/update-categoria";
        }else{
            // Handle case where bandeira is not found
            return "redirect:/tamanho"; // Or show an error page
        }
    }

    @PostMapping("/atualizar-categoria/{categoriaId}")
    public String updateCategoria(@PathVariable Integer categoriaId,@ModelAttribute CategoriaFardamento categoria, Model model){
        if (categoriaId == null) {
            // Trate os IDs nulos adequadamente
            model.addAttribute("mensagem", "IDs não podem ser nulos");
            return "erros/deuruim3";
        }

        String apiEndpoint = "http://localhost:8050/api/v1/categoria/{categoriaId}";

        try{
            //Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CategoriaFardamento> requestEntity = new HttpEntity<>(categoria, headers);



            //Faz a chamada para atualizar o posto usando o metodo HTTP POST
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiEndpoint,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    categoriaId
            );

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                return "redirect:/categorias";
            }else{
                // Trate os erros adequadamente
                System.out.println("Erro ao atualizar a Categoria. Status: " + responseEntity.getStatusCodeValue());
                model.addAttribute("mensagem", "Erro ao atualizar a Categoria Else.");
                return "erros/deuruim3";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            System.out.println("CATCH Erro ao atualizar a Posto. Exceção: " + e.getMessage());
            e.printStackTrace();  // Adicione esta linha para imprimir o stack trace completo

            if (e instanceof HttpStatusCodeException) {
                System.out.println("Resposta do servidor: " + ((HttpStatusCodeException) e).getResponseBodyAsString());
            }

            model.addAttribute("mensagem", "Erro ao atualizar a Categoria Catch.");
            return "erros/deuruim3";
        }
    }
    @GetMapping("/excluir-categoria/{categoriaId}")
    public String deleteCategoria(@PathVariable Integer categoriaId, Model model){
        ResponseEntity<CategoriaFardamento> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/categoria/getCategoriaById/{categoriaId}",
                CategoriaFardamento.class,
                categoriaId
        );
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            //Faz a chamada para excluir o Colaborador usando o metodo HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/categoria/{categoriaId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,
                    categoriaId
            );

            return "redirect:/categorias";
        }else{
            model.addAttribute("mensagem", "Erro ao Excluir Categoria.");
            return "/erros/deuruim3"; // Or show an error page
        }
    }


}
