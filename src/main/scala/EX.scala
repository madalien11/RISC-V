package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
      /**
        * TODO: Your code here.
        */
        val PCIn = Input(UInt(32.W))
        val instructionIn = Input(new Instruction)
        val instructionOut = Output(new Instruction)
        val PCOut = Output(UInt(32.W))
        val dataOut = Output(UInt(32.W))
        val readData1    = Input(UInt(32.W))
        val readData2    = Input(UInt(32.W))
        val imm    = Input(SInt(32.W))

        val controlSignals = Input(new ControlSignals)
        val branchType     = Input(UInt(3.W))
        val op1Select      = Input(UInt(1.W))
        val op2Select      = Input(UInt(1.W))
        val immType        = Input(UInt(3.W))
        val ALUop          = Input(UInt(4.W))

        val controlSignalsOut = Output(new ControlSignals)
        val branchTypeOut     = Output(UInt(3.W))
        val op1SelectOut      = Output(UInt(1.W))
        val op2SelectOut      = Output(UInt(1.W))
        val immTypeOut        = Output(UInt(3.W))
        val ALUopOut          = Output(UInt(4.W))
        val readData2Out    = Output(UInt(32.W))

    }
  )

  val alu = Module(new ALU)

  /**
    * TODO: Your code here.
    */
    // printf("Instruction ALUop is %d\n", io.ALUop)
    // printf("registerRs1 is %d\n", io.instructionIn.registerRs1)
    // printf("immediateIType is %b\n\n", io.instructionIn.immediateIType.asUInt())
    // printf("immediateIType is %d\n\n", io.instruction.immediateIType)
    // alu.io.op1 := io.instructionIn.registerRs1
    
    // printf("io.op2Select %b\n", io.op2Select)
    // printf("alu.io.op2 %b\n", io.readData2)
    // printf("alu.io.op2.asSInt() %b\n", io.readData2.asSInt())
    // alu.io.op2 := io.readData2
    
    // alu.io.immediateIType := io.instruction.immediateIType
    alu.io.instructionIn := io.instructionIn
    alu.io.aluOp := io.ALUop
    
    when(io.controlSignals.jump && io.branchType.===(branchType.jump)) {
      alu.io.op1 := io.PCIn
      alu.io.op2 := io.imm.asUInt
      io.PCOut := alu.io.aluResult
      io.dataOut := io.PCIn + 4.U
    } .elsewhen(io.controlSignals.jump && io.branchType.===(branchType.jalr)){
      alu.io.op1 := io.readData1
      alu.io.op2 := io.imm.asUInt
      io.PCOut := alu.io.aluResult.&("hfffffffe".asUInt(32.W)) 
      io.dataOut := io.PCIn + 4.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.beq)){
      alu.io.op1 := io.PCIn
      when(io.readData1.===(io.readData2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.neq)) {
      alu.io.op1 := io.PCIn
      when(io.readData1.=/=(io.readData2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.lt)) {
      alu.io.op1 := io.PCIn
      when(io.readData1.<(io.readData2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.gte)) {
      alu.io.op1 := io.PCIn
      when(io.readData1.>(io.readData2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.ltu)) {
      alu.io.op1 := io.PCIn
      when(io.readData1.asUInt.<(io.readData2.asUInt)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.gteu)) {
      alu.io.op1 := io.PCIn
      when(io.readData1.asUInt.>(io.readData2.asUInt)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.dataOut := 0.U
    } .otherwise {
      alu.io.op1 := io.readData1
      when(io.op2Select.===(0.asUInt)){
        alu.io.op2 := io.readData2
      } otherwise {
        alu.io.op2 := io.imm.asUInt
      }
      io.PCOut := io.PCIn
      io.dataOut := alu.io.aluResult
    }
    // printf("alu.io.aluResult %b\n", alu.io.aluResult)
    // printf("io.dataOut %x\n", io.dataOut)
    
    io.instructionOut := io.instructionIn

    io.controlSignalsOut := io.controlSignals
    io.branchTypeOut := io.branchType
    io.op1SelectOut := io.op1Select
    io.op2SelectOut := io.op2Select
    io.immTypeOut := io.immType
    io.ALUopOut := io.ALUop
    io.readData2Out := io.readData2
}
