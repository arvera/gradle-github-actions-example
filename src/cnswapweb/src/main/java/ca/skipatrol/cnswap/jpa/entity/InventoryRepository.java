package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
//https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
public interface InventoryRepository extends CrudRepository<Inventory, Integer> {

	public Inventory findOneByCatentry_id(String catentry_id);
	
	public List<Inventory> findAllByCatentry_idIn(List<String> catentry_id);
}