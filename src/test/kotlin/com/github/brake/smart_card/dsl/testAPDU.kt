package com.github.brake.smart_card.dsl

import io.kotlintest.TestContext
import io.kotlintest.matchers.string.contain
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.lang.NumberFormatException
import javax.smartcardio.CommandAPDU
import javax.xml.bind.DatatypeConverter.printHexBinary

class TestCorrectCreation: FunSpec({
    val apduInts = intArrayOf(0xA0, 0xA4, 0x1, 0xC4, 0x4, 0xB1, 0xB2, 0xB3, 0xB4)
    val apduBytes = apduInts.map { it.toByte() }.toByteArray()
    val apduValue = CommandAPDU(apduBytes)

    test("Test with Int and ByteArrays APDU") {

        apdu {
            cla { apduInts[0] }
            ins { apduInts[1] }
            p1 { apduInts[2] }
            p2 { apduInts[3] }
            data { apduBytes.sliceArray(5 until apduInts.size) }
        } shouldBe apduValue

        apdu {
            bytes { apduBytes }
        } shouldBe apduValue
    }

    test("Test with hex strings") {
        apdu {
            claHex { "A0" }
            insHex { "A4" }
            p1Hex { "01" }
            p2Hex { "C4" }
            dataHex { "B1B2B3B4" }
        } shouldBe apduValue

        apdu {
            bytesHex { apduBytes.toHexString() }
        } shouldBe apduValue
    }

    test("Minimal initialization (use defaults)") {
        apdu {
            ins { 0xA4 }
            dataHex { "3F00" }
        } shouldBe CommandAPDU("00A40000023F00".hexToBytesOrNull())
    }
})

const val LESSER_MESSAGE = "should be lesser than"

fun TestContext.testWithBigValue(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<IllegalArgumentException> { apdu(init) }.message should contain(LESSER_MESSAGE)
}

fun TestContext.testWithInvalidHexValue(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<NumberFormatException> { apdu(init) }
}

fun TestContext.testWithInvalidBytes(init: CommandAPDUBuilder.() -> Unit) {
    shouldThrow<IllegalArgumentException> { apdu(init) }.message should contain("Invalid hex string passed")
}

class TestIncorrectCreation: FunSpec({
    test("No initialization") {
        shouldThrow<IllegalStateException> {
            apdu { }
        }.message should contain("Instruction byte not set")
    }

    test("Invalid INS") {
        testWithBigValue { ins { 500 } }
        testWithBigValue { insHex {"AAA"} }
        testWithInvalidHexValue { insHex { "RR" } }
    }

    test("Invalid CLA") {
        testWithBigValue { cla { 500 } }
        testWithBigValue { claHex {"AAA"} }
        testWithInvalidHexValue { claHex { "RR" } }
    }

    test("Invalid P1") {
        testWithBigValue { p1 { 500 } }
        testWithBigValue { p1Hex {"AAA"} }
        testWithInvalidHexValue { p1Hex { "RR" } }
    }

    test("Invalid P2") {
        testWithBigValue { p2 { 500 } }
        testWithBigValue { p2Hex {"AAA"} }
        testWithInvalidHexValue { p2Hex { "RR" } }
    }

    test("Invalid Data") {
        testWithInvalidBytes { dataHex { "INVALID" } }
    }

    test("Invalid Bytes of whole APDU") {
        testWithInvalidBytes { bytesHex { "INVALID" } }
    }
})

fun ByteArray.toHexString(): String = printHexBinary(this)
