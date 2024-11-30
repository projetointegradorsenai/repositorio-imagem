package com.projeto.integrador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projeto.integrador.dto.JogadorDTO;
import com.projeto.integrador.service.JogadorService;

import java.util.List;

@RestController
@RequestMapping("/jogador")
public class JogadorController {

    @Autowired
    private JogadorService jogadorService;

    
    @PostMapping("/adicionar")
    public ResponseEntity<Object> createUser(@RequestParam("nome") String nome,
                                             @RequestParam("idade") int idade,
                                             @RequestParam("time") String time,
                                             @RequestParam("selecao") String selecao,
                                             @RequestParam("camisa") int camisa,
                                             @RequestParam("foto") String fotoURL) { 
        try {
            JogadorDTO jogadorDTO = new JogadorDTO(null, nome, idade, time, selecao, camisa, fotoURL);
            JogadorDTO novoJogador = jogadorService.createUser(jogadorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoJogador);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar a criação do jogador");
        }
    }

   
    @GetMapping
    public ResponseEntity<List<JogadorDTO>> getAllUsers() {
        List<JogadorDTO> jogadores = jogadorService.getAllJogadores();
        return ResponseEntity.ok(jogadores);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @RequestParam("nome") String nome,
                                             @RequestParam("idade") int idade,
                                             @RequestParam("time") String time,
                                             @RequestParam("selecao") String selecao,
                                             @RequestParam("camisa") int camisa,
                                             @RequestParam(value = "foto", required = false) String fotoURL) {  // URL da foto
        try {
            JogadorDTO jogadorDTO = new JogadorDTO(id, nome, idade, time, selecao, camisa, fotoURL);
            JogadorDTO jogadorAtualizado = jogadorService.updateUser(id, jogadorDTO);
            return ResponseEntity.ok(jogadorAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar jogador");
        }
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            jogadorService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao excluir jogador");
        }
    }
}
