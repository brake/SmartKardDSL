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

import javax.smartcardio.ResponseAPDU

val ResponseAPDU.sw1
    get() = sW1

val ResponseAPDU.SW1
    get() = sW1

val ResponseAPDU.sw2
    get() = sW2

val ResponseAPDU.SW2
    get() = sW2

val ResponseAPDU.SW
    get() = sw

fun ResponseAPDU.withResult(block: ResponseAPDU.() -> Unit) {
    block()
}

fun ResponseAPDU.assert(message: String, block: ResponseAPDU.() -> Boolean): ResponseAPDU {
    assert(block()) { message }

    return this
}
