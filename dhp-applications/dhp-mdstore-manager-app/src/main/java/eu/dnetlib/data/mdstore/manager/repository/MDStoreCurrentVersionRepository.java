package eu.dnetlib.data.mdstore.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.dnetlib.data.mdstore.manager.model.MDStoreCurrentVersion;

@Repository
public interface MDStoreCurrentVersionRepository extends JpaRepository<MDStoreCurrentVersion, String> {

	long countByCurrentVersion(String versionId);
}
