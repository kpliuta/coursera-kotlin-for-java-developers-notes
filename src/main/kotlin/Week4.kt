import java.awt.Point
import java.util.concurrent.Executors

fun main() {
    // ============================ PROPERTIES
    // ============================ Properties (property != field!)
    // java property
//    public class JavaClass {
//        private int foo = 0;
//        public int getFoo() {
//            return foo;
//        }
//        public void setFoo(int foo) {
//            this.foo = foo;
//        }
//    }

    // kotlin property
    class KotlinClass {
        var foo = 0
    }

    // or
    class KotlinClass2(var foo: Int = 0)

    // backing field might be empty
    class Rectangle(val with: Int, val height: Int) {
        val isSquare: Boolean
            get() {
                return with == height
            }
    }
    println(Rectangle(10, 10).isSquare)

    // accessing field inside accessors
    // (not working with fields directly, instead working with properties)
    class StateLogger {
        var state = false
            set(value) {
                println("change $field to $value ")
                field = value
            }
    }
    StateLogger().state = true  // will print 'change false to true'

    // if there is no field inside accessors - backed field is not created
    class StateLogger2 {
        private var boolState = false
        var state: State
            get() = if (boolState) State.ON else State.OFF
            set(value: State) {
                boolState = value == State.ON
            }
    }

    // change visibility of the setter
    class LengthCounter {
        var counter: Int = 0
            private set

        fun addWord(word: String) {
            counter += word.length
        }
    }

    val counter = LengthCounter()
    counter.addWord("abc")
    println(counter.counter)

    // ============================ More about Properties
    // property in interface
//    interface User {  // commented out and defined below
//        val nickname: String  // is just a getter in fact
//    }
    // ... then in can be used in subclass
    class ConcreteUser : User {
        override val nickname = "name"  // will be calculated only once (and store in a field)
    }

    // ... or
    class ConcreteUser2(val name: String) : User {
        override val nickname: String
            get() = "name: $name"  // will be calculated on each access (there is no backed field for this property)
    }

    // open property (one defined in interface) cannot be used in smart cast
//    interface Session {   // commented out and defined below
//        val user: User
//    }
    fun analyseUser(session: Session) {
        if (session.user is ConcreteUser2) {
//            println(session.user.name)    Smart cast to 'ConcreteUser2' is impossible, because 'session.user' is a property that has open or custom getter
        }
    }

    // ... local var can be introduced in this case
    fun analyseUserThatWorks(session: Session) {
        val user = session.user
        if (user is ConcreteUser2) {
            println(user.name)
        }
    }

    // extension properties (like extension functions)
//    val String.lastIndex: Int     // commented out and defined below
//        get() = this.length - 1
    print("abc".lastIndex)

    // mutable extension properties
//    var StringBuilder.lastChar: Char  // commented out and defined below
//        get() = get(length - 1)
//        set(c: Char) {
//            this.setCharAt(length - 1, c)
//        }
    val sb = StringBuilder("abc")
    sb.lastChar = 'd'
    println(sb)

    // ============================ Lazy or late initialization
    // lazy property initialization
    val lazyValue: String by lazy {
        println("computed")
        "abc"
    }
    println(lazyValue)  // -> computed \n abc
    println(lazyValue)  // -> abc

    // lateinit
    // instead of...
    class NonLateInit {
        var prop: String? = null
        fun init() {
            prop = "abc"
        }

        fun accessProp() {
            println(prop?.length)
        }
    }

    // ... late init can be used
    class LateInit {
        // lateinit var prop: Int   // cannot be of primitive type!
        lateinit var prop: String   // cannot be val!
        fun init() {
            prop = "abc"
        }

        fun accessProp() {
            println(this::prop.isInitialized)   // false
            println(prop.length)                // UninitializedPropertyAccessException with be thrown if property is not yet initialized
            println(this::prop.isInitialized)   // true
        }
    }

    // ============================ OBJECT-ORIENTED PROGRAMMING
    // ============================ OOP in Kotlin
    // the defaults are different:
    // - any declaration is 'public' and 'final' by default
    // - to make it non-final, it should be marked with 'open'
    // - no 'private' in kotlin, there is 'internal' instead, meaning it's visible inside the same module
    // - a module is: an Intellij module / a maven project / a gradle source set

    // visibility modifiers:
    // public - visible everywhere (jmv: public)
    // internal - visible in the same module (jmv: public & name mangling)
    // protected - visible in subclass, but not (!) in the package, like in java (jmv: protected)
    // private - visible in class or in the file, in case of top-level declaration (jmv: private / package private)

    class Foo {
        internal fun bar() {}
    }
    // under the hood is
//    public final class Foo {
//        public final void bar$production_sources_for_module_etc();
//    }

    // package structure
    // - several classes could be put into the same file
    // - top-level declarations could be in the same file as well
    // - the package doesn't need to correspond to the directory structure
    // (kotlin style guide recommend to omit company name in the directory structure)

    // ============================ Constructors, Inheritance syntax
    // default constructor
    class A

    val a = A()

    // concise primary constructor
    class Person(val name: String, val age: Int)

    // constructor body
    class B(name: String) {    // without val / var it's only a constructor parameter
        val name: String

        init {
            this.name = name
        }
    }

    // changing visibility of the constructor
    class InternalComponent
    internal constructor(name: String) {
        // ...
    }

    // secondary constructor
    class Rect(val height: Int, val width: Int) {
        constructor(side: Int) : this(side, side)
    }

    // the same syntax for 'extends' and 'implements'
    // interface Base   // commented out and defined below
    class BaseImpl : Base

    open class Parent
    class Child : Parent()  // constructor call

    // overriding a property
    open class Parent2 {
        open val foo = 1    // private final int foo = 1

        init {
            println(foo)    // getFoo()
        }
    }

    class Child2 : Parent2() {
        override val foo = 2    // private final int foo = 1
    }
    Child()     // will print '0', 'cause Parent2 constructor will call Child2 getter when foo in Child2 is not initialized yet

    // ============================ Class modifiers - I
    // enum class
//    enum class State { ON, OFF }  // commented out and defined below
    // typical use case - with when
    fun check(state: State) {
        when (state) {
            State.ON -> println("on")
            State.OFF -> println("off")
        }
    }

    // parametrized enum
//    enum class Color(val r: Int, val g: Int, val b: Int) {    // commented out and defined below
//        RED(255, 0, 0),
//        GREEN(0, 255, 0),
//        BLUE(0, 0, 255);        // the only place where semicolon is needed
//
//        fun rgb(): String = "$r $g $b"
//    }
    println(Color.RED.rgb())

    // data classes
    data class Car(val color: Color, val power: Int)
    // generates equals, hashCode, copy, toString and some other methods

    // copy
    val car1 = Car(Color.RED, 100)
    val car2 = car1.copy(power = 200)  // specify only arguments that must be changed

    // equals and reference equality
    car1 == car2    // calls equals
    car1 === car2   // checks reference equality

    // ignore properties in generated code
    data class User(val email: String) {    // takes into account only primary constructor properties
        var nickname: String? = null        // declared inside body properties are ignored
    }
    // ... for example
    val user1 = User("email@email.com")
    user1.nickname = "nickname_1"
    val user2 = User("email@email.com")
    user2.nickname = "nickname_2"
    print(user1 == user2)   // the result is true

    // ============================ Class modifiers - II
    // sealed classes
//    interface Expr    // commented out and defined below
    class Num(val value: Int) : Expr
    class Sum(val left: Expr, val right: Expr) : Expr

    //fun eval(e: Expr): Int = when (e) {     // expression must be exhaustive error
//        is Num -> e.value
//        is Sum -> eval(e.left) + eval(e.right)
//    }
    // ... could be solved this way
    fun eval(e: Expr): Int = when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        else -> throw IllegalArgumentException("unknown exception")
    }

    // ... or sealed modifier can be introduced, if whole hierarchy is known
//    sealed class Expr2    // commented out and defined below
//    class Num2(val value: Int) : Expr2()
//    class Sum2(val left: Expr2, val right: Expr2) : Expr2()
    fun eval2(e: Expr2): Int = when (e) {     // no error anymore
        is Num2 -> e.value
        is Sum2 -> eval2(e.left) + eval2(e.right)
    }

    // nested and inner classes
    // 'static class A' in java == 'class A' (by default) in kotlin == nested class
    // 'class A (by default)' in java == 'inner class A' in kotlin == inner class

    // inner class example
//    class A1 {    // commented out and defined below
//        val a = "abc"
//        class B1
//        inner class C1 {
//            fun foo() = println(this@A1.a)
//        }
//    }

    // class delegation
//    interface Repository {    // commented out and defined below
//        fun findAll(): List<Int>
//        fun removeAll()
//    }
//    interface Logger {
//        fun log()
//    }
    // either delegation could be implemented manually
    class Controller1(val repository: Repository, val logger: Logger) : Repository, Logger {
        override fun findAll(): AList<Int> = repository.findAll()
        override fun removeAll() = repository.removeAll()
        override fun log() = logger.log()
    }

    // ... or using class delegation
    class Controller2(val repository: Repository, val logger: Logger) : Repository by repository, Logger by logger

    // ============================ Objects, object expressions & companion objects
    // object == singleton
//    object Singleton {    // commented out and defined below
//        fun foo() = println("foo")
//    }
    Singleton.foo()

    // object expression - replaces java's anonymous classes
    Executors.callable(object : Runnable {
        override fun run() {
            println("abc")
        }
    })

    // companion object - there is no static methods in kotlin, so companion object might be a replacement for that
//    class Y {     // commented out and defined below
//        companion object {
//            fun foo() = 1
//        }
//    }
    println(Y.foo())

    // can implement an interface (in java static member cannot override an interface)
//    class Z {     // commented out and defined below
//        private constructor()
//
//        companion object : Runnable {
//            override fun run() {
//                println("run")
//            }
//        }
//    }

    // define extensions for companion object
    fun Z.Companion.printAbc(): Unit =
        println("abc")   // 'Companion' suffix is used to distinguish between class and companion

    // to call static method in java as X.foo()
//    class X {     // commented out and defined below
//        companion object {
//            @JvmStatic
//            fun foo() = 1         // or X.Companion.foo() could be called alternatively
//        }
//    }

    // ============================ Constants
    // compile-time constant
    // if value of a primitive or string is known in compile time - it would be inline throughout a code, e.g.
//    const val answer = 42     // commented out and defined below

    // do not generate accessors and expose a kotlin property as a field in java
//    @JvmField     // commented out and defined below
//    val prop = Y()
    // ... in case it is an object, the same as in java...
//    public static final Y prop = new Y();
    // ... in case it is a regular class, the same as in java...
//    public final Y prop = new Y();

    // expose a getter as a static member
//    object S {    // commented out and defined below
//        @JvmStatic
//        val answer = 42
//    }
    // ... in java...
//    S.getAnswer()

    // ============================ Generics
//    interface List<E> {   // commented out and defined below
//        fun get(i: Int): E
//    }

    // generic function
    fun <T> genericFunction(arg: T) = println(arg)

    // element of a such generic can be nullable
    fun <T> foo(list: kotlin.collections.List<T>) {
        for (element in list) {
            println(element)
        }
    }
    foo(listOf(1, null))

    // non-nullable upper bound
    fun <T : Any> bar(list: kotlin.collections.List<T>) = println(list)
//    bar(listOf(1, null))      wont compile

    // specifying upped bound
    fun <T : Number> half(value: T): Double = value.toDouble() / 2.0

    // specifying nullable upped bound
    fun <T : Number?> nullableHalf(value: T): Double? {
        if (value == null) return null;
        return value.toDouble() / 2.0
    }

    // multiple constraints for a type
    fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
        if (!seq.endsWith(".")) {
            seq.append(".")
        }
    }

    // it's possible to declare extension function for different generic types using @JvmName
//    fun List<Int>.average(): Double = 0.0     // commented out and defined below
//    @JvmName("averageOfDouble")
//    fun List<Double>.average(): Double = 0.0

    // ============================ CONVENTIONS
    // ============================ Operator Overloading
    // arithmetic operations
    operator fun Point.plus(b: Point): Point = Point(x + b.x, y + b.y)
    Point(1, 2) + Point(3, 4)

    // correspondence of expression and function name
    // a + b    ->      plus
    // a - b    ->      minus
    // a * b    ->      times
    // a / b    ->      div
    // a % b    ->      mod

    // no restriction on parameter type
    operator fun Point.times(b: Int): Point = Point(x * b, y * b)
    Point(1, 2) * 5

    // unary operations
    operator fun Point.unaryMinus(): Point = Point(-x, -y)
    -Point(1, 2)

    // correspondence of unary operators and function name
    // +a          ->      unaryPlus
    // -a          ->      unaryMinus
    // !a          ->      not
    // ++a, a++    ->      inc
    // --a, a--    ->      dec

    // assignment operations
    // a += b resolves first to a.plusAssign(b) if defined or a = a.plus(b) if not

    // convention for lists
    val list = listOf(1, 2, 3)
    val newList = list + 4  // creates a new list with the new element

    val mutableList = mutableListOf(1, 2, 3)
    mutableList += 4    // adds an element to mutable list

    // ============================ Conventions
    // compare comparable objects
    "abc" < "cba"
    // a >= b    ->    a.compareTo(b) >= 0

    // equality check
    "abc" == "cba"  // == "abc".equals("cba")
    // correctly handles nullable variables
    null == "abc"
    null == null    // true

    // accessing elements by index
    mapOf(1 to "one", 2 to "two")[1]        // access 1 element
    mutableMapOf(1 to "one")[2] = "two"     // puts 2 element

    // x[a, b]      ->      x.get(a, b)
    // x[a, b] = c  ->      x.set(a, b, c)

    // in convention
    if (1 in mapOf(1 to "one", 2 to "two")) {
    }
    if (1 in listOf(1, 2, 3)) {
    }
    // a in b   ->  b.contains(a)

    // rangeTo convention
    for (i in 1..2) {
    }
    // start..end   ->   start.rangeTo(end)

    // iterator convention
//    operator fun CharSequence.iterator(): CharIterator
    for (c in "abc") {
    }

    // destructing declarations
    val (number, str) = 1 to "one"  // a pair
    for ((k, v) in mapOf(1 to "one")) {
    }
    mapOf(1 to "one").forEach { (k, v) -> println("$k - $v") }
    // val (a, b) = c       ->      val a = c.component1()
    //                              val b = c.component2()

    // destructing in lambdas
    // { a -> ... }             // one parameter
    // { a, b -> ... }          // two parameters
    // { (a, b) -> ... }        // a destructed pair
    // { (a, b), c -> ... }     // a destructed pair and another parameter

    // iterating over list with indexes
    for ((i, e) in listOf(1, 2, 3).withIndex()) {
        println("$i - $e")
    }
    // ... under the hood is...
    val withIndex: Iterable<IndexedValue<Int>> = listOf(1, 2, 3).withIndex()
    for (c in withIndex) {
        val i = c.component1()
        val e = c.component2()
        println("$i - $e")
    }

    // destructing data classes
    data class Contact(val name: String, val email: String, val phoneNumber: String) {
        // component functions are automatically generated
        // fun component1() = name
        // fun component2() = email
        // fun component3() = phoneNumber
    }

    // omit a variable during destructing
    val (name, _, num) = Contact("name", "email", "number")
}

fun AList<Int>.average(): Double = 0.0

@JvmName("averageOfDouble")
fun AList<Double>.average(): Double = 0.0

interface AList<E> {
    fun get(i: Int): E
}

object S {
    @JvmStatic
    val answer = 42
}

@JvmField
val prop = Y()

const val answer = 42

class Y {
    companion object {
        fun foo() = 1
    }
}

class Z {
    private constructor()

    companion object : Runnable {
        override fun run() {
            println("run")
        }
    }
}

class X {
    companion object {
        @JvmStatic
        fun foo() = 1
    }
}

object Singleton {
    fun foo() = println("foo")
}

interface Repository {
    fun findAll(): AList<Int>
    fun removeAll()
}

interface Logger {
    fun log()
}

class A1 {
    val a = "abc"

    class B1
    inner class C1 {
        fun foo() = println(this@A1.a)
    }
}

interface Expr

sealed class Expr2
class Num2(val value: Int) : Expr2()
class Sum2(val left: Expr2, val right: Expr2) : Expr2()

interface User {
    val nickname: String
}

interface Session {
    val user: User
}

val String.lastIndex: Int
    get() = this.length - 1

var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(c: Char) {
        this.setCharAt(length - 1, c)
    }

enum class State { ON, OFF }

enum class Color(val r: Int, val g: Int, val b: Int) {
    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255);

    fun rgb(): String = "$r $g $b"
}

interface Base