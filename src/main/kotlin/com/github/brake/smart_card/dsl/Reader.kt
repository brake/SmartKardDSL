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
 * Connect to [Card] using given protocol and run [block] in context of implicitly opened basic [CardChannel].
 *
 * Usually [block] will construct APDUs and transmit them to the card through [CardChannel]
 *
 * @throws CardNotPresentException - if no card is present in this terminal
 * @throws CardException - if a connection could not be established using the specified protocol,
 *      if a connection has previously been established using a different protocol
 *      or if the card operation failed
 * @throws SecurityException - if a SecurityManager exists and the caller does not have the required permission
 * @throws IllegalStateException - if this channel has been closed or if the corresponding Card has been disconnected
 * @throws IllegalArgumentException - if the APDU encodes a MANAGE CHANNEL command
 *
 * Example:
 * ```kotlin
 *      try {
 *          reader.connectT0 {
 *              transmit {
 *                  apdu { ... }
 *              }.withResult {
 *                  // do something in context of ResponseAPDU
 *              }
 *          }
 *       }  catch(e: Exception) {
 *           // do something with Exception
 *       }
 * ```
 */
fun CardTerminal.connect(how: Protocol, block: CardChannel.() -> Unit) {
    connect(how.protocol).invoke(block)
}

/**
 * Connect to [Card] using protocol T0 and run [block] in context of implicitly opened basic [CardChannel].
 *
 * Usually [block] will construct APDUs and transmit them to the card through [CardChannel]
 *
 * @throws CardNotPresentException - if no card is present in this terminal
 * @throws CardException - if a connection could not be established using the specified protocol,
 *      if a connection has previously been established using a different protocol
 *      or if the card operation failed
 * @throws SecurityException - if a SecurityManager exists and the caller does not have the required permission
 * @throws IllegalStateException - if this channel has been closed or if the corresponding Card has been disconnected
 * @throws IllegalArgumentException - if the APDU encodes a MANAGE CHANNEL command
 */
fun CardTerminal.connectT0(block: CardChannel.() -> Unit) {
    connect(Protocol.T0, block)
}

/**
 * Connect to [Card] using protocol T1 and run [block] in context of implicitly opened basic [CardChannel].
 *
 * Usually [block] will construct APDUs and transmit them to the card through [CardChannel]
 *
 * @throws CardNotPresentException - if no card is present in this terminal
 * @throws CardException - if a connection could not be established using the specified protocol,
 *      if a connection has previously been established using a different protocol
 *      or if the card operation failed
 * @throws SecurityException - if a SecurityManager exists and the caller does not have the required permission
 * @throws IllegalStateException - if this channel has been closed or if the corresponding Card has been disconnected
 * @throws IllegalArgumentException - if the APDU encodes a MANAGE CHANNEL command
 */
fun CardTerminal.connectT1(block: CardChannel.() -> Unit) {
    connect(Protocol.T1, block)
}

/**
 * Connect to [Card] using any supported protocol and run [block] in context of implicitly opened basic [CardChannel].
 *
 * Usually [block] will construct APDUs and transmit them to the card through [CardChannel]
 *
 * @throws CardNotPresentException - if no card is present in this terminal
 * @throws CardException - if a connection could not be established using the specified protocol,
 *      if a connection has previously been established using a different protocol
 *      or if the card operation failed
 * @throws SecurityException - if a SecurityManager exists and the caller does not have the required permission
 * @throws IllegalStateException - if this channel has been closed or if the corresponding Card has been disconnected
 * @throws IllegalArgumentException - if the APDU encodes a MANAGE CHANNEL command
 */
fun CardTerminal.connectAuto(block: CardChannel.() -> Unit) {
    connect(Protocol.AUTO, block)
}

/** Return [List] of [CardTerminal] objects having given [State] (by default returns all available readers) */
fun readers(state: State = State.ALL): List<CardTerminal> =
        TerminalFactory
            .getDefault()
            .terminals()
            .list(state)

/** Return [List] of [CardTerminal] objects having [State.CARD_ABSENT] */
fun readersNoCard(): List<CardTerminal> = readers(State.CARD_ABSENT)

/** Return [List] of [CardTerminal] objects having given [State.CARD_INSERTION] */
fun readersInsertion(): List<CardTerminal> = readers(State.CARD_INSERTION)

/** Return [List] of [CardTerminal] objects having given [State.CARD_REMOVAL] */
fun readersRemoval(): List<CardTerminal> = readers(State.CARD_REMOVAL)

/** Return [List] of [CardTerminal] objects having given [State.CARD_PRESENT] */
fun readersWithCard(): List<CardTerminal> = readers(State.CARD_PRESENT)
