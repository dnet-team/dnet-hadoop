package eu.dnetlib.dhp.wf.importer.facade;

import java.util.Collections;

import org.apache.log4j.Logger;

import eu.dnetlib.enabling.is.lookup.rmi.ISLookUpDocumentNotFoundException;
import eu.dnetlib.enabling.is.lookup.rmi.ISLookUpException;
import eu.dnetlib.enabling.is.lookup.rmi.ISLookUpService;

/**
 * WebService based database facade.
 * 
 * @author mhorst
 *
 */
public class WebServiceISLookupFacade extends AbstractResultSetAwareWebServiceFacade<ISLookUpService> implements ISLookupFacade {

    private static final Logger log = Logger.getLogger(WebServiceISLookupFacade.class);

    
    //------------------------ CONSTRUCTORS -------------------
    
    /**
     * @param serviceLocation database service location
     * @param serviceReadTimeout service read timeout
     * @param serviceConnectionTimeout service connection timeout
     * @param resultSetReadTimeout result set providing database results read timeout
     * @param resultSetConnectionTimeout result set connection timeout
     * @param resultSetPageSize result set data chunk size
     */
    public WebServiceISLookupFacade(String serviceLocation, 
            long serviceReadTimeout, long serviceConnectionTimeout,
            long resultSetReadTimeout, long resultSetConnectionTimeout, int resultSetPageSize) {
        super(ISLookUpService.class, serviceLocation, 
                serviceReadTimeout, serviceConnectionTimeout, 
                resultSetReadTimeout, resultSetConnectionTimeout, resultSetPageSize);
    }

    //------------------------ LOGIC --------------------------
    
    @Override
    public Iterable<String> searchProfile(String xPathQuery) throws ServiceFacadeException {
        try {
            return getService().quickSearchProfile(xPathQuery);
        }  catch (ISLookUpDocumentNotFoundException e) {
            log.error("unable to find profile for query: " + xPathQuery, e);
            return Collections.emptyList();
        } catch (ISLookUpException e) {
            throw new ServiceFacadeException("searching profiles in ISLookup failed with query '" + xPathQuery + "'", e);
        }
        
    }

}
