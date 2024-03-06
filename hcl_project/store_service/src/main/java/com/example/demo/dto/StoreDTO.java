package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDTO {
    private Long id;
    @NotBlank(message = "Store name cannot be blank")
    @Size(max = 255, message = "Store name must not exceed 255 characters")
    private String name;
}