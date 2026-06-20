package com.leotech.benefits.authorizer.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArchitectureTest {

    private static final String BASE = "com.leotech.benefits.authorizer";
    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages(BASE);
    }

    @Test
    @DisplayName("domain must not depend on any other layer")
    void domainMustNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..app..", "..infra..", "..config..");

        rule.check(classes);
    }

    @Test
    @DisplayName("api must not depend directly on infra or config")
    void apiMustNotDependOnInfraOrConfig() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infra..", "..config..");

        rule.check(classes);
    }

    @Test
    @DisplayName("infra must not depend on api or config")
    void infraMustNotDependOnApiOrConfig() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..infra..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..config..");

        rule.check(classes);
    }

    @Test
    @DisplayName("app must not depend on api, infra or config")
    void appMustNotDependOnApiInfraOrConfig() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..app..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..infra..", "..config..");

        rule.check(classes);
    }

    @Test
    @DisplayName("there must be no cyclic dependencies between packages")
    void noCyclicDependencies() {
        final var mainClasses = new ClassFileImporter()
                .importPackages(BASE + ".api", BASE + ".app", BASE + ".config", BASE + ".domain", BASE + ".infra");

        ArchRule rule = slices()
                .matching(BASE + ".(*)..")
                .should().beFreeOfCycles();

        rule.check(mainClasses);
    }
}
