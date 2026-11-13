package com.example.apirestspringboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Terms", description = "Route dedicated for explain operation of lib apirestful.")
public class TermsController {
    @Operation(summary = "Get terms of service")
    @ApiResponse(responseCode = "200", description = "Returned terms of service")
    @GetMapping("/terms")
    public Map<String, Object> terms() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("título", "Termos de Uso — LIB84 API");
        response.put("versão", "1.0.0");
        response.put("status", "Em desenvolvimento");
        response.put("autor", "Henrique Maximo");
        response.put("introdução", "Ao utilizar a LIB84 API, " +
                "você concorda com os termos descritos abaixo. " +
                "Esta API é mantida por Henrique Maximo.");

        Map<String, Object> usoPermitido = new LinkedHashMap<>();
        usoPermitido.put("descrição", "A API deve ser utilizada apenas para fins legítimos.");
        usoPermitido.put("proibido", List.of(
                "Usar a API para fins ilegais ou não autorizados",
                "Interferir no funcionamento adequado do serviço",
                "Tentar acessar dados de outros usuários"
        ));
        response.put("uso_permitido", usoPermitido);

        Map<String, String> privacidade = new LinkedHashMap<>();
        privacidade.put("Armazenamento", "Os dados de perfil são armazenados exclusivamente para uso do próprio usuário.");

        response.put("isencao_responsabilidade", "A API é fornecida \"no estado em que se encontra\", sem garantias de qualquer tipo. O autor não se responsabiliza por danos decorrentes do uso ou incapacidade de uso do serviço.");

        response.put("licenca_codigo_fonte", "O código-fonte da LIB84 API é distribuído sob a licença MIT. Isso se aplica ao código em si, não ao serviço hospedado nem aos dados dos usuários.");

        Map<String, String> documentacao = new LinkedHashMap<>();
        documentacao.put("url_docs", "https://lib84-api.onrender.com/swagger-ui/index.html#/");
        documentacao.put("repositorio", "GitHub (https://github.com/Henrry-Maximo/lib84_api)");
        response.put("documentacao", documentacao);

        return response;
    }
}
