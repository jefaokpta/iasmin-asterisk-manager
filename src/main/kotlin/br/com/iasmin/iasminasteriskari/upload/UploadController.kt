package br.com.iasmin.iasminasteriskari.upload

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.ResponseEntity

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@RestController
@RequestMapping("/uploads")
class UploadController(private val uploadService: UploadService) {
    
    @PostMapping("/{id}")
    fun uploadAudio(@PathVariable id: String, @RequestParam("audio") audio: MultipartFile): ResponseEntity<String> {
        if (audio.isEmpty) {
            return ResponseEntity.badRequest().body("Faltando arquivo de audio")
        }
        if (audio.size > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("Arquivo maior que 5MB")
        }
        if (!audio.contentType?.equals("audio/mpeg", true)!!) {
            return ResponseEntity.badRequest().body("Somente arquivos MP3 são permitidos")
        }
        return ResponseEntity.ok(jacksonObjectMapper().writeValueAsString(uploadService.uploadAudio(id, audio)))
    }
}