package br.com.iasmin.iasminasteriskari

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "ari.enabled=false",
        "ari.base-url=http://localhost:8088/ari",
        "ari.username=test",
        "ari.password=test",
        "ari.app-name=test-app"
    ]
)
class IasminAsteriskAriApplicationTests {

    @Test
    fun contextLoads() {
    }

}
