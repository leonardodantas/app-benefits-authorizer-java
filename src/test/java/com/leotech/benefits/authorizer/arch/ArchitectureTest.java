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
                .resideInAnyPackage(BASE + ".api", BASE + ".app", BASE + ".infra", BASE + ".config");

        rule.check(classes);
    }

    @Test
    @DisplayName("api must not depend directly on infra or config")
    void apiMustNotDependOnInfraOrConfig() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".api")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".infra", BASE + ".config");

        rule.check(classes);
    }

    @Test
    @DisplayName("infra must not depend on api or config")
    void infraMustNotDependOnApiOrConfig() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".infra")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".api", BASE + ".config");

        rule.check(classes);
    }

    @Test
    @DisplayName("app must not depend on api or infra")
    void appMustNotDependOnApiOrInfra() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE + ".app..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(BASE + ".api", BASE + ".infra");

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
