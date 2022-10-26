package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class EXBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
        val PCIn = Input(UInt(32.W))
        val instructionIn = Input(new Instruction)
        val instructionOut = Output(new Instruction)
        val PCOut = Output(UInt(32.W))
        val dataIn = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))

        val controlSignals = Input(new ControlSignals)
        val branchType     = Input(UInt(3.W))
        val op1Select      = Input(UInt(1.W))
        val op2Select      = Input(UInt(1.W))
        val immType        = Input(UInt(3.W))
        val ALUop          = Input(UInt(4.W))
        val readData2In    = Input(UInt(32.W))

        val controlSignalsOut = Output(new ControlSignals)
        val branchTypeOut     = Output(UInt(3.W))
        val op1SelectOut      = Output(UInt(1.W))
        val op2SelectOut      = Output(UInt(1.W))
        val immTypeOut        = Output(UInt(3.W))
        val ALUopOut          = Output(UInt(4.W))
        val readData2Out    = Output(UInt(32.W))
    }
  )

  val instruction = RegInit(UInt(32.W), 0.U)
  instruction := io.instructionIn.instruction
  io.instructionOut := instruction.asTypeOf(new Instruction)
  
  val data = RegInit(UInt(32.W), 0.U)
  data := io.dataIn
  io.dataOut := data
  
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
  ALUopReg := io.ALUop
  io.ALUopOut := ALUopReg

  val readData2Reg = RegInit(UInt(32.W), 0.U)
  readData2Reg := io.readData2In
  io.readData2Out := readData2Reg

}
