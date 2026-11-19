package br.com.fiap.filainvisivel.controller;

import br.com.fiap.filainvisivel.bo.RegistroFilaBo;
import br.com.fiap.filainvisivel.dto.request.RegistroFilaRequest;
import br.com.fiap.filainvisivel.dto.response.RegistroFilaResponse;
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
public class RegistroFilaController {

    private final RegistroFilaBo registroFilaBo;

    public RegistroFilaController(RegistroFilaBo registroFilaBo) {
        this.registroFilaBo = registroFilaBo;
    }

    @GetMapping("/registros-fila")
    public ResponseEntity<List<RegistroFilaResponse>> listarTodos() {
        return ResponseEntity.ok(registroFilaBo.listarTodos());
    }

    @GetMapping("/registros-fila/{id}")
    public ResponseEntity<RegistroFilaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(registroFilaBo.buscarPorId(id));
    }

    @GetMapping("/ubs/{ubsId}/registros-fila")
    public ResponseEntity<List<RegistroFilaResponse>> listarPorUbs(@PathVariable String ubsId) {
        return ResponseEntity.ok(registroFilaBo.listarPorUbs(ubsId));
    }

    @PostMapping("/registros-fila")
    public ResponseEntity<RegistroFilaResponse> criar(@Valid @RequestBody RegistroFilaRequest request) {
        RegistroFilaResponse criado = registroFilaBo.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/registros-fila/{id}")
    public ResponseEntity<RegistroFilaResponse> atualizar(@PathVariable String id,
                                                          @Valid @RequestBody RegistroFilaRequest request) {
        return ResponseEntity.ok(registroFilaBo.atualizar(id, request));
    }

    @DeleteMapping("/registros-fila/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        registroFilaBo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
