package eu.dnetlib.dhp.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;

public class DHPUtils {

    public static String md5(final String s) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes("UTF-8"));
            return new String(Hex.encodeHex(md.digest()));
        } catch (final Exception e) {
            System.err.println("Error creating id");
            return null;
        }
    }

    public static String generateIdentifier(final String originalId, final String nsPrefix) {
        return String.format("%s::%s",nsPrefix, DHPUtils.md5(originalId));
    }

}
