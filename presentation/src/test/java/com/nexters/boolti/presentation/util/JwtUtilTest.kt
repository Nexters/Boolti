package com.nexters.boolti.presentation.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class JwtUtilTest : BehaviorSpec() {
    private val sut = JwtUtil()

    init {
        given("jwt가 주어지고") {
            `when`("이 jwt를 디코드 했을 때") {
                then("payload의 key value 쌍을 반환한다.") {
                    val payloadMap = sut.decodePayload("eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIyZDVjYmUwYjRiYWNjMjUzMmY4NzE2NmUyYTdlNTNiMSIsInN1YiI6IjMzMjA1NjU1NjQiLCJhdXRoX3RpbWUiOjE3MDY2MTg0MTgsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwibmlja25hbWUiOiLshqHspIDsmIEiLCJleHAiOjE3MDY2NjE2MTgsImlhdCI6MTcwNjYxODQxOCwicGljdHVyZSI6Imh0dHA6Ly9rLmtha2FvY2RuLm5ldC9kbi8xRzlrcC9idHNBb3Q4bGlPbi84Q1d1ZGkzdXkwN3J2Rk5Va2szRVIwL2ltZ18xMTB4MTEwLmpwZyJ9.f-pvLKlSly9KCDBHc0d_-ccKlwr48ezCrLuwxiD5VCjLoEJRACCDnB49K3ygCTzLKZZHG5KNJNheVci5v7kdqegsxRa3xLcRLQ7Vz-NluSwFHiSe6Ska9QRXEDY1lOPRSh103b2fEEAykVUH6VqRwnal6S1-X3d9C3QMSvz1mXIUbqkFL5sCygbvszQY1cERC0NLQ55qIhD8AWTyeWKp_wQwvBpSW3WQEhjyWaTJZqrb9i7s5ZouqsK6B9rKmml3bM18qmLydh8ruV48yUbcI-Zm3ugwLI-8KZbfWASwCgEdyQYROq8fspziPdNXlSGnEnFRziwitP4ZFA5UVbt_FQ")
                    payloadMap["nickname"] shouldBe "송준영"
                    payloadMap["picture"] shouldBe "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg"
                    payloadMap["sub"] shouldBe "3320565564"
                }
            }

            `when`("이 하이픈 들어간 jwt를 디코드 했을 때") {
                then("payload의 key value 쌍을 반환한다.") {
                    val payloadMap =
                        sut.decodePayload("eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.ewogICJhdWQiOiAiMmQ1Y2JlMGI0YmFjYzI1MzJmODcxNjZlMmE3ZTUzYjHvuqoiLAogICJzdWIiOiAiMzMyMDU2NTU2NCIsCiAgImF1dGhfdGltZSI6IDE3MDY2MTg0MTgsCiAgImlzcyI6ICJodHRwczovL2thdXRoLmtha2FvLmNvbSIsCiAgIm5pY2tuYW1lIjogIu2Zjeq4uOuPme-6kiIsCiAgImV4cCI6IDE3MDY2NjE2MTgsCiAgImlhdCI6IDE3MDY2MTg0MTgsCiAgInBpY3R1cmUiOiAiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuLzFHOWtwL2J0c0FvdDhsaU9uLzhDV3VkaTN1eTA3cnZGTlVrazNFUjAvaW1nXzExMHgxMTAuanBnIgp9.f-pvLKlSly9KCDBHc0d_-ccKlwr48ezCrLuwxiD5VCjLoEJRACCDnB49K3ygCTzLKZZHG5KNJNheVci5v7kdqegsxRa3xLcRLQ7Vz-NluSwFHiSe6Ska9QRXEDY1lOPRSh103b2fEEAykVUH6VqRwnal6S1-X3d9C3QMSvz1mXIUbqkFL5sCygbvszQY1cERC0NLQ55qIhD8AWTyeWKp_wQwvBpSW3WQEhjyWaTJZqrb9i7s5ZouqsK6B9rKmml3bM18qmLydh8ruV48yUbcI-Zm3ugwLI-8KZbfWASwCgEdyQYROq8fspziPdNXlSGnEnFRziwitP4ZFA5UVbt_FQ")
                    payloadMap["nickname"] shouldBe "홍길동ﺒ"
                    payloadMap["picture"] shouldBe "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg"
                    payloadMap["sub"] shouldBe "3320565564"
                }
            }

            `when`("그런데 만약 JWT의 payload 부분이 표준대로 Base64 URL 인코드된 스트링이 아니라 그냥 Base64 인코딩된 스트링이라면") {
                then("IllegalArgumentException을 터뜨린다.") {
                    shouldThrow<IllegalArgumentException> {
                        sut.decodePayload("eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.ewogICJhdWQiOiAiMmQ1Y2JlMGI0YmFjYzI1MzJmODcxNjZlMmE3ZTUzYjHvuqoiLAogICJzdWIiOiAiMzMyMDU2NTU2NCIsCiAgImF1dGhfdGltZSI6IDE3MDY2MTg0MTgsCiAgImlzcyI6ICJodHRwczovL2thdXRoLmtha2FvLmNvbSIsCiAgIm5pY2tuYW1lIjogIu2Zjeq4uOuPme+6kiIsCiAgImV4cCI6IDE3MDY2NjE2MTgsCiAgImlhdCI6IDE3MDY2MTg0MTgsCiAgInBpY3R1cmUiOiAiaHR0cDovL2sua2FrYW9jZG4ubmV0L2RuLzFHOWtwL2J0c0FvdDhsaU9uLzhDV3VkaTN1eTA3cnZGTlVrazNFUjAvaW1nXzExMHgxMTAuanBnIgp9.f-pvLKlSly9KCDBHc0d_-ccKlwr48ezCrLuwxiD5VCjLoEJRACCDnB49K3ygCTzLKZZHG5KNJNheVci5v7kdqegsxRa3xLcRLQ7Vz-NluSwFHiSe6Ska9QRXEDY1lOPRSh103b2fEEAykVUH6VqRwnal6S1-X3d9C3QMSvz1mXIUbqkFL5sCygbvszQY1cERC0NLQ55qIhD8AWTyeWKp_wQwvBpSW3WQEhjyWaTJZqrb9i7s5ZouqsK6B9rKmml3bM18qmLydh8ruV48yUbcI-Zm3ugwLI-8KZbfWASwCgEdyQYROq8fspziPdNXlSGnEnFRziwitP4ZFA5UVbt_FQ")
                    }
                }
            }
        }
    }
}
