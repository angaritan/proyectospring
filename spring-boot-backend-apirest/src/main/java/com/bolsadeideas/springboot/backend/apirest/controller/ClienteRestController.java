package com.bolsadeideas.springboot.backend.apirest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.services.IClienteService;


@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){		
		return clienteService.findAll();		
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){	
		//return clienteService.findAll(PageRequest.of(page, 4)); ò
		Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
		
	}
	
	
/**	@GetMapping("/clientes/{id}")
	public Cliente show(@PathVariable Long id){		
		return clienteService.findById(id);
		
	}**/
	/**
	 * Tratando errores en el backen metodo show adaptado. *  
	 */
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id){	
		Cliente cliente= null;
		Map<String, Object> response = new HashMap<>();
		try {
             cliente = clienteService.findById(id);             
             
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al consultar la base de datos!");
			response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString()).concat(" no existe en la Base de Datos!"));
			return new  ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return  new  ResponseEntity<Cliente>(cliente, HttpStatus.OK);
		
	}
	
	/***
	 * Se adiciona tratamiento de errores para este metodo crear()
	 * @param cliente
	 * @return
	 */
	/**
	 * @PostMapping ("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente crear(@RequestBody Cliente cliente) {		
		return clienteService.save(cliente);
		
	}**/
	
	@PostMapping ("/clientes")	
	public ResponseEntity<?> crear(@Valid @RequestBody Cliente cliente, BindingResult result) {		
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()){
		/**List<String> errors = new ArrayList<>();		
			for(FieldError err: result.getFieldErrors()){
				errors.add("El campo '"+ err.getField()+ "' "+ err.getDefaultMessage());
				}**/			
			List<String> errors = result.getFieldErrors()
					.stream()
					/**.map(err ->{						
						return "El campo '"+ err.getField()+ "' "+ err.getDefaultMessage();
					})*/
					.map(err ->"El campo '"+ err.getField()+ "' "+ err.getDefaultMessage())
					.collect(Collectors.toList());		
				response.put("errors", errors);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);			
			}
		    
				
		try {
			clienteNew = clienteService.save(cliente);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar insert en la base de datos!");
			response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);		
		}
		response.put("mensaje", "El cliente ha sido creado con èxito!");
		response.put("cliente", clienteNew);
		return new  ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
	}
	
	/***
	 * Se adiciona tratamiento de errores para este metodo crear()
	 * @param cliente
	 * @return
	 *	
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente update(@RequestBody Cliente cliente, @PathVariable Long id) {
		
		Cliente clienteActual = clienteService.findById(id);		
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setEmail(cliente.getEmail());		
		return clienteService.save(clienteActual);	
	}
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {		
		clienteService.delete(id);		
		
	}**/
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		Cliente clienteUpdated = null;
		Cliente clienteActual = clienteService.findById(id);
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()){
			/**List<String> errors = new ArrayList<>();		
				for(FieldError err: result.getFieldErrors()){
					errors.add("El campo '"+ err.getField()+ "' "+ err.getDefaultMessage());
					}**/			
				List<String> errors = result.getFieldErrors()
						.stream()
						/**.map(err ->{						
							return "El campo '"+ err.getField()+ "' "+ err.getDefaultMessage();
						})*/
						.map(err ->"El campo '"+ err.getField()+ "' "+ err.getDefaultMessage())
						.collect(Collectors.toList());		
					response.put("errors", errors);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);			
				}		
		
		if(clienteActual== null) {
			response.put("mensaje", "Error: no se puede editar, el ciente con ID: ".concat(id.toString()).concat(" no existe en la Base de Datos!"));
			return new  ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
		
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());	
			clienteActual.setCreateAt(cliente.getCreateAt());
			clienteUpdated = clienteService.save(clienteActual);
		
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos!");
			response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido actualizado con èxito!");
		response.put("cliente", clienteUpdated);
		return new  ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);		
		
	}
	
	/***
	 * Se adiciona tratamiento de errores para este metodo delete()
	 * @param cliente
	 * @return
	 *	
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {		
		clienteService.delete(id);		
		
	}**/
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {	
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.delete(id);	
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar cliente en la base de datos!");
			response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return  new  ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);			
			
		}
		response.put("mensaje", "El cliente ha sido eliminado con èxito");
		return new  ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
