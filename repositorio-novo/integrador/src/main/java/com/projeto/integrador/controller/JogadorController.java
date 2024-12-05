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


	private static final String UPLOAD_DIR = "/site/wwwroot/uploads/";

	
	@PostMapping("/adicionar")
	public ResponseEntity<Object> createUser(@RequestParam("nome") String nome, @RequestParam("idade") int idade,
			@RequestParam("time") String time, @RequestParam("selecao") String selecao,
			@RequestParam("camisa") int camisa, @RequestParam("foto") MultipartFile foto) {
		try {
			
			String fotoURL = saveFile(foto);

			JogadorDTO jogadorDTO = new JogadorDTO(null, nome, idade, time, selecao, camisa, fotoURL);
			JogadorDTO novoJogador = jogadorService.createUser(jogadorDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoJogador);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse("Erro ao processar a criação do jogador: " + e.getMessage()));
		}
	}

	
	private String saveFile(MultipartFile foto) throws IOException {
		String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
		java.nio.file.Path path = Paths.get(UPLOAD_DIR + fileName);

		
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		
		Files.write(path, foto.getBytes());
		return "/uploads/" + fileName; 
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
	        JogadorDTO jogadorDTO = new JogadorDTO(id, nome, idade, time, selecao, camisa, null);
	        if (foto != null) {
	            String fotoURL = saveFile(foto); 
	            jogadorDTO.setFoto(fotoURL);
	        }

	        JogadorDTO jogadorAtualizado = jogadorService.updateUser(id, jogadorDTO);
	        if (jogadorAtualizado != null) {
	            return ResponseEntity.ok(jogadorAtualizado);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new ErrorResponse("Jogador não encontrado"));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ErrorResponse("Erro ao atualizar jogador: " + e.getMessage()));
	    }
	}



	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
		boolean deleted = jogadorService.deleteUser(id); 
		if (deleted) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Jogador não encontrado")); 
																													
																																																																																					
																												
		}
	}

	@CrossOrigin(origins = "https://osdrakedosenai.azurewebsites.net/jogador") 
	@GetMapping
	public ResponseEntity<List<JogadorDTO>> getAllUsers() {
		List<JogadorDTO> jogadores = jogadorService.getAllJogadores();
		return ResponseEntity.ok(jogadores);
	}

	
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
