package com.example.demo.mapper;

import com.example.demo.dto.StoreDTO;
import com.example.demo.model.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoreMapper {

    StoreMapper MAPPER = Mappers.getMapper(StoreMapper.class);

    StoreDTO mapStoreToDto(Store store);

    @Mapping(target = "items", ignore = true)
    Store mapDTOToStore(StoreDTO dto);

    @Mapping(target = "items", ignore = true)
    void updateStoreFromDTO(StoreDTO dto, @MappingTarget Store entity);
}