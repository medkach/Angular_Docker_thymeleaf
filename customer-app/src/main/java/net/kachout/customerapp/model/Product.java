package net.kachout.customerapp.model;


import lombok.*;

import java.util.UUID;


@Getter@AllArgsConstructor@NoArgsConstructor
@Setter@Builder
public class Product {
    private UUID id;
    private String name;
    private double price;
    private int quantity;
}
