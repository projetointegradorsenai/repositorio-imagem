package com.projeto.integrador.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.integrador.dto.JogadorDTO;
import com.projeto.integrador.service.JogadorService;

@RestController
@RequestMapping("/jogador")
public class JogadorController {

    @Autowired
    private JogadorService jogadorService;

    private static final String UPLOAD_DIR = "src/main/resources/static/imagens/";  // Diretório onde as imagens serão salvas

    // Método POST - Criar jogador (com upload de imagem)
    @PostMapping("/adicionar")
    public ResponseEntity<Object> createUser (@RequestParam("nome") String nome,
                                             @RequestParam("idade") int idade,
                                             @RequestParam("time") String time,
                                             @RequestParam("selecao") String selecao,
                                             @RequestParam("camisa") int camisa,
                                             @RequestParam("foto") MultipartFile foto) {
        try {
            // Renomeia o arquivo para evitar conflito de nomes
            String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + nomeArquivo);
            Files.createDirectories(path.getParent());  // Cria os diretórios, se não existirem
            foto.transferTo(path);  // Salva o arquivo no diretório especificado

            // Criar o objeto Jogador
            JogadorDTO jogadorDTO = new JogadorDTO(null, nome, idade, time, selecao, camisa, nomeArquivo);
            JogadorDTO novoJogador = jogadorService.createUser (jogadorDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(novoJogador);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao fazer upload da imagem");
        }
    }

    // Método GET - Obter todos os jogadores
    @GetMapping
    public ResponseEntity<List<JogadorDTO>> getAllUsers() {
        List<JogadorDTO> jogadores = jogadorService.getAllJogadores();
        return ResponseEntity.ok(jogadores);
    }

    // Método PUT - Atualizar jogador (com upload de imagem)
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser (@PathVariable Long id,
                                             @RequestParam("nome") String nome,
                                             @RequestParam("idade") int idade,
                                             @RequestParam("time") String time,
                                             @RequestParam("selecao") String selecao,
                                             @RequestParam("camisa") int camisa,
                                             @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            JogadorDTO jogadorDTO = new JogadorDTO(id, nome, idade, time, selecao, camisa, null); // Inicializa foto com null

            // Verifica se foi enviada uma nova foto
            if (foto != null && !foto.isEmpty()) {
                String nomeArquivo = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + nomeArquivo);
                Files.createDirectories(path.getParent());
                foto.transferTo(path);
                jogadorDTO.setFoto(nomeArquivo);  // Se nova foto, setamos o nome do arquivo
            } else {
                // Caso não haja foto, manter a foto atual
                JogadorDTO jogadorExistente = jogadorService.getJogadorById(id);
                jogadorDTO.setFoto(jogadorExistente.getFoto());  // Não modifica a foto, apenas mantém
            }

            JogadorDTO jogadorAtualizado = jogadorService.updateUser (id, jogadorDTO);

            if (jogadorAtualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jogador não encontrado");
            }

            return ResponseEntity.ok(jogadorAtualizado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao fazer upload da imagem");
        }
    }

    // Método DELETE - Deletar jogador por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        boolean deleted = jogadorService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jogador não encontrado");
    }
}
