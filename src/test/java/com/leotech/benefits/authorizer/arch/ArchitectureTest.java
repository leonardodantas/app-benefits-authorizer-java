package com.leotech.benefits.authorizer.arch;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.leotech.benefits.authorizer";
    private static com.tngtech.archunit.core.domain.JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    @Test
    @DisplayName("domain must not depend on any other layer")
    void domainMustNotDependOnOtherLayers() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".api..", BASE + ".app..", BASE + ".infra..", BASE + ".config")
                .check(classes);
    }

    @Test
    @DisplayName("api must not depend directly on infra")
    void apiMustNotDependOnInfra() {
        noClasses()
                .that().resideInAPackage(BASE + ".api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".infra..")
                .check(classes);
    }

    @Test
    @DisplayName("app must not depend on api or infra")
    void appMustNotDependOnApiOrInfra() {
        noClasses()
                .that().resideInAPackage(BASE + ".app..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".api..", BASE + ".infra..")
                .check(classes);
    }

    @Test
    @DisplayName("infra must not depend on api or config")
    void infraMustNotDependOnApiOrConfig() {
        noClasses()
                .that().resideInAPackage(BASE + ".infra..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".api..", BASE + ".config")
                .check(classes);
    }

    @Test
    @DisplayName("api must not depend on config")
    void apiMustNotDependOnConfig() {
        noClasses()
                .that().resideInAPackage(BASE + ".api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".config")
                .check(classes);
    }

    @Test
    @DisplayName("controllers must be only in api layer")
    void controllersMustBeOnlyInApi() {
        noClasses()
                .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .should().resideOutsideOfPackage(BASE + ".api..")
                .check(classes);
    }

    @Test
    @DisplayName("services must be only in app layer")
    void servicesMustBeOnlyInApp() {
        noClasses()
                .that().areAnnotatedWith("org.springframework.stereotype.Service")
                .should().resideOutsideOfPackage(BASE + ".app..")
                .check(classes);
    }

}
