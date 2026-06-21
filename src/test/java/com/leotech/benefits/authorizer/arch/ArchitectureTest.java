package com.leotech.benefits.authorizer.arch;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final String BASE = "com.leotech.benefits.authorizer";

    @Test
    @DisplayName("domain must not depend on any other layer")
    void domainMustNotDependOnOtherLayers() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..app..", "..infra..", "..config..")
                .check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    @DisplayName("api must not depend directly on infra")
    void apiMustNotDependOnInfra() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infra..")
                .check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    @DisplayName("app must not depend on api or infra")
    void appMustNotDependOnApiOrInfra() {
        noClasses()
                .that().resideInAPackage("..app..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..infra..")
                .check(new ClassFileImporter().importPackages(BASE));
    }

    @Test
    @DisplayName("infra must not depend on api or config")
    void infraMustNotDependOnApiOrConfig() {
        noClasses()
                .that().resideInAPackage("..infra..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..config..")
                .check(new ClassFileImporter().importPackages(BASE));
    }
}
