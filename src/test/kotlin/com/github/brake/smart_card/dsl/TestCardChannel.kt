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
import io.kotlintest.matchers.collections.shouldEndWith
import io.kotlintest.matchers.collections.shouldStartWith
import io.kotlintest.specs.StringSpec
import javax.smartcardio.CommandAPDU

class TestCardChannel : StringSpec({
    val sw = arrayOf(0xF1, 0xF2)
    val testCardChannel = TestCard(Protocol.T1, sw[0], sw[1]).basicChannel

    "CardChannel.transmit extension with block" {
        val apdu = "001122330C445566778899AABBCCDDEEFF".hexToBytesOrThrow()

        testCardChannel.transmit {
            CommandAPDU(apdu)
        }.bytes.run {
            val list = toList()

            list shouldStartWith apdu.toList()
            list shouldEndWith sw.map { it.toByte() }
        }
    }
})