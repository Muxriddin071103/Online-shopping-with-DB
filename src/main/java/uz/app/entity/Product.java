package uz.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    private long id;
    private String name;
    private Integer price;
    private boolean available;
    private int count;
    private Category category;
    private User owner;
}
