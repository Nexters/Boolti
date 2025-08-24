package com.nexters.boolti.data.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class YouTubeUrlUtilsTest : DescribeSpec({

    describe("extractVideoId") {
        context("유효한 YouTube URL이 주어졌을 때") {
            it("www.youtube.com/watch?v= 형태에서 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.youtube.com/watch?v=eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("youtu.be/ 형태에서 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://youtu.be/eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("youtube.com/embed/ 형태에서 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.youtube.com/embed/eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("youtube.com/v/ 형태에서 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.youtube.com/v/eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("m.youtube.com/watch?v= 형태에서 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://m.youtube.com/watch?v=eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("http 프로토콜에서도 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("http://www.youtube.com/watch?v=eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("프로토콜이 없어도 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("www.youtube.com/watch?v=eP4ga_fNm-E")
                videoId shouldBe "eP4ga_fNm-E"
            }

            it("추가 파라미터가 있어도 video ID를 추출한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.youtube.com/watch?v=eP4ga_fNm-E&t=10s&list=abc123")
                videoId shouldBe "eP4ga_fNm-E"
            }
        }

        context("유효하지 않은 입력이 주어졌을 때") {
            it("null 입력에 대해 null을 반환한다") {
                val videoId = YouTubeUrlUtils.extractVideoId(null)
                videoId shouldBe null
            }

            it("빈 문자열 입력에 대해 null을 반환한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("")
                videoId shouldBe null
            }

            it("공백만 있는 문자열 입력에 대해 null을 반환한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("   ")
                videoId shouldBe null
            }

            it("YouTube가 아닌 URL에 대해 null을 반환한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.google.com")
                videoId shouldBe null
            }

            it("유효하지 않은 video ID 길이에 대해 null을 반환한다") {
                val videoId = YouTubeUrlUtils.extractVideoId("https://www.youtube.com/watch?v=shortid")
                videoId shouldBe null
            }
        }
    }

    describe("isValidVideoId") {
        context("유효한 video ID가 주어졌을 때") {
            it("11자리 영문자/숫자/_/- 조합에 대해 true를 반환한다") {
                YouTubeUrlUtils.isValidVideoId("eP4ga_fNm-E") shouldBe true
                YouTubeUrlUtils.isValidVideoId("0123456789a") shouldBe true
                YouTubeUrlUtils.isValidVideoId("abc_def-hij") shouldBe true
            }
        }

        context("유효하지 않은 video ID가 주어졌을 때") {
            it("null에 대해 false를 반환한다") {
                YouTubeUrlUtils.isValidVideoId(null) shouldBe false
            }

            it("빈 문자열에 대해 false를 반환한다") {
                YouTubeUrlUtils.isValidVideoId("") shouldBe false
            }

            it("11자리가 아닌 문자열에 대해 false를 반환한다") {
                YouTubeUrlUtils.isValidVideoId("shortid") shouldBe false
                YouTubeUrlUtils.isValidVideoId("toolongvideoid123") shouldBe false
            }

            it("허용되지 않은 문자가 포함된 경우 false를 반환한다") {
                YouTubeUrlUtils.isValidVideoId("dQw4w9WgXc@") shouldBe false
                YouTubeUrlUtils.isValidVideoId("dQw4w9WgXc#") shouldBe false
            }
        }
    }

    describe("isValidYouTubeUrl") {
        context("유효한 YouTube URL이 주어졌을 때") {
            it("true를 반환한다") {
                YouTubeUrlUtils.isValidYouTubeUrl("https://www.youtube.com/watch?v=eP4ga_fNm-E") shouldBe true
                YouTubeUrlUtils.isValidYouTubeUrl("https://youtu.be/eP4ga_fNm-E") shouldBe true
            }
        }

        context("유효하지 않은 YouTube URL이 주어졌을 때") {
            it("false를 반환한다") {
                YouTubeUrlUtils.isValidYouTubeUrl("https://www.google.com") shouldBe false
                YouTubeUrlUtils.isValidYouTubeUrl(null) shouldBe false
            }
        }
    }

    describe("getThumbnailUrl") {
        it("기본 품질로 썸네일 URL을 생성한다") {
            val thumbnailUrl = YouTubeUrlUtils.getThumbnailUrl("eP4ga_fNm-E")
            thumbnailUrl shouldBe "https://img.youtube.com/vi/eP4ga_fNm-E/mqdefault.jpg"
        }

        it("지정된 품질로 썸네일 URL을 생성한다") {
            val thumbnailUrl = YouTubeUrlUtils.getThumbnailUrl("eP4ga_fNm-E", "hqdefault")
            thumbnailUrl shouldBe "https://img.youtube.com/vi/eP4ga_fNm-E/hqdefault.jpg"
        }
    }
})
