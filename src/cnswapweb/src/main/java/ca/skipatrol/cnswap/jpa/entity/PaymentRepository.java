package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface PaymentRepository extends CrudRepository<Payment, Integer> {

    public List<Payment> findByOrdersId(Order ordersId);
    
}