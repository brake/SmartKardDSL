/*
 *        Copyright 2019 Constantin Roganov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.brake.smart_card.dsl

import javax.xml.bind.DatatypeConverter.parseHexBinary
import javax.xml.bind.DatatypeConverter.printHexBinary

private fun String.removeSpaces(): String =
    if (contains(' '))  replace("\\s+", "")
    else this

/** Convert hex [String] to [ByteArray] if [this] string contains correct hexadecimal digits.
 * Whitespaces will be removed if present in [this] string.
 */
fun String.hexToBytesOrNull(): ByteArray? = try {
    parseHexBinary(this.removeSpaces())
} catch (_: Exception) {
    null
}

/**
 * Convert hex [String] to [ByteArray] if [this] string contains correct hexadecimal digits.
 *
 * Whitespaces will be removed if present in [this] string.
 * @throws NumberFormatException if [this] contains invalid hex digits.
 */
fun String.hexToBytesOrThrow(): ByteArray = parseHexBinary(this.removeSpaces())

/** Convert [ByteArray] to hex [String] */
fun ByteArray.toHexString(): String = printHexBinary(this)

/** Convert each ASCII character to its byte code and return result array of bytes
 *
 * Whitespaces will be removed if present in [this] string.
 */
fun String.toASCIIBytes(): ByteArray = removeSpaces()
    .map(Char::toInt)
    .map(Int::toByte)
    .toByteArray()

/** Convert hex string like 982143 to 891234. Return null if length of [this] [String] is odd */
fun String.swapNibblesOrNull(): String? = when {
    isEmpty() -> this
    length % 2 != 0 -> null
    else -> {
        zipWithNext()
            .filterIndexed { i, _ -> i % 2 == 0 }
            .fold(StringBuffer(length)) { acc, (f, s) -> acc.append(s).append(f) }
            .toString()
    }
}

/**
 * Transform [Int] to two byte [ByteArray] containing high and low bytes of receiver.
 * Assumes that receiver actually contains the [Short] value.
 */
fun Int.toShortBytes(): ByteArray =
    ByteArray(Short.SIZE_BYTES) {
        (this shr (it * 8)).toByte()
    }.apply {
        reverse()
    }

/** Convert [this] value to array of two bytes */
fun Short.toBytes(): ByteArray = toInt().toShortBytes()

/** Return [Pair] from high and low bytes of two byte [Int] */
fun Int.toHiLo(): Pair<Int, Int> =
    (1 downTo 0).map {
        this shr (it * 8) and 0xFF
    }.let { it[0] to it[1] }

