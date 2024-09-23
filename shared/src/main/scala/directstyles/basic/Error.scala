package directstyles.basic

import scala.util.boundary
import scala.util.boundary.{break, Label}

final class Error[-A](using label: Label[A]):
  def raise(error: A): Nothing = break(error)

type Raise[A] = Error[A] ?=> A

object Raise:
  inline def apply[A](inline body: Error[A] ?=> A): Raise[A] = body

  def raise[A](error: A)(using err: Error[A]): Nothing = err.raise(error)

  def run[A](raise: Raise[A]): A =
    boundary[A]:
      given Error[A] = new Error[A]()
      raise

  @main
  def runError() =
    val find3: Raise[String] =
      Raise:
        List(1, 2, 3, 4).foreach(x => if x == 3 then Raise.raise("found 3"))
        "no 3 found"
    val result = Raise.run(find3)
    println(result)
