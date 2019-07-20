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

package com.github.brake.smart_card.dsl.commands

import com.github.brake.smart_card.dsl.CommandAPDUBuilder
import javax.smartcardio.CommandAPDU

/** Constants to be shared between builders of record (LF, CY) based commands */
const val MAX_RECORD_NUMBER = 254
const val REFER_BY_NUMBER = 4
const val CURRENT_RECORD = 0

/**
 * READ RECORD (INS=B2)
 * Read bytes from specified record of Linear Fixed (LF) or Cyclic (CY) file.
 * Records addressed by absolute record number (1 .. 254)
 *
 * Default CLA=0
 */
class ReadRecordAPDUBuilder : CommandAPDUBuilder() {

    companion object : CommandNameHolder {
        override val CMD = "READ RECORD"
    }

    init {
        ins { Instructions.ReadRecordSimple }
        p2 { REFER_BY_NUMBER }   // read record by number defined in P1
    }

    /** Set record number to read
     * 0 <= [recNum] <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [recNum] value is out of range
     */
    fun recordNumber(recNum: Int) {
        if (recNum < 0 || recNum > MAX_RECORD_NUMBER)
            throw IllegalArgumentException("$CMD: record number should be in [0..$MAX_RECORD_NUMBER] but $recNum passed")

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

    /** Configure APDU to read the current record of selected file */
    fun currentRecord() {
        recordNumber(CURRENT_RECORD)
    }
}

inline fun readRecord(init: ReadRecordAPDUBuilder.() -> Unit): CommandAPDU = ReadRecordAPDUBuilder().apply(init).build()

val READ_RECORD = ::readRecord

/**
 * UPDATE RECORD (INS=DC)
 * Write bytes to specified record of Linear Fixed (LF) file
 * Records addressed by absolute record number (1 .. 254)
 *
 * Default CLA=0
 */
class UpdateRecordAPDUBuilder : CommandAPDUBuilder() {
    init {
        ins { Instructions.UpdateRecordSimple }
        p2 { REFER_BY_NUMBER }   // update record by number defined in P1
    }

    companion object : CommandNameHolder {
        override val CMD = "UPDATE RECORD"
    }

    /** Set record number to update
     * 0 <= [recNum] <= [MAX_RECORD_NUMBER] (254)
     * Note that record number 0 has special meaning - denotes the _current_ record (that record referenced by the record pointer).
     * @throws IllegalArgumentException if [recNum] value is out of range
     */
    fun recordNumber(recNum: Int) {
        if (recNum < 0 || recNum > MAX_RECORD_NUMBER)
            throw IllegalArgumentException("$CMD: record number should be in [0..$MAX_RECORD_NUMBER] but $recNum passed")

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

    /** Configure APDU to update the current record of selected file */
    fun currentRecord() {
        recordNumber(CURRENT_RECORD)
    }
}

inline fun updateRecord(init: UpdateRecordAPDUBuilder.() -> Unit): CommandAPDU =
    UpdateRecordAPDUBuilder().apply(init).build()

val UPDATE_RECORD = ::updateRecord

