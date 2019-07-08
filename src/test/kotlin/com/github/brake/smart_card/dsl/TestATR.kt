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
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import javax.smartcardio.ATR

class TestATR : FunSpec({
    val atrString = "3B0A210026074F53459808F8"
    val historicalBytesPos = 6

    val testATR = ATR(
        atrString
            .chunked(2)
            .map { it.toInt(16) }
            .map { it.toByte() }
            .toByteArray()
    )

    test("ATR.hex") {
        testATR.hex shouldBe atrString
    }

    test("ATR.historicalBytesHex") {
        testATR.historicalBytesHex shouldContain atrString.substring(historicalBytesPos)
    }
})