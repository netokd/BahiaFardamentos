package com.neto.bahiafardamentos.controller;

import com.neto.bahiafardamentos.dto.ColaboradorDTO;
import com.neto.bahiafardamentos.model.Colaborador;
import com.neto.bahiafardamentos.model.Posto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class ColaboradorController {
    private final RestTemplate restTemplate;
    private PostosController postosController;

    public ColaboradorController(RestTemplate restTemplate, PostosController postosController) {
        this.restTemplate = restTemplate;
        this.postosController = postosController;
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

            // Adicione logs para depuração
            System.out.println("Enviando solicitação para adicionar colaborador: " + colaborador);
            // Adiciona logs para depuração
            System.out.println("Data de Contratação convertida: " + colaborador.getDataContratacao());
            System.out.println("Data do Último Kit Enviado convertida: " + colaborador.getDataUltimoKitEnviado());

            // Adiciona logs antes e após a chamada ao método postForObject
            System.out.println("Antes de enviar a solicitação para adicionar colaborador.");

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



}
