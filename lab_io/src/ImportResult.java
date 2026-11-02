import java.util.List;

public sealed interface ImportResult permits ImportResult.Imported, ImportResult.Rejected {

    record Imported(List<Course> courses) implements ImportResult { }

    record Rejected(List<LineError> errors) implements ImportResult { }

    record LineError(int lineNumber, String line, String message) { }
}
