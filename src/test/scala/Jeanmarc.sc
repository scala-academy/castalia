
val m1 = Map(("a", "1"), ("b", "2"))
val m2 = Map(("b", "2"), ("a", "1"))

val r = m1 == m2

val l1 = List(("a", "1"), ("b", "2"))

l1.toMap == m1
l1.toMap == m2
