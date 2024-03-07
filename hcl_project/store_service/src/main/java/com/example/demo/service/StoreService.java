package com.example.demo.service;

import com.example.demo.dto.StoreDTO;
import com.example.demo.mapper.StoreMapper;
import com.example.demo.model.Store;
import com.example.demo.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store with id " + storeId + " not found"));
    }

    public StoreDTO createStore(StoreDTO newStoreDto) {
        Store newStore = StoreMapper.MAPPER.mapDTOToStore(newStoreDto);
        Store savedStore = storeRepository.save(newStore);
        return StoreMapper.MAPPER.mapStoreToDto(savedStore);
    }

    public StoreDTO updateStore(Long storeId, StoreDTO updatedStoreDto) {
        Store existingStore = getStore(storeId);
        StoreMapper.MAPPER.updateStoreFromDTO(updatedStoreDto, existingStore);
        Store updatedStore = storeRepository.save(existingStore);
        return StoreMapper.MAPPER.mapStoreToDto(updatedStore);
    }

    public StoreDTO getStoreById(Long storeId) {
        Store store = getStore(storeId);
        return StoreMapper.MAPPER.mapStoreToDto(store);
    }

    public void deleteStore(Long storeId) {
        Store store = getStore(storeId);
        storeRepository.delete(store);
    }
}