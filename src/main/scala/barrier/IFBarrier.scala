package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class IFBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
        val PCIn = Input(UInt(32.W))
        val instructionIn = Input(new Instruction)
        val instructionOut = Output(new Instruction)
        val PCOut = Output(UInt(32.W))

        val stallIn    = Input(UInt(1.W))
        val isBranching = Input(Bool())
        val controlSignals = Input(new ControlSignals)
    }
  )
  val PC = RegInit(0.U(32.W))
  val branchReg = RegInit(Reg(Bool()))
  // val instruction = RegInit(Reg(new Instruction))

  when(io.stallIn.===(1.U)) {
    PC := io.PCOut
  } .otherwise {
    PC := io.PCIn 
  }

  branchReg := io.isBranching

  when(io.controlSignals.jump || io.controlSignals.branch){
  // when(io.isBranching){
    printf("IFBarrier Branching is  %d\n", io.isBranching)
    io.instructionOut := Instruction.NOP
  } .otherwise {
    io.instructionOut := io.instructionIn
  }

  io.PCOut := PC
}