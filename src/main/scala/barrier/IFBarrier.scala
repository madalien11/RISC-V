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
    }
  )

  io.instructionOut := io.instructionIn

  val PC = RegInit(0.U(32.W))

  PC := io.PCIn
  io.PCOut := PC
  
  // printf("PC in is %x, PC out is %x\n", io.PCIn, io.PCOut)
}
