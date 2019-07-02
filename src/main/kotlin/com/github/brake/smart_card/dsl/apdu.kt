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

import javax.smartcardio.CommandAPDU

const val MAX_BYTE: Int = 255

//card {
//    transmit {
//        apdu {
//            cla {0}
//            ins {"A4"}
//            p1 {0}
//            p2 {0}
//        }
//    } expecting {
//        sw1 { arrayListOf(0x90, 0x61, 0x9F) }
//    }
//
//    transmit { SELECT { DF {"7F20"} } }
//
//    transmit { SELECT { DFplmnsel } } expecting { sw { 0x9F0F } }
//}

/** Data to save and transfer partially initialized CommandAPDU */
data class PartialCommandAPDU(val cla: Int, val ins: Int?,
                              val p1: Int, val p2: Int,
                              val nr: Int?, val data: ByteArray?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PartialCommandAPDU

        if (cla != other.cla) return false
        if (ins != other.ins) return false
        if (p1 != other.p1) return false
        if (p2 != other.p2) return false
        if (nr != other.nr) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cla
        result = 31 * result + (ins ?: 0)
        result = 31 * result + p1
        result = 31 * result + p2
        result = 31 * result + (nr ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val noValue = "??"
        val template = "%02X"
        val ch = template.format(cla)
        val ih = ins?.let { template.format(ins) } ?: noValue
        val p1h = template.format(p1)
        val p2h = template.format(p2)
        val nrh = nr?.let { template.format(nr) } ?: noValue
        val dh = data?.toHexString() ?: noValue

        return "${this.javaClass.simpleName}(cla=$ch,ins=$ih,p1=$p1h,p2=$p2h,nr=$nrh,data=$dh)"
    }
}

/**
 * Builder of [CommandAPDU] object.
 * Supports these ways to construct [CommandAPDU] (tries to create object in this order) :
 *   CommandAPDU(byte[] apdu)
 *   CommandAPDU(int cla, int ins, int p1, int p2, byte[] data, int ne)
 *   CommandAPDU(int cla, int ins, int p1, int p2, byte[] data)
 *   CommandAPDU(int cla, int ins, int p1, int p2, int ne)
 *   CommandAPDU(int cla, int ins, int p1, int p2)
 */
open class CommandAPDUBuilder() {
    var commandClass: Int = 0
    var instruction: Int? = null
    var p1value: Int = 0
    var p2value: Int = 0
    var expectedResponseLength: Int? = null
    var dataBytes: ByteArray? = null
    var fullAPDU: ByteArray? = null

    constructor(partial: PartialCommandAPDU): this() {
        with(partial) {
            commandClass = cla
            instruction = ins
            p1value = p1
            p2value = p2
            expectedResponseLength = nr
            dataBytes = data
        }
    }

    inline fun cla(init: CommandAPDUBuilder.() -> Int) {
        commandClass = this.init()
        checkIntRepresentsByte(commandClass, "CLA")
    }

    /** @throws NumberFormatException */
    inline fun claHex(initFromHexString: CommandAPDUBuilder.() -> String) {
        cla { initFromHexString().toInt(16) }
    }

    inline fun ins(init: CommandAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        instruction = init()
        checkIntRepresentsByte(instruction!!, "INS")
    }

    /** @throws NumberFormatException */
    inline fun insHex(initFromHexString: CommandAPDUBuilder.() -> String) {
        ins { initFromHexString().toInt(16) }
    }

    inline fun p1(init: CommandAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        p1value = init()
        checkIntRepresentsByte(p1value, "P1")
    }

    /** @throws NumberFormatException */
    inline fun p1Hex(initFromHexString: CommandAPDUBuilder.() -> String) {
        p1 { initFromHexString().toInt(16) }
    }

    inline fun p2(init: CommandAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        p2value = init()
        checkIntRepresentsByte(p2value, "P2")
    }

    /** @throws NumberFormatException */
    inline fun p2Hex(initFromHexString: CommandAPDUBuilder.() -> String) {
        p2 { initFromHexString().toInt(16) }
    }

    inline fun responseLength(init: CommandAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        expectedResponseLength = init()
        checkIntRepresentsByte(expectedResponseLength!!, "Expected Response Length")
    }

    inline fun nr(init: CommandAPDUBuilder.() -> Int) = responseLength(init)

    /** @throws NumberFormatException */
    inline fun responseLengthHex(initFromHexString: CommandAPDUBuilder.() -> String) {
        responseLength { initFromHexString().toInt(16) }
    }

    inline fun data(init: CommandAPDUBuilder.() -> ByteArray) {
        @Suppress("UNUSED_EXPRESSION")
        dataBytes = init()
    }

    /** @throws IllegalArgumentException in case of invalid hex string passed */
    inline fun dataHex(initFromHexString: CommandAPDUBuilder.() -> String) {
        @Suppress("UNUSED_EXPRESSION")
        val hexString = initFromHexString()
        dataBytes = hexString.hexToBytesOrNull() ?: throwInvalidStringForHex(hexString)
    }

    inline fun bytes(init: CommandAPDUBuilder.() -> ByteArray) {
        @Suppress("UNUSED_EXPRESSION")
        fullAPDU = init()
    }

    /** @throws IllegalArgumentException in case of invalid hex string passed */
    inline fun bytesHex(initFromHexString: CommandAPDUBuilder.() -> String) {
        @Suppress("UNUSED_EXPRESSION")
        val hexString = initFromHexString()
        fullAPDU = hexString.hexToBytesOrNull() ?: throwInvalidStringForHex(hexString)
    }

    /** @throws IllegalStateException when not all requires invariant have set */
    fun build(): CommandAPDU {
        if (fullAPDU != null) return CommandAPDU(fullAPDU)

        instruction ?: throw IllegalStateException("Instruction byte not set!")

        return if (dataBytes != null) {
            if (expectedResponseLength != null) {
                CommandAPDU(commandClass, instruction!!, p1value, p2value, dataBytes, expectedResponseLength!!)
            } else {
                CommandAPDU(commandClass, instruction!!, p1value, p2value, dataBytes)
            }
        } else {
            if (expectedResponseLength != null) {
                CommandAPDU(commandClass, instruction!!, p1value, p2value, expectedResponseLength!!)
            } else {
                CommandAPDU(commandClass, instruction!!, p1value, p2value)
            }
        }
    }

    /** Allows to save builder's data to be used later to continue APDU creation */
    fun buildPartial(): PartialCommandAPDU = PartialCommandAPDU(
        commandClass, instruction,
        p1value, p2value,
        expectedResponseLength, dataBytes)

    companion object {
        fun throwInvalidStringForHex(hex: String): Nothing {
            throw IllegalArgumentException("Invalid hex string passed [$hex]")
        }

        fun checkIntRepresentsByte(value: Int, valueName: String) {
            if(value > MAX_BYTE)
                throw IllegalArgumentException("$valueName should be lesser than $MAX_BYTE but $value passed")
        }
    }
}

/** Entry point for creation of CommandAPDU with DSL
 *
 *  val command: CommandAPDU = apdu {
 *      cla { 0xA0 }
 *      insHex { "A4" }
 *      p1 { 0 }
 *      p2 { 0 }
 *      dataHex { "3F00" }
 *  }
 */
inline fun apdu(init: CommandAPDUBuilder.() -> Unit): CommandAPDU  = CommandAPDUBuilder().apply(init).build()

inline fun apdu(savedAPDU: PartialCommandAPDU, continueInit: CommandAPDUBuilder.() -> Unit): CommandAPDU =
    CommandAPDUBuilder(savedAPDU)
        .apply(continueInit)
        .build()

inline fun partialAPDU(initPartial: CommandAPDUBuilder.() -> Unit): PartialCommandAPDU  =
    CommandAPDUBuilder()
        .apply(initPartial)
        .buildPartial()

