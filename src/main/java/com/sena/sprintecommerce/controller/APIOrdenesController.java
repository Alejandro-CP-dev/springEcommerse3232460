package com.sena.sprintecommerce.controller;

import java.util.ArrayList;
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

	// Lista temporal de DetallesOrden
	private List<DetalleOrden> detalleTemp = new ArrayList<>();

	// Variable Temporal para la Orden
	private Orden ordenTemp = new Orden();

	// Endpoint GET para obtener todas los Ordenes
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
	
	// Endpoint GET para mostrar el detalle orden temporal
		@GetMapping("/temporden")
		public ResponseEntity<List<DetalleOrden>> verOrdenTemp() {
			return ResponseEntity.ok(detalleTemp);
		}
	
	// Endpoint POST para agregar productos temporales
		@PostMapping("/agregar")
		public ResponseEntity<List<DetalleOrden>> addProduct(@RequestBody DetalleOrden detalle) {
			var producto = productoService.get(detalle.getProducto().getId())
					.orElseThrow(() -> new RuntimeException("Producto no encontrado"));

			// verificar si ya esta existiendo
			boolean existe = false;
			for (DetalleOrden d : detalleTemp) {
				if (d.getProducto().getId().equals(producto.getId())) {
					d.setCantidad(d.getCantidad() + detalle.getCantidad()); // Si ya existe, solo aumenta la cantidad
					d.setTotal(d.getCantidad() * d.getPrecio()); // multiplica la cantidad por el precio
					existe = true;
					break;
				}
			}

			if (!existe) {
				detalle.setProducto(producto);
				detalle.setPrecio(producto.getPrecio());
				detalle.setTotal(producto.getPrecio() * detalle.getCantidad());
				detalleTemp.add(detalle);
			}

			// Actualizar el total
			double total = detalleTemp.stream().mapToDouble(DetalleOrden::getTotal).sum();
			ordenTemp.setTotal(total);
			return ResponseEntity.ok(detalleTemp);
		}

	// Endpoint POST para crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<Orden> createOrden(@RequestBody Orden orden) {
		// Buscar el usuario
		Usuario u = usuarioService.findById(1).get();
		orden.setUsuario(u);
		
		orden.setTotal(0.0);
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setFechacreacion(new Date());

		Orden savedOrden = ordenService.save(orden);

		// Guardar cada detalle y asociarlo a la orden
		for (DetalleOrden d : detalleTemp) {
			d.setOrden(savedOrden);
			detalleService.save(d);

			// Descontar stock de Producto
			Producto producto = d.getProducto();
			producto.setCantidad((int) (producto.getCantidad() - d.getCantidad()));
			productoService.uptade(producto);
		}

		// Limpiar listas Temporales
		detalleTemp.clear();
		ordenTemp = new Orden();

		return ResponseEntity.status(HttpStatus.CREATED).body(savedOrden);
	}

}
