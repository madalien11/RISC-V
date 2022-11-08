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
        
        val regAddressMEM    = Input(UInt(32.W))
        val regAddressWB     = Input(UInt(32.W))
        val signalMEM        = Input(UInt(32.W))
        val signalWB         = Input(UInt(32.W))
        val regWriteMEM      = Input(Bool())
        val regWriteWB       = Input(Bool())

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

        val isBranching    = Output(Bool())
        // val PCBranchingOut = Output(UInt(32.W))

    }
  )
  // printf("to check if controlSignals always starts as false %d\n", io.controlSignals.branch || io.controlSignals.jump)
  val alu = Module(new ALU)

  /**
    * TODO: Your code here.
    */
    // printf("io.EXinstructionIn.registerRd %d\n", io.instructionIn.registerRd)
    // printf("io.EXinstructionIn.registerRs1 %d\n", io.instructionIn.registerRs1)
    // printf("io.EXinstructionIn.registerRs2 %d\n", io.instructionIn.registerRs2)
    // printf("EX.io.regAddressMEM %d\n", io.regAddressMEM)
    // printf("EX.io.regAddressWB %d\n", io.regAddressWB)
    // printf("EX.io.signalMEM %x\n", io.signalMEM)
    // printf("EX.io.signalWB %x\n", io.signalWB)

    val rs1 = Wire(UInt(32.W))
    val rs2 = Wire(UInt(32.W))

    alu.io.instructionIn := io.instructionIn
    alu.io.aluOp := io.ALUop
    /*
    when((io.instructionIn.registerRs1.=/=(io.regAddressMEM) || ~io.regWriteMEM ) && (io.instructionIn.registerRs1.=/=(io.regAddressWB) || ~io.regWriteWB)) {
      rs1 := io.readData1
    } .elsewhen */
    when(io.instructionIn.registerRs1.=/=(io.regAddressMEM) && io.instructionIn.registerRs1.===(io.regAddressWB) && io.regWriteWB){
      rs1 := io.signalWB
    } .elsewhen(io.instructionIn.registerRs1.===(io.regAddressMEM) && io.regWriteMEM){
      rs1 := io.signalMEM
    // } .elsewhen(io.regAddressMEM) {
      // read/write $zero
    } .otherwise {
      rs1 := io.readData1
    }
    
    when(io.instructionIn.registerRs2.=/=(io.regAddressMEM) && io.instructionIn.registerRs2.===(io.regAddressWB) && io.regWriteWB){
      rs2 := io.signalWB
    }
    //  .elsewhen(io.instructionIn.registerRs2.=/=(io.regAddressMEM) && io.instructionIn.registerRs2.=/=(io.regAddressWB) && io.regWriteWB) {
    //   rs2 := io.readData2
    // } 
    .elsewhen(io.instructionIn.registerRs2.===(io.regAddressMEM) && io.regWriteMEM){
      rs2 := io.signalMEM
    } .otherwise {
      rs2 := io.readData2
    }

   
    
    when(io.controlSignals.jump && io.branchType.===(branchType.jump)) {
      alu.io.op1 := io.PCIn
      alu.io.op2 := io.imm.asUInt
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := io.PCIn + 4.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.jump && io.branchType.===(branchType.jalr)){
      alu.io.op1 := rs1
      alu.io.op2 := io.imm.asUInt
      io.PCOut := alu.io.aluResult.&("hfffffffe".asUInt(32.W))
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := io.PCIn + 4.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.beq)){
      alu.io.op1 := io.PCIn
      when(rs1.===(rs2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.neq)) {
      alu.io.op1 := io.PCIn
      when(rs1.=/=(rs2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.lt)) {
      alu.io.op1 := io.PCIn
      when(rs1.<(rs2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.gte)) {
      alu.io.op1 := io.PCIn
      when(rs1.>(rs2)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.ltu)) {
      alu.io.op1 := io.PCIn
      when(rs1.asUInt.<(rs2.asUInt)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .elsewhen(io.controlSignals.branch && io.branchType.===(branchType.gteu)) {
      alu.io.op1 := io.PCIn
      when(rs1.asUInt.>(rs2.asUInt)) {
        alu.io.op2 := io.imm.asUInt
      } otherwise{
        alu.io.op2 := 4.U
      }
      io.PCOut := alu.io.aluResult
      io.isBranching := io.PCIn.=/=(alu.io.aluResult)
      io.dataOut := 0.U
      io.readData2Out := rs2
    } .otherwise {

      alu.io.op1 := rs1
      when(io.op2Select.=/=(0.asUInt)){
        alu.io.op2 := io.imm.asUInt
      } .otherwise {
        alu.io.op2 := rs2
      }
     
      io.PCOut := io.PCIn
      io.isBranching := false.B
      io.dataOut := alu.io.aluResult
      
    }
    
    io.instructionOut := io.instructionIn

    io.controlSignalsOut := io.controlSignals
    io.branchTypeOut := io.branchType
    io.op1SelectOut := io.op1Select
    io.op2SelectOut := io.op2Select
    io.immTypeOut := io.immType
    io.ALUopOut := io.ALUop
    //  when(io.instructionIn.registerRs2.=/=(io.regAddressMEM) && io.instructionIn.registerRs2.===(io.regAddressWB)){
    //   io.readData2Out := io.signalWB
    // } .elsewhen(io.instructionIn.registerRs2.=/=(io.regAddressMEM) && io.instructionIn.registerRs2.=/=(io.regAddressWB)) {
    //   io.readData2Out := io.readData2
    // } .elsewhen(io.instructionIn.registerRs2.===(io.regAddressMEM)){
    //   io.readData2Out := io.signalMEM
    // } .otherwise {
    // }
      io.readData2Out := rs2

  // printf("instruction is  %b\n", io.instructionIn.opcode)
  // printf("registerRs1 is  %b\n", io.instructionIn.registerRs1)
  // printf("registerRs2 is  %b\n", io.instructionIn.registerRs2)
  // printf("registerRd  is  %b\n", io.instructionIn.registerRd)
  // printf("EXBranching is  %d\n", io.isBranching)
}
