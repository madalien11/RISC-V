package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
        val PCIn = Input(UInt(32.W))
        val PCOut = Output(UInt(32.W))
        val instructionIn = Input(new Instruction)
        val instructionOut = Output(new Instruction)

        val controlSignals = Input(new ControlSignals)
        val branchType     = Input(UInt(3.W))
        val op1Select      = Input(UInt(1.W))
        val op2Select      = Input(UInt(1.W))
        val immType        = Input(UInt(3.W))
        val ALUopIn        = Input(UInt(4.W))

        val imm          = Input(SInt(32.W))
        val readData1    = Input(UInt(32.W))
        val readData2    = Input(UInt(32.W))

        val controlSignalsOut = Output(new ControlSignals)
        val branchTypeOut     = Output(UInt(3.W))
        val op1SelectOut      = Output(UInt(1.W))
        val op2SelectOut      = Output(UInt(1.W))
        val immTypeOut        = Output(UInt(3.W))
        val ALUopOut          = Output(UInt(4.W))

        val immOut          = Output(SInt(32.W))
        val readData1Out    = Output(UInt(32.W))
        val readData2Out    = Output(UInt(32.W))
    }
  )

  val instruction = RegInit(UInt(32.W), 0.U)
  instruction := io.instructionIn.instruction
  io.instructionOut := instruction.asTypeOf(new Instruction)

  val PC = RegInit(0.U(32.W))
  PC := io.PCIn
  io.PCOut := PC

  val controlSignalsReg = RegInit(Reg(new ControlSignals))
  controlSignalsReg := io.controlSignals
  io.controlSignalsOut := controlSignalsReg.asTypeOf(new ControlSignals)

  val branchTypeReg = RegInit(UInt(3.W), 0.U)
  branchTypeReg := io.branchType
  io.branchTypeOut := branchTypeReg
  
  val op1SelectReg = RegInit(UInt(1.W), 0.U)
  op1SelectReg := io.op1Select
  io.op1SelectOut := op1SelectReg
  
  val op2SelectReg = RegInit(UInt(1.W), 0.U)
  op2SelectReg := io.op2Select
  io.op2SelectOut := op2SelectReg
  
  val immTypeReg = RegInit(UInt(3.W), 0.U)
  immTypeReg := io.immType
  io.immTypeOut := immTypeReg
  
  val ALUopReg = RegInit(UInt(4.W), 0.U)
  ALUopReg := io.ALUopIn
  io.ALUopOut := ALUopReg
  
  val readData1Reg = RegInit(UInt(32.W), 0.U)
  readData1Reg := io.readData1
  io.readData1Out := readData1Reg
  
  val readData2Reg = RegInit(UInt(32.W), 0.U)
  readData2Reg := io.readData2
  io.readData2Out := readData2Reg
  
  val immReg = RegInit(SInt(32.W), 0.S)
  immReg := io.imm
  io.immOut := immReg

}
