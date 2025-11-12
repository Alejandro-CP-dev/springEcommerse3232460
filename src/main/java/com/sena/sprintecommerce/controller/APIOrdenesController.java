package com.sena.sprintecommerce.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.sprintecommerce.model.Orden;
import com.sena.sprintecommerce.model.Usuario;
import com.sena.sprintecommerce.service.IDetalleOrdenService;
import com.sena.sprintecommerce.service.IOrdenService;
import com.sena.sprintecommerce.service.IProductoService;
import com.sena.sprintecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiordenes")
public class APIOrdenesController {

	@Autowired
	private IDetalleOrdenService detalleService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IProductoService productoService;

	// Endpoint GET para obtener todos los Ordenes
	@GetMapping("/list")
	public List<Orden> getALLOrdenes() {
		return ordenService.findAll();
	}

	// Endpoint GET para obtener un Orden por ID
	@GetMapping("/orden/{id}")
	public ResponseEntity<Orden> getOrdenById(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Endpoint POST para crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<Orden> createOrden(@RequestBody Orden orden) {
		Usuario u = usuarioService.findById(orden.getUsuario().getId())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		orden.setUsuario(u);
		Date setFechaCreacion = new Date();
		orden.setTotal(0.0);
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setFechacreacion(setFechaCreacion);
		orden.setUsuario(u);
		Orden savedOrden = ordenService.save(orden);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedOrden);
	}
   
}
