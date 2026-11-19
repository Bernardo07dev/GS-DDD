package br.com.fiap.filainvisivel.model;

public enum Semaforo {

    VERDE("Baixa lotação — ótimo momento para ir à UBS"),
    AMARELO("Lotação moderada — espera intermediária"),
    VERMELHO("Alta lotação — evite este horário se não for urgente");

    private final String orientacao;

    Semaforo(String orientacao) {
        this.orientacao = orientacao;
    }

    public String getOrientacao() {
        return orientacao;
    }
}
