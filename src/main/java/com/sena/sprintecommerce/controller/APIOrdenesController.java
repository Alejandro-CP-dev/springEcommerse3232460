package com.sena.sprintecommerce.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sena.sprintecommerce.model.DetalleOrden;
import com.sena.sprintecommerce.model.Orden;
import com.sena.sprintecommerce.model.Producto;
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

	// GET: todas las órdenes
	@GetMapping("/list")
	public List<Orden> getAllOrdenes() {
		return ordenService.findAll();
	}

	// GET: orden por ID
	@GetMapping("/orden/{id}")
	public ResponseEntity<Orden> getOrdenById(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// POST: crear nueva orden con lista de detalles
	@PostMapping("/create")
	public ResponseEntity<?> createOrden(@RequestBody List<DetalleOrden> detalles, @RequestParam Integer usuarioId) {
		try {

			if (detalles == null || detalles.isEmpty()) {
				return ResponseEntity.badRequest().body("La lista de productos no puede estar vacía");
			}

			Usuario usuario = usuarioService.findById(usuarioId)
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			// Crear orden VACÍA y guardarla primero
			Orden orden = new Orden();
			orden.setUsuario(usuario);
			orden.setNumero(ordenService.generarNumeroOrden());
			orden.setFechacreacion(new Date());
			orden.setTotal(0.0); // temporal

			Orden savedOrden = ordenService.save(orden); // YA TIENE ID

			double totalOrden = 0;

			//  Guardar detalles uno por uno con la ORDEN ya persistida
			for (DetalleOrden d : detalles) {

				Producto producto = productoService.get(d.getProducto().getId())
						.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

				if (producto.getCantidad() < d.getCantidad()) {
					return ResponseEntity.badRequest()
							.body("Stock insuficiente para el producto: " + producto.getNombre());
				}

				d.setOrden(savedOrden);
				d.setProducto(producto);
				d.setPrecio(producto.getPrecio());
				d.setTotal(producto.getPrecio() * d.getCantidad());

				// actualizar stock
				producto.setCantidad((int) (producto.getCantidad() - d.getCantidad()));
				productoService.update(producto);

				detalleService.save(d);

				totalOrden += d.getTotal();
			}

			// actualizar total de la orden ya guardada
			savedOrden.setTotal(totalOrden);
			ordenService.save(savedOrden);

			return ResponseEntity.status(HttpStatus.CREATED).body(savedOrden);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al crear la orden: " + e.getMessage());
		}
	}

}