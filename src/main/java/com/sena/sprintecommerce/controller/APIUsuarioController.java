package com.sena.sprintecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.sprintecommerce.model.Usuario;
import com.sena.sprintecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiusuarios")
public class APIUsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	// Endoint GET para obetener todos los Usuarios
	@GetMapping("/list")
	public List<Usuario> getALLUsuarios() {
		return usuarioService.findAll();
	}

	// Endpoint GET para obtener un producto por ID
	@GetMapping("/user/{id}")
	public ResponseEntity<Usuario> getUserById(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Endpoint POST para crear un nuevo Usuario
	@PostMapping("/create")
	public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
		usuario.setRol("User");

		Usuario savedUser = usuarioService.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}

	// Endpoint PUT para actualizar al Usuario
	@PutMapping("/update/{id}")
	public ResponseEntity<Usuario> updateUser(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Usuario existingUSer = usuario.get();
		existingUSer.setDireccion(usuarioDetails.getDireccion());
		existingUSer.setEmail(usuarioDetails.getEmail());
		existingUSer.setNombre(usuarioDetails.getNombre());
		existingUSer.setPassword(usuarioDetails.getPassword());
		existingUSer.setTelefono(usuarioDetails.getTelefono());
		usuarioService.update(existingUSer);
		return ResponseEntity.ok(existingUSer);
	}

	// Endpoint DELETE para eliminar un Usuario
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		usuarioService.delete(id);
		return ResponseEntity.ok().build();
	}

}
