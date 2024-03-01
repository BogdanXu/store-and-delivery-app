package com.example.demo.service;

import com.example.demo.dto.ItemDTO;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.model.Store;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item with id " + itemId + " not found"));
    }


    public boolean isInStock(Long itemId, int requestedQuantity) {
        Item item = getItem(itemId);
        int availableQuantity = item.getQuantity();
        return availableQuantity >= requestedQuantity;
    }

    public ItemDTO getItemById(Long itemId) {
        Item foundItem = getItem(itemId);
        return ItemMapper.MAPPER.mapItemToDto(foundItem);
    }

    public ItemDTO createItem(ItemDTO newItemDto) {
        Item newItem = ItemMapper.MAPPER.mapDTOToItem(newItemDto, storeRepository);
        mapStoreIdToStore(newItemDto.getStoreId(), newItem);
        return ItemMapper.MAPPER.mapItemToDto(itemRepository.save(newItem));
    }

    public ItemDTO updateItem(Long itemId, ItemDTO newItemDto) {
        Item existingItem = getItem(itemId);
        ItemMapper.MAPPER.updateItemFromDTO(newItemDto, existingItem);
        mapStoreIdToStore(newItemDto.getStoreId(), existingItem);

        return ItemMapper.MAPPER.mapItemToDto(itemRepository.save(existingItem));
    }

    public void deleteItem(Long itemId) {
        Item item = getItem(itemId);
        itemRepository.delete(item);
    }

    private void mapStoreIdToStore(Long storeId, Item item) {
        if (storeId != null) {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new EntityNotFoundException("Store with id " + storeId + " not found"));
            item.setStore(store);
        }
    }

    public List<ItemDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(ItemMapper.MAPPER::mapItemToDto)
                .collect(Collectors.toList());
    }
}