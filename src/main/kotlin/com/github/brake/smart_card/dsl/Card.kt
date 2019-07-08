/**
 *    Copyright 2019 Constantin Roganov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.brake.smart_card.dsl

import javax.smartcardio.*

//card {
//    transmit {
//        apdu {
//            cla {0}
//            ins {"A4"}
//            p1 {0}
//            p2 {0}
//        }
//    } expecting {
//        sw1 oneOf { arrayListOf(0x90, 0x61, 0x9F) }
//        sw equalTo { 0x9000 }
//    } onError {
//
//    }
//
//    transmit { SELECT { DF {"7F20"} } }
//
//    transmit { SELECT { DFplmnsel } } expecting { sw { 0x9F0F } }
//}


/** Run [transmitFn] on a card's basic channel */
operator fun Card.invoke(transmitFn: CardChannel.() -> Unit) {
    basicChannel.transmitFn()
}

val Card.ATR: ByteArray
    get() = atr.bytes

val Card.atrHex: String
    get() = atr.hex

//val Card.ICCID
//    get() {
//        TODO("Select MF + read ICCID + swap nibbles")
//    }
