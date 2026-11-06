
package com.mounika.ecommerce_backend.service;

import com.mounika.ecommerce_backend.dto.ProductRequest;
import com.mounika.ecommerce_backend.dto.ProductResponse;
import com.mounika.ecommerce_backend.entity.Product;
import com.mounika.ecommerce_backend.repository.ProductRepository;
import com.mounika.ecommerce_backend.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Convert Entity into Response DTO
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    // Find product entity by ID
    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        )
                );
    }

    // CREATE
    public ProductResponse addProduct(ProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    // READ ALL
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // READ BY ID
    public ProductResponse getProductById(Long id) {

        Product product = findProduct(id);

        return convertToResponse(product);
    }

    // UPDATE
    public ProductResponse updateProduct(
            Long id, ProductRequest request) {

        Product existingProduct = findProduct(id);

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());

        Product updatedProduct =
                productRepository.save(existingProduct);

        return convertToResponse(updatedProduct);
    }

    // DELETE
    public void deleteProduct(Long id) {

        Product product = findProduct(id);

        productRepository.delete(product);
    }

    // pagination

    public Page<ProductResponse> getProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort.Direction sortDirection =
                Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return productRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    // for a particular product

    public Page<ProductResponse> searchProducts(
            String name,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::convertToResponse);
    }


}
