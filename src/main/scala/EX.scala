package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule
import chisel3.util.MuxLookup

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
        val branchTaken    = Output(Bool())
        val predictionIsTakenIn    = Input(Bool())
        val predictionIsWrong   = Input(Bool())
        // val PCBranchingOut = Output(UInt(32.W))

    }
  )
  val alu = Module(new ALU)

  /**
    * TODO: Your code here.
    */

  val rs1 = Wire(UInt(32.W))
  val rs2 = Wire(UInt(32.W))

  alu.io.instructionIn := io.instructionIn
  alu.io.aluOp := io.ALUop
  when(io.instructionIn.registerRs1.=/=(io.regAddressMEM) && io.instructionIn.registerRs1.===(io.regAddressWB) && io.regWriteWB) {
    rs1 := io.signalWB
  } .elsewhen(io.instructionIn.registerRs1.===(io.regAddressMEM) && io.regWriteMEM) {
    rs1 := io.signalMEM
  // } .elsewhen(io.regAddressMEM) {
    // read/write $zero
  } .otherwise {
    rs1 := io.readData1
  }
  
  when(io.instructionIn.registerRs2.=/=(io.regAddressMEM) && io.instructionIn.registerRs2.===(io.regAddressWB) && io.regWriteWB) {
    rs2 := io.signalWB
  } .elsewhen(io.instructionIn.registerRs2.===(io.regAddressMEM) && io.regWriteMEM) {
    rs2 := io.signalMEM
  } .otherwise {
    rs2 := io.readData2
  }

  alu.io.op1 := Mux((io.controlSignals.jump || io.controlSignals.branch) && io.branchType.=/=(branchType.jalr), io.PCIn, rs1)
  io.PCOut := Mux((io.controlSignals.jump || io.controlSignals.branch) && io.branchType.=/=(branchType.jalr), alu.io.aluResult, Mux(io.branchType.===(branchType.jalr), alu.io.aluResult.&("hfffffffe".asUInt(32.W)), io.PCIn))
  io.isBranching := Mux(io.controlSignals.jump || io.controlSignals.branch, io.PCIn.=/=(alu.io.aluResult), false.B)
  io.dataOut := Mux(io.controlSignals.jump, io.PCIn + 4.U, Mux(io.controlSignals.branch, 0.U, alu.io.aluResult))
  when(io.controlSignals.jump || io.controlSignals.branch) {
    alu.io.op2 := MuxLookup(io.branchType, 0.U, Array(
      branchType.jump -> io.imm.asUInt, 
      branchType.jalr -> io.imm.asUInt, 
      branchType.beq -> Mux(rs1.===(rs2), io.imm.asUInt, 4.U), 
      branchType.neq -> Mux(rs1.=/=(rs2), io.imm.asUInt, 4.U), 
      branchType.lt -> Mux(rs1.<(rs2), io.imm.asUInt, 4.U), 
      branchType.gte -> Mux(rs1.>(rs2), io.imm.asUInt, 4.U), 
      branchType.ltu -> Mux(rs1.asUInt.<(rs2.asUInt), io.imm.asUInt, 4.U), 
      branchType.gteu -> Mux(rs1.asUInt.>(rs2.asUInt), io.imm.asUInt, 4.U), 
    ))
  } .otherwise {
    alu.io.op2 := Mux(io.op2Select.=/=(0.asUInt), io.imm.asUInt, rs2)
  }
  // alu.io.op1 := Mux(io.controlSignals.jump && io.branchType.=/=(branchType.jalr), io.PCIn, rs1)
  // io.PCOut := Mux(io.controlSignals.jump && io.branchType.=/=(branchType.jalr), alu.io.aluResult, Mux(io.branchType.===(branchType.jalr), alu.io.aluResult.&("hfffffffe".asUInt(32.W)), io.PCIn))
  // io.isBranching := io.branchTaken || Mux(io.controlSignals.jump, io.PCIn.=/=(alu.io.aluResult), false.B)
  // io.dataOut := Mux(io.controlSignals.jump, io.PCIn + 4.U, alu.io.aluResult)
  // when(io.controlSignals.jump) {
  //   alu.io.op2 := io.imm.asUInt
  // } .otherwise {
  //   alu.io.op2 := Mux(io.op2Select.=/=(0.asUInt), io.imm.asUInt, rs2)
  // }
  io.branchTaken := io.controlSignals.branch && MuxLookup(io.branchType, false.B, Array(
    branchType.beq -> rs1.===(rs2),
    branchType.neq -> rs1.=/=(rs2),
    branchType.lt -> rs1.<(rs2),
    branchType.gte -> rs1.>(rs2),
    branchType.ltu -> rs1.asUInt.<(rs2.asUInt),
    branchType.gteu -> rs1.asUInt.>(rs2.asUInt),
  ))

  io.instructionOut := io.instructionIn
  io.controlSignalsOut := io.controlSignals
  io.branchTypeOut := io.branchType
  io.op1SelectOut := io.op1Select
  io.op2SelectOut := io.op2Select
  io.immTypeOut := io.immType
  io.ALUopOut := io.ALUop
  io.readData2Out := rs2

}
