package modelo;

import java.util.Date;

public class Evento {
    private int id;
    private String nome;
    private String caminhoFt;
    private double valor;
    private Date cadastro;
    private Date valida;
    private Date ocorreu;

    public Evento(int id, String nome, String caminhoFt, double valor, Date cadastro, Date valida, Date ocorreu) {
        this.id = id;
        this.nome = nome;
        this.caminhoFt = caminhoFt;
        this.valor = valor;
        this.cadastro = cadastro;
        this.valida = valida;
        this.ocorreu = ocorreu;
    }

    public Evento(String nome, String caminhoFt, double valor, Date cadastro, Date valida, Date ocorreu) {
        this.nome = nome;
        this.caminhoFt = caminhoFt;
        this.valor = valor;
        this.cadastro = cadastro;
        this.valida = valida;
        this.ocorreu = ocorreu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminhoFt() {
        return caminhoFt;
    }

    public void setCaminhoFt(String caminhoFt) {
        this.caminhoFt = caminhoFt;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getCadastro() {
        return cadastro;
    }

    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }

    public Date getValida() {
        return valida;
    }

    public void setValida(Date valida) {
        this.valida = valida;
    }

    public Date getOcorreu() {
        return ocorreu;
    }

    public void setOcorreu(Date ocorreu) {
        this.ocorreu = ocorreu;
    }
}
