package com.example.demo.controller;

import com.example.demo.dto.StoreDTO;
import com.example.demo.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO> getStore(@PathVariable Long storeId) {
        StoreDTO storeDTO = storeService.getStoreById(storeId);
        return ResponseEntity.ok(storeDTO);
    }

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDTO) {
        StoreDTO createdStoreDTO = storeService.createStore(storeDTO);
        return new ResponseEntity<>(createdStoreDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Long storeId, @RequestBody StoreDTO storeDTO) {
        StoreDTO updatedStoreDTO = storeService.updateStore(storeId, storeDTO);
        return ResponseEntity.ok(updatedStoreDTO);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }
}