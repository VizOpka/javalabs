public class CatalogException extends Exception {

    private static final long serialVersionUID = 1L;

    public CatalogException(String message) {
        super(message);
    }

    public CatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
