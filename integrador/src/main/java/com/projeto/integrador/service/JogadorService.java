package com.projeto.integrador.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.integrador.dto.JogadorDTO;
import com.projeto.integrador.model.Jogador;
import com.projeto.integrador.repository.JogadorRepository;

@Service
public class JogadorService {

    @Autowired
    private JogadorRepository jogadorRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/src/imagens/";  // Diretório onde as imagens serão salvas

    // Método para buscar todos os jogadores
    public List<JogadorDTO> getAllJogadores() {
        List<Jogador> jogadores = jogadorRepository.findAll();
        return jogadores.stream()
                .map(JogadorDTO::new)  // Converte cada Jogador em JogadorDTO
                .collect(Collectors.toList());
    }

    // Método para criar um novo jogador
    public JogadorDTO createUser(JogadorDTO jogadorDTO) {
        Jogador jogador = new Jogador(jogadorDTO);
        jogador.setFoto(jogadorDTO.getFoto());  // Armazena o nome do arquivo da imagem
        Jogador jogadorCriado = jogadorRepository.save(jogador);
        return new JogadorDTO(jogadorCriado);
    }

    // Método para atualizar um jogador
    public JogadorDTO updateUser(Long id, JogadorDTO jogadorDTO) {
        Optional<Jogador> jogadorExistente = jogadorRepository.findById(id);
        if (jogadorExistente.isPresent()) {
            Jogador jogador = jogadorExistente.get();
            jogador.setNome(jogadorDTO.getNome());
            jogador.setIdade(jogadorDTO.getIdade());
            jogador.setTime(jogadorDTO.getTime());
            jogador.setSelecao(jogadorDTO.getSelecao());
            jogador.setNumeroCamisa(jogadorDTO.getCamisa());
            jogador.setFoto(jogadorDTO.getFoto());  // Atualiza a foto
            Jogador jogadorAtualizado = jogadorRepository.save(jogador);
            return new JogadorDTO(jogadorAtualizado);
        }
        return null;
    }

    // Método para excluir um jogador
    public boolean deleteUser(Long id) {
        Optional<Jogador> jogadorExistente = jogadorRepository.findById(id);
        if (jogadorExistente.isPresent()) {
            Jogador jogador = jogadorExistente.get();
            // Exclui o arquivo de imagem associado ao jogador
            String fotoPath = UPLOAD_DIR + jogador.getFoto();
            try {
                Files.deleteIfExists(Paths.get(fotoPath));  // Apaga a imagem se existir
            } catch (IOException e) {
                System.out.println("Erro ao excluir imagem: " + e.getMessage());
            }

            jogadorRepository.deleteById(id);  // Deleta o jogador do banco
            return true;
        }
        return false;
    }

    // Método para buscar jogador por ID
    public JogadorDTO getJogadorById(Long id) {
        Optional<Jogador> jogador = jogadorRepository.findById(id);
        return jogador.map(JogadorDTO::new).orElse(null);
    }
}
