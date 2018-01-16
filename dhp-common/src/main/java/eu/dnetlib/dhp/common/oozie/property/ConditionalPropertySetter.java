package eu.dnetlib.dhp.common.oozie.property;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import eu.dnetlib.dhp.common.java.PortBindings;
import eu.dnetlib.dhp.common.java.Process;
import eu.dnetlib.dhp.common.java.porttype.PortType;
import org.apache.hadoop.conf.Configuration;

import static eu.dnetlib.dhp.common.WorkflowRuntimeParameters.OOZIE_ACTION_OUTPUT_FILENAME;

/**
 * This process is a solution for setting dynamic properties in oozie workflow definition.
 * 
 * Expects three parameters to be provided: the first 'condition' parameter is boolean value 
 * based on which either first 'inCaseOfTrue' or second 'elseCase' parameter value is set as 
 * the 'result' property.
 *  
 * This can be understood as the: 
 * 
 * condition ? inCaseOfTrue : elseCase
 * 
 * java syntax equivalent.
 * 
 * @author mhorst
 *
 */
public class ConditionalPropertySetter implements Process {

	public static final String PARAM_CONDITION = "condition";
	public static final String PARAM_INCASEOFTRUE = "inCaseOfTrue";
	public static final String PARAM_ELSECASE = "elseCase";
	
	public static final String OUTPUT_PROPERTY_RESULT = "result";
	
	@Override
	public Map<String, PortType> getInputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, PortType> getOutputPorts() {
		return Collections.emptyMap();
	}

	@Override
	public void run(PortBindings portBindings, Configuration conf,
			Map<String, String> parameters) throws Exception {

		String condition = parameters.get(PARAM_CONDITION);
		if (condition == null) {
			throw new RuntimeException("unable to make decision: " + 
					PARAM_CONDITION + " parameter was not set!");
		}

		Properties props = new Properties();
        props.setProperty(OUTPUT_PROPERTY_RESULT, 
        		Boolean.parseBoolean(condition)?
        				parameters.get(PARAM_INCASEOFTRUE):
        					parameters.get(PARAM_ELSECASE));
        OutputStream os = new FileOutputStream(
        		new File(System.getProperty(OOZIE_ACTION_OUTPUT_FILENAME)));
        try {
        	props.store(os, "");	
        } finally {
        	os.close();	
        }

	}

}
