package br.com.fiap.filainvisivel.controller;

import br.com.fiap.filainvisivel.bo.UbsBo;
import br.com.fiap.filainvisivel.dto.request.UbsRequest;
import br.com.fiap.filainvisivel.dto.response.MelhorHorarioResponse;
import br.com.fiap.filainvisivel.dto.response.UbsResponse;
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
@RequestMapping("/api/ubs")
public class UbsController {

    private final UbsBo ubsBo;

    public UbsController(UbsBo ubsBo) {
        this.ubsBo = ubsBo;
    }

    @GetMapping
    public ResponseEntity<List<UbsResponse>> listarTodos() {
        return ResponseEntity.ok(ubsBo.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UbsResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(ubsBo.buscarPorId(id));
    }

    @GetMapping("/{id}/melhor-horario")
    public ResponseEntity<MelhorHorarioResponse> recomendarMelhorHorario(@PathVariable String id) {
        return ResponseEntity.ok(ubsBo.recomendarMelhorHorario(id));
    }

    @PostMapping
    public ResponseEntity<UbsResponse> criar(@Valid @RequestBody UbsRequest request) {
        UbsResponse criada = ubsBo.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbsResponse> atualizar(@PathVariable String id, @Valid @RequestBody UbsRequest request) {
        return ResponseEntity.ok(ubsBo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        ubsBo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
