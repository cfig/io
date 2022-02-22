package cc.cfig.io

import cc.cfig.io.Struct.Companion.hexString2ByteArray
import cc.cfig.io.Struct.Companion.toHexString
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream

class StructTest {
    private fun constructorTestFun1(inFormatString: String) {
        println(Struct(inFormatString))
    }

    @Test
    fun constructorTest() {
        constructorTestFun1("3s")
        constructorTestFun1("5x")
        constructorTestFun1("5b")
        constructorTestFun1("5B")

        constructorTestFun1("2c")
        constructorTestFun1("1h")
        constructorTestFun1("1H")
        constructorTestFun1("1i")
        constructorTestFun1("3I")
        constructorTestFun1("3q")
        constructorTestFun1("3Q")
        constructorTestFun1(">2b202x1b19B")
        constructorTestFun1("<2b2x1b3i2q")
    }

    @Test
    fun calcSizeTest() {
        Assert.assertEquals(3, Struct("3s").calcSize())
        Assert.assertEquals(5, Struct("5x").calcSize())
        Assert.assertEquals(5, Struct("5b").calcSize())
        Assert.assertEquals(5, Struct("5B").calcSize())

        Assert.assertEquals(9, Struct("9c").calcSize())
        Assert.assertEquals(8, Struct("2i").calcSize())
        Assert.assertEquals(8, Struct("2I").calcSize())
        Assert.assertEquals(24, Struct("3q").calcSize())
        Assert.assertEquals(24, Struct("3Q").calcSize())
    }

    @Test
    fun toStringTest() {
        println(Struct("!4s2L2QL11QL4x47sx80x"))
        println(Struct("@4s2L2QL11QL4x47sx80x"))
    }

    //x
    @Test
    fun paddingTest() {
        Assert.assertEquals("0000000000", Struct("5x").pack(null).toHexString())
        Assert.assertEquals("0000000000", Struct("5x").pack(0).toHexString())
        Assert.assertEquals("0101010101", Struct("5x").pack(1).toHexString())
        Assert.assertEquals("1212121212", Struct("5x").pack(0x12).toHexString())
        //Integer高位被截掉
        Assert.assertEquals("2323232323", Struct("5x").pack(0x123).toHexString())
        // minus 0001_0011 -> 补码 1110 1101
        Assert.assertEquals("ededededed", Struct("5x").pack(-0x13).toHexString())
        //0xff
        Assert.assertEquals("ffffffffff", Struct("5x").pack(-1).toHexString())

        try {
            Struct("5x").pack("bad")
            Assert.assertTrue("should throw exception here", false)
        } catch (_: IllegalArgumentException) {
        }

        //unpack
        Struct("3x").unpack(ByteArrayInputStream(hexString2ByteArray("000000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(0.toByte(), it[0])
        }
        Struct("x2xx").unpack(ByteArrayInputStream(hexString2ByteArray("01121210"))).let {
            Assert.assertEquals(3, it.size)
            Assert.assertEquals(0x1.toByte(), it[0])
            Assert.assertEquals(0x12.toByte(), it[1])
            Assert.assertEquals(0x10.toByte(), it[2])
        }
    }

    //c
    @Test
    fun characterTest() {
        //constructor
        Struct("c")

        //calcSize
        Assert.assertEquals(3, Struct("3c").calcSize())

        //pack illegal
        try {
            Struct("c").pack("a")
            Assert.fail("should throw exception here")
        } catch (e: Throwable) {
            Assert.assertTrue(e is AssertionError || e is IllegalArgumentException)
        }

        //pack legal
        Assert.assertEquals("61", Struct("!c").pack('a').toHexString())
        Assert.assertEquals("61", Struct("c").pack('a').toHexString())
        Assert.assertEquals("616263", Struct("3c").pack('a', 'b', 'c').toHexString())

        //unpack
        Struct("3c").unpack(ByteArrayInputStream(hexString2ByteArray("616263"))).let {
            Assert.assertEquals(3, it.size)
            Assert.assertEquals('a', it[0])
            Assert.assertEquals('b', it[1])
            Assert.assertEquals('c', it[2])
        }
    }

    //b
    @Test
    fun bytesTest() {
        //constructor
        Struct("b")

        //calcSize
        Assert.assertEquals(3, Struct("3b").calcSize())

        //pack
        Assert.assertEquals("123456", Struct("3b").pack(byteArrayOf(0x12, 0x34, 0x56)).toHexString())
        Assert.assertEquals("123456", Struct("!3b").pack(byteArrayOf(0x12, 0x34, 0x56)).toHexString())
        Assert.assertEquals("123400", Struct("3b").pack(byteArrayOf(0x12, 0x34)).toHexString())

        //unpack
        Struct("3b").unpack(ByteArrayInputStream(hexString2ByteArray("123400"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("123400", (it[0] as ByteArray).toHexString())
        }
        Struct("bbb").unpack(ByteArrayInputStream(hexString2ByteArray("123400"))).let {
            Assert.assertEquals(3, it.size)
            Assert.assertEquals("12", (it[0] as ByteArray).toHexString())
            Assert.assertEquals("34", (it[1] as ByteArray).toHexString())
            Assert.assertEquals("00", (it[2] as ByteArray).toHexString())
        }
    }

    //B: UByte array
    @Test
    fun uBytesTest() {
        //constructor
        Struct("B")

        //calcSize
        Assert.assertEquals(3, Struct("3B").calcSize())

        //pack
        Assert.assertEquals("123456", (Struct("3B").pack(byteArrayOf(0x12, 0x34, 0x56))).toHexString())
        Assert.assertEquals("123456", (Struct("!3B").pack(byteArrayOf(0x12, 0x34, 0x56))).toHexString())
        Assert.assertEquals("123400", (Struct("3B").pack(byteArrayOf(0x12, 0x34))).toHexString())

        //unpack
        Struct("3B").unpack(ByteArrayInputStream(hexString2ByteArray("123400"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("123400", (it[0] as UByteArray).toHexString())
        }
        Struct("BBB").unpack(ByteArrayInputStream(hexString2ByteArray("123400"))).let {
            Assert.assertEquals(3, it.size)
            Assert.assertEquals("12", (it[0] as UByteArray).toHexString())
            Assert.assertEquals("34", (it[1] as UByteArray).toHexString())
            Assert.assertEquals("00", (it[2] as UByteArray).toHexString())
        }
    }

    //s
    @Test
    fun stringTest() {
        //constructor
        Struct("s")

        //calcSize
        Assert.assertEquals(3, Struct("3s").calcSize())

        //pack
        Struct("3s").pack("a")
        Struct("3s").pack("abc")
        try {
            Struct("3s").pack("abcd")
            Assert.fail("should throw exception here")
        } catch (e: Throwable) {
            Assert.assertTrue(e.toString(), e is AssertionError || e is IllegalArgumentException)
        }

        //unpack
        Struct("3s").unpack(ByteArrayInputStream(hexString2ByteArray("616263"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("abc", it[0])
        }
        Struct("3s").unpack(ByteArrayInputStream(hexString2ByteArray("610000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals("a", it[0])
        }
    }

    //h
    @Test
    fun shortTest() {
        //constructor
        Struct("h")

        //calcSize
        Assert.assertEquals(6, Struct("3h").calcSize())

        //pack
        Assert.assertEquals("ff7f", Struct("h").pack(0x7fff).toHexString())
        Assert.assertEquals("0080", Struct("h").pack(-0x8000).toHexString())
        Assert.assertEquals("7fff0000", Struct(">2h").pack(0x7fff, 0).toHexString())

        //unpack
        Struct(">2h").unpack(ByteArrayInputStream(hexString2ByteArray("7fff0000"))).let {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals(0x7fff.toShort(), it[0])
            Assert.assertEquals(0.toShort(), it[1])
        }
    }

    //H
    @Test
    fun uShortTest() {
        //constructor
        Struct("H")

        //calcSize
        Assert.assertEquals(6, Struct("3H").calcSize())

        //pack
        Assert.assertEquals("0100", Struct("H").pack((1U).toUShort()).toHexString())
        Assert.assertEquals("0100", Struct("H").pack(1U).toHexString())
        Assert.assertEquals("ffff", Struct("H").pack(65535U).toHexString())
        Assert.assertEquals("ffff", Struct("H").pack(65535).toHexString())
        try {
            Struct("H").pack(-1)
            Assert.fail("should throw exception here")
        } catch (e: Throwable) {
            Assert.assertTrue(e is AssertionError || e is IllegalArgumentException)
        }
        //unpack
        Struct("H").unpack(ByteArrayInputStream(hexString2ByteArray("ffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(65535U.toUShort(), it[0])
        }
    }

    //i, l
    @Test
    fun intTest() {
        //constructor
        Struct("i")
        Struct("l")

        //calcSize
        Assert.assertEquals(12, Struct("3i").calcSize())
        Assert.assertEquals(12, Struct("3l").calcSize())

        //pack
        Struct("i").pack(65535 + 1)
        Struct("i").pack(-1)
        //unpack
        Struct("i").unpack(ByteArrayInputStream(hexString2ByteArray("00000100"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(65536, it[0])
        }
        Struct("i").unpack(ByteArrayInputStream(hexString2ByteArray("ffffffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(-1, it[0])
        }
    }

    //I, L
    @Test
    fun uIntTest() {
        //constructor
        Struct("I")
        Struct("L")

        //calcSize
        Assert.assertEquals(12, Struct("3I").calcSize())
        Assert.assertEquals(12, Struct("3L").calcSize())

        //pack
        Assert.assertEquals("01000000", Struct("I").pack(1U).toHexString())
        Assert.assertEquals("80000000", Struct(">I").pack(Int.MAX_VALUE.toUInt() + 1U).toHexString())
        //unpack
        Struct("I").unpack(ByteArrayInputStream(hexString2ByteArray("01000000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(1U, it[0])
        }
        Struct(">I").unpack(ByteArrayInputStream(hexString2ByteArray("80000000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(Int.MAX_VALUE.toUInt() + 1U, it[0])
        }
    }

    //q: Long
    @Test
    fun longTest() {
        //constructor
        Struct("q")

        //calcSize
        Assert.assertEquals(24, Struct("3q").calcSize())

        //pack
        Assert.assertEquals("8000000000000000", Struct(">q").pack(Long.MIN_VALUE).toHexString())
        Assert.assertEquals("7fffffffffffffff", Struct(">q").pack(Long.MAX_VALUE).toHexString())
        Assert.assertEquals("ffffffffffffffff", Struct(">q").pack(-1L).toHexString())
        //unpack
        Struct(">q").unpack(ByteArrayInputStream(hexString2ByteArray("8000000000000000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(Long.MIN_VALUE, it[0])
        }
        Struct(">q").unpack(ByteArrayInputStream(hexString2ByteArray("7fffffffffffffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(Long.MAX_VALUE, it[0])
        }
        Struct(">q").unpack(ByteArrayInputStream(hexString2ByteArray("ffffffffffffffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(-1L, it[0])
        }
    }

    //Q: ULong
    @Test
    fun uLongTest() {
        //constructor
        Struct("Q")

        //calcSize
        Assert.assertEquals(24, Struct("3Q").calcSize())

        //pack
        Assert.assertEquals("7fffffffffffffff", Struct(">Q").pack(Long.MAX_VALUE).toHexString())
        Assert.assertEquals("0000000000000000", Struct(">Q").pack(ULong.MIN_VALUE).toHexString())
        Assert.assertEquals("ffffffffffffffff", Struct(">Q").pack(ULong.MAX_VALUE).toHexString())
        try {
            Struct(">Q").pack(-1L)
        } catch (e: Throwable) {
            Assert.assertTrue(e is AssertionError || e is IllegalArgumentException)
        }
        //unpack
        Struct(">Q").unpack(ByteArrayInputStream(hexString2ByteArray("7fffffffffffffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(Long.MAX_VALUE.toULong(), it[0])
        }
        Struct(">Q").unpack(ByteArrayInputStream(hexString2ByteArray("0000000000000000"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(ULong.MIN_VALUE, it[0])
        }
        Struct(">Q").unpack(ByteArrayInputStream(hexString2ByteArray("ffffffffffffffff"))).let {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(ULong.MAX_VALUE, it[0])
        }
    }

    @Test
    fun legacyTest() {
        Assert.assertTrue(
            Struct("<2i4b4b").pack(
                1, 7321, byteArrayOf(1, 2, 3, 4), byteArrayOf(200.toByte(), 201.toByte(), 202.toByte(), 203.toByte())
            )
                .contentEquals(hexString2ByteArray("01000000991c000001020304c8c9cacb"))
        )
        Assert.assertTrue(
            Struct("<2i4b4B").pack(
                1, 7321, byteArrayOf(1, 2, 3, 4), intArrayOf(200, 201, 202, 203)
            )
                .contentEquals(hexString2ByteArray("01000000991c000001020304c8c9cacb"))
        )

        Assert.assertTrue(Struct("b2x").pack(byteArrayOf(0x13), null).contentEquals(hexString2ByteArray("130000")))
        Assert.assertTrue(
            Struct("b2xi").pack(byteArrayOf(0x13), null, 55).contentEquals(hexString2ByteArray("13000037000000"))
        )

        Struct("5s").pack("Good").contentEquals(hexString2ByteArray("476f6f6400"))
        Struct("5s1b").pack("Good", byteArrayOf(13)).contentEquals(hexString2ByteArray("476f6f64000d"))
    }


    @Test
    fun legacyIntegerLE() {
        //int (4B)
        Assert.assertTrue(Struct("<2i").pack(1, 7321).contentEquals(hexString2ByteArray("01000000991c0000")))
        val ret = Struct("<2i").unpack(ByteArrayInputStream(hexString2ByteArray("01000000991c0000")))
        Assert.assertEquals(2, ret.size)
        Assert.assertTrue(ret[0] is Int)
        Assert.assertTrue(ret[1] is Int)
        Assert.assertEquals(1, ret[0] as Int)
        Assert.assertEquals(7321, ret[1] as Int)

        //unsigned int (4B)
        Assert.assertTrue(Struct("<I").pack(2L).contentEquals(hexString2ByteArray("02000000")))
        Assert.assertTrue(Struct("<I").pack(2).contentEquals(hexString2ByteArray("02000000")))
        //greater than Int.MAX_VALUE
        Assert.assertTrue(Struct("<I").pack(2147483748L).contentEquals(hexString2ByteArray("64000080")))
        Assert.assertTrue(Struct("<I").pack(2147483748).contentEquals(hexString2ByteArray("64000080")))
        try {
            Struct("<I").pack(-12)
            throw Exception("should not reach here")
        } catch (e: Throwable) {
            Assert.assertTrue(e is AssertionError || e is IllegalArgumentException)
        }

        //negative int
        Assert.assertTrue(Struct("<i").pack(-333).contentEquals(hexString2ByteArray("b3feffff")))
    }

    @Test
    fun legacyIntegerBE() {
        run {
            Assert.assertTrue(Struct(">2i").pack(1, 7321).contentEquals(hexString2ByteArray("0000000100001c99")))
            val ret = Struct(">2i").unpack(ByteArrayInputStream(hexString2ByteArray("0000000100001c99")))
            Assert.assertEquals(1, ret[0] as Int)
            Assert.assertEquals(7321, ret[1] as Int)
        }

        run {
            Assert.assertTrue(Struct("!i").pack(-333).contentEquals(hexString2ByteArray("fffffeb3")))
            val ret2 = Struct("!i").unpack(ByteArrayInputStream(hexString2ByteArray("fffffeb3")))
            Assert.assertEquals(-333, ret2[0] as Int)
        }
    }
}
