package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import lookup._
import chisel3.util.MuxLookup

/**
  * This module is already done. Have one on me
  *  
  * When a write and read conflicts this might result in stale data.
  * This caveat must be handled using a bypass.
 */
class ALU() extends MultiIOModule {
  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction)
      val op1 = Input(UInt(32.W))
      val op2 = Input(UInt(32.W))
      // val imm = Input(SInt(32.W))
      val aluOp = Input(UInt(32.W))
      val aluResult = Output(UInt(32.W))
    })
    
    val ALUopMap = Seq(
    ALUOps.ADD    -> (io.op1 + io.op2),
    ALUOps.SUB    -> (io.op1 - io.op2),
    ALUOps.SLT    -> io.op1.asSInt().<(io.op2.asSInt()),
    ALUOps.SLTU    -> io.op1.<(io.op2),
    ALUOps.SLL    -> io.op1.<<(io.op2.asSInt()(4,0)),
    ALUOps.SRA    -> io.op1.asSInt.>>(io.op2.asSInt()(4,0)).asUInt,
    ALUOps.SRL    -> io.op1.>>(io.op2.asSInt()(4,0)),
    ALUOps.AND    -> io.op1.&(io.op2),
    ALUOps.OR    -> io.op1.|(io.op2),
    ALUOps.XOR    -> io.op1.^(io.op2),
    ALUOps.COPY_B    -> io.op2,
    ALUOps.COPY_A    -> io.op1,
    )



    // printf("%x + %x = %x\n", io.op1, io.op2, io.op1 + io.op2)
    // printf("jjj %b\n", io.op2.asSInt()(5,0))

    // val temp = Wire(SInt(32.W))
    // temp := io.op2.asSInt
    // registers.io.writeData    := temp.asUInt
    io.aluResult := MuxLookup(io.aluOp, 0.U(32.W), ALUopMap)
    // printf("op1 is %b\n", io.op1)
    // printf("op2 is %b\n", io.op2)
    // printf("io.aluOp is %d\n", io.aluOp)
    // printf("result is %x\n", MuxLookup(io.aluOp, 0.U(32.W), ALUopMap).asUInt)
    // printf("result is %x\n\n", MuxLookup(io.aluOp, 0.U(32.W), ALUopMap))
    // printf("io.aluResult is %x\n\n", io.aluResult)
    // printf("Instructions is %b\n\n", io.instructionIn.instruction)
}
