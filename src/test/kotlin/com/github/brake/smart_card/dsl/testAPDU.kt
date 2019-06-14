package com.github.brake.smart_card.dsl

import io.kotlintest.TestContext
import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import javax.smartcardio.CommandAPDU

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

    test("Initialization with Nr parameter") {
        val respLen = 0x0A
        val (c, i, p1v, p2v) = apduInts

        apdu {
            cla { c }
            ins { i }
            p1 { p1v }
            p2 { p2v }
            data { apduBytes.sliceArray(5 until apduInts.size) }
            responseLength { respLen }
        } shouldBe CommandAPDU("A0A401C404B1B2B3B40A".hexToBytesOrNull())
    }

    test("Create partial APDU a then complete and finalize") {
        val (c, i, p1v, p2v) = apduInts

        val PARTIAL_APDU = partialAPDU {
            ins { i }
            p1 { p1v }
            p2 { p2v }
        }
        apdu(PARTIAL_APDU) {
            cla { c }
            data { apduBytes.sliceArray(5 until apduInts.size) }
        } shouldBe apduValue
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

class TestPartialAPDU: FunSpec({
    val apduInts = intArrayOf(0xA0, 0xA4, 0x1, 0xC4, 0x4, 0xB1, 0xB2, 0xB3, 0xB4)
    val apduBytes = apduInts.map { it.toByte() }.toByteArray()

    test("toString no data") {
        val (_, i, p1v, p2v) = apduInts

        partialAPDU {
            ins { i }
            p1 { p1v }
            p2 { p2v }
        }.toString() shouldBe "PartialCommandAPDU(cla=00,ins=A4,p1=01,p2=C4,nr=??,data=??)"
    }

    test("toString with data") {
        partialAPDU {
            data { apduBytes.sliceArray(5 until apduBytes.size) }
        }.toString() shouldBe "PartialCommandAPDU(cla=00,ins=??,p1=00,p2=00,nr=??,data=B1B2B3B4)"
    }
})
