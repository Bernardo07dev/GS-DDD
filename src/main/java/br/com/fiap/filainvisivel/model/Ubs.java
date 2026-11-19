package br.com.fiap.filainvisivel.model;

import java.util.ArrayList;
import java.util.List;

public class Ubs {

    private String id;
    private String nome;
    private String bairro;
    private int capacidade;
    private List<Especialidade> especialidades;

    public Ubs() {
        this.especialidades = new ArrayList<>();
    }

    public Ubs(String id, String nome, String bairro, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.bairro = bairro;
        this.capacidade = capacidade;
        this.especialidades = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public List<Especialidade> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidade> especialidades) {
        this.especialidades = especialidades != null ? especialidades : new ArrayList<>();
    }
}
