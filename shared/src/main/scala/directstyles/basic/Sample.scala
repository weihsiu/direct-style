package directstyles.basic

import directstyles.*
import scala.util.Random

type SampleReq = Random
type SampleCap[A] = Cap[SampleReq, A]

object SampleCap:
  inline def apply[A](inline body: SampleCap[A]): SampleCap[A] = body

  val int: SampleCap[Int] = summon[Random].nextInt()

  val double: SampleCap[Double] = summon[Random].nextDouble()

  def run[A](sample: SampleCap[A])(using random: Random = scala.util.Random) =
    given rand: Random = random
    sample

@main
def runPrintSample(): Unit =
  val printSample: Cap[Reqs[(SampleReq, PrintReq)], Unit] =
    summon[SampleReq]
    PrintCap:
      SampleCap:
        val i = SampleCap.int
        PrintCap.println(i)
  PrintCap.run(SampleCap.run(printSample))
  SampleCap.run(PrintCap.run(printSample))
