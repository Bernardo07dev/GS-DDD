package br.com.fiap.filainvisivel.controller;

import br.com.fiap.filainvisivel.bo.AlertaBo;
import br.com.fiap.filainvisivel.dto.request.AlertaRequest;
import br.com.fiap.filainvisivel.dto.response.AlertaResponse;
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
public class AlertaController {

    private final AlertaBo alertaBo;

    public AlertaController(AlertaBo alertaBo) {
        this.alertaBo = alertaBo;
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaResponse>> listarTodos() {
        return ResponseEntity.ok(alertaBo.listarTodos());
    }

    @GetMapping("/alertas/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(alertaBo.buscarPorId(id));
    }

    @GetMapping("/ubs/{ubsId}/alertas")
    public ResponseEntity<List<AlertaResponse>> listarPorUbs(@PathVariable String ubsId) {
        return ResponseEntity.ok(alertaBo.listarPorUbs(ubsId));
    }

    @PostMapping("/alertas")
    public ResponseEntity<AlertaResponse> criar(@Valid @RequestBody AlertaRequest request) {
        AlertaResponse criado = alertaBo.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/alertas/{id}")
    public ResponseEntity<AlertaResponse> atualizar(@PathVariable String id,
                                                    @Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.ok(alertaBo.atualizar(id, request));
    }

    @DeleteMapping("/alertas/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        alertaBo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
