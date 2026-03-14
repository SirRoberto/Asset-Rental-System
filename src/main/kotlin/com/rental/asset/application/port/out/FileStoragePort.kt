package com.rental.asset.application.port.out

interface FileStoragePort {
    /**
     * Uploads a file to storage.
     * @param path the object key / path in the bucket
     * @param content file bytes
     * @param contentType MIME type (e.g. image/jpeg)
     * @return public URL of the uploaded file
     */
    fun uploadFile(path: String, content: ByteArray, contentType: String): String
}
