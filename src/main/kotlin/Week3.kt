fun main() {
    // ============================ NULLABILITY
    // ============================ Nullable types
    // declaring a nullable type
    val s1: String = "abc"
//    val s2:String = null  // won't compile
    val s3: String? = null

    s1.length
//    s3.length // won't compile

    // safe access
    s3?.length  // the same as if (s3 != null) s3.length

    // safe access returns nullable type
    val length: Int? = s3?.length

    // default value with sage access
    val default: Int = s3?.length ?: 0

    // explicitly throw NPE if operand is null
//    val npe: Int = s3!!.length

    // ============================ Nullable types under the hood
    // list of nullable elements vs nullable list
    var nullableList: List<Int>?
    var listOfNullElements: List<Int?>

    // ============================ Safe casts
    val abc = "abc"
    // type check
    if (abc is String) {
        abc.length  // smart cast
    }

    // type cast
    val s = abc as String

    // save cast (null or cast)
    (abc as? String)?.length

    // ============================ FUNCTIONAL PROGRAMMING
    // ============================ Lambdas
    // init lambda
    val lambda1 = { x: Int, y: Int -> x + y }

    // pass lambda as an argument
    listOf(1).any({ x: Int -> x > 0 })
    listOf(1).any() { x: Int -> x > 0 } // () could be omitted if there is no arguments
    listOf(1).any { x: Int -> x > 0 }
    listOf(1).any { x -> x > 0 }    // type could be omitted if it could be inferred
    listOf(1).any { it > 0 }    // it could be used if there's only argument
    listOf(1).any {
        println("Processing $it")
        it > 0
    }

    // using with map
    mapOf(1 to "one").map { (k, v) -> "key - $k, value - $v" }

    // if one of parameters is not used
    mapOf(1 to "one").map { (_, v) -> "value - $v" }

    // ============================ Common Operations on collections
    // filter
    listOf(1).filter { it % 2 == 0 }

    // map
    listOf(1).map { it * 2 }

    // any (at least one element satisfy given predicate)
    listOf(1).any { it % 2 == 0 }

    // find / first / firstOrNull
    listOf(1).find { it % 2 == 0 }
    listOf(1).first { it % 2 == 0 } // throws exception if not found
    listOf(1).firstOrNull { it % 2 == 0 }   // same as find - returns null

    // count
    listOf(1).count { it % 2 == 0 }

    // partition (divides into two collections)
    listOf(1).partition { it % 2 == 0 }

    // group by
    listOf("1", "12", "123").groupBy { it.length }

    // associateBy (map object -> map with unique keys - duplicates are removed!)
    listOf("1", "12", "123").associateBy { it.length }

    // associate (map list -> map)
    listOf(1, 2, 3).associate { 'a' + it to 10 * it } // a -> 10, b -> 20, c -> 30

    // zip (combines two lists, result is the size of shortest list - other elements are ignored)
    listOf(1).zip(listOf("a", "b")) { x, y -> "$x - $y" }

    // zipWithNext (combines current and next elements)
    listOf(1, 2, 3).zipWithNext { current, next -> "$current - $next" }

    // flatten (flattens a list of lists)
    listOf(listOf(1), listOf(2), listOf(3)).flatten()

    // flat map (maps and flattens as one operation)
    listOf("abc", "def").flatMap { it.toCharArray().asList() }  // = list<Char>(a, b, c, d, f)

    // ============================ Function Types
    // init
    val fType: (Int, Int) -> Int = { x, y -> x + y }

    // calling
    println(fType(1, 2));

    // calling lambda directly
    { println("hello") }()

    // better version for calling lambda directly
    run { println("hello") }

    // init Runnable explicitly
    val runnable = Runnable { println(1) }

    // nullable lambda
    val f: (() -> Int)? = if (1 > 2) ({ 1 }) else null

    // calling nullable lambda
    if (f != null) {
        f()
    }
    // or
    f?.invoke()

    // ============================ Member References
    class Person(val name: String, val age: Int)
    listOf(Person("1", 1)).maxBy { it.age }
    listOf(Person("1", 1)).maxBy(Person::age)   // the same as in java

    // lambda can be stored in var, function - cannot
    fun fun1() = 1
//    val f1 = fun1

    // ... but function reference can be
    val f1 = ::fun1
    // or
    val f2 = { f1() }

    // non-bound reference (static)
    class Cat(val name: String, val age: Int) {
        fun isOlder(limit: Int) = age > limit
    }
    // init non-bound reference
    val agePredicate: (Cat, Int) -> Boolean = Cat::isOlder
//    val agePredicate -> Boolean = Cat::isOlder
    // usage
    val cat = Cat("cat", 10)
    agePredicate(cat, 100)

    // bound reference (attach to an instance)
    val alice = Cat("alice", 10)
    val aliceAgePredicate: (Int) -> Boolean = alice::isOlder
    // usage
    aliceAgePredicate(100)

    // bound to _this_ reference
    class Dog(val name: String, val age: Int) {
        fun isOlder(limit: Int) = age > limit
        fun agePredicate() = ::isOlder  // or this::isOlder
    }

    // ============================ Return from Lambda
    // return always return from function marked with 'fun'
    fun duplicateNonZeroElements(l: List<Int>): List<Int> {
        return l.flatMap {
            if (it == 0) return listOf();
            listOf(it * it)
        }
    }
    println(duplicateNonZeroElements(listOf(3, 0, 5)))  // will return empty list

    // return from a lambda - use function name as a label
    listOf(0, 1, 2).flatMap {
        if (it == 0) return@flatMap listOf()
        listOf(it * it)
    }
    // return from a lambda - specify a label name
    listOf(0, 1, 2).flatMap l@{
        if (it == 0) return@l listOf()
        listOf(it * it)
    }

    // solution using local function
    fun duplicateNonZeroLocalFunction(l: List<Int>): List<Int> {
        fun duplicateNonZeroElement(e: Int): List<Int> {
            if (e == 0) return listOf()
            return listOf(e * e)
        }
        return l.flatMap(::duplicateNonZeroElement)
    }
    println(duplicateNonZeroLocalFunction(listOf(3, 0, 5)))

    // solution using anonymous function
    fun duplicateNonZeroAnonymousFunction(l: List<Int>): List<Int> {
        return l.flatMap(fun(e: Int): List<Int> {
            if (e == 0) return listOf()
            return listOf(e * e)
        })
    }
    println(duplicateNonZeroAnonymousFunction(listOf(3, 0, 5)))

    // solution with not using return
    fun duplicateNonZeroWithoutReturn(l: List<Int>): List<Int> {
        return l.flatMap {
            if (it == 0) listOf() else listOf(it * it)
        }
    }
    println(duplicateNonZeroWithoutReturn(listOf(3, 0, 5)))

    // using return instead continue in forEach loop
    listOf(0, 1, 2).forEach {
        if (it == 0) return@forEach
        println(it)
    }
    // ... is the same as
    for (it in listOf(0, 1, 2)) {
        if (it == 0) continue
        println(it)
    }
}
