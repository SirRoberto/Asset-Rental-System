package com.rental.asset

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AssetRentalSystemApplication

fun main(args: Array<String>) {
    runApplication<AssetRentalSystemApplication>(*args)
}
