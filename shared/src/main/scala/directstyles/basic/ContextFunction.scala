package directstyles.basic

object ContextFunction:
  trait A
  trait B
  trait C
  def requireA(): A ?=> Unit = ???
  // def requireA()(using A): Unit = ???
  def requireB(): B ?=> Unit = ???
  def requireC(): C ?=> Unit = ???

  def requireNone(): Unit =
    given A = new A {}
    requireA()

  def requireAB1(): (A, B) ?=> Unit =
    requireA()
    requireB()

  def requireA_1(): A ?=> Unit =
    given B = new B {}
    requireAB1()
    ()

  def requireAB2(): A ?=> B ?=> Unit = ???

  def requireABC(): (A, B, C) ?=> Unit = ???

  def hasB3(): (C, A) ?=> Unit =
    given B = new B {}
    requireABC()
    ()

  def hasB4(): C ?=> A ?=> Unit =
    given B = new B {}
    requireABC()
    ()
