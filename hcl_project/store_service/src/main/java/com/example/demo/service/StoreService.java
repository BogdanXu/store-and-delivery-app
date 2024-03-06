package com.example.demo.service;

import com.example.demo.dto.StoreDTO;
import com.example.demo.mapper.StoreMapper;
import com.example.demo.model.Store;
import com.example.demo.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Autowired
    public StoreService(StoreRepository storeRepository, StoreMapper storeMapper) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store with id " + storeId + " not found"));
    }

    public StoreDTO createStore(StoreDTO newStoreDto) {
        Store newStore = storeMapper.mapDTOToStore(newStoreDto);
        Store savedStore = storeRepository.save(newStore);
        return storeMapper.mapStoreToDto(savedStore);
    }

    public StoreDTO updateStore(Long storeId, StoreDTO updatedStoreDto) {
        Store existingStore = getStore(storeId);
        storeMapper.updateStoreFromDTO(updatedStoreDto, existingStore);
        Store updatedStore = storeRepository.save(existingStore);
        return storeMapper.mapStoreToDto(updatedStore);
    }

    public StoreDTO getStoreById(Long storeId) {
        Store store = getStore(storeId);
        return storeMapper.mapStoreToDto(store);
    }

    public void deleteStore(Long storeId) {
        Store store = getStore(storeId);
        storeRepository.delete(store);
    }
}