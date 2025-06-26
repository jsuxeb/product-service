package resource;

import dto.ProductDto;
import exceptions.ErrorHandlerUtil;
import exceptions.MessageError;
import exceptions.ProductAlreadyExistsException;
import exceptions.ProductNotFoundException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Product;
import service.ProductService;

@ApplicationScoped
@Path("/api/v1/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {


    @Inject
    ProductService productService;

    @POST
    public Uni<Response> createProduct(Product product) {
        return productService.saveProduct(product)
                .onItem().transform(insertResult -> Response.status(Response.Status.CREATED).build())
                .onFailure(ProductAlreadyExistsException.class)
                .recoverWithItem(this::throwError);
    }

    @GET
    @Path("/{sku}")
    public Uni<Response> findProductBySku(String sku) {
        return productService.findBySku(sku)
                .onItem()
                .transform(insertResult -> Response.status(Response.Status.OK).entity(insertResult).build())
                .onFailure(ProductNotFoundException.class)
                .recoverWithItem(this::throwError);
    }


    @POST
    @Path("/discountstock")
    public Uni<Response> discountStock(ProductDto product) {
        return productService.discountStockFlow(product)
                .onItem()
                .transform(insertResult -> Response.status(Response.Status.OK).entity(insertResult).build())
                .onFailure(ProductNotFoundException.class)
                .recoverWithItem(this::throwError);


    }

    private Response throwError(Throwable ex) {
        MessageError error = ErrorHandlerUtil.createErrorMessage(ex);
        return Response
                .status(error.getStatus())
                .entity(ErrorHandlerUtil.createErrorMessage(ex))
                .build();
    }


}
