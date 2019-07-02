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

package com.github.brake.smart_card.dsl

import javax.smartcardio.CommandAPDU

@Suppress("PropertyName")
interface CommandNameHolder {
    val CMD: String
}

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
        ins { 0xA4 }
    }

    /** Set File ID to select */
    fun file(id: ConvertibleToBytes) {
        data{ id.bytes }
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
        data { init() }
    }

    /** Response should contain FCP template */
    fun requestFCP() {
        p2 { 4 }
    }

    /** Indicate selection of DF by AID */
    fun byAID() {
        requestFCP()
        p2 { 0x0C }
    }

}

inline fun select(init: SelectAPDUBuilder.() -> Unit): CommandAPDU = SelectAPDUBuilder().apply(init).build()

val SELECT = ::select

/**
 * READ_BINARY APDU (INS=B0)
 * Read bytes from transparent (TR) file
 *
 * Default CLA=0
 * Default offset is 0
 * Default number of bytes to read is 0 (that means "read all available bytes but 255 maximum").
 */
class ReadBinaryAPDUBuilder : CommandAPDUBuilder() {

    companion object : CommandNameHolder {
        const val MAX_OFFSET: Int = 32_767
        const val MAX_READ_BYTES: Int = 255
        override val CMD = "READ BINARY"
    }

    init {
        ins { 0xB0 }
    }

    /**
     * Set offset in bytes from the beginning of TR file
     * 0 <= [init] return value <= 32767
     * @throws IllegalArgumentException if offset value returned from [init] is out of range
     */
    inline fun offset(init: ReadBinaryAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        offset(init())
    }

    /**
     * Set offset in bytes from the beginning of TR file
     * 0 <= [offset] <= 32767
     * @throws IllegalArgumentException if [offset] value is out of range
     */
    fun offset(offset: Int) {
        if (MAX_OFFSET < offset || offset < 0)
            throw IllegalArgumentException("$CMD: Offset should be in [0 ... $MAX_OFFSET] but $offset passed")

        val (high, low) = if(offset == 0) 0 to 0
                            else offset.toHiLo()
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
            throw IllegalStateException("$CMD: Number of bytes should be in [0 ... $MAX_READ_BYTES] but $num passed")

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
}

inline fun readBinary(init: ReadBinaryAPDUBuilder.() -> Unit): CommandAPDU = ReadBinaryAPDUBuilder().apply(init).build()

inline fun READ_BINARY(init: ReadBinaryAPDUBuilder.() -> Unit): CommandAPDU = readBinary(init)

/**
 * READ RECORD (INS=B2)
 * Read bytes from specified record of Linear Fixed (LF) or Cyclic (CY) file.
 *
 * Default CLA=0
 */
class ReadRecordAPDUBuilder : CommandAPDUBuilder() {

    companion object : CommandNameHolder {
        const val MAX_RECORD_NUMBER = 254
        override val CMD = "READ RECORD"
    }

    init {
        ins { 0xB2 }
        p2 { 0x4 }   // read record by number defined in P1
    }

    /** Set record number to read
     * 0 <= [recNum] <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [recNum] value is out of range
     */
    fun recordNumber(recNum: Int) {
        if (recNum < 0 || recNum > MAX_RECORD_NUMBER)
            throw IllegalArgumentException("$CMD: record number should be in [0 ... $MAX_RECORD_NUMBER] but $recNum passed")

        p1 { recNum }
    }

    /** Set record number to read
     * 0 <= [init] return value <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [init] return value is out of range
     */
    inline fun recordNumber(init: ReadRecordAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        recordNumber(init())
    }
}

inline fun readRecord(init: ReadRecordAPDUBuilder.() -> Unit): CommandAPDU = ReadRecordAPDUBuilder().apply(init).build()

val READ_RECORD = ::readRecord

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
        ins { 0xD6 }
    }

    companion object : CommandNameHolder {
        override val CMD = "UPDATE BINARY"
        const val MAX_OFFSET: Int = 32_767
    }

    /**
     * Set offset in bytes 0..[MAX_OFFSET], from beginning of the file, of the area to be updated
     * @throws IllegalArgumentException if [value] is out of range
     */
    fun offset(value: Int) {
        if (value < 0 || value > MAX_OFFSET)
            throw IllegalArgumentException("$CMD: offset should be in [0..$MAX_OFFSET], but $value passed")

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

/**
 * UPDATE RECORD (INS=DC)
 * Write bytes to specified record of Linear Fixed (LF) file
 * Default CLA=0
 */
class UpdateRecordAPDUBuilder : CommandAPDUBuilder() {
    init {
        ins { 0xDC }
        p2 { 0x4 }   // update record by number defined in P1
    }

    companion object : CommandNameHolder {
        const val MAX_RECORD_NUMBER = 254
        override val CMD = "UPDATE RECORD"

    }

    /** Set record number to update
     * 0 <= [recNum] <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [recNum] value is out of range
     */
    fun recordNumber(recNum: Int) {
        if (recNum < 0 || recNum > MAX_RECORD_NUMBER)
            throw IllegalArgumentException("$CMD: record number should be in [0 ... $MAX_RECORD_NUMBER] but $recNum passed")

        p1 { recNum }
    }

    /** Set record number to value returned by [init] extension function
     * 0 <= [init] return value <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [init] return value is out of range
     */
    inline fun recordNumber(init: UpdateRecordAPDUBuilder.() -> Int) {
        @Suppress("UNUSED_EXPRESSION")
        recordNumber(init())
    }
}

inline fun updateRecord(init: UpdateRecordAPDUBuilder.() -> Unit): CommandAPDU =
    UpdateRecordAPDUBuilder().apply(init).build()

val UPDATE_RECORD = ::updateRecord

/**
 * GET RESPONSE (INS=C0)
 *
 * Default - read the full available response (but 255 bytes max)
 */
class GetResponseAUDUBuilder : CommandAPDUBuilder() {
    init {
        ins { 0xC0 }
        nr { 0 }
    }
}

fun getResponse(init: (GetResponseAUDUBuilder.() -> Unit)? = null): CommandAPDU =
    GetResponseAUDUBuilder().apply {
        init?.invoke(this)
    }.build()

val GET_RESPONSE = ::getResponse
