package apptive.fin.term.dto;


import java.time.Instant;

public record TermResponseDto (
    Long id,
    Long versionId,
    String code,
    String title,
    String content,
    Instant effectiveFrom,
    boolean isRequired,
    boolean agreed
) {
}
