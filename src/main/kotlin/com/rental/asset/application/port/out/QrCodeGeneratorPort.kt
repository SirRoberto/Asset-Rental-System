package com.rental.asset.application.port.out

interface QrCodeGeneratorPort {
    fun generateToken(prefix: String = "INV"): String
}
