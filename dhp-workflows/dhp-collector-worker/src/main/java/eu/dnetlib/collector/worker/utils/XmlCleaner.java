package eu.dnetlib.collector.worker.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author jochen, Andreas Czerniak
 *
 */
public class XmlCleaner {

	/**
	 * Pattern for numeric entities.
	 */
	private static Pattern validCharacterEntityPattern = Pattern.compile("^&#x?\\d{2,4};"); //$NON-NLS-1$
	// private static Pattern validCharacterEntityPattern = Pattern.compile("^&#?\\d{2,4};"); //$NON-NLS-1$

	// see https://www.w3.org/TR/REC-xml/#charsets , not only limited to &#11;
	private static Pattern invalidControlCharPattern = Pattern.compile("&#x?1[0-9a-fA-F];");

	/**
	 * Pattern that negates the allowable XML 4 byte unicode characters. Valid are: #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
	 * [#x10000-#x10FFFF]
	 */
	private static Pattern invalidCharacterPattern = Pattern.compile("[^\t\r\n\u0020-\uD7FF\uE000-\uFFFD]"); //$NON-NLS-1$

	// Map entities to their unicode equivalent
	private static Set<String> goodEntities = new HashSet<>();
	private static Map<String, String> badEntities = new HashMap<>();

	static {
		// pre-defined XML entities
		goodEntities.add("&quot;"); //$NON-NLS-1$ // quotation mark
		goodEntities.add("&amp;"); //$NON-NLS-1$ // ampersand
		goodEntities.add("&lt;"); //$NON-NLS-1$ // less-than sign
		goodEntities.add("&gt;"); //$NON-NLS-1$ // greater-than sign
		// control entities
		// badEntities.put("&#11;", "");
		badEntities.put("&#127;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#128;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#129;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#130;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#131;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#132;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#133;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#134;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#135;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#136;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#137;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#138;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#139;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#140;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#141;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#142;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#143;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#144;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#145;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#146;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#147;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#148;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#149;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#150;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#151;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#152;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#153;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#154;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#155;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#156;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#157;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#158;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		badEntities.put("&#159;", " "); //$NON-NLS-1$ //$NON-NLS-2$ // illegal HTML character
		// misc entities
		badEntities.put("&euro;", "\u20AC"); //$NON-NLS-1$ //$NON-NLS-2$ // euro
		badEntities.put("&lsquo;", "\u2018"); //$NON-NLS-1$ //$NON-NLS-2$ // left single quotation mark
		badEntities.put("&rsquo;", "\u2019"); //$NON-NLS-1$ //$NON-NLS-2$ // right single quotation mark
		// Latin 1 entities
		badEntities.put("&nbsp;", "\u00A0"); //$NON-NLS-1$ //$NON-NLS-2$ // no-break space
		badEntities.put("&iexcl;", "\u00A1"); //$NON-NLS-1$ //$NON-NLS-2$ // inverted exclamation mark
		badEntities.put("&cent;", "\u00A2"); //$NON-NLS-1$ //$NON-NLS-2$ // cent sign
		badEntities.put("&pound;", "\u00A3"); //$NON-NLS-1$ //$NON-NLS-2$ // pound sign
		badEntities.put("&curren;", "\u00A4"); //$NON-NLS-1$ //$NON-NLS-2$ // currency sign
		badEntities.put("&yen;", "\u00A5"); //$NON-NLS-1$ //$NON-NLS-2$ // yen sign
		badEntities.put("&brvbar;", "\u00A6"); //$NON-NLS-1$ //$NON-NLS-2$ // broken vertical bar
		badEntities.put("&sect;", "\u00A7"); //$NON-NLS-1$ //$NON-NLS-2$ // section sign
		badEntities.put("&uml;", "\u00A8"); //$NON-NLS-1$ //$NON-NLS-2$ // diaeresis
		badEntities.put("&copy;", "\u00A9"); //$NON-NLS-1$ //$NON-NLS-2$ // copyright sign
		badEntities.put("&ordf;", "\u00AA"); //$NON-NLS-1$ //$NON-NLS-2$ // feminine ordinal indicator
		badEntities.put("&laquo;", "\u00AB"); //$NON-NLS-1$ //$NON-NLS-2$ // left-pointing double angle quotation mark
		badEntities.put("&not;", "\u00AC"); //$NON-NLS-1$ //$NON-NLS-2$ // not sign
		badEntities.put("&shy;", "\u00AD"); //$NON-NLS-1$ //$NON-NLS-2$ // soft hyphen
		badEntities.put("&reg;", "\u00AE"); //$NON-NLS-1$ //$NON-NLS-2$ // registered sign
		badEntities.put("&macr;", "\u00AF"); //$NON-NLS-1$ //$NON-NLS-2$ // macron
		badEntities.put("&deg;", "\u00B0"); //$NON-NLS-1$ //$NON-NLS-2$ // degree sign
		badEntities.put("&plusmn;", "\u00B1"); //$NON-NLS-1$ //$NON-NLS-2$ // plus-minus sign
		badEntities.put("&sup2;", "\u00B2"); //$NON-NLS-1$ //$NON-NLS-2$ // superscript two
		badEntities.put("&sup3;", "\u00B3"); //$NON-NLS-1$ //$NON-NLS-2$ // superscript three
		badEntities.put("&acute;", "\u00B4"); //$NON-NLS-1$ //$NON-NLS-2$ // acute accent
		badEntities.put("&micro;", "\u00B5"); //$NON-NLS-1$ //$NON-NLS-2$ // micro sign
		badEntities.put("&para;", "\u00B6"); //$NON-NLS-1$ //$NON-NLS-2$ // pilcrow sign
		badEntities.put("&middot;", "\u00B7"); //$NON-NLS-1$ //$NON-NLS-2$ // middle dot
		badEntities.put("&cedil;", "\u00B8"); //$NON-NLS-1$ //$NON-NLS-2$ // cedilla
		badEntities.put("&sup1;", "\u00B9"); //$NON-NLS-1$ //$NON-NLS-2$ // superscript one
		badEntities.put("&ordm;", "\u00BA"); //$NON-NLS-1$ //$NON-NLS-2$ // masculine ordinal indicator
		badEntities.put("&raquo;", "\u00BB"); //$NON-NLS-1$ //$NON-NLS-2$ // right-pointing double angle quotation mark
		badEntities.put("&frac14;", "\u00BC"); //$NON-NLS-1$ //$NON-NLS-2$ // vulgar fraction one quarter
		badEntities.put("&frac12;", "\u00BD"); //$NON-NLS-1$ //$NON-NLS-2$ // vulgar fraction one half
		badEntities.put("&frac34;", "\u00BE"); //$NON-NLS-1$ //$NON-NLS-2$ // vulgar fraction three quarters
		badEntities.put("&iquest;", "\u00BF"); //$NON-NLS-1$ //$NON-NLS-2$ // inverted question mark
		badEntities.put("&Agrave;", "\u00C0"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with grave
		badEntities.put("&Aacute;", "\u00C1"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with acute
		badEntities.put("&Acirc;", "\u00C2"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with circumflex
		badEntities.put("&Atilde;", "\u00C3"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with tilde
		badEntities.put("&Auml;", "\u00C4"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with diaeresis
		badEntities.put("&Aring;", "\u00C5"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter A with ring above
		badEntities.put("&AElig;", "\u00C6"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter AE
		badEntities.put("&Ccedil;", "\u00C7"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter C with cedilla
		badEntities.put("&Egrave;", "\u00C8"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter E with grave
		badEntities.put("&Eacute;", "\u00C9"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter E with acute
		badEntities.put("&Ecirc;", "\u00CA"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter E with circumflex
		badEntities.put("&Euml;", "\u00CB"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter E with diaeresis
		badEntities.put("&Igrave;", "\u00CC"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter I with grave
		badEntities.put("&Iacute;", "\u00CD"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter I with acute
		badEntities.put("&Icirc;", "\u00CE"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter I with circumflex
		badEntities.put("&Iuml;", "\u00CF"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter I with diaeresis
		badEntities.put("&ETH;", "\u00D0"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter ETH
		badEntities.put("&Ntilde;", "\u00D1"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter N with tilde
		badEntities.put("&Ograve;", "\u00D2"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with grave
		badEntities.put("&Oacute;", "\u00D3"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with acute
		badEntities.put("&Ocirc;", "\u00D4"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with circumflex
		badEntities.put("&Otilde;", "\u00D5"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with tilde
		badEntities.put("&Ouml;", "\u00D6"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with diaeresis
		badEntities.put("&times;", "\u00D7"); //$NON-NLS-1$ //$NON-NLS-2$ // multiplication sign
		badEntities.put("&Oslash;", "\u00D8"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter O with stroke
		badEntities.put("&Ugrave;", "\u00D9"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter U with grave
		badEntities.put("&Uacute;", "\u00DA"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter U with acute
		badEntities.put("&Ucirc;", "\u00DB"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter U with circumflex
		badEntities.put("&Uuml;", "\u00DC"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter U with diaeresis
		badEntities.put("&Yacute;", "\u00DD"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter Y with acute
		badEntities.put("&THORN;", "\u00DE"); //$NON-NLS-1$ //$NON-NLS-2$ // latin capital letter THORN
		badEntities.put("&szlig;", "\u00DF"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter sharp s
		badEntities.put("&agrave;", "\u00E0"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with grave
		badEntities.put("&aacute;", "\u00E1"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with acute
		badEntities.put("&acirc;", "\u00E2"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with circumflex
		badEntities.put("&atilde;", "\u00E3"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with tilde
		badEntities.put("&auml;", "\u00E4"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with diaeresis
		badEntities.put("&aring;", "\u00E5"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter a with ring above
		badEntities.put("&aelig;", "\u00E6"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter ae
		badEntities.put("&ccedil;", "\u00E7"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter c with cedilla
		badEntities.put("&egrave;", "\u00E8"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter e with grave
		badEntities.put("&eacute;", "\u00E9"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter e with acute
		badEntities.put("&ecirc;", "\u00EA"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter e with circumflex
		badEntities.put("&euml;", "\u00EB"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter e with diaeresis
		badEntities.put("&igrave;", "\u00EC"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter i with grave
		badEntities.put("&iacute;", "\u00ED"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter i with acute
		badEntities.put("&icirc;", "\u00EE"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter i with circumflex
		badEntities.put("&iuml;", "\u00EF"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter i with diaeresis
		badEntities.put("&eth;", "\u00F0"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter eth
		badEntities.put("&ntilde;", "\u00F1"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter n with tilde
		badEntities.put("&ograve;", "\u00F2"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with grave
		badEntities.put("&oacute;", "\u00F3"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with acute
		badEntities.put("&ocirc;", "\u00F4"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with circumflex
		badEntities.put("&otilde;", "\u00F5"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with tilde
		badEntities.put("&ouml;", "\u00F6"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with diaeresis
		badEntities.put("&divide;", "\u00F7"); //$NON-NLS-1$ //$NON-NLS-2$ // division sign
		badEntities.put("&oslash;", "\u00F8"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter o with stroke
		badEntities.put("&ugrave;", "\u00F9"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter u with grave
		badEntities.put("&uacute;", "\u00FA"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter u with acute
		badEntities.put("&ucirc;", "\u00FB"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter u with circumflex
		badEntities.put("&uuml;", "\u00FC"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter u with diaeresis
		badEntities.put("&yacute;", "\u00FD"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter y with acute
		badEntities.put("&thorn;", "\u00FE"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter thorn
		badEntities.put("&yuml;", "\u00FF"); //$NON-NLS-1$ //$NON-NLS-2$ // latin small letter y with diaeresis
	}

	/**
	 * For each entity in the input that is not allowed in XML, replace the entity with its unicode equivalent or remove it. For each
	 * instance of a bare {@literal &}, replace it with {@literal &amp;<br/>
	 * } XML only allows 4 entities: {@literal &amp;amp;}, {@literal &amp;quot;}, {@literal &amp;lt;} and {@literal &amp;gt;}.
	 *
	 * @param broken
	 *            the string to handle entities
	 * @return the string with entities appropriately fixed up
	 */
	static public String cleanAllEntities(final String broken) {
		if (broken == null) { return null; }

		String working = invalidControlCharPattern.matcher(broken).replaceAll("");
		working = invalidCharacterPattern.matcher(working).replaceAll("");

		int cleanfrom = 0;

		while (true) {
			int amp = working.indexOf('&', cleanfrom);
			// If there are no more amps then we are done
			if (amp == -1) {
				break;
			}
			// Skip references of the kind &#ddd;
			if (validCharacterEntityPattern.matcher(working.substring(amp)).find()) {
				cleanfrom = working.indexOf(';', amp) + 1;
				continue;
			}
			int i = amp + 1;
			while (true) {
				// if we are at the end of the string then just escape the '&';
				if (i >= working.length()) { return working.substring(0, amp) + "&amp;" + working.substring(amp + 1); //$NON-NLS-1$
				}
				// if we have come to a ; then we have an entity
				// If it is something that xml can't handle then replace it.
				final char c = working.charAt(i);
				if (c == ';') {
					final String entity = working.substring(amp, i + 1);
					final String replace = handleEntity(entity);
					working = working.substring(0, amp) + replace + working.substring(i + 1);
					break;
				}
				// Did we end an entity without finding a closing ;
				// Then treat it as an '&' that needs to be replaced with &amp;
				if (!Character.isLetterOrDigit(c)) {
					working = working.substring(0, amp) + "&amp;" + working.substring(amp + 1); //$NON-NLS-1$
					amp = i + 4; // account for the 4 extra characters
					break;
				}
				i++;
			}
			cleanfrom = amp + 1;
		}

		if (Pattern.compile("<<").matcher(working).find()) {
			working = working.replaceAll("<<", "&lt;&lt;");
		}

		if (Pattern.compile(">>").matcher(working).find()) {
			working = working.replaceAll(">>", "&gt;&gt;");
		}

		return working;
	}

	/**
	 * Replace entity with its unicode equivalent, if it is not a valid XML entity. Otherwise strip it out. XML only allows 4 entities:
	 * &amp;amp;, &amp;quot;, &amp;lt; and &amp;gt;.
	 *
	 * @param entity
	 *            the entity to be replaced
	 * @return the substitution for the entity, either itself, the unicode equivalent or an empty string.
	 */
	private static String handleEntity(final String entity) {
		if (goodEntities.contains(entity)) { return entity; }

		final String replace = badEntities.get(entity);
		if (replace != null) { return replace; }

		return replace != null ? replace : "";
	}
}
