package br.com.fiap.filainvisivel.model;

public class Alerta {

    private String id;
    private String ubsId;
    private String contatoAnonimizado;
    private String horarioPreferido;

    public Alerta() {
    }

    public Alerta(String id, String ubsId, String contatoAnonimizado, String horarioPreferido) {
        this.id = id;
        this.ubsId = ubsId;
        this.contatoAnonimizado = contatoAnonimizado;
        this.horarioPreferido = horarioPreferido;
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

    public String getContatoAnonimizado() {
        return contatoAnonimizado;
    }

    public void setContatoAnonimizado(String contatoAnonimizado) {
        this.contatoAnonimizado = contatoAnonimizado;
    }

    public String getHorarioPreferido() {
        return horarioPreferido;
    }

    public void setHorarioPreferido(String horarioPreferido) {
        this.horarioPreferido = horarioPreferido;
    }
}
