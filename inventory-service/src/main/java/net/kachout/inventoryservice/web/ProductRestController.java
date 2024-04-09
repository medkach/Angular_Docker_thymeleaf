package net.kachout.inventoryservice.web;

import net.kachout.inventoryservice.entity.Product;
import net.kachout.inventoryservice.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductRestController {
    private  final ProductRepository productRepository;

    public ProductRestController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
@GetMapping("/products")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    @GetMapping("/products/id")
    public Optional<Product> getProductById(UUID id)
    {
       return productRepository.findById(id);
    }
}
