package br.com.iasmin.iasminasteriskari.ami

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

@Disabled("Não precisam ser executados em cada build")
class CdrServiceTest {

    private class TestableCdrService : CdrService() {
        var captured: List<String>? = null
        override fun launch(command: List<String>) {
            captured = command
        }
    }

    @Test
    fun `convertAudioToMp3 builds expected ffmpeg command`() {
        val audioRecordBase = "/tmp/records"
        val uniqueId = "1757431597.1004"
        val callRecord = "1757431597-1004.mp3"

        val cdr = Cdr(
            peer = "1",
            src = "1000",
            destination = "2000",
            callerId = "1000",
            duration = 10,
            billableSeconds = 10,
            uniqueId = uniqueId,
            disposition = "ANSWERED",
            company = "999",
            startTime = "2025-09-09 12:00:00",
            callRecord = callRecord,
            channel = "PJSIP/1-00000001",
            userfield = "OUTBOUND",
            destinationChannel = "PJSIP/2-00000002"
        )

        val service = TestableCdrService()
        // inject the AUDIO_RECORD property
        ReflectionTestUtils.setField(service, "AUDIO_RECORD", audioRecordBase)

        service.convertAudioToMp3(cdr)

        val expected = listOf(
            "ffmpeg",
            "-i",
            "$audioRecordBase/${uniqueId.replace('.', '-')}-MIXED.sln",
            "-vn",
            "-acodec",
            "libmp3lame",
            "-ab",
            "128k",
            "$audioRecordBase/mp3s/$callRecord"
        )

        assertEquals(expected, service.captured, "ffmpeg command should match expected arguments and paths")
    }
}
