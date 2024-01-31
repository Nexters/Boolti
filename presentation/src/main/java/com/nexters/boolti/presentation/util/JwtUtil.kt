package com.nexters.boolti.presentation.util

import java.util.Base64

class JwtUtil {
    fun decodePayload(jwt: String): Map<String, String> {
        val originPayload = jwt.split(".")[1]
        val decodedPayload = Base64.getDecoder()
            .decode(originPayload)
            .toString(Charsets.UTF_8)
        val resultMap = mutableMapOf<String, String>()
        decodedPayload
            .substring(1, decodedPayload.length - 1) // {, } 제거
            .split(",").forEach {
                // 이미지 주소 url에 :가 포함됨에 유의
                val key = it.split(":").first().replace("\"", "")
                val value = it.substringAfter(":").replace("\"", "")
                resultMap[key.trim()] = value.trim()
            }

        return resultMap
    }
}