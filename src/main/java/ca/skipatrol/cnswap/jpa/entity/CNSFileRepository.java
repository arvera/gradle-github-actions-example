package ca.skipatrol.cnswap.jpa.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_MANAGE')")
public interface CNSFileRepository extends CrudRepository<CNSFile, String> {

}