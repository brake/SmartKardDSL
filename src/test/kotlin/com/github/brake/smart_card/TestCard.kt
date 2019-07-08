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

package com.github.brake.smart_card

import com.github.brake.smart_card.dsl.Protocol
import com.github.brake.smart_card.dsl.hexToBytesOrNull
import javax.smartcardio.ATR
import javax.smartcardio.Card
import javax.smartcardio.CardChannel

class TestCard(private val protocol: Protocol, private val sw1: Int, private val sw2: Int): Card() {

    companion object {
        val atr = ATR("3B0A210026074F53459808F8".hexToBytesOrNull())
    }
    override fun beginExclusive() {}

    override fun transmitControlCommand(controlCode: Int, command: ByteArray?): ByteArray =
        throw UnsupportedOperationException("TestCard.transmitControlCommand()")

    override fun endExclusive() {}

    override fun openLogicalChannel(): CardChannel = throw UnsupportedOperationException("TestCard.openLogicalChannel()")

    override fun getProtocol(): String = protocol.protocol

    override fun disconnect(reset: Boolean) {}

    override fun getATR(): ATR = TestCard.atr

    override fun getBasicChannel(): CardChannel = EchoCardChannel(this, sw1, sw2)
}