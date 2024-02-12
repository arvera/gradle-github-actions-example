package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface VendorRepository extends CrudRepository<Vendor, Integer> {
  
	List<Vendor> findAllByVendortype(Vendortype vendortypeId);

}