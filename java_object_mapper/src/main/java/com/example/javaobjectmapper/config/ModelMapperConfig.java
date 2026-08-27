package com.example.javaobjectmapper.config;

import com.example.javaobjectmapper.doma.MappedPeopleModelmapper;
import com.example.javaobjectmapper.doma.SourcePeople;
import com.example.javaobjectmapper.mapper.AddressFormatter;
import com.example.javaobjectmapper.mapper.AgeGroups;
import com.example.javaobjectmapper.mapper.LombokMappedPerson;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(SourcePeople.class, MappedPeopleModelmapper.class)
                .setConverter(context -> {
                    SourcePeople source = context.getSource();
                    MappedPeopleModelmapper target = new MappedPeopleModelmapper();
                    target.setId(source.getId());
                    target.setSourceId(source.getId());
                    target.setFullName(source.getFirstName() + " " + source.getLastName());
                    target.setAge(source.getAge());
                    target.setAgeGroup(AgeGroups.from(source.getAge()));
                    target.setEmail(source.getEmail());
                    target.setAddressLine(AddressFormatter.format(source.getCity(), source.getStreet(), source.getPostalCode()));
                    target.setLoyaltyPoints(source.getLoyaltyPoints());
                    target.setCreatedAt(source.getCreatedAt());
                    return target;
                });
        modelMapper.createTypeMap(SourcePeople.class, LombokMappedPerson.class)
                .setConverter(context -> {
                    SourcePeople source = context.getSource();
                    LombokMappedPerson target = new LombokMappedPerson();
                    target.setId(source.getId());
                    target.setSourceId(source.getId());
                    target.setFullName(source.getFirstName() + " " + source.getLastName());
                    target.setAge(source.getAge());
                    target.setAgeGroup(AgeGroups.from(source.getAge()));
                    target.setEmail(source.getEmail());
                    target.setAddressLine(AddressFormatter.format(source.getCity(), source.getStreet(), source.getPostalCode()));
                    target.setLoyaltyPoints(source.getLoyaltyPoints());
                    target.setCreatedAt(source.getCreatedAt());
                    return target;
                });
        return modelMapper;
    }
}
