package com.example.javaobjectmapper.config;

import com.example.javaobjectmapper.doma.MappedPeopleLombokMapstruct;
import com.example.javaobjectmapper.doma.MappedPeopleLombokModelmapper;
import com.example.javaobjectmapper.doma.MappedPeopleMapstruct;
import com.example.javaobjectmapper.doma.MappedPeopleModelmapper;
import com.example.javaobjectmapper.doma.SourcePeople;
import com.example.javaobjectmapper.mapper.LombokMappedPerson;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;

@Component
@ImportRuntimeHints(NativeRuntimeHints.MapperHints.class)
public class NativeRuntimeHints {

    static class MapperHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("META-INF/com/example/javaobjectmapper/doma/PersonDao/*.sql");
            registerDomaEntity(hints, SourcePeople.class);
            registerDomaEntity(hints, MappedPeopleModelmapper.class);
            registerDomaEntity(hints, MappedPeopleMapstruct.class);
            registerDomaEntity(hints, MappedPeopleLombokModelmapper.class);
            registerDomaEntity(hints, MappedPeopleLombokMapstruct.class);
            registerModelMapperType(hints, LombokMappedPerson.class);
            hints.reflection().registerType(TypeReference.of("org.modelmapper.internal.typetools.TypeResolver"),
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.ACCESS_DECLARED_FIELDS);
            hints.reflection().registerConstructor(LombokMappedPerson.class.getDeclaredConstructors()[0], ExecutableMode.INVOKE);
        }

        private static void registerDomaEntity(RuntimeHints hints, Class<?> type) {
            hints.reflection().registerType(type,
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                    MemberCategory.ACCESS_DECLARED_FIELDS);
        }

        private static void registerModelMapperType(RuntimeHints hints, Class<?> type) {
            hints.reflection().registerType(type,
                    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS,
                    MemberCategory.ACCESS_DECLARED_FIELDS);
        }
    }
}
