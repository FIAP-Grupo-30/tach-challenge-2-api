package tach_challenge_2.api_financeiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tach_challenge_2.api_financeiro.dto.TransacaoRequestDTO;
import tach_challenge_2.api_financeiro.model.TransacaoFinanceira;
import tach_challenge_2.api_financeiro.service.TransacaoService;

@RestController
@RequestMapping("/api/v1/financeiro/transacao")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody TransacaoRequestDTO dto) {
        TransacaoFinanceira transacaoFinanceiraSalva = transacaoService.criar(dto);
        return ResponseEntity.ok(transacaoFinanceiraSalva);
    }

    @GetMapping
    public String hello() {
        return "Hello, World!";
    }


}
