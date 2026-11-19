package br.com.fiap.filainvisivel.model;

import java.time.LocalDateTime;

public class RegistroFila {

    private String id;
    private String ubsId;
    private LocalDateTime dataHora;
    private int pessoasNaFila;
    private int tempoEsperaMin;

    public RegistroFila() {
    }

    public RegistroFila(String id, String ubsId, LocalDateTime dataHora, int pessoasNaFila, int tempoEsperaMin) {
        this.id = id;
        this.ubsId = ubsId;
        this.dataHora = dataHora;
        this.pessoasNaFila = pessoasNaFila;
        this.tempoEsperaMin = tempoEsperaMin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUbsId() {
        return ubsId;
    }

    public void setUbsId(String ubsId) {
        this.ubsId = ubsId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getPessoasNaFila() {
        return pessoasNaFila;
    }

    public void setPessoasNaFila(int pessoasNaFila) {
        this.pessoasNaFila = pessoasNaFila;
    }

    public int getTempoEsperaMin() {
        return tempoEsperaMin;
    }

    public void setTempoEsperaMin(int tempoEsperaMin) {
        this.tempoEsperaMin = tempoEsperaMin;
    }
}
