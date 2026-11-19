package br.com.fiap.filainvisivel.controller;

import br.com.fiap.filainvisivel.bo.PrevisaoBo;
import br.com.fiap.filainvisivel.dto.request.PrevisaoRequest;
import br.com.fiap.filainvisivel.dto.response.PrevisaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PrevisaoController {

    private final PrevisaoBo previsaoBo;

    public PrevisaoController(PrevisaoBo previsaoBo) {
        this.previsaoBo = previsaoBo;
    }

    @GetMapping("/previsoes")
    public ResponseEntity<List<PrevisaoResponse>> listarTodos() {
        return ResponseEntity.ok(previsaoBo.listarTodos());
    }

    @GetMapping("/previsoes/{id}")
    public ResponseEntity<PrevisaoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(previsaoBo.buscarPorId(id));
    }

    @GetMapping("/ubs/{ubsId}/previsoes")
    public ResponseEntity<List<PrevisaoResponse>> listarPorUbs(@PathVariable String ubsId) {
        return ResponseEntity.ok(previsaoBo.listarPorUbs(ubsId));
    }

    @PostMapping("/previsoes")
    public ResponseEntity<PrevisaoResponse> criar(@Valid @RequestBody PrevisaoRequest request) {
        PrevisaoResponse criada = previsaoBo.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/previsoes/{id}")
    public ResponseEntity<PrevisaoResponse> atualizar(@PathVariable String id,
                                                      @Valid @RequestBody PrevisaoRequest request) {
        return ResponseEntity.ok(previsaoBo.atualizar(id, request));
    }

    @DeleteMapping("/previsoes/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        previsaoBo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
