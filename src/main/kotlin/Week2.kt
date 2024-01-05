//@file:JvmName("AnotherName")    // change class name from Week2 to AnotherName
import java.io.IOException

fun main(args: Array<String>) {    // args: Array<String> parameter could be omitted
    // ============================ BASICS
    // ============================ "Hello, world" example
    // String template
    val name = "Kotlin"
    println("Hello, $name! Function call ${someFunction()}. ${args.getOrNull(0)}")

    // ============================ Variables
    // immutable
    // local type inference
    val val1 = "example"
    println("Val1 = $val1")
    // won't compile
//    val1 = "example"

    // mutable
    var var1 = "example"
    println("Var1 = $var1")
    var1 = "another"
    println("Var1 = $var1")

    // immutable list
    val immutableList = listOf(1)
//    immutableList.addLast(2)      throws UnsupportedOperationException

    // mutable list
    val mutableList = mutableListOf(1)
    mutableList.addLast(2)

    // ============================ Functions
    // function
    fun max(a: Int, b: Int): Int {
        return if (a > b) a else b
    }

    // inline function
    fun maxInline(a: Int, b: Int): Int = if (a > b) a else b

    // Unit == void
    fun implicitUnit(a: Int, b: Int) = println("${a + b}")
    fun explicitUnit(a: Int, b: Int): Unit = println("${a + b}")

    // ============================ Named & default arguments
    // default arguments
    println(listOf('a', 'b', 'c').joinToString(separator = "", prefix = "(", postfix = ")"))

    // @JvmOverloads   // will generate overload methods for Java
    fun printSeparator(character: Char = '*', size: Int = 10) {
        repeat(size) {
            print(character)
        }
        println()
    }
    printSeparator()
    printSeparator(size = 5)

    // ============================ CONTROL STRUCTURES
    // ============================ Conditionals: if & when
    // when as switch
    fun printColor(color: String) {
        when (color) {
            "green", "g" -> println("1")
            "red", "r" -> println("2")
            "yellow", "y" -> println("3")
        }
    }

    fun printColorMix(c1: String, c2: String) = when (setOf(c1, c2)) {
        setOf("green", "yellow") -> println("1")
        setOf("red", "yellow") -> println("2")
        else -> throw Exception("wrong color")
    }

    // type checking
    fun printType(someObject: Any) = when (someObject) {
        is MutableSet<*> -> println("MutableSet")
        is MutableList<*> -> println("MutableList")
        else -> println("unknown type")
    }

    // init val in when
    fun printTypeOfInitVal() = when (val someObject = initAny()) {
        is MutableSet<*> -> println("MutableSet")
        is MutableList<*> -> println("MutableList")
        else -> println("unknown type")
    }

    // when without condition
    val someValue = when {
        1 == 1 -> 1
        1 != 1 -> 2
        else -> 3
    }

    // ============================ Loops
    // while / do-while pretty much the same like in Java
    // for loop
    // in
    for (s in listOf(1, 2, 3)) println(s)

    // explicit element type
    for (s in listOf(1, 2, 3)) println(s)

    // iteration with index
    for ((index, value) in listOf(1, 2, 3).withIndex()) println("$index = $value")

    // iterating map
    val map = mapOf(1 to "one", 2 to "two", 3 to "three")   // to creates a pair
    for ((key, value) in map) println("$key = $value")

    // iterate over ranges (last number inclusively)
    for (i in 0..9) println(i)

    // iterate over ranges with until (last number is excluded)
    for (i in 0 until 9) println(i)

    // iterating backwards over step
    for (i in 9 downTo 1 step 2) println(i)

    // iterate over string
    for (ch in "abc") println(ch)

    // ============================ 'in' checks & ranges
    // in for checking belonging
    fun isLetter(ch: Char) = ch in 'a'..'z' || ch in 'A'..'Z'
    fun isNotDigit(ch: Char) = ch !in '0'..'9'

    // using with when
    fun recognize(ch: Char) = when (ch) {
        in '0'..'9' -> println("digit")
        in 'a'..'z', in 'A'..'Z' -> println("letter")
        else -> println("unknown")
    }

    // range could be customized (works with any Comparable)
    val closedRange: ClosedRange<String> = "ac".."az"

    // contains in collection
    val elementIsInList = "element" in listOf("1", "2", "3")

    // ============================ Exceptions
    // no difference between checked and unchecked exceptions
    // throw an exception
    fun checkPercentage(number: Int) =
        if (number in 0..100) number else throw IllegalArgumentException("A percentage should be from 0 to 100")

    // try is an expression
    val percentage = try {
        checkPercentage(200)
    } catch (e: IllegalArgumentException) {
        0
    }
    println(percentage)

    // checked exception and Java
    @Throws(IOException::class)
    fun someValue() = 0

    // ============================ EXTENSIONS
    // ============================ Extension Functions
    fun String.getLastChar() = this[length - 1]
    println("abc".getLastChar())

    // should be imported to use outside a package
    // import org.example.getLastChar

    // ============================ Examples from the Standard Library
    // infix extension function
    infix fun String.charInPosition(pos: Int) = this[pos]
    println("abc" charInPosition 1)

    // multiline strings
    val multiline = """
        aaa
        bbb
        ccc
    """.trimIndent()
    println(multiline)

    // convert string to regex
    val regex = "\\d{2}.\\d{2}.\\d{4}".toRegex()
//    val regex = """\d{2}.\d{2}.\d{4}""".toRegex() \\ no need to escape special character with triple quota
    println(regex.matches("15.02.2022"))

    // convert string to number
    println("123".toInt())

    // pair extension
    val pair: Pair<String, Int> = "abc" to 123

    // ============================ Calling Extensions
    // calling extension function in inheritance (extension function interpreted to jvm static functions
    // and cannot be overridden cause of this)
    open class Parent
    class Child : Parent()

    fun Parent.foo() = "parent"
    fun Child.foo() = "child"

    val parent: Parent = Child()
    println(parent.foo())

    val child = Child()
    println(child.foo())

    // member always win extension function
    fun String.get(i: Int) = "abc"
    println("abc".get(1))

    // ...overriding works well though
    fun String.get(i: Int, j: Int) = "abc"
    println("abc".get(1, 1))
}

fun someFunction(): String {
    return "return value"
}

fun initAny(): Any {
    return 1
}
