package directstyles.basic

object Caps:
  trait Cap:
    type Deps
    type Rtn[A] = Deps ?=> A

  type Console = Console.type

  trait Print extends Cap:
    def print(msg: Any): Rtn[Unit]
    def println(msg: Any): Rtn[Unit]

  object Print:
    def apply(): Print = new Print:
      type Deps = Console
      def print(msg: Any): Rtn[Unit] = summon[Console].print(msg)
      def println(msg: Any): Rtn[Unit] = summon[Console].println(msg)

    def run(body: Console ?=> Unit): Unit =
      given c: Console = Console
      body(using c)

  val printlnMessage: Console ?=> Unit = Print().println("world")

  @main
  def runPrintCap(): Unit =
    given Console = Console
    type Deps = Console
    type Rtn[A] = Deps ?=> A
    def println2(msg: Any): Rtn[Unit] = summon[Console].println(msg)
    println2("hello")
    val r: Console ?=> Unit = printlnMessage
    r
