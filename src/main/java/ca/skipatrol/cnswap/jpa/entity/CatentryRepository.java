package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface CatentryRepository extends CrudRepository<Catentry, String> {

	List<Catentry> findAllByVendorId(Integer id);

	
}