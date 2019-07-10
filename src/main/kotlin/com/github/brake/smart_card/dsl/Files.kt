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


interface ConvertibleToBytes {
    val bytes: ByteArray
}

data class FileId(val id: Short) : ConvertibleToBytes {

    /** Can throw [NumberFormatException] */
    constructor(hexId: String): this(hexId.toShort(16))

    override val bytes: ByteArray
        get() = id.toBytes()
}

fun fileId(id: Short): FileId = FileId(id)

fun fieldId(id: Int): FileId = fileId(id.toShort())

/** Files under Master File (MF) */
enum class MF(private val fileId: FileId) : ConvertibleToBytes {
    Self(fileId(0x3F00)),
    EF_DIR(fileId(0x2F00)), // TS 102 221 13.1
    EF_ICCID(fileId(0x2FE2));  // 10 bytes

    override val bytes: ByteArray
        get() = fileId.bytes
}

/** Files under ADF USIM */
//enum class ADF_USIM(val fileId: FileId) : ConvertibleToBytes {;
//
//    override val bytes: ByteArray
//        get() = fileId.bytes
//}

