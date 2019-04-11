package eu.dnetlib.dhp.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public static String compressString(final String input ) {
        try ( ByteArrayOutputStream out = new ByteArrayOutputStream();  Base64OutputStream b64os = new Base64OutputStream(out)) {
            GZIPOutputStream gzip = new GZIPOutputStream(b64os);
            gzip.write(input.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            return out.toString();
        } catch (Throwable e ) {
            return null;
        }
    }


    public static String decompressString(final String input) {
        byte[] byteArray = Base64.decodeBase64(input.getBytes());
        int len;
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream((byteArray))); ByteArrayOutputStream bos = new ByteArrayOutputStream(byteArray.length)) {
            byte[] buffer = new byte[1024];
            while((len = gis.read(buffer)) != -1){
                bos.write(buffer, 0, len);
            }
            return bos.toString();
        } catch (Exception e) {
            return null;
        }

    }

}
