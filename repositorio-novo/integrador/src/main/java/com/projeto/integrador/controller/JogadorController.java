package com.projeto.integrador.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

	// Diretório onde as fotos dos jogadores serão salvas
	private static final String UPLOAD_DIR = "/site/wwwroot/uploads/";

	// Endpoint para adicionar um novo jogador
	@PostMapping("/adicionar")
	public ResponseEntity<Object> createUser(@RequestParam("nome") String nome, @RequestParam("idade") int idade,
			@RequestParam("time") String time, @RequestParam("selecao") String selecao,
			@RequestParam("camisa") int camisa, @RequestParam("foto") MultipartFile foto) {
		try {
			// Salva a foto no servidor e obtém a URL
			String fotoURL = saveFile(foto);

			JogadorDTO jogadorDTO = new JogadorDTO(null, nome, idade, time, selecao, camisa, fotoURL);
			JogadorDTO novoJogador = jogadorService.createUser(jogadorDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoJogador);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse("Erro ao processar a criação do jogador: " + e.getMessage()));
		}
	}

	// Método auxiliar para salvar o arquivo na pasta /uploads
	private String saveFile(MultipartFile foto) throws IOException {
		String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
		java.nio.file.Path path = Paths.get(UPLOAD_DIR + fileName);

		// Cria o diretório se não existir
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// Salva o arquivo
		Files.write(path, foto.getBytes());
		return "/uploads/" + fileName; // Retorna o caminho relativo da imagem
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id,
	                                          @RequestParam("nome") String nome,
	                                          @RequestParam("idade") int idade,
	                                          @RequestParam("time") String time,
	                                          @RequestParam("selecao") String selecao,
	                                          @RequestParam("camisa") int camisa,
	                                          @RequestParam(value = "foto", required = false) MultipartFile foto) {
	    try {
	        JogadorDTO jogadorDTO = new JogadorDTO(id, nome, idade, time, selecao, camisa, null); // Foto será opcional
	        if (foto != null) {
	            String fotoURL = saveFile(foto);  // Se a foto for fornecida, salva a nova foto
	            jogadorDTO.setFoto(fotoURL);
	        }

	        JogadorDTO jogadorAtualizado = jogadorService.updateUser(id, jogadorDTO);
	        if (jogadorAtualizado != null) {
	            return ResponseEntity.ok(jogadorAtualizado); // Retorna o jogador atualizado
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new ErrorResponse("Jogador não encontrado"));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ErrorResponse("Erro ao atualizar jogador: " + e.getMessage()));
	    }
	}


	// Endpoint para excluir um jogador
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		boolean deleted = jogadorService.deleteUser(id); // Chama o serviço de exclusão
		if (deleted) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Retorna 204 No Content
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Jogador não encontrado")); // Retorna
																													// 404
																													// Not
																													// Found
																													// se
																													// não
																													// encontrado
		}
	}

	@CrossOrigin(origins = "https://osdrakedosenai.azurewebsites.net/jogador") // Substitua com o seu front-end
	@GetMapping
	public ResponseEntity<List<JogadorDTO>> getAllUsers() {
		List<JogadorDTO> jogadores = jogadorService.getAllJogadores();
		return ResponseEntity.ok(jogadores);
	}

	// Endpoint para servir a imagem quando necessário
	@GetMapping("/uploads/{fileName}")
	public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws IOException {
		java.nio.file.Path path = Paths.get(UPLOAD_DIR + fileName);
		if (Files.exists(path)) {
			byte[] imageBytes = Files.readAllBytes(path);
			return ResponseEntity.ok().body(imageBytes);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	// Classe de resposta de erro em formato JSON
	public static class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
