package directstyles.effect

import directstyles.*
import javax.smartcardio.ATR
import directstyles.basic.ContextFunction.A
import Tuple.*

object Example0:
  class R1
  class R2
  class R3

  summon[Contains[(R1, R2), R3] =:= false]
  summon[Distinct[(R1, R2), (R2, R3)] =:= (R1, R2, R3)]

  type C1[A] = R1 ?=> A
  type C2[A] = R1 ?=> R2 ?=> A
  type C3[A] = R1 ?=> R2 ?=> R3 ?=> A

  def f1: C2[Unit] =
    val r1 = summon[R1]
    ()

  summon[Cap[R1, A] =:= C1[A]]
  summon[Cap[(R1, R2), A] =:= C2[A]]
  summon[Cap[(R1, R2, R3), A] =:= C3[A]]

  type D1[A] = Cap[R1, A]
  type D2[A] = Cap[(R1, R2), A]
  type D3[A] = Cap[(R1, R2, R3), A]

  summon[C1[A] =:= D1[A]]
  summon[C2[A] =:= D2[A]]
  summon[C3[A] =:= D3[A]]

  summon[EnsureTuple[R1] =:= R1 *: EmptyTuple]
  summon[EnsureTuple[(R1, R2)] =:= (R1, R2)]

  summon[ToTuples[(R1, R2, (R1, R2))] =:= (R1 *: EmptyTuple, R2 *: EmptyTuple, (R1, R2))]

  // def f0: (R1, R1) ?=> Unit =
  //   summon[R1]

  // def f1: Ctx[C1[?]] ?=> Unit =
  //   summon[R1]

  // def f2: Ctx[C2[?]] ?=> Unit =
  //   f1
  //   summon[R2]
