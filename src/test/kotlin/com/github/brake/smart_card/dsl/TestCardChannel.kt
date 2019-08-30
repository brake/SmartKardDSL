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
import com.github.brake.smart_card.dsl.Settings.apdu
import com.github.brake.smart_card.dsl.Settings.TEST_SW
import com.github.brake.smart_card.dsl.Settings.TEST_SW_LIST
import com.github.brake.smart_card.dsl.Settings.testCardChannel
import com.github.brake.smart_card.dsl.commands.*
import io.kotlintest.matchers.collections.shouldEndWith
import io.kotlintest.matchers.collections.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import javax.smartcardio.CommandAPDU
import javax.smartcardio.ResponseAPDU

object Settings {
    val apdu = "001122330C445566778899AABBCCDDEEFF".hexToBytesOrThrow()
    val TEST_SW = arrayOf(0xF1, 0xF2)
    val TEST_SW_LIST = TEST_SW.map { it.toByte() }
    val testCardChannel = TestCard(Protocol.T1, TEST_SW[0], TEST_SW[1]).basicChannel
}

class TestTransmit : StringSpec({

    "CardChannel.transmit extension with block" {

        testCardChannel.transmit {
            CommandAPDU(apdu)
        }.bytes.run {
            val list = toList()

            list shouldStartWith apdu.toList()
            list shouldEndWith TEST_SW_LIST
        }
    }
})

class TestCardChannelAPDU : StringSpec({
    fun ResponseAPDU.check() {
        listOf(apdu, bytes)
            .map { it.toList() }
            .let { (apduList, echoedList) ->
                echoedList shouldStartWith apduList
                echoedList shouldEndWith TEST_SW_LIST
            }
    }

    fun ResponseAPDU.compareWithEqualCommand(equalCommand: CommandAPDU) {
        CommandAPDU(data).run {
            this shouldBe equalCommand
        }
    }

    "CardChannel.APDU with CommandAPDU" {
        testCardChannel
            .APDU(CommandAPDU(apdu))
            .check()
    }

    "CardChannel.APDU with code block" {
        testCardChannel
            .APDU { bytes { apdu } }
            .check()
    }

    // tests below check that CommandAPDU transmitted through
    // CardChannel is same as CommandAPDU created by means of its specific builder
    "CardChannel.SELECT" {
        testCardChannel
            .SELECT { }
            .compareWithEqualCommand( select {  })
    }

    "CardChannel.UPDATE_BINARY" {
        testCardChannel
            .UPDATE_BINARY {  }
            .compareWithEqualCommand( updateBinary {  })
    }

    "CardChannel.READ_BINARY" {
        testCardChannel
            .READ_BINARY {  }
            .compareWithEqualCommand( readBinary {  })
    }

    "CardChannel.UPDATE_RECORD" {
        testCardChannel
            .UPDATE_RECORD {  }
            .compareWithEqualCommand( updateRecord {  })
    }

    "CardChannel.READ_RECORD" {
        testCardChannel
            .READ_RECORD {  }
            .compareWithEqualCommand( readRecord {  })
    }
})

