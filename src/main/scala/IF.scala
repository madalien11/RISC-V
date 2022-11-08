package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.{ BitPat, MuxCase }

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val PCNew = Input(UInt(32.W))
      val PC = Output(UInt())

      val controlSignals = Input(new ControlSignals)
      val instruction = Output(new Instruction)
      
      val stallIn    = Input(UInt(1.W))
    })

  val IMEM  = Module(new IMEM)
  val PC    = RegInit(UInt(32.W), 0.U)
  val PCOld = RegInit(UInt(32.W), 0.U)

  PCOld := io.PC


  // printf("\n\n")
  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  /**
    * TODO: Your code here.
    * 
    * You should expand on or rewrite the code below.
    */

  val instruction = Wire(new Instruction)
  when(io.controlSignals.jump || io.controlSignals.branch) {
    PC := io.PCNew
  } otherwise {
    when(io.stallIn.===(1.U)){
      PC := PC
    } .otherwise{
      PC := PC + 4.U
    }
  }
  io.PC := Mux(io.stallIn.===(1.U), PCOld, PC)
  IMEM.io.instructionAddress := io.PC

  instruction := IMEM.io.instruction.asTypeOf(new Instruction)
  io.instruction := instruction

  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    PC := 0.U
    instruction := Instruction.NOP
  }
}
