package com.example.demo.mapper;

import com.example.demo.dto.ItemDTO;
import com.example.demo.model.Item;
import com.example.demo.repository.StoreRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {

    ItemMapper MAPPER = Mappers.getMapper( ItemMapper.class );

    @Mapping(target = "store", ignore = true)
    @Mapping(target = "id", ignore = true)
    Item mapDTOToItem(ItemDTO dto, @Context StoreRepository storeRepository);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "store", ignore = true)
    void updateItemFromDTO(ItemDTO dto, @MappingTarget Item entity);

    @Mapping(target = "storeId", source = "store.id")
    ItemDTO mapItemToDto(Item save);
}