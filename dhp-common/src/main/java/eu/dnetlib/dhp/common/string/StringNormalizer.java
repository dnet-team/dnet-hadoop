package eu.dnetlib.dhp.common.string;

/**
 * String normalizer.
 *
 * @author Łukasz Dumiszewski
 *
 */
public interface StringNormalizer {
    
    /**
     * Normalizes the given string value.
     */
    String normalize(String value);
    
}
