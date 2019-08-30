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

import java.nio.ByteBuffer
import javax.smartcardio.Card
import javax.smartcardio.CardChannel
import javax.smartcardio.CommandAPDU
import javax.smartcardio.ResponseAPDU

/** Echoes [CommandAPDU] bytes in [ResponseAPDU] */
class EchoCardChannel(private val _card: Card, private val sw1: Int, private val sw2: Int) : CardChannel() {

    /** method places [command]'s data to [ResponseAPDU] data */
    override fun transmit(command: CommandAPDU): ResponseAPDU {
        val commandBytes = command.bytes
        val length = commandBytes.size + 2
        val response = ByteArray(length) {
            when(it) {
                in commandBytes.indices -> commandBytes[it]
                length - 2 -> sw1.toByte()
                length - 1 -> sw2.toByte()
                else -> throw IllegalArgumentException("Invalid position in EchoCardChannel.transmit()")
            }
        }
        return ResponseAPDU(response)
    }

    override fun transmit(command: ByteBuffer?, response: ByteBuffer?): Int =
        throw NotImplementedError("TestCardChannel.transmit() with ByteBuffer not implemented!")

    override fun getCard(): Card = _card

    override fun close() {}

    override fun getChannelNumber(): Int = 0
}