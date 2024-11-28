package com.projeto.integrador.model;

import com.projeto.integrador.dto.JogadorDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "jogadores")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int idade;
    private String time;
    private String selecao;
    private Integer numeroCamisa;  
    private String foto;  // Novo campo para armazenar o nome do arquivo da imagem

    // Construtor para converter DTO para Entidade
    public Jogador(JogadorDTO jogadorDTO) {
        this.id = jogadorDTO.getId();
        this.nome = jogadorDTO.getNome();
        this.idade = jogadorDTO.getIdade();
        this.time = jogadorDTO.getTime();
        this.selecao = jogadorDTO.getSelecao();
        this.numeroCamisa = jogadorDTO.getCamisa();
        this.foto = jogadorDTO.getFoto();  // Atribui o nome da foto do DTO
    }
    
    // Construtor para permitir a criação de uma instância de Jogador com todos os campos
    public Jogador(Long id, String nome, int idade, String time, String selecao, Integer numeroCamisa, String foto) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.time = time;
        this.selecao = selecao;
        this.numeroCamisa = numeroCamisa;
        this.foto = foto;
    }
    
    // Getters e Setters
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

    public int getNumeroCamisa() {
        return numeroCamisa;
    }

    public void setNumeroCamisa(int numeroCamisa) {
        this.numeroCamisa = numeroCamisa;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
