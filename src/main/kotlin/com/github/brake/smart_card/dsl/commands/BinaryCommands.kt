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

import com.github.brake.smart_card.dsl.CommandAPDUBuilder
import com.github.brake.smart_card.dsl.toHiLo
import javax.smartcardio.CommandAPDU

/** Constants to be shared between builders of binary (TR) based commands */
const val MAX_OFFSET: Int = 32_767
const val MIN_OFFSET: Int = 0

//interface CommandWithOffset<out T: CommandWithOffset<T>> {
//    fun offset(offset: Int)
//    fun offset(calc: T.() -> Int)
//}

/**
 * READ_BINARY APDU (INS=B0)
 * Read bytes from transparent (TR) file
 *
 * Default CLA=0
 * Default offset is 0
 * Default number of bytes to read is 0 (that means "read all available bytes but 255 maximum").
 */
class ReadBinaryAPDUBuilder : CommandAPDUBuilder() {

    companion object :  CommandNameHolder {
        const val MAX_READ_BYTES: Int = 255
        override val CMD = "READ BINARY"
    }

    init {
        ins { Instructions.ReadBinarySimple }
    }

    /**
     * Set offset in bytes from the beginning of TR file
     * 0 <= [calc] return value <= 32767
     * @throws IllegalArgumentException if offset value returned from [calc] is out of range
     */
    inline fun offset(calc: ReadBinaryAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        offset(calc())
    }

    /**
     * Set offset in bytes from the beginning of TR file
     * 0 <= [offset] <= 32767
     * @throws IllegalArgumentException if [offset] value is out of range
     */
    fun offset(offset: Int) {
        if (MAX_OFFSET < offset || offset < MIN_OFFSET)
            throw IllegalArgumentException(
                "$CMD: Offset should be in [$MIN_OFFSET .. $MAX_OFFSET] but $offset passed")

        val (high, low) = if(offset == 0) 0 to 0 else offset.toHiLo()
        p1 { high }
        p2 { low }
    }

    /**
     * Set number of bytes to read from given offset
     * 0 <=[num] <= 255
     * @throws IllegalArgumentException if [num] value is out of range
     */
    fun numBytes(num: Int) {
        if (num < 0 || num > MAX_READ_BYTES)
            throw IllegalArgumentException("$CMD: Number of bytes should be in [0 ... $MAX_READ_BYTES] but $num passed")

        nr { num }
    }

    /**
     * Set number of bytes to read from given offset
     * 0 <=[init] return value <= 255
     * @throws IllegalArgumentException if value returned from [init] is out of range
     */
    inline fun numBytes(init: ReadBinaryAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        numBytes(init())
    }

    /** Read all available bytes but [MAX_READ_BYTES] maximum */
    fun readMaxBytes() {
        numBytes(0)
    }
}

inline fun readBinary(init: ReadBinaryAPDUBuilder.() -> Unit): CommandAPDU = ReadBinaryAPDUBuilder().apply(init).build()

inline fun READ_BINARY(init: ReadBinaryAPDUBuilder.() -> Unit): CommandAPDU =
    readBinary(init)

/**
 * UPDATE BINARY (INS=D6)
 * Write bytes to transparent file (TR). Can write by chunks with size of 255 bytes.
 * P1 and P2 encode high and low parts of offset respectively. Offset should be in range 0..32_767
 *
 * Default CLA=0
 * Default offset is 0
 */
class UpdateBinaryAPDUBuilder : CommandAPDUBuilder() {
    init {
        ins { Instructions.UpdateBinarySimple }
    }

    companion object : CommandNameHolder {
        override val CMD = "UPDATE BINARY"
    }

    /**
     * Set offset in bytes [MIN_OFFSET]..[MAX_OFFSET], from beginning of the file, of the area to be updated
     * @throws IllegalArgumentException if [value] is out of range
     */
    fun offset(value: Int) {
        if (value < MIN_OFFSET || value > MAX_OFFSET)
            throw IllegalArgumentException(
                "$CMD: offset should be in [$MIN_OFFSET..$MAX_OFFSET], but $value passed")

        val (hi, lo) = value.toHiLo()
        p1 { hi }
        p2 { lo }
    }

    inline fun offset(init: UpdateBinaryAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        offset(init())
    }
}

inline fun updateBinary(init: UpdateBinaryAPDUBuilder.() -> Unit): CommandAPDU =
    UpdateBinaryAPDUBuilder().apply(init).build()

val UPDATE_BINARY = ::updateBinary
