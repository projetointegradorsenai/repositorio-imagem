package com.projeto.integrador.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.integrador.dto.JogadorDTO;
import com.projeto.integrador.model.Jogador;
import com.projeto.integrador.repository.JogadorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JogadorService {

    @Autowired
    private JogadorRepository jogadorRepository;

    
    public JogadorDTO createUser(JogadorDTO jogadorDTO) {
        Jogador jogador = new Jogador(jogadorDTO);
        jogador.setFoto(jogadorDTO.getFoto());  
        Jogador jogadorCriado = jogadorRepository.save(jogador);
        return new JogadorDTO(jogadorCriado);
    }

   
    public List<JogadorDTO> getAllJogadores() {
        List<Jogador> jogadores = jogadorRepository.findAll();
        return jogadores.stream().map(JogadorDTO::new).toList();
    }

    
    public JogadorDTO updateUser(Long id, JogadorDTO jogadorDTO) {
        Optional<Jogador> jogadorOptional = jogadorRepository.findById(id);
        if (jogadorOptional.isPresent()) {
            Jogador jogador = jogadorOptional.get();
            jogador.setNome(jogadorDTO.getNome());
            jogador.setIdade(jogadorDTO.getIdade());
            jogador.setTime(jogadorDTO.getTime());
            jogador.setSelecao(jogadorDTO.getSelecao());
            jogador.setNumeroCamisa(jogadorDTO.getCamisa());
            jogador.setFoto(jogadorDTO.getFoto());  
            Jogador jogadorAtualizado = jogadorRepository.save(jogador);
            return new JogadorDTO(jogadorAtualizado);
        } else {
            return null;
        }
    }

    
    public boolean deleteUser(Long id) {
        Optional<Jogador> jogadorOptional = jogadorRepository.findById(id);
        if (jogadorOptional.isPresent()) {
            jogadorRepository.delete(jogadorOptional.get());
            return true;
        }
        return false;
    }
}
