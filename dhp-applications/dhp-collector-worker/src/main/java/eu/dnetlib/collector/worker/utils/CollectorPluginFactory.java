package eu.dnetlib.collector.worker.utils;

import eu.dnetlib.collector.worker.DnetCollectorException;
import eu.dnetlib.collector.worker.plugins.CollectorPlugin;
import eu.dnetlib.collector.worker.plugins.oai.OaiCollectorPlugin;


public class CollectorPluginFactory {

    public CollectorPlugin getPluginByProtocol(final String protocol) throws DnetCollectorException {
        if (protocol==null) throw  new DnetCollectorException("protocol cannot be null");
        switch (protocol.toLowerCase().trim()){
            case "oai":
                return new OaiCollectorPlugin();
            default:
                throw  new DnetCollectorException("UNknown protocol");
        }

    }
}
