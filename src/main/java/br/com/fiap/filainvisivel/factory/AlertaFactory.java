package br.com.fiap.filainvisivel.factory;

import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.model.Alerta;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AlertaFactory {

    private AlertaFactory() {
    }

    public static Alerta criar(String id, String ubsId, String contatoBrutoOuAnonimo, String horarioPreferido) {
        String contatoAnonimizado = anonimizarContato(contatoBrutoOuAnonimo);
        return new Alerta(id, ubsId, contatoAnonimizado, horarioPreferido);
    }

    public static String anonimizarContato(String contato) {
        if (contato == null || contato.isBlank()) {
            throw new RegraNegocioException("O contato do cidadão é obrigatório.");
        }

        String limpo = contato.trim();
        if (limpo.startsWith("anon-") || limpo.contains("***")) {
            return limpo;
        }

        String hashCurto = gerarHashCurto(limpo);
        String sufixo = limpo.length() >= 2 ? limpo.substring(limpo.length() - 2) : limpo;
        return "anon-" + hashCurto + "-***" + sufixo;
    }

    private static String gerarHashCurto(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                hex.append(String.format("%02x", bytes[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível.", e);
        }
    }
}
