package eu.dnetlib.dhp.wf.importer.facade;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import eu.dnetlib.data.objectstore.rmi.ObjectStoreService;
import eu.dnetlib.data.objectstore.rmi.ObjectStoreServiceException;
import eu.dnetlib.enabling.resultset.client.ResultSetClientFactory;
import eu.dnetlib.enabling.tools.JaxwsServiceResolverImpl;

/**
 * WebService based ObjectStore facade.
 * 
 * @author mhorst
 *
 */
public class WebServiceObjectStoreFacade extends AbstractResultSetAwareWebServiceFacade<ObjectStoreService> implements ObjectStoreFacade {

    
    //------------------------ CONSTRUCTORS -------------------
    
    /**
     * @param serviceLocation ObjectStore webservice location
     * @param serviceReadTimeout service read timeout
     * @param serviceConnectionTimeout service connection timeout
     * @param resultSetReadTimeout resultset read timeout
     * @param resultSetConnectionTimeout result set connection timeout
     * @param resultSetPageSize resultset page size
     */
    public WebServiceObjectStoreFacade(String serviceLocation, 
            long serviceReadTimeout, long serviceConnectionTimeout,
            long resultSetReadTimeout, long resultSetConnectionTimeout, int resultSetPageSize) {
        super(ObjectStoreService.class, serviceLocation, 
                serviceReadTimeout, serviceConnectionTimeout, 
                resultSetReadTimeout, resultSetConnectionTimeout, resultSetPageSize);
    }
    
    //------------------------ LOGIC --------------------------
    
    @Override
    public Iterable<String> deliverObjects(String objectStoreId, long from, long until) throws ServiceFacadeException {
        try {
            W3CEndpointReference eprResult = getService().deliverObjects(objectStoreId, from, until);
            ResultSetClientFactory rsFactory = new ResultSetClientFactory(
                    getResultSetPageSize(), getResultSetReadTimeout(), getResultSetConnectionTimeout());
            rsFactory.setServiceResolver(new JaxwsServiceResolverImpl());
            return rsFactory.getClient(eprResult);
        } catch (ObjectStoreServiceException e) {
            throw new ServiceFacadeException("delivering records for object store " + objectStoreId + " failed!", e);
        }
    }

}
