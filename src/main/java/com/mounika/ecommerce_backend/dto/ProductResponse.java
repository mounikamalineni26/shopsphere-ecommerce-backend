
package com.mounika.ecommerce_backend.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name,
                           String description,
                           Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
