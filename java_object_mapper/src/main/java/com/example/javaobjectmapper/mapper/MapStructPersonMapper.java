package com.example.javaobjectmapper.mapper;

import com.example.javaobjectmapper.doma.MappedPeopleMapstruct;
import com.example.javaobjectmapper.doma.SourcePeople;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {AddressFormatter.class, AgeGroups.class})
public interface MapStructPersonMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "sourceId", source = "id")
    @Mapping(target = "fullName", expression = "java(source.getFirstName() + \" \" + source.getLastName())")
    @Mapping(target = "ageGroup", expression = "java(AgeGroups.from(source.getAge()))")
    @Mapping(target = "addressLine", expression = "java(AddressFormatter.format(source.getCity(), source.getStreet(), source.getPostalCode()))")
    MappedPeopleMapstruct toEntity(SourcePeople source);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "sourceId", source = "id")
    @Mapping(target = "fullName", expression = "java(source.getFirstName() + \" \" + source.getLastName())")
    @Mapping(target = "ageGroup", expression = "java(AgeGroups.from(source.getAge()))")
    @Mapping(target = "addressLine", expression = "java(AddressFormatter.format(source.getCity(), source.getStreet(), source.getPostalCode()))")
    LombokMappedPerson toLombokDto(SourcePeople source);
}
