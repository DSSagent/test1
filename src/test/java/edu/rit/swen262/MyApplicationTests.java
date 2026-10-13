
package edu.rit.swen262;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.profiles.active=test")
class MyApplicationTests {
    @Test void contextLoadsWithoutStartingTheInteractiveMenu() {}
}