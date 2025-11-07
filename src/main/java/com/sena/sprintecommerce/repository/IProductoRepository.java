package com.sena.sprintecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.sprintecommerce.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer>{

}
