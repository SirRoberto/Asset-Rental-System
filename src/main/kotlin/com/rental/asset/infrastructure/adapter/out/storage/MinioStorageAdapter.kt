package com.rental.asset.infrastructure.adapter.out.storage

import com.rental.asset.application.port.out.FileStoragePort
import com.rental.asset.infrastructure.config.StorageProperties
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class MinioStorageAdapter(
    private val s3Client: S3Client,
    private val storageProperties: StorageProperties
) : FileStoragePort {

    override fun uploadFile(path: String, content: ByteArray, contentType: String): String {
        val putRequest = PutObjectRequest.builder()
            .bucket(storageProperties.bucket)
            .key(path)
            .contentType(contentType)
            .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(content))

        return "${storageProperties.endpoint}/${storageProperties.bucket}/$path"
    }
}
