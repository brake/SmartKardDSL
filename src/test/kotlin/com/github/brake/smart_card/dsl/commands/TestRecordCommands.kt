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

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

open class TestRecordCommand : StringSpec() {
    companion object {
        const val validRecordNumber = MAX_RECORD_NUMBER - 10
        const val incorrectRecordNumber = MAX_RECORD_NUMBER + 10
    }
}

class TestReadRecord : TestRecordCommand() {
    init {

        "'recordNumber' method with `Int` value" {
            readRecord {
                recordNumber(validRecordNumber)
            }.apply {
                p1 shouldBe validRecordNumber
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.ReadRecordSimple
            }

            shouldThrow<IllegalArgumentException> {
                readRecord {
                    recordNumber(incorrectRecordNumber)
                }
            }

            shouldThrow<IllegalArgumentException> {
                readRecord {
                    recordNumber(-1)
                }
            }
        }

        "'recordNumber' method with `() -> Int` function" {
            readRecord {
                recordNumber { validRecordNumber }
            }.apply {
                p1 shouldBe validRecordNumber
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.ReadRecordSimple
            }

            shouldThrow<IllegalArgumentException> {
                readRecord {
                    recordNumber { incorrectRecordNumber }
                }
            }

            shouldThrow<IllegalArgumentException> {
                readRecord {
                    recordNumber { -1 }
                }
            }
        }

        "'currentRecord' method" {
            readRecord {
                currentRecord()
            }.apply {
                p1 shouldBe CURRENT_RECORD
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.ReadRecordSimple
            }
        }
    }
}

class TestUpdateRecord : TestRecordCommand() {
    init {

        "'recordNumber' method with `Int` value" {
            updateRecord {
                recordNumber(validRecordNumber)
            }.apply {
                p1 shouldBe validRecordNumber
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.UpdateRecordSimple
            }

            shouldThrow<IllegalArgumentException> {
                updateRecord {
                    recordNumber(incorrectRecordNumber)
                }
            }

            shouldThrow<IllegalArgumentException> {
                updateRecord {
                    recordNumber(-1)
                }
            }
        }

        "'recordNumber' method with `() -> Int` function" {
            updateRecord {
                recordNumber { validRecordNumber }
            }.apply {
                p1 shouldBe validRecordNumber
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.UpdateRecordSimple
            }

            shouldThrow<IllegalArgumentException> {
                updateRecord {
                    recordNumber { incorrectRecordNumber }
                }
            }

            shouldThrow<IllegalArgumentException> {
                updateRecord {
                    recordNumber { -1 }
                }
            }
        }

        "'currentRecord' method" {
            updateRecord {
                currentRecord()
            }.apply {
                p1 shouldBe CURRENT_RECORD
                p2 shouldBe REFER_BY_NUMBER

                ins shouldBe Instructions.UpdateRecordSimple
            }
        }
    }
}
