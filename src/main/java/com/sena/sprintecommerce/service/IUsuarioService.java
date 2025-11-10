package com.sena.sprintecommerce.service;

import java.util.List;
import java.util.Optional;

import com.sena.sprintecommerce.model.Usuario;

public interface IUsuarioService {

	//Metodos CRUD 
	public Usuario save(Usuario usuario);

	public Optional<Usuario> get(Integer id);

	public void update(Usuario usuario);

	public void delete(Integer id);

	Optional<Usuario> findById(Integer id);

	Optional<Usuario> findByEmail(String email);

	List<Usuario> findAll();
}
