package eu.dnetlib.data.mdstore.manager.repository;

import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

	Optional<T> findById(ID id);

	boolean existsById(ID id);

	Iterable<T> findAll();

	long count();
}