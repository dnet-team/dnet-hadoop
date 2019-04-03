package eu.dnetlib.data.mdstore.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.dnetlib.data.mdstore.manager.common.model.MDStoreVersion;

@Repository
public interface MDStoreVersionRepository extends JpaRepository<MDStoreVersion, String> {

	void deleteByMdstore(String id);

	int countByMdstoreAndWriting(String id, boolean b);

	int countByMdstoreAndReadCountGreaterThan(String id, int count);

	Iterable<MDStoreVersion> findByMdstore(String mdId);

}
