package directstyles

import scala.util.boundary
import scala.Tuple.*

type Distinct[X <: Tuple, Y <: Tuple] <: Tuple = X match
  case EmptyTuple => Y
  case x *: xs =>
    Contains[Y, x] match
      case true  => Distinct[xs, Y]
      case false => Distinct[xs, x *: Y]

type EnsureTuple[X] <: Tuple = X match
  case EmptyTuple => EmptyTuple
  case x *: xs    => x *: EnsureTuple[xs]
  case _          => X *: EmptyTuple

type ToTuples[X <: Tuple] = Map[X, EnsureTuple]

type Cap[R, A] = R match
  case EmptyTuple      => Nothing
  case r *: EmptyTuple => (r) ?=> A
  case r *: rs         => (r) ?=> Cap[rs, A]
  case _               => (R) ?=> A

type Reqs[R <: Tuple] = ToTuples[R] match
  case EmptyTuple      => EmptyTuple
  case r *: EmptyTuple => r
  case r1 *: r2 *: rs  => Reqs[Distinct[r1, r2] *: rs]

object optional:
  inline def apply[T](x: boundary.Label[None.type] ?=> T): Option[T] =
    boundary { Some(x) }

  extension [T](x: Option[T])
    inline def ?(using boundary.Label[None.type]): T =
      x.getOrElse(boundary.break(None))
