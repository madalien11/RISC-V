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
      // val PCIn = Input(UInt(32.W))
      val instructionIn = Input(new Instruction)
      val dataIn      = Input(UInt(32.W))
      // val dataAddress = Input(UInt(12.W))
      val controlSignals = Input(new ControlSignals)
      val readData2In    = Input(UInt(32.W))
      
      // val PCOut = Output(UInt(32.W))
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
  // io.PCOut              := io.PCIn
  io.instructionOut     := io.instructionIn
  io.controlSignalsOut  := io.controlSignals
    // io.dataOut := io.dataIn

  // DMEM.io.dataIn      := 0.U
  // DMEM.io.dataAddress := 0.U
  // DMEM.io.writeEnable := false.B
  // printf("io.dataIn %b\n", io.dataIn)
  DMEM.io.dataIn      := io.readData2In
  io.dataOut          := io.dataIn
  DMEM.io.dataAddress := io.dataIn
  DMEM.io.writeEnable := io.controlSignals.memWrite
  io.memDataOut       := DMEM.io.dataOut
  // printf("dataAddress is %d\n", io.dataIn)
  // printf("Output is %d\n", DMEM.io.dataOut)
  // printf("dataAddress is %d\n\n", io.dataAddress)

  // io.dataOut := MuxLookup(io.controlSignals.memRead, 0.U(32.W), Array(
  //   true.B -> DMEM.io.dataOut,
  //   false.B -> io.dataIn,
  // ))
}
