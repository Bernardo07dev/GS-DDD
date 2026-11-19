package br.com.fiap.filainvisivel.factory;

import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.model.Previsao;
import br.com.fiap.filainvisivel.model.Semaforo;

import java.time.LocalDateTime;

public class PrevisaoFactory {

    public static final double LIMITE_VERDE = 0.40;
    public static final double LIMITE_AMARELO = 0.70;

    private PrevisaoFactory() {
    }

    public static Previsao criar(String id, String ubsId, LocalDateTime dataHora, double probLotacao, int esperaEstimadaMin) {
        Semaforo semaforo = calcularSemaforo(probLotacao);
        return new Previsao(id, ubsId, dataHora, probLotacao, esperaEstimadaMin, semaforo);
    }

    public static Semaforo calcularSemaforo(double probLotacao) {
        if (probLotacao < 0.0 || probLotacao > 1.0) {
            throw new RegraNegocioException("A probabilidade de lotação deve estar entre 0.0 e 1.0.");
        }
        if (probLotacao < LIMITE_VERDE) {
            return Semaforo.VERDE;
        }
        if (probLotacao < LIMITE_AMARELO) {
            return Semaforo.AMARELO;
        }
        return Semaforo.VERMELHO;
    }
}
