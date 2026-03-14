package com.rental.asset.infrastructure.config

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import java.net.URI

@ConfigurationProperties(prefix = "storage")
data class StorageProperties(
    val endpoint: String = "http://localhost:9000",
    val accessKey: String = "minioadmin",
    val secretKey: String = "minioadmin",
    val bucket: String = "asset-photos"
)

@Configuration
class StorageConfig {

    private val log = LoggerFactory.getLogger(StorageConfig::class.java)

    @Bean
    fun s3Client(props: StorageProperties): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(props.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.accessKey, props.secretKey)
                )
            )
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build()
    }

    @Bean
    fun ensureBucketExists(s3Client: S3Client, props: StorageProperties) = CommandLineRunner {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(props.bucket).build())
            log.info("Storage bucket '${props.bucket}' already exists")
        } catch (e: NoSuchBucketException) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(props.bucket).build())
            log.info("Created storage bucket '${props.bucket}'")
        } catch (e: Exception) {
            log.warn("Could not verify storage bucket '${props.bucket}': ${e.message}. Storage will be unavailable.")
        }
    }
}
