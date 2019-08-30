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

import com.github.brake.smart_card.dsl.commands.CommandAPDUBuilder
import com.github.brake.smart_card.dsl.commands.apdu
import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import javax.smartcardio.CommandAPDU

class TestCorrectCreation: StringSpec({
    val apduInts = intArrayOf(0xA0, 0xA4, 0x1, 0xC4, 0x4, 0xB1, 0xB2, 0xB3, 0xB4)
    val apduBytes = apduInts.map { it.toByte() }.toByteArray()
    val apduValue = CommandAPDU(apduBytes)

    "Test with Int and ByteArrays APDU" {

        apdu {
            cla { apduInts[0] }
            ins { apduInts[1] }
            p1 { apduInts[2] }
            p2 { apduInts[3] }
            data { apduBytes.sliceArray(5 until apduInts.size) }
        } shouldBe apduValue

        apdu {
            bytes { apduBytes }
        } shouldBe apduValue
    }

    "Test with hex strings" {
        apdu {
            claHex { "A0" }
            insHex { "A4" }
            p1Hex { "01" }
            p2Hex { "C4" }
            dataHex { "B1B2B3B4" }
        } shouldBe apduValue

        apdu {
            bytesHex { apduBytes.toHexString() }
        } shouldBe apduValue
    }

    "Minimal initialization (use defaults)" {
        apdu {
            ins { 0xA4 }
            dataHex { "3F00" }
        } shouldBe CommandAPDU("00A40000023F00".hexToBytesOrNull())
    }

    "Initialization with Nr parameter" {
        val respLen = 0x0A
        val (c, i, p1v, p2v) = apduInts

        apdu {
            cla { c }
            ins { i }
            p1 { p1v }
            p2 { p2v }
            data { apduBytes.sliceArray(5 until apduInts.size) }
            responseLength { respLen }
        } shouldBe CommandAPDU("A0A401C404B1B2B3B40A".hexToBytesOrNull())
    }
})

const val LESSER_MESSAGE = "should be lesser than"

fun testWithBigValue(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<IllegalArgumentException> { apdu(init) }.message should contain(LESSER_MESSAGE)
}

fun testWithInvalidHexValue(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<NumberFormatException> { apdu(init) }
}

fun testWithInvalidBytes(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<IllegalArgumentException> { apdu(init) }.message should contain("Invalid hex string passed")
}

class TestIncorrectCreation: StringSpec({
    "No initialization" {
        shouldThrow<IllegalStateException> {
            apdu { }
        }.message should contain("Instruction byte not set")
    }

    "Invalid INS" {
        testWithBigValue { ins { 500 } }
        testWithBigValue { insHex {"AAA"} }
        testWithInvalidHexValue { insHex { "RR" } }
    }

    "Invalid CLA" {
        testWithBigValue { cla { 500 } }
        testWithBigValue { claHex {"AAA"} }
        testWithInvalidHexValue { claHex { "RR" } }
    }

    "Invalid P1" {
        testWithBigValue { p1 { 500 } }
        testWithBigValue { p1Hex {"AAA"} }
        testWithInvalidHexValue { p1Hex { "RR" } }
    }

    "Invalid P2" {
        testWithBigValue { p2 { 500 } }
        testWithBigValue { p2Hex {"AAA"} }
        testWithInvalidHexValue { p2Hex { "RR" } }
    }

    "Invalid Data" {
        testWithInvalidBytes { dataHex { "INVALID" } }
    }

    "Invalid Bytes of whole APDU" {
        testWithInvalidBytes { bytesHex { "INVALID" } }
    }
})

