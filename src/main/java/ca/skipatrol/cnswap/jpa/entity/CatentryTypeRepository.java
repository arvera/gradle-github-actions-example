package ca.skipatrol.cnswap.jpa.entity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface CatentryTypeRepository extends CrudRepository<CatentryType, String> {
  
    
	public Optional<CatentryType> findByCatentryTypeName(String catentryType);
}