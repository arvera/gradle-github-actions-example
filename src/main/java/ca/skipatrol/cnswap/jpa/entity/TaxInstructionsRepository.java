package ca.skipatrol.cnswap.jpa.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.transaction.Transactional;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface TaxInstructionsRepository extends CrudRepository<TaxInstruction, Integer> {

    public List<TaxInstruction> findByOrdersId(Order ordersId);
    @Transactional
    public List<TaxInstruction> deleteAllByOrdersId(Order ordersId);
    
}