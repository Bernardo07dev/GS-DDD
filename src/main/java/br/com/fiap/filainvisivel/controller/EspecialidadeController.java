package br.com.fiap.filainvisivel.controller;

import br.com.fiap.filainvisivel.bo.EspecialidadeBo;
import br.com.fiap.filainvisivel.dto.request.EspecialidadeRequest;
import br.com.fiap.filainvisivel.dto.response.EspecialidadeResponse;
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
@RequestMapping("/api/especialidades")
public class EspecialidadeController {

    private final EspecialidadeBo especialidadeBo;

    public EspecialidadeController(EspecialidadeBo especialidadeBo) {
        this.especialidadeBo = especialidadeBo;
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponse>> listarTodos() {
        return ResponseEntity.ok(especialidadeBo.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(especialidadeBo.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponse> criar(@Valid @RequestBody EspecialidadeRequest request) {
        EspecialidadeResponse criada = especialidadeBo.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> atualizar(@PathVariable String id,
                                                           @Valid @RequestBody EspecialidadeRequest request) {
        return ResponseEntity.ok(especialidadeBo.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        especialidadeBo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
