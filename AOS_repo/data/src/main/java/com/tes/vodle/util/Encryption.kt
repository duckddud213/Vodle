package com.tes.vodle.util

import com.tes.data.BuildConfig
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val HMAC_ALGORITHM = "HmacSHA256"

@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
fun calculateHmac(data: String): String {
    val secretKeySpec = SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), HMAC_ALGORITHM)
    val mac = Mac.getInstance(HMAC_ALGORITHM)
    mac.init(secretKeySpec)
    val hmacBytes = mac.doFinal(data.toByteArray())
    return Base64.getEncoder().encodeToString(hmacBytes).toString()
}
