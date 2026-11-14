package com.nexters.boolti.presentation.util.bridge

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

private suspend inline fun <reified T : Any> receive(command: String, data: String): T? {
    var received: T? = null
    WebBridge(CoroutineScope(Job())) { handle { t: T -> received = t } }
        .dispatch("""{"id":"1","timestamp":"1759000000000","command":"$command","data":$data}""")
    return received
}

/**
 * 웹(boolti-web packages/bridge)이 실제로 보내는 형태의 메시지를 앱 커맨드 타입으로 읽을 수 있는지 확인한다.
 */
class WebBridgeCommandsTest : DescribeSpec({

    it("SHOW_TOAST의 메시지와 대문자 duration을 읽는다") {
        receive<ShowToast>("SHOW_TOAST", """{"message":"주소를 복사했어요.","duration":"LONG"}""") shouldBe
            ShowToast(message = "주소를 복사했어요.", duration = ToastDuration.LONG)
    }

    it("NAVIGATE_TO_SHOW_DETAIL의 숫자 showId를 읽는다") {
        receive<NavigateToShowDetail>("NAVIGATE_TO_SHOW_DETAIL", """{"showId":144}""") shouldBe
            NavigateToShowDetail(showId = 144)
    }

    it("NAVIGATE_TO_PLACE_DETAIL의 숫자 placeId를 읽는다") {
        receive<NavigateToPlaceDetail>("NAVIGATE_TO_PLACE_DETAIL", """{"placeId":3}""") shouldBe
            NavigateToPlaceDetail(placeId = 3)
    }

    it("VIEW_PLACE_PHOTO_LIST는 앱이 쓰지 않는 imageIds가 있어도 읽는다") {
        receive<ViewPlacePhotoList>("VIEW_PLACE_PHOTO_LIST", """{"id":3,"imageIds":[10,11]}""") shouldBe
            ViewPlacePhotoList(placeId = 3)
    }

    it("VIEW_PLACE_PHOTO_DETAIL의 id를 placeId로 읽는다") {
        receive<ViewPlacePhotoDetail>("VIEW_PLACE_PHOTO_DETAIL", """{"id":3,"imageId":10}""") shouldBe
            ViewPlacePhotoDetail(placeId = 3, imageId = 10)
    }
})
