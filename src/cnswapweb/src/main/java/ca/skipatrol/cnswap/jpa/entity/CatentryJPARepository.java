package ca.skipatrol.cnswap.jpa.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface CatentryJPARepository extends JpaRepository<Catentry, String> {

	Page<Catentry> findAllByVendorId(Integer Integer, Pageable pageable);
	

	
}