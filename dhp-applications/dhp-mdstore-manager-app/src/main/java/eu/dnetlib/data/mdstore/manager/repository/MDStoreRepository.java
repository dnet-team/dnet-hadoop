package eu.dnetlib.data.mdstore.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.dnetlib.data.mdstore.manager.model.MDStore;

@Repository
public interface MDStoreRepository extends JpaRepository<MDStore, String> {

}
