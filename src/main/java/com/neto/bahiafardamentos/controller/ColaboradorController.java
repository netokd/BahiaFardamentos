package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.dto.ColaboradorDTO;
import com.neto.bahiafardamentos.model.Colaborador;
import com.neto.bahiafardamentos.model.Posto;
import com.neto.bahiafardamentos.repository.ColaboradorRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
public class ColaboradorController {
    private final RestTemplate restTemplate;
    private PostosController postosController;
    private ColaboradorRepository colaboradorRepository;

    public ColaboradorController(RestTemplate restTemplate, PostosController postosController, ColaboradorRepository colaboradorRepository) {
        this.restTemplate = restTemplate;
        this.postosController = postosController;
        this.colaboradorRepository = colaboradorRepository;
    }

    @GetMapping("/colaborador")
    public String postos(Model model){
        String apiEndPoint = "http://localhost:8050/api/v1/colaborador";

        try{
            List<Colaborador> colaboradorList = restTemplate.exchange(
                    apiEndPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Colaborador>>(){
                    }).getBody();
            if(colaboradorList.isEmpty()){
                model.addAttribute("mensagem", "Nenhum Colaborador encontrado");
            }else{
                model.addAttribute("colaboradorList", colaboradorList);
            }
        } catch (HttpClientErrorException.NotFound ex) {
            model.addAttribute("mensagem", "Desculpe, não foi possível encontrar nenhuma Colaborador neste momento.");
        }
        return "colaborador/colaborador";
    }
    @GetMapping("/adicionar-colaborador")
    public String showAddColaboradorForm(Model model){
        List<Posto> posto = postosController.obterTodosPostos();

        if(posto.isEmpty()){
            model.addAttribute("mensagem", "Erro, Bandeira não localizada");
            return "redirect:/colaborador";
        }else{
            model.addAttribute("colaborador", new Colaborador());
            model.addAttribute("posto", posto);
            return "colaborador/create-colaborador";
        }
    }
    @PostMapping("/adicionar-colaborador")
    @Transactional
    public String addColaborador(@ModelAttribute @Valid Colaborador colaborador, BindingResult bindingResult, Model model) {
        String apiEndpoint = "http://localhost:8050/api/v1/colaborador";

        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário com os erros
            System.out.println("BindingResult.");
            System.out.println("Erro ao adicionar o colaborador. Erros de validação: " + bindingResult.getAllErrors());
            return "colaborador/deuruim";
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dataContratacaoString = colaborador.getDataContratacaoString();

            //Testa se Data do Ultimo Envio e nulo ou não
            try {
                if (dataContratacaoString != null && !dataContratacaoString.isEmpty()) {
                    colaborador.setDataContratacao(dateFormat.parse(dataContratacaoString));
                } else {
                    // Se a string de data estiver ausente ou vazia, defina uma data padrão
                    colaborador.setDataContratacao(dateFormat.parse("2000-01-01"));
                }
            } catch (ParseException | NullPointerException e) {
                // Lidar com a exceção de análise aqui
                e.printStackTrace(); // Apenas para depuração, considere logar a exceção
                // Se necessário, você pode adicionar uma mensagem de erro ao modelo
                model.addAttribute("mensagem", "Erro ao analisar a data de contratação.");
                return "colaborador/deuruim2";
            }
            //Testa se Data do Ultimo Envio e nulo ou não
            String dataUltimoKitEnviadoString = colaborador.getDataUltimoKitEnviadoString();
            try {
                if (dataUltimoKitEnviadoString != null && !dataUltimoKitEnviadoString.isEmpty()) {
                    colaborador.setDataUltimoKitEnviado(dateFormat.parse(dataUltimoKitEnviadoString));
                }
            } catch (ParseException | NullPointerException e) {
                // Lidar com a exceção de análise aqui
                e.printStackTrace(); // Apenas para depuração, considere logar a exceção
                // Se necessário, você pode adicionar uma mensagem de erro ao modelo
                model.addAttribute("mensagem", "Erro ao analisar a data de Envio de Kit.");
                return "colaborador/deuruim2";
            }
            // Faça a chamada para adicionar o colaborador
            ColaboradorDTO colaboradorDTO = new ColaboradorDTO();
            colaboradorDTO.setNome(colaborador.getNome());
            colaboradorDTO.setCargo(colaborador.getCargo());
            colaboradorDTO.setDataContratacao(colaborador.getDataContratacao());
            colaboradorDTO.setDataUltimoKitEnviado(colaborador.getDataUltimoKitEnviado());

            colaboradorDTO.setPostoId(colaborador.getPosto().getId());

            restTemplate.postForObject(apiEndpoint, colaboradorDTO, Colaborador.class);

            System.out.println("Após a solicitação para adicionar colaborador.");

            // Redireciona para a página de listagem após a adição
            return "redirect:/colaborador";
        } catch (Exception e) {
            // Adicione logs para depuração
            e.printStackTrace();
            model.addAttribute("mensagem", "Erro ao adicionar o colaborador. Detalhes do erro: " + e.getMessage());
            return "colaborador/deuruim3";
        }
    }

    @GetMapping("/atualizar-colaborador/{colaboradorId}")
    public String showUpdateColaboradorForm(@PathVariable Integer colaboradorId, Model model) {
        ResponseEntity<Colaborador> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/colaborador/getColaboradorById/{colaboradorId}",
                Colaborador.class,
                colaboradorId
        );
        List<Posto> postos = postosController.obterTodosPostos();
        if (postos.isEmpty()) {

            model.addAttribute("mensagem", "Posto não encontrado");
            return "colaborador/deuruim";

        } else {
            model.addAttribute("posto", postos);
        }
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            Colaborador colaborador = responseEntity.getBody();
            model.addAttribute("colaborador", colaborador);
            return "colaborador/update-colaborador";
        }else{
            model.addAttribute("mensagem", "Colaborador não encontrado");
            return "colaborador/deuruim3";
        }



    }

    @PostMapping("/atualizar-colaborador/{colaboradorId}")
    public String updateColaborador(
            @PathVariable Integer colaboradorId,
            @ModelAttribute @Valid Colaborador colaborador,
            Model model
    ) {
        String apiEndpoint = "http://localhost:8050/api/v1/colaborador/{colaboradorId}";
        try{
            //Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Colaborador> requestEntity = new HttpEntity<>(colaborador,headers);

            System.out.println("colaborador passado para a Entity" + colaborador.toString());
            System.out.println("Entity passada" + requestEntity);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
              apiEndpoint,
              HttpMethod.PUT,
              requestEntity,
              String.class,
              colaboradorId
            );

            if(responseEntity.getStatusCode() == HttpStatus.OK){
                return "redirect:/colaborador";
            }else{
                // Trate os erros adequadamente
                System.out.println("Erro ao atualizar a Colaborador. Status: " + responseEntity.getStatusCodeValue());
                model.addAttribute("mensagem", "Erro ao atualizar a Colaborador.");
                return "redirect:/atualizar-colaborador/{colaboradorId}";
            }
        } catch (Exception e) {
            // Trate os erros adequadamente
            System.out.println("CATCH Erro ao atualizar a Colaborador. Exceção: " + e.getMessage());
            e.printStackTrace();  // Adicione esta linha para imprimir o stack trace completo

            if (e instanceof HttpStatusCodeException) {
                System.out.println("Resposta do servidor: " + ((HttpStatusCodeException) e).getResponseBodyAsString());
            }

            model.addAttribute("mensagem", "Erro ao atualizar a Posto.");
            return "redirect:/atualizar-colaborador/{colaboradorId}";
        }
    }

    @GetMapping("/excluir-colaborador/{colaboradorId}")
    public String deleteColaborador(@PathVariable Integer colaboradorId, Model model){
        ResponseEntity<Colaborador> responseEntity = restTemplate.getForEntity(
                "http://localhost:8050/api/v1/colaborador/getColaboradorById/{colaboradorId}",
                Colaborador.class,
                colaboradorId
        );

        if(responseEntity.getStatusCode() == HttpStatus.OK){
            // Configuração da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //Cria o objeto HttpEntity com as headers
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            //Faz a chamada para excluir o Colaborador usando o metodo HTTP DELETE
            restTemplate.exchange(
                    "http://localhost:8050/api/v1/colaborador/{colaboradorId}",
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class,
                    colaboradorId
            );

            return "redirect:/colaborador";
        }else{
                model.addAttribute("mensagem", "Erro ao Excluir Colaborador.");
                return "/colaborador/deuruim3"; // Or show an error page
            }
    }
}





