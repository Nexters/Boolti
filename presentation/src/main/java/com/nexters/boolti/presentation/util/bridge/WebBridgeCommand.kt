package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable
import kotlinx.serialization.SerialInfo

/**
 * 웹 브릿지 커맨드의 data 타입에 붙인다. `@Serializable`을 따로 붙이지 않아도 직렬화된다.
 *
 * ```
 * @WebBridgeCommand("SHOW_TOAST")
 * data class ShowToast(val message: String)
 * ```
 *
 * @param name 웹과 맞춘 커맨드 이름
 */
@OptIn(ExperimentalSerializationApi::class)
@MetaSerializable
@SerialInfo
@Target(AnnotationTarget.CLASS)
annotation class WebBridgeCommand(val name: String)
