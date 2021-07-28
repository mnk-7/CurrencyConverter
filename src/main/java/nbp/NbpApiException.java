package nbp;

public class NbpApiException extends RuntimeException {

    public NbpApiException(String message) {
        super(message);
    }

    public NbpApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
