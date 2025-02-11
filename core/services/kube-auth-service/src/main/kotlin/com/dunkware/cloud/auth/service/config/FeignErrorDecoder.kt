package com.dunkware.cloud.auth.service.config

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException

class FeignErrorDecoder : ErrorDecoder {
    private val delegate = ErrorDecoder.Default()
    private val objectMapper = ObjectMapper()

    override fun decode(methodKey: String, response: Response): Exception {
        return when (response.status()) {
            HttpStatus.BAD_REQUEST.value() -> {
                try {
                    val errorBody = response.body().asInputStream().use { 
                        objectMapper.readValue(it, ErrorResponse::class.java)
                    }
                    BadCredentialsException(errorBody.message)
                } catch (e: Exception) {
                    BadCredentialsException("Invalid credentials")
                }
            }
            else -> delegate.decode(methodKey, response)
        }
    }
}

data class ErrorResponse(
    val message: String
)