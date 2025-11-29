package rayka.sos.utils;

import java.time.Instant;
import java.util.List;

public record ErroResponse(Instant timestamp, int status, String error, String message, List<String> details) {
}
