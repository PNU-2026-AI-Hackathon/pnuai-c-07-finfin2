package apptive.fin.apicollector;

public enum Mode {
    SYNC,
    NORMALIZE_ONLY;

    public boolean isNormalizeOnly() {
        return this == NORMALIZE_ONLY;
    }
}
