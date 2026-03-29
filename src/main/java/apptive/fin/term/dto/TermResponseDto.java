package apptive.fin.term.dto;


import java.time.LocalDateTime;

public record TermResponseDto (
    Long id,
    Long versionId,
    String code,
    String title,
    String content,
    LocalDateTime effectiveFrom,
    boolean isRequired,
    boolean agreed
) {
}
