package eu.dnetlib.dhp.wf.importer.facade;

/**
 * MDStore service facade.
 * 
 * @author mhorst
 *
 */
public interface MDStoreFacade {

    /**
     * Delivers all records for given MDStore identifier
     * @param mdStoreId MDStore identifier
     */
    Iterable<String> deliverMDRecords(String mdStoreId) throws ServiceFacadeException;
    
}
