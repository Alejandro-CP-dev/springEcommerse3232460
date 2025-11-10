package com.sena.sprintecommerce.service;

import java.util.List;
import java.util.Optional;

import com.sena.sprintecommerce.model.Orden;
import com.sena.sprintecommerce.model.Usuario;

public interface IOrdenService {

	public Orden save(Orden orden);

	public List<Orden> findAll();

	public List<Orden> findByUsuario(Usuario usuario);

	public Optional<Orden> findById(Integer id);

	public String generarNumeroOrden();

}
