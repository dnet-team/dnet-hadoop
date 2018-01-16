package eu.dnetlib.dhp.common.report;

import eu.dnetlib.dhp.common.schemas.ReportEntry;
import eu.dnetlib.dhp.common.schemas.ReportEntryType;

/**
 * Factory of {@link ReportEntry} objects.
 * 
 * @author madryk
 */
public final class ReportEntryFactory {

    // ----------------------- CONSTRUCTORS -----------------------------
    
    private ReportEntryFactory() {}

    // ----------------------- LOGIC ------------------------------------
    
    /**
     * Creates {@link ReportEntry} with {@link ReportEntryType#COUNTER} type
     */
    public static ReportEntry createCounterReportEntry(String key, long count) {
        return new ReportEntry(key, ReportEntryType.COUNTER, String.valueOf(count));
    }
    
    /**
     * Creates {@link ReportEntry} with {@link ReportEntryType#DURATION} type
     */
    public static ReportEntry createDurationReportEntry(String key, long duration) {
        return new ReportEntry(key, ReportEntryType.DURATION, String.valueOf(duration));
    }
}
