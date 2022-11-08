package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup
import chisel3.util.Cat


class InstructionDecode extends MultiIOModule {

  // Don't touch the test harness
  val testHarness = IO(
    new Bundle {
      val registerSetup = Input(new RegisterSetupSignals)
      val registerPeek  = Output(UInt(32.W))

      val testUpdates   = Output(new RegisterUpdates)
    })


  val io = IO(
    new Bundle {
      /**
        * TODO: Your code here.
        */
        val PCIn = Input(UInt(32.W))
        val instruction = Input(new Instruction)
        val writeEnable = Input(Bool())
        val writeData = Input(UInt(32.W))
        val writeAddress = Input(UInt(32.W))
        val immOut = Output(SInt(32.W))
        
        val EXinstructionIn = Input(new Instruction)
        val EXcontrolSignalsIn = Input(new ControlSignals)
        
        val PCOut = Output(UInt(32.W))
        val instructionOut = Output(new Instruction)
        val controlSignalsOut = Output(new ControlSignals)
        val branchTypeOut     = Output(UInt(3.W))
        val op1SelectOut      = Output(UInt(1.W))
        val op2SelectOut      = Output(UInt(1.W))
        val immTypeOut        = Output(UInt(3.W))
        val ALUopOut          = Output(UInt(4.W))

        val readData1Out    = Output(UInt(32.W))
        val readData2Out    = Output(UInt(32.W))

        val stallOut    = Output(UInt(1.W))
    }
  )

  val registers = Module(new Registers)
  val decoder   = Module(new Decoder).io


  /**
    * Setup. You should not change this code
    */
  registers.testHarness.setup := testHarness.registerSetup
  testHarness.registerPeek    := registers.io.readData1
  testHarness.testUpdates     := registers.testHarness.testUpdates


  /**
    * TODO: Your code here.
    */
  io.PCOut := io.PCIn
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  registers.io.writeData    := io.writeData

  decoder.instruction := io.instruction
  io.controlSignalsOut := decoder.controlSignals
  io.branchTypeOut := decoder.branchType
  io.op1SelectOut := decoder.op1Select
  io.op2SelectOut := decoder.op2Select
  io.immTypeOut := decoder.immType
  io.ALUopOut := decoder.ALUop
  io.instructionOut := io.instruction

  io.immOut := MuxLookup(decoder.immType, 0.S(32.W), Array(
    ImmFormat.ITYPE -> io.instruction.immediateIType,
    ImmFormat.STYPE -> io.instruction.immediateSType,
    ImmFormat.BTYPE -> io.instruction.immediateBType,
    ImmFormat.UTYPE -> io.instruction.immediateUType,
    ImmFormat.JTYPE -> io.instruction.immediateJType,
    ImmFormat.DC -> io.instruction.immediateZType,
  ))

  when(io.writeAddress.===(io.instruction.registerRs1) && io.instruction.registerRs1.=/=(0.U) && io.writeEnable){
    io.readData1Out :=  io.writeData
  } .otherwise {
    io.readData1Out :=  registers.io.readData1
  }
  when(io.writeAddress.===(io.instruction.registerRs2) && io.instruction.registerRs2.=/=(0.U) && io.writeEnable){
    io.readData2Out :=  io.writeData
  } .otherwise {
    io.readData2Out :=  registers.io.readData2
  }
  
  io.stallOut := io.EXcontrolSignalsIn.memRead && (io.EXinstructionIn.registerRd.===(io.instruction.registerRs1) || io.EXinstructionIn.registerRd.===(io.instruction.registerRs2))

}
