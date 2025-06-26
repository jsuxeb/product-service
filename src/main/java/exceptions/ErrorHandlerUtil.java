package exceptions;

import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

public class ErrorHandlerUtil {

    public static MessageError createErrorMessage(Throwable ex) {
        LocalDateTime now = LocalDateTime.now();

        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        String exceptionName = cause.getClass().getSimpleName();

        return switch (exceptionName) {
            case "ProductAlreadyExistsException" -> new MessageError(now.toString(), Response.Status.CONFLICT, cause.getMessage());
            case "ProductNotFoundException"      -> new MessageError(now.toString(),Response.Status.NOT_FOUND, "Product not found");
            case "OrderNotFoundException"        -> new MessageError(now.toString(),Response.Status.NOT_FOUND, "Order not found");
            case "CustomerNotFoundException"     -> new MessageError(now.toString(),Response.Status.NOT_FOUND, "Customer not found");
            default                              -> new MessageError(now.toString(),Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error");
        };

    }


}
