package br.com.iasmin.iasminasteriskari.upload

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */

@Service
class UploadService {

    @Value("\${audio.record}")
    private lateinit var AUDIO_RECORD: String

    fun uploadAudio(id: String, audio: MultipartFile): UploadResponse {
        val audioName = audioName(id)
        val mp3Dir = "$AUDIO_RECORD/mp3s"
        audio.transferTo(File("$mp3Dir/$audioName"))
        return UploadResponse(audioName)
    }

    private fun audioName(id: String): String {
        val timestamp = System.currentTimeMillis()
        return "upload-$id-$timestamp.mp3"
    }
}