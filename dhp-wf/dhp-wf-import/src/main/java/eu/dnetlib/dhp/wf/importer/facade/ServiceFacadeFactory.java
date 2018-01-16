package eu.dnetlib.dhp.wf.importer.facade;

import java.util.Map;

/**
 * Generic service facade factory. All implementations must be instantiable with no-argument construtor.
 * 
 * @author mhorst
 * 
 */
public interface ServiceFacadeFactory<T> {

    /**
     * Creates service of given type configured with parameters.
     * 
     * @param parameters service configuration
     * 
     */
    T instantiate(Map<String, String> parameters);
}
