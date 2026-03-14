package com.rental.asset.infrastructure.adapter.out

import com.rental.asset.application.port.out.QrCodeGeneratorPort
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DefaultQrCodeGeneratorAdapter : QrCodeGeneratorPort {
    override fun generateToken(prefix: String): String {
        // Simple NanoID-like generator or UUID based for demonstration
        val randomString = UUID.randomUUID().toString().substring(0, 8).uppercase()
        return "$prefix-$randomString"
    }
}
