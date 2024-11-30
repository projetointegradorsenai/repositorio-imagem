package com.projeto.integrador.dto;

import com.projeto.integrador.model.Jogador;

public class JogadorDTO {

    private Long id;
    private String nome;
    private int idade;
    private String time;
    private String selecao;
    private int camisa;
    private String foto;  

    // Construtores
    public JogadorDTO() {}

    public JogadorDTO(Long id, String nome, int idade, String time, String selecao, int camisa, String foto) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.time = time;
        this.selecao = selecao;
        this.camisa = camisa;
        this.foto = foto;
    }

    
    public JogadorDTO(Jogador jogador) {
        this.id = jogador.getId();
        this.nome = jogador.getNome();
        this.idade = jogador.getIdade();
        this.time = jogador.getTime();
        this.selecao = jogador.getSelecao();
        this.camisa = jogador.getNumeroCamisa();
        this.foto = jogador.getFoto();  
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSelecao() {
        return selecao;
    }

    public void setSelecao(String selecao) {
        this.selecao = selecao;
    }

    public int getCamisa() {
        return camisa;
    }

    public void setCamisa(int camisa) {
        this.camisa = camisa;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
