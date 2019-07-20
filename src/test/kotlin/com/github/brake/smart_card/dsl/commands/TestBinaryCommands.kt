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

import com.github.brake.smart_card.dsl.commands.ReadBinaryAPDUBuilder.Companion.MAX_READ_BYTES
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

open class TestBinaryCommand : WordSpec() {
    companion object {
        const val offsetHi = 0x22
        const val offsetLo = 0x33
        const val offsetFull = (offsetHi shl 8) or offsetLo
        const val unacceptableOffset = MAX_OFFSET + 500

        init {
            assert(offsetFull == 0x2233) { "invalid bit calculation ${offsetFull.toString(16)}" }
        }
    }
}

class TestReadBinary : TestBinaryCommand() {

    init {

        "'offset' method with Int value" should {

            "work with correct Int value" {
                readBinary {
                    offset(offsetFull)
                }.apply {
                    p1 shouldBe offsetHi
                    p2 shouldBe offsetLo

                    ins shouldBe Instructions.ReadBinarySimple
                }
            }

            "fail with offset greater than $MAX_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    READ_BINARY {
                        offset(unacceptableOffset)
                    }
                }
            }

            "fail with offset lesser than $MIN_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    READ_BINARY {
                        offset(-1)
                    }
                }
            }
        }

        "'offset' method with `() -> Int` function" should {
            "work with function returning correct value" {
                readBinary {
                    offset { offsetFull }
                }.apply {
                    p1 shouldBe offsetHi
                    p2 shouldBe offsetLo

                    ins shouldBe Instructions.ReadBinarySimple
                }
            }

            "fail with exception when function return value greater than $MAX_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    READ_BINARY {
                        offset { unacceptableOffset }
                    }
                }
            }

            "fail with exception when function return value lesser than $MIN_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    READ_BINARY {
                        offset { -1 }
                    }
                }
            }
        }

        val num = 100
        val unacceptableNumBytes = MAX_READ_BYTES + 100

        "'numBytes' method with `() -> Int` function" should {
            "work with correct Int value" {
                READ_BINARY {
                    numBytes { num }
                }.apply {
                    p1 shouldBe 0
                    p2 shouldBe 0

                    ne shouldBe num
                }
            }

            "fail when function result value is greater than $MAX_OFFSET" {
                shouldThrow<java.lang.IllegalArgumentException> {
                    readBinary {
                        numBytes { unacceptableNumBytes }
                    }
                }
            }

            "fail when function result value is lesser than $MIN_OFFSET" {
                shouldThrow<java.lang.IllegalArgumentException> {
                    readBinary {
                        numBytes { -1 }
                    }
                }
            }
        }

        "'numBytes' method with `Int` value" should {
            "work with correct Int value" {
                READ_BINARY {
                    numBytes(num)
                }.apply {
                    p1 shouldBe 0
                    p2 shouldBe 0

                    ne shouldBe num

                    ins shouldBe Instructions.ReadBinarySimple
                }
            }

            "fail when `Int` value greater than $MAX_READ_BYTES" {
                shouldThrow<java.lang.IllegalArgumentException> {
                    readBinary {
                        numBytes(unacceptableNumBytes)
                    }
                }
            }

            "fail when `Int` value is negative" {
                shouldThrow<java.lang.IllegalArgumentException> {
                    readBinary {
                        numBytes(-1)
                    }
                }
            }
        }

        "'readMaxBytes' method" should {
            "Produce `CommandAPDU` with `re` property set to zero" {
                READ_BINARY {
                    readMaxBytes()
                }.apply {
                    ne shouldBe 0
                }
            }
        }
    }
}

class TestUpdateBinary : TestBinaryCommand() {

    init {
        "'offset' method with Int" should {
            "work with correct `Int` value" {
                updateBinary {
                    offset(offsetFull)
                }.apply {
                    p1 shouldBe offsetHi
                    p2 shouldBe offsetLo

                    ins shouldBe Instructions.UpdateBinarySimple
                }
            }

            "fail when `Int` value is greater than $MAX_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    UPDATE_BINARY {
                        offset(unacceptableOffset)
                    }
                }
            }

            "fail when `Int` value is lesser than $MIN_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    updateBinary {
                        offset(MIN_OFFSET - 10)
                    }
                }
            }
        }

        "'offset' method with `() -> Int` function" should {
            "work with function returning correct value" {
                updateBinary {
                    offset { offsetFull }
                }.apply {
                    p1 shouldBe offsetHi
                    p2 shouldBe offsetLo

                    ins shouldBe Instructions.UpdateBinarySimple
                }
            }

            "fail with exception when function return value greater than $MAX_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    UPDATE_BINARY {
                        offset { unacceptableOffset }
                    }
                }
            }

            "fail with exception when function return value lesser than $MIN_OFFSET" {
                shouldThrow<IllegalArgumentException> {
                    READ_BINARY {
                        offset { -1 }
                    }
                }
            }
        }
    }
}

