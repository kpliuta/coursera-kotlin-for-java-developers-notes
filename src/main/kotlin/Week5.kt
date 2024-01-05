import java.io.BufferedReader
import java.io.FileReader
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.random.Random

fun main() {
    // ============================ INLINE FUNCTIONS
    // ============================ Library functions looking like built-in constructs
    // useful library functions, that can be used without any performance drawbacks
    // run, let, takeIf, takeUnless, repeat

    // 'run' function
    // (runs the block of code (lambda) and returns the last expression as a result)
    val foo = run {
        println("calculating foo")
        "foo"
    }

    // 'let' function
    // (allows to check the argument for being non-null)
    fun getEmail(): String? = ""
    fun sendEmail(email: String) = println("sent")

    // ... with explicit null check
    val email = getEmail()
    if (email != null) sendEmail(email)
    // ... or let can be used instead
    email?.let { e -> sendEmail(e) }
    // ... or
    getEmail()?.let { sendEmail(it) }
//    getEmail().let { sendEmail(it) }  won't compile as String is expected, not String?

    // 'let' can be used instead of smart cast
//    interface Session {   // commented out and defined below
//        val user: User
//    }
    class ConcreteUser(override val nickname: String) : User

    fun analyseUserSmartCast(session: Session) {
        val user = session.user
        if (user is ConcreteUser) {
            println(user.nickname)
        }
    }

    // ... 'let' can be used instead
    fun analyseUserWithLet(session: Session) {
        (session.user as? ConcreteUser)?.let { println(it.nickname) }
    }

    // 'takeIf' function
    // (returns the receiver object if it satisfies given predicate, otherwise null)
    "abc".takeIf { it == "bca" }
    "abc".takeIf(String::isNotEmpty)

    // using 'takeIf' in chained calls
    listOf("abc", "bca").filter { it == "bca" }.takeIf(List<String>::isNotEmpty)
        ?.let { println("some items are found") }

    // 'takeUnless' function
    // (returns the receiver object if predicate is NOT satisfied)
    "abc".takeUnless(String?::isNullOrEmpty)

    // repeat function
    repeat(10) {
        println("welcome")
    }

    // ============================ The power of inline
//    inline fun <R> lamRun(lam: () -> R): R = lam()  // commented out and defined below
    val abc = "abc"
    lamRun { println(abc) }
    // ... will be inlined in a bytecode
//    val abc = "abc"
//    println(abc)              // no performance overhead

    // inlining with synchronize
    val obj = Object()
    synchronized(obj) {
        println("object: $obj")
    }

    // 'withLock' function
    val lock = ReentrantLock()
    lock.withLock { println("inside the lock") }

    // 'use' function (resource management)
    fun read(path: String): String {
        BufferedReader(FileReader(path)).use { br -> return br.readLine() }
    }
    // ... instead of in java
    // try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    //      return br.readLine();
    // }

    // @kotlin.internal.InlineOnly
    // (specifies that the function should not be called without inlining)
    // (such functions will be excluded from the compiled application - are not callable from java)

    // ============================ SEQUENCES
    // ============================ Collections vs Sequences
    // extensions on collections (filter, map, etc.) are inlined (no performance overhead)
    // but intermediate collections are created for chained calls
    // sequences can be used to avoid this (can be compared to the java 8 streams - both perform computations in a lazy manner)
    listOf(1, 2, 3).filter { it > 0 }.map { it * it }.max()     // collection
    listOf(1, 2, 3).asSequence().filter { it > 0 }.map { it * it }.max()     // sequence

    // ============================ More about Sequences
    // horizontal evaluation (collections) - perform under all the elements on each step of a chain
    // vertical evaluation (sequences) - all the chain is performed on each element one-by-one until result is found

    // - in case of sequences nothing is happened until terminal operation is called (same as with streams in java)
    // - order of operations is important

    // ============================ Creating Sequences
    // - sequence is an interface: interface Sequence<out T>
    // - extensions on sequences match extensions on collections (but intermediate operations are not inlined)

    // generating a sequence
    generateSequence { Random.nextInt() }

    // reading input
    val input = generateSequence { readlnOrNull().takeUnless { it == "exit" } }
    println(input.toList())
    // >> a
    // >> b
    // >> exit
    // [a, b]

    // generate an infinite sequence
    val numbers = generateSequence(0) { it + 1 }
    numbers.take(5).toList()    // [0, 1, 2, 3, 4]
    // ... or to prevent integer overflow
    generateSequence(BigInteger.ZERO) { it + BigInteger.ONE }

    // how many times 'abc' is printed?
    val abcNumbers = generateSequence(3) { n ->
        println("abc")
        (n + 1).takeIf { it < 7 }
    }
    print(abcNumbers.first())   // answer: 0 times, 'case 1st element is already given and won't be calculated

    // yield
    val yieldNumbers = sequence {
        var x = 0
        while (true) {
            yield(x++)
        }
    }
    yieldNumbers.take(5).toList()   // [0, 1, 2, 3, 4]

    sequence {
        yield("abc")                                  // value
        // doSomething()                                    // any logic can be placed in between
        yieldAll(listOf("a", "b", "c"))                     // list
        // doSomething()
        yieldAll(listOf("a", "b", "c").asSequence())        // sequence
    }

    // building a sequence in a lazy manner
    fun seq() = sequence {
        println("one element")
        yield(1)
        println("a range")
        yieldAll(3..5)  // last line that will be processed (cause value '4' satisfies predicate above)
        println("a list")
        yieldAll(listOf(7, 9))
    }
    println(seq().map { it * it }.filter { it > 10 }.first())

    // ============================ Library Functions
    listOf(1, 2, 3).filter { c -> c > 0 }.size
    // ... can be optimized
    listOf(1, 2, 3).count { c -> c > 0 }

    listOf(3, 1, 2).sortedBy { it }.reversed()
    // ... can be optimized
    listOf(3, 1, 2).sortedByDescending { it }

    listOf(1, 2, 3, null).map { it?.toString() }.filterNotNull()
    // ... can be optimized
    listOf(1, 2, 3, null).mapNotNull { it?.toString() }

    class Person(val name: String, val age: Int)

    fun ageToPeopleFunSubOptimal(people: List<Person>): MutableMap<Int, MutableList<Person>> {
        val ageToPeople = mutableMapOf<Int, MutableList<Person>>()
        for (person in people) {
            if (person.age !in ageToPeople) {
                ageToPeople[person.age] = mutableListOf()
            }
            ageToPeople.getValue(person.age) += person
        }
        return ageToPeople
    }

    // ... can be optimized
    fun ageToPeopleFun(people: List<Person>): MutableMap<Int, MutableList<Person>> {
        val ageToPeople = mutableMapOf<Int, MutableList<Person>>()
        for (person in people) {
            ageToPeople.getOrPut(person.age) { mutableListOf() } += person
            // ... or
//            ageToPeople.computeIfAbsent(person.age) { mutableListOf() }
//            ageToPeople.getValue(person.age) += person
        }
        return ageToPeople
    }

    // ... can be optimized even further
    fun ageToPeopleFunSuperior(people: List<Person>) = people.groupBy { it.age }

    // lazy 'groupBy' for sequence
    fun ageToPeopleFunSeq(people: List<Person>) = people.asSequence().groupingBy { it.age }.eachCount()

    // ============================ LAMBDA WITH RECEIVER
    // ============================ Lambda with receiver (= Extension Function & Lambda)
    // - regular function == regular lambda
    val regularLambda: (Int) -> Boolean = { it % 2 == 0 }
    regularLambda(0)
    // - extension function == lambda with receiver
    val lambdaWithReceiver: Int.() -> Boolean = { this % 2 == 0 }
    1.lambdaWithReceiver()

    // 'with' function
    val sb = StringBuilder()
    sb.appendLine("Alphabet: ")
    for (c in 'a'..'z') {
        sb.append(c)
    }
    sb.toString()
    // ... to avoid repeating val name 'with' function can be used
    val sb2 = StringBuilder()
    with(sb2) {
        appendLine("Alphabet: ")
        for (c in 'a'..'z') {
            append(c)
        }
        toString()
    }

    // 'buildString' function
    val abz = buildString {
        appendLine("Alphabet: ")
        for (c in 'a'..'z') {
            append(c)
        }
    }

    // ============================ More useful library functions
    // 'run' function (like 'with', but extension)
    val z: String? = null
    z?.run {
        println(length)
    }

    // last expression in 'with' and 'run' will be result of invocation
    val aChar = with("abc") {
        println(length)
        this[0]     // 'a'
    }

    // 'apply' function (return receiver as a result)
    val r1 = z?.apply {
        println(length)
    } ?: return     // if result is null - stop current execution and return out from the function

    // 'also' function (similar to apply, but takes a regular lambda, not a lambda with receiver)
    val r2 = z?.apply {
        println(length)
    }?.also {
        println(it)
    }

    // ============================ TYPES
    // ============================ Basic types
    // - no primitives in the language
    // - Int == int in java, Int? == Integer in java
    // - List<Int> == List<Integer> in java
    // - Array<Int> == Integer[] in java
    // - IntArray == int[] in java

    // confusing methods in kotlin.String
    "one.two.".replace(".", "*")     // one*two*
    "one.two.".replace(".".toRegex(), "*")  // ********

    // - Any == Object in java (also supertype for types like Int, corresponding to primitives)

    // function types
    // - () -> Boolean == Function0<Boolean> in java
    // - (Int) -> Boolean == Function1<Int, Boolean> in java
    // - (Int, Int) -> Boolean == Function2<Int, Int, Boolean> in java

    // ============================ Kotlin type hierarchy
    // - Any - supertype
    // - Unit is used instead of 'void' in java
    // - Nothing - a type that has no values, and can be used as a return type for function, that never returns
    fun fail(): Nothing {
        throw RuntimeException()
    }

    // ... or
    fun infinite(): Nothing {
        while (true) {
        }
    }

    // expressions that will be interpreted as Nothing
    fun nothingExpressions() {
        throw RuntimeException()
        return
        TODO()      // will throw not implemented error
    }

    // Unit expression
    fun unit(): Unit = println("abc")
    val xyz: Unit = unit()
    println(xyz)    // kotlin.Unit

    // using Unit in 'if' expression
    val r01: Any = if (true) {  // infers to Any
        42      // Int
    } else {
        unit()  // Unit
    }

    // using Nothing in 'if' expression
    val r02: Int = if (true) {  // infers to Int
        42      // Int
    } else {
        fail()  // Nothing
    }

    // nullable Nothing?
    val nullable: Nothing? = null
    // ... or
    val nullableList: List<Nothing?> = listOf(null)

    // ============================ Nullable Types
    // nullability annotations
    // - @Nullable Type in java == Type?
    // - @NonNull (@NotNull / @CheckForNull) Type in java == Type

    // default nullability can be specified in java (JSR-305)
    // - @ParametersAreNonnullByDefault
    // - @MyNonnullByDefault
    // - can be applied to a package in (package-info.java)

    // non-annotated type
    // - Type in java == Type! (platform type / unknown nullability type)

    // preventing npe when using java types
    val b0 = StringBuffer().append(0)                   // NullPointerException will be thrown in case of null
    val b1: StringBuffer? = StringBuffer().append(0)    // add explicit nullable - returns null or value
    val b2: StringBuffer =
        StringBuffer().append(0)     // IllegalStateException will be thrown in case of null (kotlin adds additional check)

    // ============================ Collection types
    // - List - read-only list (!= immutable, and can be changes through mutable interface reference)
    // - MutableList
    // - Both List and MutableList are replaced with List interface in java
    // - List<String> from java is treated as (Mutable)List<String!> platform type

}

inline fun <R> lamRun(lam: () -> R): R = lam()
