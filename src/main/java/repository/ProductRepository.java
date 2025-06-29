package repository;

import com.mongodb.client.result.InsertOneResult;
import dto.ProductDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Product;
import org.bson.conversions.Bson;

public interface ProductRepository {

    Uni<InsertOneResult> save(Product product);
    Uni <Product> findById(Bson id);
    Uni <Product> findByNameAndBrand(String name, String brand);
    public Uni<ProductDto> discountStock(String sku, int quantityDiscount);
    Uni<Product> findBySku(String sku);
}
