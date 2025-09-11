package br.com.iasmin.iasminasteriskari.peer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertTrue

class PeerWriterServiceTest {

    @Test
    fun `geracao de senha MD5`() {
        // Arrange
        val service = PeerWriterService()
        val user = User(
            id = 1,
            name = "Alice",
            controlNumber = "VIP",
            roles = listOf("ROLE_USER"),
            ddr = "1234"
        )

        // Act: generate the peer config text
        val hash = generateMd5Hash(user)

        // Compute expected MD5(username:asterisk:username)
        val expectedHash = "2be9322c5cbe1c03b45f5f3f5fcaf639"

        // Assert
        assertEquals(expectedHash, hash, "md5_cred deve ser igual a MD5(username:asterisk:username)")
    }

    @Test
    fun `escrita do arquivo pjsip`(@TempDir tempDir: Path){
        val user = User(
            id = 1,
            name = "Alice",
            controlNumber = "VIP",
            roles = listOf("ROLE_USER"),
            ddr = "1234"
        )
        val user2 = User(
            id = 2,
            name = "Bob",
            controlNumber = "VIP",
            roles = listOf("ROLE_USER"),
            ddr = "1234"
        )
        val service = PeerWriterService()

        // Inicializa a propriedade injetada pelo Spring para o teste
        val field = PeerWriterService::class.java.getDeclaredField("ASTERISK_FOLDER")
        field.isAccessible = true
        field.set(service, tempDir.toAbsolutePath().toString())

        service.writePeers(listOf(user, user2))
        val file = tempDir.resolve("pjsip-peers.conf").toFile()
        // se quiser verificar a escrita o arquivo
//        FileOutputStream("/tmp/pjsip-peers.conf").use {
//            it.write(file.readBytes())
//            it.flush()
//            it.close()
//        }
        assertTrue(file.exists(), "O arquivo pjsip-peers.conf deve existir")
        assertTrue(file.readText().contains("Alice"), "O arquivo pjsip-peers.conf deve conter o usuário Alice")
        assertTrue(file.readText().contains("Bob"), "O arquivo pjsip-peers.conf deve conter o usuário Bob")
    }

}
