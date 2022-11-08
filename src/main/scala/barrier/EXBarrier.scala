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

        val stallIn    = Input(UInt(1.W))
        val isBranching = Input(Bool())
        val isBranchingOut = Output(Bool())

        val controlSignalsOut = Output(new ControlSignals)
        val branchTypeOut     = Output(UInt(3.W))
        val op1SelectOut      = Output(UInt(1.W))
        val op2SelectOut      = Output(UInt(1.W))
        val immTypeOut        = Output(UInt(3.W))
        val ALUopOut          = Output(UInt(4.W))
        val readData2Out    = Output(UInt(32.W))
    }
  )
  val instruction = RegInit(Reg(new Instruction))
  val data = RegInit(UInt(32.W), 0.U)
  val PC = RegInit(0.U(32.W))
  val controlSignalsReg = RegInit(Reg(new ControlSignals))
  val branchTypeReg = RegInit(UInt(3.W), 0.U)
  val op1SelectReg = RegInit(UInt(1.W), 0.U)
  val op2SelectReg = RegInit(UInt(1.W), 0.U)
  val immTypeReg = RegInit(UInt(3.W), 0.U)
  val ALUopReg = RegInit(UInt(4.W), 0.U)
  val readData2Reg = RegInit(UInt(32.W), 0.U)
  val branchReg = RegInit(Reg(Bool()))
  // branchReg := io.isBranching
  // io.isBranchingOut := branchReg
  io.isBranchingOut := io.isBranching
  
  // when(io.isBranching){
  //   controlSignalsReg := ControlSignals.nop
  //   instruction := Instruction.NOP
  //   branchTypeReg := 0.U
  //   op1SelectReg := 0.U
  //   op2SelectReg := 0.U
  //   immTypeReg := 0.U
  //   ALUopReg := 15.U
  //   // readData1Reg := 0.U
  //   readData2Reg := 0.U
  //   // immReg := 0.S
  // } .otherwise {
    instruction := io.instructionIn
    controlSignalsReg := io.controlSignals    
    branchTypeReg := io.branchType    
    op1SelectReg := io.op1Select    
    op2SelectReg := io.op2Select    
    immTypeReg := io.immType    
    ALUopReg := io.ALUop    
    readData2Reg := io.readData2In 
  // }
  data := io.dataIn
  PC := io.PCIn

  io.instructionOut := instruction.asTypeOf(new Instruction)
  io.dataOut := data
  io.PCOut := PC
  io.controlSignalsOut := controlSignalsReg.asTypeOf(new ControlSignals)
  io.branchTypeOut := branchTypeReg
  io.op1SelectOut := op1SelectReg
  io.op2SelectOut := op2SelectReg
  io.immTypeOut := immTypeReg
  io.ALUopOut := ALUopReg
  io.readData2Out := readData2Reg
}
