package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

  val testHarness = IO(
    new Bundle {
      val setupSignals = Input(new SetupSignals)
      val testReadouts = Output(new TestReadouts)
      val regUpdates   = Output(new RegisterUpdates)
      val memUpdates   = Output(new MemUpdates)
      val currentPC    = Output(UInt(32.W))
    }
  )

  /**
    You need to create the classes for these yourself
    */
  val IFBarrier  = Module(new IFBarrier).io
  val IDBarrier  = Module(new IDBarrier).io
  val EXBarrier  = Module(new EXBarrier).io
  val MEMBarrier = Module(new MEMBarrier).io

  val IF  = Module(new InstructionFetch)
  val ID  = Module(new InstructionDecode)
  val EX  = Module(new Execute)
  val MEM = Module(new MemoryFetch)
  val BHT = Module(new BHT)
  // val WB  = Module(new Execute) (You may not need this one?)


  /**
    * Setup. You should not change this code
    */
  IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
  ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
  MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

  testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
  testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

  /**
    spying stuff
    */
  testHarness.regUpdates := ID.testHarness.testUpdates
  testHarness.memUpdates := MEM.testHarness.testUpdates
  testHarness.currentPC  := IF.testHarness.PC


  /**
    TODO: Your code here
    */
    IFBarrier.PCIn := IF.io.PC
    IFBarrier.instructionIn := IF.io.instruction
    IFBarrier.stallIn := ID.io.stallOut
    IFBarrier.isBranching := EX.io.isBranching
    IFBarrier.predictionIsTakenIn := BHT.io.predictionIsTaken
    
    ID.io.PCIn := IFBarrier.PCOut
    ID.io.instruction := IFBarrier.instructionOut
    ID.io.EXcontrolSignalsIn := EX.io.controlSignalsOut
    ID.io.EXinstructionIn := EX.io.instructionOut
    
    IDBarrier.instructionIn := ID.io.instructionOut
    IDBarrier.PCIn := ID.io.PCOut
    IDBarrier.PCBranchIn := ID.io.PCBranchOut
    IDBarrier.initPCBranchIn := ID.io.initPCBranchOut
    IDBarrier.predictionIsTakenIn := BHT.io.predictionIsTaken
    IDBarrier.controlSignals := ID.io.controlSignalsOut
    IDBarrier.branchType := ID.io.branchTypeOut
    IDBarrier.op1Select := ID.io.op1SelectOut
    IDBarrier.op2Select := ID.io.op2SelectOut
    IDBarrier.immType := ID.io.immTypeOut
    IDBarrier.ALUopIn := ID.io.ALUopOut
    IDBarrier.readData1 := ID.io.readData1Out
    IDBarrier.readData2 := ID.io.readData2Out
    IDBarrier.imm := ID.io.immOut
    IDBarrier.stallIn := ID.io.stallOut
    IDBarrier.isBranching := EXBarrier.isBranching
    
    EX.io.predictionIsTakenIn := IDBarrier.predictionIsTakenOut
    EX.io.PCIn := IDBarrier.PCOut
    EX.io.instructionIn := IDBarrier.instructionOut
    EX.io.readData1 := IDBarrier.readData1Out
    EX.io.readData2 := IDBarrier.readData2Out
    EX.io.imm := IDBarrier.immOut
    EX.io.controlSignals := IDBarrier.controlSignalsOut
    EX.io.branchType := IDBarrier.branchTypeOut
    EX.io.op1Select := IDBarrier.op1SelectOut
    EX.io.op2Select := IDBarrier.op2SelectOut
    EX.io.immType := IDBarrier.immTypeOut
    EX.io.ALUop := IDBarrier.ALUopOut

    EX.io.predictionIsWrong := IDBarrier.predictionIsTakenOut ^ EX.io.branchTaken
    EX.io.regAddressMEM := MEM.io.instructionOut.registerRd
    EX.io.regAddressWB := MEMBarrier.instructionOut.registerRd
    EX.io.signalMEM := EXBarrier.dataOut
    EX.io.regWriteMEM := MEM.io.controlSignalsOut.regWrite
    EX.io.regWriteWB := MEMBarrier.controlSignalsOut.regWrite
    when(MEMBarrier.controlSignalsOut.memRead) {
      EX.io.signalWB := MEM.io.memDataOut
    } otherwise {
      EX.io.signalWB := MEMBarrier.dataOut
    }
    
    EXBarrier.PCIn := EX.io.PCOut
    EXBarrier.instructionIn := EX.io.instructionOut
    EXBarrier.dataIn := EX.io.dataOut
    EXBarrier.controlSignals := EX.io.controlSignalsOut
    EXBarrier.branchType := EX.io.branchTypeOut
    EXBarrier.op1Select := EX.io.op1SelectOut
    EXBarrier.op2Select := EX.io.op2SelectOut
    EXBarrier.immType := EX.io.immTypeOut
    EXBarrier.ALUop := EX.io.ALUopOut
    EXBarrier.readData2In := EX.io.readData2Out
    EXBarrier.stallIn := ID.io.stallOut
    EXBarrier.isBranchingIn := EX.io.isBranching
    EXBarrier.branchTakenIn := EX.io.branchTaken
    EXBarrier.PCBranchIn := IDBarrier.PCBranchOut
    EXBarrier.initPCBranchIn := IDBarrier.initPCBranchOut

    IF.io.controlSignals := IDBarrier.controlSignalsOut
    IF.io.branchControlSignals := ID.io.controlSignalsOut
    IF.io.PCbranch := IDBarrier.PCBranchOut
    IF.io.initPCbranch := IDBarrier.initPCBranchOut
    IF.io.PCbranchFromID := ID.io.PCBranchOut
    IF.io.initPCbranchFromID := ID.io.initPCBranchOut
    IF.io.branchTaken := EX.io.branchTaken
    IF.io.PCjump := EX.io.PCOut
    IF.io.stallIn := ID.io.stallOut
    MEM.io.instructionIn := EXBarrier.instructionOut
    MEM.io.dataIn := EXBarrier.dataOut
    MEM.io.controlSignals := EXBarrier.controlSignalsOut
    MEM.io.readData2In := EXBarrier.readData2Out

    MEMBarrier.instructionIn := MEM.io.instructionOut
    MEMBarrier.dataIn := MEM.io.dataOut
    MEMBarrier.controlSignals := MEM.io.controlSignalsOut
    MEMBarrier.memDataIn := MEM.io.memDataOut
    MEMBarrier.stallIn := ID.io.stallOut

    ID.io.writeAddress := MEMBarrier.instructionOut.registerRd
    when(MEMBarrier.controlSignalsOut.memRead) {
      ID.io.writeData := MEM.io.memDataOut
    } otherwise {
      ID.io.writeData := MEMBarrier.dataOut
    }
    ID.io.writeEnable := MEMBarrier.controlSignalsOut.regWrite

    BHT.io.key := Mux(ID.io.controlSignalsOut.branch, IF.io.instruction.instruction(6,0).asUInt, "b0000000".U)
    BHT.io.predictionIsWrong := Mux(IDBarrier.predictionIsTakenOut ^ EX.io.branchTaken, IDBarrier.instructionOut.instruction(6,0).asUInt, "b0000000".U)
    IF.io.predictionIsTaken := BHT.io.predictionIsTaken
    IF.io.PCbranchFromEX := EX.io.PCOut
    IF.io.predictionIsWrong := IDBarrier.predictionIsTakenOut ^ EX.io.branchTaken
    IFBarrier.predictionIsWrong := IDBarrier.predictionIsTakenOut ^ EX.io.branchTaken
    IDBarrier.predictionIsWrong := IDBarrier.predictionIsTakenOut ^ EX.io.branchTaken
}
