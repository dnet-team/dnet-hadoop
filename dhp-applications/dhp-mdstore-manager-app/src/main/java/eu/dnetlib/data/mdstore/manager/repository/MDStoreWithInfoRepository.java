package eu.dnetlib.data.mdstore.manager.repository;

import org.springframework.stereotype.Repository;

import eu.dnetlib.data.mdstore.manager.common.model.MDStoreWithInfo;

@Repository
public interface MDStoreWithInfoRepository extends ReadOnlyRepository<MDStoreWithInfo, String> {

}
