package exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String errorMessage){
        super(errorMessage);
    }
}
