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
  // printf("registerRs1 is %d\n", io.instruction.registerRs1)
  // printf("immediateIType is %d\n\n", io.instruction.immediateIType)
  registers.io.readAddress1 := io.instruction.registerRs1
  registers.io.readAddress2 := io.instruction.registerRs2
  //TODO: check these stuff
  registers.io.writeEnable  := io.writeEnable
  registers.io.writeAddress := io.writeAddress
  // printf("write data is %d\n", io.writeData)
  // val temp = Wire(SInt(32.W))
  // temp := io.writeData(11,0).asSInt
  // registers.io.writeData    := temp.asUInt
  registers.io.writeData    := io.writeData

  decoder.instruction := io.instruction

  io.controlSignalsOut := decoder.controlSignals
  io.branchTypeOut := decoder.branchType
  io.op1SelectOut := decoder.op1Select
  io.op2SelectOut := decoder.op2Select
  io.immTypeOut := decoder.immType
  io.ALUopOut := decoder.ALUop
  io.instructionOut := io.instruction

  io.readData1Out := registers.io.readData1
  io.readData2Out := registers.io.readData2
  // when(decoder.op2Select.===(0.asUInt)){
  //     io.readData2Out := registers.io.readData2
  //   } otherwise {
  //     val temp = Wire(SInt(32.W))
  //     temp := io.instruction.immediateIType(11,0).asSInt
  //     io.readData2Out := temp.asUInt
  //   }
  
  // printf("ALUop is %d\n", decoder.ALUop)
  // printf("immediateIType is %d\n", decoder.instruction.immediateIType)
  // printf("imm is %d\n", decoder.immType)
  // printf("op2Select is %d\n\n", decoder.op2Select)

  // io.imm := ImmFormat.ITYPE
  // io.imm := MuxLookup(decoder.immType, ImmFormat.DC, Array(
  //   ImmFormat.ITYPE -> io.instruction.instruction(31, 20).asSInt,
  //   ImmFormat.STYPE -> Cat(io.instruction.instruction(31, 25), io.instruction.instruction(11,7)).asSInt,
  //   ImmFormat.BTYPE -> Cat(io.instruction.instruction(31), io.instruction.instruction(7), io.instruction.instruction(30, 25), io.instruction.instruction(11, 8), 0.U(1.W)).asSInt,
  //   ImmFormat.UTYPE -> Cat(io.instruction.instruction(31, 12), 0.U(12.W)).asSInt,
  //   ImmFormat.JTYPE -> Cat(io.instruction.instruction(31), io.instruction.instruction(19, 12), io.instruction.instruction(20), io.instruction.instruction(30, 25), io.instruction.instruction(24, 21), 0.U(1.W)).asSInt,
  //   ImmFormat.SHAMT -> io.instruction.instruction(19, 15).zext,
  // ))
  io.immOut := MuxLookup(decoder.immType, 0.S(32.W), Array(
    ImmFormat.ITYPE -> io.instruction.immediateIType,
    ImmFormat.STYPE -> io.instruction.immediateSType,
    ImmFormat.BTYPE -> io.instruction.immediateBType,
    ImmFormat.UTYPE -> io.instruction.immediateUType,
    ImmFormat.JTYPE -> io.instruction.immediateJType,
    ImmFormat.DC -> io.instruction.immediateZType,
  ))


}
