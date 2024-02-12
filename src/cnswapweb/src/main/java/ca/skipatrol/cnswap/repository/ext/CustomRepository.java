package ca.skipatrol.cnswap.repository.ext;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

// https://dzone.com/articles/adding-entitymanagerrefresh-to-all-spring-data-rep
@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable>
extends CrudRepository<T, ID> {
    void refresh(T t);
}