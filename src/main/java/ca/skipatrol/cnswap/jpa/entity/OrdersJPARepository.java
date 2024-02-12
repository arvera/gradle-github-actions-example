package ca.skipatrol.cnswap.jpa.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface OrdersJPARepository extends JpaRepository<Order, Integer> {
//https://dzone.com/articles/adding-entitymanagerrefresh-to-all-spring-data-rep
	Order findOneById(Integer id);
	Page<Order> findByTransUsernameUsernameIgnoreCase(String keyword, Pageable pageable);
	
}