package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule

class MEMBarrier extends MultiIOModule {

  val io = IO(
    new Bundle {
        val instructionIn = Input(new Instruction)
        val instructionOut = Output(new Instruction)
        val dataIn = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
        val memDataIn = Input(UInt(32.W))
        val memDataOut = Output(UInt(32.W))

        val controlSignals = Input(new ControlSignals)
        // val branchType     = Input(UInt(3.W))
        // val op1Select      = Input(UInt(1.W))
        // val op2Select      = Input(UInt(1.W))
        // val immType        = Input(UInt(3.W))
        // val ALUop          = Input(UInt(4.W))

        val controlSignalsOut = Output(new ControlSignals)
        // val branchTypeOut     = Output(UInt(3.W))
        // val op1SelectOut      = Output(UInt(1.W))
        // val op2SelectOut      = Output(UInt(1.W))
        // val immTypeOut        = Output(UInt(3.W))
        // val ALUopOut          = Output(UInt(4.W))
    }
  )
  // printf("Output is %d\n", io.dataIn)
  val instruction = RegInit(UInt(32.W), 0.U)
  instruction := io.instructionIn.instruction
  io.instructionOut := instruction.asTypeOf(new Instruction)
  
  val data = RegInit(UInt(32.W), 0.U)
  data := io.dataIn
  io.dataOut := data

  val controlSignalsReg = RegInit(Reg(new ControlSignals))
  controlSignalsReg := io.controlSignals
  io.controlSignalsOut := controlSignalsReg.asTypeOf(new ControlSignals)

  io.memDataOut := io.memDataIn
  // val memData = RegInit(UInt(32.W), 0.U)
  // memData := io.memDataIn
  // io.memDataOut := memData

//   io.controlSignalsOut := io.controlSignals
//   io.branchTypeOut := io.branchType
//   io.op1SelectOut := io.op1Select
//   io.op2SelectOut := io.op2Select
//   io.immTypeOut := io.immType
//   io.ALUopOut := io.ALUop

}
