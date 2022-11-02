package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class MemoryFetch() extends MultiIOModule {


  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val DMEMsetup      = Input(new DMEMsetupSignals)
      val DMEMpeek       = Output(UInt(32.W))

      val testUpdates    = Output(new MemUpdates)
    })

  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction)
      val dataIn      = Input(UInt(32.W))
      val controlSignals = Input(new ControlSignals)
      val readData2In    = Input(UInt(32.W))
      
      val instructionOut = Output(new Instruction)
      val dataOut      = Output(UInt(32.W))
      val memDataOut      = Output(UInt(32.W))
      val controlSignalsOut = Output(new ControlSignals)
    })


  val DMEM = Module(new DMEM)


  /**
    * Setup. You should not change this code
    */
  DMEM.testHarness.setup  := testHarness.DMEMsetup
  testHarness.DMEMpeek    := DMEM.io.dataOut
  testHarness.testUpdates := DMEM.testHarness.testUpdates


  /**
    * Your code here.
    */
  
  io.instructionOut     := io.instructionIn
  io.controlSignalsOut  := io.controlSignals
    
  DMEM.io.dataIn      := io.readData2In
  io.dataOut          := io.dataIn
  DMEM.io.dataAddress := io.dataIn
  DMEM.io.writeEnable := io.controlSignals.memWrite
  io.memDataOut       := DMEM.io.dataOut
}
