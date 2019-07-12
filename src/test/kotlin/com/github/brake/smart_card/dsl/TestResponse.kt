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

import com.github.brake.smart_card.TestCard
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import javax.smartcardio.CommandAPDU

class TestResponse : FunSpec({
    val statusWord = arrayOf(0xF1, 0xF2)
    val testCardChannel = TestCard(Protocol.T1, statusWord[0], statusWord[1]).basicChannel
    val apdu = "001122330C445566778899AABBCCDDEEFF".hexToBytesOrThrow()
    val result = testCardChannel.transmit {
        CommandAPDU(apdu)
    }

    test("withResult extension") {
        result.withResult {
            SW shouldBe 0xF1F2
        }
    }

    test("assert extension with success") {
        result.assert("Shouldn't be visible") {
            sw1 in listOf(0xF0, 0xF1, 0xF3)

        }.withResult {
                
        }
    }

    test("assert extension with failure") {
        val message = "Invalid SW!"

        shouldThrow<AssertionError> {
            result.assert(message) {
                SW in listOf(0x9000, 0x6100, 0x9804)
            }
        }.message shouldBe message

    }
})