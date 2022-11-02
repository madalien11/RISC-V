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
        val controlSignalsOut = Output(new ControlSignals)

        val stallIn    = Input(UInt(1.W))
    }
  )

  val instruction = RegInit(UInt(32.W), 0.U)
  val data = RegInit(UInt(32.W), 0.U)
  val controlSignalsReg = RegInit(Reg(new ControlSignals))

  // when(io.stallIn.===(0.U)){
    instruction := io.instructionIn.instruction
    data := io.dataIn
    controlSignalsReg := io.controlSignals
  // }

    io.instructionOut := instruction.asTypeOf(new Instruction)
    io.dataOut := data
    io.controlSignalsOut := controlSignalsReg.asTypeOf(new ControlSignals)
    io.memDataOut := io.memDataIn
}
