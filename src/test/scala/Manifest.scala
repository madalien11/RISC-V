package FiveStage

import org.scalatest.{Matchers, FlatSpec}
import cats._
import cats.implicits._
import fileUtils._

import chisel3.iotesters._
import scala.collection.mutable.LinkedHashMap

import fansi.Str

import Ops._
import Data._
import VM._

import PrintUtils._
import LogParser._

object Manifest {

  // val singleTest = "addi.s"
  // val singleTest = "arithImm.s"
  // val singleTest = "arith.s"
  // val singleTest = "load.s"
  // val singleTest = "load2.s"
  // val singleTest = "forward1.s"
  // val singleTest = "forward2.s"
  // val singleTest = "constants.s"
  val singleTest = "naiveFib.s"

  // val singleTest = "memoFib.s"
  // val singleTest = "square.s"
  // val singleTest = "searchRegularO0.s"
  // val singleTest = "palindromeO3.s"
  // val singleTest = "palindrome.s"
  // val singleTest = "BTreeO3.s"
  // val singleTest = "BTreeManyO3.s"

  val nopPadded = false

  val singleTestOptions = TestOptions(
    printIfSuccessful  = true,
    printErrors        = true,
    printParsedProgram = false,
    printVMtrace       = false,
    printVMfinal       = false,
    printMergedTrace   = true,
    printBinary        = false,
    nopPadded          = nopPadded,
    breakPoints        = Nil, // not implemented
    testName           = singleTest,
    maxSteps           = 15000)


  val allTestOptions: String => TestOptions = name => TestOptions(
    printIfSuccessful  = false,
    printErrors        = false,
    printParsedProgram = false,
    printVMtrace       = false,
    printVMfinal       = false,
    printMergedTrace   = false,
    printBinary        = false,
    nopPadded          = nopPadded,
    breakPoints        = Nil, // not implemented
    testName           = name,
    maxSteps           = 50000)

}



class ProfileBranching extends FlatSpec with Matchers {
  it should "profile some branches" in {
    BranchProfiler.profileBranching(
      Manifest.singleTestOptions.copy(testName = "branchProfiling.s", maxSteps = 150000)
    ) should be(true)
  }
}

class ProfileCache extends FlatSpec with Matchers {
  it should "profile a cache" in {
    CacheProfiler.profileCache(
      Manifest.singleTestOptions.copy(testName = "convolution.s", maxSteps = 150000)
    ) should be(true)
  }
}

class SingleTest extends FlatSpec with Matchers {
  it should "just werk" in {
    TestRunner.run(Manifest.singleTestOptions) should be(true)
  }
}


class AllTests extends FlatSpec with Matchers {
  it should "just werk" in {
    val werks = getAllTestNames.filterNot(_ == "convolution.s").map{testname => 
      say(s"testing $testname")
      val opts = Manifest.allTestOptions(testname)
      (testname, TestRunner.run(opts))
    }
    if(werks.foldLeft(true)(_ && _._2))
      say(Console.GREEN + "All tests successful!" + Console.RESET)
    else {
      val success = werks.map(x => if(x._2) 1 else 0).sum
      val total   = werks.size
      say(s"$success/$total tests successful")
      werks.foreach{ case(name, success) =>
        val msg = if(success) Console.GREEN + s"$name successful" + Console.RESET
        else Console.RED + s"$name failed" + Console.RESET
        say(msg)
      }
    }
  }
}



/**
  * Not tested at all
  */
class AllTestsWindows extends FlatSpec with Matchers {
  it should "just werk" in {
    val werks = getAllWindowsTestNames.filterNot(_ == "convolution.s").map{testname => 
      say(s"testing $testname")
      val opts = Manifest.allTestOptions(testname)
      (testname, TestRunner.run(opts))
    }
    if(werks.foldLeft(true)(_ && _._2))
      say(Console.GREEN + "All tests successful!" + Console.RESET)
    else {
      val success = werks.map(x => if(x._2) 1 else 0).sum
      val total   = werks.size
      say(s"$success/$total tests successful")
      werks.foreach{ case(name, success) =>
        val msg = if(success) Console.GREEN + s"$name successful" + Console.RESET
        else Console.RED + s"$name failed" + Console.RESET
        say(msg)
      }
    }
  }
}
