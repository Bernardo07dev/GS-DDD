package br.com.fiap.filainvisivel.model;

import java.time.LocalDateTime;

public class Previsao {

    private String id;
    private String ubsId;
    private LocalDateTime dataHora;
    private double probLotacao;
    private int esperaEstimadaMin;
    private Semaforo semaforo;

    public Previsao() {
    }

    public Previsao(String id, String ubsId, LocalDateTime dataHora, double probLotacao, int esperaEstimadaMin, Semaforo semaforo) {
        this.id = id;
        this.ubsId = ubsId;
        this.dataHora = dataHora;
        this.probLotacao = probLotacao;
        this.esperaEstimadaMin = esperaEstimadaMin;
        this.semaforo = semaforo;
    }

    public String orientarCidadao() {
        String descricaoSemaforo = semaforo != null ? semaforo.getOrientacao() : "";
        return "Semáforo " + semaforo + ": " + descricaoSemaforo
                + ". Espera estimada de " + esperaEstimadaMin + " minutos.";
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

    public double getProbLotacao() {
        return probLotacao;
    }

    public void setProbLotacao(double probLotacao) {
        this.probLotacao = probLotacao;
    }

    public int getEsperaEstimadaMin() {
        return esperaEstimadaMin;
    }

    public void setEsperaEstimadaMin(int esperaEstimadaMin) {
        this.esperaEstimadaMin = esperaEstimadaMin;
    }

    public Semaforo getSemaforo() {
        return semaforo;
    }

    public void setSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }
}
