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

import com.github.brake.smart_card.dsl.commands.apdu

/** Just an abstract example of Reader module usage */
fun test() {
    readers().let {
        if (it.isEmpty())  return

        with(it[0]) {
            if (waitForCardPresent(1000)) {
                try {
                    connectAuto {
                        transmit {
                            apdu {

                            }
                        }.assert("Invalid SW") {
                            sw1 == 0x90
                        }
                    }
                } catch (e: Exception) {
                    // deal with exceptions
                }
            }
        }
    }
}
