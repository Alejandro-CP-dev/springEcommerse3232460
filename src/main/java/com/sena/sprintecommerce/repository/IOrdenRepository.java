package com.sena.sprintecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.sprintecommerce.model.Orden;
import com.sena.sprintecommerce.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer>{

	List<Orden> findByUsuario(Usuario usuario);
	
}
