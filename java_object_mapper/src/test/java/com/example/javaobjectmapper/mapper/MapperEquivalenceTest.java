package com.example.javaobjectmapper.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.javaobjectmapper.doma.MappedPeopleMapstruct;
import com.example.javaobjectmapper.doma.MappedPeopleModelmapper;
import com.example.javaobjectmapper.doma.SourcePeople;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "benchmark.auto-run=false")
class MapperEquivalenceTest {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MapStructPersonMapper mapStructPersonMapper;

    @Test
    void modelMapperAndMapStructCreateSameTargetValues() {
        SourcePeople source = new SourcePeople();
        source.setId(10L);
        source.setFirstName("Taro");
        source.setLastName("Yamada");
        source.setAge(34);
        source.setEmail("taro@example.com");
        source.setCity("Tokyo");
        source.setStreet("1-2-3 Marunouchi");
        source.setPostalCode("100-0005");
        source.setLoyaltyPoints(1234);
        source.setCreatedAt(LocalDateTime.of(2026, 1, 2, 3, 4, 5));

        MappedPeopleModelmapper modelMapperResult = modelMapper.map(source, MappedPeopleModelmapper.class);
        MappedPeopleMapstruct mapStructResult = mapStructPersonMapper.toEntity(source);

        assertThat(modelMapperResult.getId()).isEqualTo(mapStructResult.getId());
        assertThat(modelMapperResult.getSourceId()).isEqualTo(mapStructResult.getSourceId());
        assertThat(modelMapperResult.getFullName()).isEqualTo(mapStructResult.getFullName());
        assertThat(modelMapperResult.getAge()).isEqualTo(mapStructResult.getAge());
        assertThat(modelMapperResult.getAgeGroup()).isEqualTo(mapStructResult.getAgeGroup());
        assertThat(modelMapperResult.getEmail()).isEqualTo(mapStructResult.getEmail());
        assertThat(modelMapperResult.getAddressLine()).isEqualTo(mapStructResult.getAddressLine());
        assertThat(modelMapperResult.getLoyaltyPoints()).isEqualTo(mapStructResult.getLoyaltyPoints());
        assertThat(modelMapperResult.getCreatedAt()).isEqualTo(mapStructResult.getCreatedAt());

        LombokMappedPerson modelMapperLombokResult = modelMapper.map(source, LombokMappedPerson.class);
        LombokMappedPerson mapStructLombokResult = mapStructPersonMapper.toLombokDto(source);

        assertThat(modelMapperLombokResult).usingRecursiveComparison().isEqualTo(mapStructLombokResult);
    }
}
