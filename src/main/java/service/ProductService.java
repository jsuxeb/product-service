package service;

import dto.ProductDto;
import exceptions.ProductAlreadyExistsException;
import exceptions.ProductNotFoundException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.Product;
import model.ProductTypeEnum;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    @Inject
    ProductRepository productRepository;

    public Uni<Product> saveProduct(Product product) {

        return productRepository.findByNameAndBrand(product.getProductName(), product.getBrand())
                .onItem()
                .ifNotNull()
                .invoke((e) -> {
                    log.info("Ya existe un producto con el nombre {} y la marca: {}", e.getProductName(), e.getBrand());
                    throw new ProductAlreadyExistsException("Ya existe un producto con el nombre " + e.getProductName() + " y la marca " + e.getBrand());
                })
                .onItem()
                .ifNull()
                .switchTo(() -> saveProductInDatabase(product));
    }

    private Uni<Product> saveProductInDatabase(Product product) {
        return productRepository.save(product)
                .onItem()
                .transform(insertResult -> {
                    log.info("Producto guardado con éxito: {}", product.getProductName());
                    return product;
                })
                .onFailure()
                .invoke(throwable -> log.error("Error al guardar el producto: {}", throwable.getMessage()))
                .replaceWith(product);
    }


    public Uni<Product> findBySku(String sku) {
        Bson filter = new org.bson.Document("sku", sku);
        return productRepository.findById(filter)
                .onItem()
                .ifNull()
                .switchTo(() -> Uni.createFrom().failure(new ProductNotFoundException("No se encontró el producto con SKU: " + sku)));

    }

    public Uni<ProductDto> discountStockFlow(ProductDto productDto) {
        return productRepository.findBySku(productDto.getSku())
                .onItem()
                .ifNull()
                .failWith(new ProductNotFoundException("No existe product con sku: " + productDto.getSku()))
                .onItem()
                .ifNotNull()
                .transformToUni(producto -> {
                    log.info("Descontando al producto con SKU {}", producto.getSku());
                    if (ProductTypeEnum.NORMAL.name().equalsIgnoreCase(producto.getProductType())) {
                        return discountStock(productDto.getQuantityDiscount(), productDto.getSku());
                    } else {
                        List<ProductDto> products = producto.getComponentPack()
                                .stream().parallel()
                                .map(s -> {
                                    ProductDto dto = new ProductDto();
                                    dto.setSku(s.getSku());
                                    dto.setQuantityDiscount(s.getQuantity() * productDto.getQuantityDiscount());
                                    dto.setWarehouseId(productDto.getWarehouseId());
                                    return dto;
                                }).toList();
                        List<ProductDto> allProducts = new ArrayList<>(products);
                        allProducts.add(productDto);
                        Uni<List<ProductDto>> responses = Multi.createFrom().iterable(allProducts)
                                .onItem()
                                .transformToUniAndConcatenate(p -> discountStock(p.getQuantityDiscount(), p.getSku()))
                                .collect().asList();

                        return responses.flatMap(e -> Uni.createFrom().item(e.get(0)));
                    }
                });
    }


    private Uni<ProductDto> discountStock(int quantityDiscount, String sku) {
        return productRepository.discountStock(sku, quantityDiscount)
                .onFailure().recoverWithItem(e -> {
                    System.out.println("Fallo SKU: " + sku + " → " + e.getMessage());
                    return null;
                });

    }
}



