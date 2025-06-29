package repository.impl;

import com.mongodb.client.result.InsertOneResult;
import dto.ProductDto;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Product;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ProductRepository;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    @Inject
    ReactiveMongoClient reactiveMongoClient;

    private ReactiveMongoCollection<Product> getCollection() {
        return reactiveMongoClient
                .getDatabase("stockdb")
                .getCollection("products", Product.class);


    }

    @Override
    public Uni<InsertOneResult> save(Product product) {
        return getCollection().insertOne(product);
    }

    @Override
    public Uni<Product> findById(Bson id) {
        return getCollection()
                .find(id)
                .toUni();

    }

    @Override
    public Uni<Product> findByNameAndBrand(String name, String brand) {
        Bson filter = new org.bson.Document("name", name)
                .append("brand", brand);
        return getCollection()
                .find(filter)
                .toUni();
    }

    public Uni<ProductDto> discountStock(String sku, int quantityDiscount) {
        log.info("Eliminando para sku: {}", sku);
        Bson filter = new org.bson.Document("sku", sku);
        Bson update = new org.bson.Document("$inc", new org.bson.Document("stock", -quantityDiscount));
        return getCollection()
                .findOneAndUpdate(filter, update)
                .onItem()
                .transformToUni(p -> {
                    if (p != null) {
                        return Uni.createFrom().item(new ProductDto());
                    } else {
                        return Uni.createFrom().nullItem();
                    }
                });
    }

    @Override
    public Uni<Product> findBySku(String sku) {
        log.info("🔍 Buscando en Mongo: '{}'", sku);
        Bson filter = new org.bson.Document("sku", sku);
        return getCollection()
                .find(filter)
                .toUni()
                .invoke( product -> {
                    if (product != null) {
                        log.info("✅ Producto encontrado: {}", product.getSku());
                    } else {
                        log.warn("⚠️ Producto no encontrado para SKU: {}", sku);
                    }
                });
    }
}
