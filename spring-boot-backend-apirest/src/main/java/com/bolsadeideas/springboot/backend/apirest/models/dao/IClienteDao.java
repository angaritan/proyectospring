package com.bolsadeideas.springboot.backend.apirest.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;

/**public interface IClienteDao extends CrudRepository<Cliente, Long> {

} 
 Usamos Jpa Repository que es una sub interfaz de PagingAndSortRepository para la paginacion y de CrudRepository

**/
public interface IClienteDao extends JpaRepository<Cliente, Long> {

}
