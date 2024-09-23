package directstyles

import ox.*
import scala.concurrent.duration.*
import scala.util.Random

object HelloOx:
  @main
  def runHelloOx() =
    val input = (1 to 1000000).toList
    val result = par(input.map: n =>
      () =>
        sleep(Random.nextInt(10).millis)
        // println(Thread.currentThread().toString())
        n
    )
    println(result.length)

  @main
  def runSupervised() =
    supervised {
      forkUser {
        sleep(1.second)
        println(Thread.currentThread())
      }
      forkUser {
        sleep(500.millis)
        println(Thread.currentThread())
      }
    }
