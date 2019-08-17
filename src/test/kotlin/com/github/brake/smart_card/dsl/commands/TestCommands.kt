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

import com.github.brake.smart_card.dsl.MF
import com.github.brake.smart_card.dsl.hexToBytesOrThrow
import com.github.brake.smart_card.dsl.toShortBytes
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TestSelect : StringSpec({

    "'file' method with `ConvertibleToBytes`" {

        SELECT {
            file(MF.EF_ICCID)
        }.apply {
            ins shouldBe Instructions.Select
            (data contentEquals MF.EF_ICCID.bytes) shouldBe true
        }
    }

    "'file' method with `ByteArray`" {
        select {
            file(byteArrayOf(0x2F, 0xE2.toByte()))
        }.apply {
            ins shouldBe Instructions.Select
            (data contentEquals MF.EF_ICCID.bytes) shouldBe true
        }
    }

    "'file' method with `Short` value" {
        select {
            file(0x2FE2.toShort())
        }.apply {
            ins shouldBe Instructions.Select
            (data contentEquals MF.EF_ICCID.bytes) shouldBe true
        }
    }

    "'file' method with `Int` value" {
        select {
            file(0x2FE2)
        }.apply {
            ins shouldBe Instructions.Select
            (data contentEquals MF.EF_ICCID.bytes) shouldBe true
        }
    }

    "'file' method with `() -> ByteArray` initialization function" {
        SELECT {
            file { 0x2FE2.toShortBytes() }
        }.apply {
            ins shouldBe Instructions.Select
            (data contentEquals MF.EF_ICCID.bytes) shouldBe true
        }        
    }

    "'requestFCP' method" {
        SELECT {
            requestFCP()
        }.apply {
            ins shouldBe Instructions.Select

            p2 shouldBe SelectAPDUBuilder.REQUEST_FCP_P2
            p1 shouldBe 0
            data.size shouldBe 0
        }
    }

    val appId = "FF00FF01FF02FF03FF04".hexToBytesOrThrow()

    "'application' method with `ByteArray`" {
        select {
            application(appId)
        }.apply {
            ins shouldBe Instructions.Select

            p1 shouldBe SelectAPDUBuilder.SELECT_APP_P1
            p2 shouldBe SelectAPDUBuilder.SELECT_APP_P2
            (data contentEquals appId) shouldBe true
        }
    }

    "'application' method with `() -> ByteArray` function" {
        select {
            application { appId }
        }.apply {
            ins shouldBe Instructions.Select

            p1 shouldBe SelectAPDUBuilder.SELECT_APP_P1
            p2 shouldBe SelectAPDUBuilder.SELECT_APP_P2
            (data contentEquals appId) shouldBe true
        }
    }
})
