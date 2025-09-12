package br.com.iasmin.iasminasteriskari.upload

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@RestController
@RequestMapping("/uploads")
class UploadController(private val uploadService: UploadService) {

    data class ErrorResponse(val message: String)

    @CrossOrigin
    @PostMapping(
        path = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadAudio(@PathVariable id: String, @RequestParam("audio") audio: MultipartFile): ResponseEntity<Any> {
        if (audio.isEmpty) {
            return ResponseEntity.badRequest().body(ErrorResponse("Faltando arquivo de audio"))
        }
        if (audio.size > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(ErrorResponse("Arquivo maior que 5MB"))
        }
        if (!audio.contentType?.equals("audio/mpeg", true)!!) {
            return ResponseEntity.badRequest().body(ErrorResponse("Somente arquivos MP3 são permitidos"))
        }
        return ResponseEntity.ok(uploadService.uploadAudio(id, audio))
    }
}