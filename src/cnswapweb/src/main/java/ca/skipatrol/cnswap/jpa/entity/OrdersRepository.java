package ca.skipatrol.cnswap.jpa.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_TELLER')")
public interface OrdersRepository extends CrudRepository<Order, Integer> {
//https://dzone.com/articles/adding-entitymanagerrefresh-to-all-spring-data-rep
	Order findOneById(Integer id);
	
}