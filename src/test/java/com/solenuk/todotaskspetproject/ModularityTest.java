package com.solenuk.todotaskspetproject;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {
    static final ApplicationModules modules = ApplicationModules.of(ToDoTasksPetProjectApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void printsModuleStructure() {
        modules.forEach(System.out::println);
    }
}
