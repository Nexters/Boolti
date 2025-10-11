package com.nexters.boolti.presentation.extension

/**
 * 한글 자음(초성)인지 판단하는 확장 함수
 *
 * 판단 범위: ㄱ ~ ㅎ (19개 자음)
 * 유니코드 범위: U+3131 ~ U+314E
 *
 * @return 한글 자음이면 true, 아니면 false
 */
fun Char.is자음(): Boolean = this in '\u3131'..'\u314E'

/**
 * 한글 모음인지 판단하는 확장 함수
 *
 * 판단 범위: ㅏ ~ ㅣ (21개 모음)
 * 유니코드 범위: U+314F ~ U+3163
 *
 * @return 한글 모음이면 true, 아니면 false
 */
fun Char.is모음(): Boolean = this in '\u314F'..'\u3163'

/**
 * 한글 자음 또는 모음인지 판단하는 확장 함수
 *
 * 판단 범위: ㄱ ~ ㅣ (자음 19개 + 모음 21개)
 * 유니코드 범위: U+3131 ~ U+3163
 *
 * @return 한글 자음 또는 모음이면 true, 아니면 false
 */
fun Char.is한글자모(): Boolean = this in '\u3131'..'\u3163'
