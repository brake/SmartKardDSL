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

import javax.smartcardio.*
import javax.smartcardio.CardTerminals.State

typealias CardReader = CardTerminal

/** Protocols can be used to connect to the [Card] through [CardTerminal] */
enum class Protocol(val protocol: String) {
    T0("T=0"), T1("T=1"), TCL("T=CL"), AUTO("*")
}

/**
 * Connect to [Card] using given protocol and run [block] in context of implicitly opened basic [CardChannel]
 * @return null on success and thrown [SmartCardException] in case of error
 *
 * Example:
 * ```kotlin
 *      reader.connectT0 {
 *          transmit {
 *              apdu { ... }
 *          }.withResult {
 *              // do something in context of ResponseAPDU
 *          }
 *      }?.let {
 *          // do something with Exception thrown
 *      }
 * ```
 */
fun CardTerminal.connect(how: Protocol, block: CardChannel.() -> Unit): SmartCardException? =
    try {
        var result: SmartCardException? = null

        val card = connect(how.protocol)
        card {
            try {
                block()
            } catch (e: Exception) {
                result = TransmitAPDUException(e)
            }
        }
        result
    } catch (e: Exception) {
        ReaderConnectException(e)
    }

fun CardTerminal.connectT0(block: CardChannel.() -> Unit): SmartCardException? = connect(Protocol.T0, block)

fun CardTerminal.connectT1(block: CardChannel.() -> Unit): SmartCardException? = connect(Protocol.T1, block)

fun CardTerminal.connectAuto(block: CardChannel.() -> Unit): SmartCardException? = connect(Protocol.AUTO, block)

fun CardTerminal.waitForCardPresent(): Boolean = waitForCardPresent(0)

fun CardTerminal.waitForCardAbsent(): Boolean = waitForCardAbsent(0)

fun readers(state: State = State.ALL): List<CardTerminal> =
        TerminalFactory
            .getDefault()
            .terminals()
            .list(state)

fun readersNoCard(): List<CardTerminal> = readers(State.CARD_ABSENT)

fun readersInsertion(): List<CardTerminal> = readers(State.CARD_INSERTION)

fun readersRemoval(): List<CardTerminal> = readers(State.CARD_REMOVAL)

fun readersWithCard(): List<CardTerminal> = readers(State.CARD_PRESENT)
