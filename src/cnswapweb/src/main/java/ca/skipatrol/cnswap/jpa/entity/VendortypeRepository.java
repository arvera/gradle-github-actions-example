package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface VendortypeRepository extends CrudRepository<Vendortype, String> {
  
    
	public List<Vendortype> findAll();
}