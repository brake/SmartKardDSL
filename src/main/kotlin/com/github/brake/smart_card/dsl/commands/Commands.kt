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

@file:Suppress("FunctionName")

package com.github.brake.smart_card.dsl.commands

import com.github.brake.smart_card.dsl.ConvertibleToBytes
import com.github.brake.smart_card.dsl.toBytes
import javax.smartcardio.CommandAPDU

/**
 * SELECT APDU (INS=A4)
 * Select MF, DF, ADF, EF
 *
 * Default CLA=00
 * Default P1=00
 * Default P2=00
 */
class SelectAPDUBuilder: CommandAPDUBuilder() {

    init {
        ins { Instructions.Select }
    }

    /** Set File ID to select */
    fun file(id: ConvertibleToBytes) {
        data { id.bytes }
    }

    /** Set File ID to select */
    fun file(id: ByteArray) {
        data { id }
    }

    /** Set File ID to select */
    fun file(id: Short) {
        data { id.toBytes() }
    }

    /** Set File ID to select */
    fun file(id: Int) {
        file(id.toShort())
    }

    /** Set File ID to select */
    inline fun file(init: SelectAPDUBuilder.() -> ByteArray) {
        @Suppress("UNUSED_EXPRESSION")
        data { init() }
    }

    /** Response should contain FCP template */
    fun requestFCP() {
        p2 { REQUEST_FCP_P2 }
    }

    /** Indicate selection of application DF by AID giving [aid] value */
    fun application(aid: ByteArray) {
        file { aid }
        p1 { SELECT_APP_P1 }
        p2 { SELECT_APP_P2 }
    }

    /** Indicate selection of application DF by AID giving value as result of running of [block] */
    inline fun application(block: SelectAPDUBuilder.() -> ByteArray) {
        @Suppress("UNUSED_EXPRESSION")
        application(block())
    }

    companion object {
        const val REQUEST_FCP_P2 = 4
        const val SELECT_APP_P2 = 0x0C
        const val SELECT_APP_P1 = 4  // Select app by [partial] AID
    }
}

inline fun select(init: SelectAPDUBuilder.() -> Unit): CommandAPDU = SelectAPDUBuilder().apply(init).build()

