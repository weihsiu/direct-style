package directstyles.async

import gears.async.*
import gears.async.default.given
import java.util.concurrent.locks.Lock

def time(body: => Unit): Unit =
  val start = System.currentTimeMillis()
  body
  println(s"${System.currentTimeMillis() - start} ms elapsed")

def checkInterruption()(using Async): Unit = AsyncOperations.sleep(0)

extension (lock: Lock)
  def mutex(body: => Unit): Unit =
    lock.lock()
    try body
    finally lock.unlock()
