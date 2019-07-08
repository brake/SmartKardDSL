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

import com.github.brake.smart_card.EchoCardChannel
import com.github.brake.smart_card.TestCard
import io.kotlintest.matchers.collections.shouldEndWith
import io.kotlintest.matchers.collections.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import javax.smartcardio.Card
import javax.smartcardio.CommandAPDU


fun test(card: Card) {
    card {
        transmit {
            apdu {
                ins { 0xA4 }
                dataHex { "3F00" }
            }
        }.assert("Invalid SW") {
            sw == 0x9000
        }.assert("Invalid data") {
            0xFF.toByte() in data
        }

        transmit {
            apdu { ins {0xA4 } }
        }.withResult {
            assert(sw1 in arrayOf(0x90, 0x61, 0x9F)) { "SW1 ($SW1) not in 90, 61, 9F" }
        }

    }
}

class TestCardBuilder : FunSpec({
    val testCard = TestCard(Protocol.T0, 0xF1, 0xF2)

    test("Card.invoke()") {
        testCard {
            val apdu = "001122330C445566778899AABBCCDDEEFF".hexToBytesOrThrow()
            transmit(CommandAPDU(apdu)).apply {
                val result = bytes.toList()
                val command = apdu.toList()

                result shouldStartWith command
                result shouldEndWith listOf(0xF1.toByte(), 0xF2.toByte())
            }
        }
    }

    test("Card.ATR") {
        (testCard.ATR contentEquals TestCard.atr.bytes) shouldBe true
    }

    test("Card.atrHex") {
        testCard.atrHex shouldBe TestCard.atr.bytes.toHexString()
    }
})