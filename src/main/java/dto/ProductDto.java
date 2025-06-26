package dto;

public class ProductDto {

    private String sku;
    private int quantityDiscount;
    private int warehouseId;


    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantityDiscount() {
        return quantityDiscount;
    }

    public void setQuantityDiscount(int quantityDiscount) {
        this.quantityDiscount = quantityDiscount;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
}
