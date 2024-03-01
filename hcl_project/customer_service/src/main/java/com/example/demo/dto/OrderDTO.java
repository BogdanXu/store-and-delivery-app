package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotNull(message = "Ordered items cannot be null")
    @Size(min = 1, message = "At least one ordered item must be present")
    private Map<Long, Integer> orderedItems;
    @NotEmpty(message = "Address cannot be empty")
    private String address;
}
