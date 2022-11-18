package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class IDBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
        val PCIn = Input(UInt(32.W))
        val PCOut = Output(UInt(32.W))
        val PCBranchIn = Input(UInt(32.W))
        val PCBranchOut = Output(UInt(32.W))
        val initPCBranchIn = Input(UInt(32.W))
        val initPCBranchOut = Output(UInt(32.W))
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

        val stallIn    = Input(UInt(1.W))
        val isBranching = Input(Bool())
        val predictionIsWrong = Input(Bool())
        val predictionIsTakenIn    = Input(Bool())
        val predictionIsTakenOut   = Output(Bool())

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

  // val predictionIsTaken1 = RegInit(Reg(Bool()))
  val instruction = RegInit(Reg(new Instruction))
  val PC = RegInit(0.U(32.W))
  val PCBranch = RegInit(0.U(32.W))
  val initPCBranch = RegInit(0.U(32.W))
  val predictionIsTaken = RegInit(Reg(Bool()))  
  val controlSignalsReg = RegInit(Reg(new ControlSignals))
  val branchTypeReg = RegInit(UInt(3.W), 0.U)
  val op1SelectReg = RegInit(UInt(1.W), 0.U)
  val op2SelectReg = RegInit(UInt(1.W), 0.U)
  val immTypeReg = RegInit(UInt(3.W), 0.U)
  val ALUopReg = RegInit(UInt(4.W), 0.U)
  val readData1Reg = RegInit(UInt(32.W), 0.U)
  val readData2Reg = RegInit(UInt(32.W), 0.U)
  val immReg = RegInit(SInt(32.W), 0.S)
  val branchReg = RegInit(Reg(Bool()))  
  branchReg := io.isBranching

  when(io.stallIn.===(1.U) || io.isBranching) {
    controlSignalsReg := ControlSignals.nop
    PC := io.PCIn
    PCBranch := io.PCBranchIn
    initPCBranch := io.initPCBranchIn
    predictionIsTaken := io.predictionIsTakenIn
    instruction := Instruction.NOP
    branchTypeReg := 0.U
    op1SelectReg := 0.U
    op2SelectReg := 0.U
    immTypeReg := 0.U
    ALUopReg := 15.U
    readData1Reg := 0.U
    readData2Reg := 0.U
    immReg := 0.S
  } .otherwise {
    instruction := io.instructionIn
    PC := io.PCIn
    PCBranch := io.PCBranchIn
    initPCBranch := io.initPCBranchIn
    predictionIsTaken := io.predictionIsTakenIn
    controlSignalsReg := io.controlSignals
    branchTypeReg := io.branchType
    op1SelectReg := io.op1Select
    op2SelectReg := io.op2Select
    immTypeReg := io.immType
    ALUopReg := io.ALUopIn
    readData1Reg := io.readData1
    readData2Reg := io.readData2
    immReg := io.imm
  }
  
  io.instructionOut := instruction.asTypeOf(new Instruction)
  io.PCOut := PC
  io.PCBranchOut := PCBranch
  io.initPCBranchOut := initPCBranch
  io.predictionIsTakenOut := predictionIsTaken
  io.controlSignalsOut := controlSignalsReg.asTypeOf(new ControlSignals)
  io.branchTypeOut := branchTypeReg
  io.op1SelectOut := op1SelectReg
  io.op2SelectOut := op2SelectReg
  io.immTypeOut := immTypeReg
  io.ALUopOut := ALUopReg
  io.readData1Out := readData1Reg
  io.readData2Out := readData2Reg
  io.immOut := immReg

}
