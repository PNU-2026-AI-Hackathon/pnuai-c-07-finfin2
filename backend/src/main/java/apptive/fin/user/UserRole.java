package apptive.fin.user;

public enum UserRole {
    BEFORE_AGREED,
    RECOMMENDATION,
    ADMIN;

    public boolean canUseRecommendation() {
        return this == RECOMMENDATION || this == ADMIN;
    }
}
