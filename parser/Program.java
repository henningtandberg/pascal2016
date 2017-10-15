package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class Program extends PascalDecl {
    Block progBlock;

    public Program(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("program");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("\nprogram ");
        Main.log.prettyPrintLn(name + ";");
        progBlock.prettyPrint();
        Main.log.prettyPrintLn(".");
    }

    @Override
    public void check(Block curScope, Library lib) {
        progBlock.check(curScope, lib);
    }

    /* Generates main
    *  Generates a function of the program
    */
    @Override
    public void genCode(CodeFile f){
        String label = "prog$"+f.getLabel(name);

        //Main
        f.genInstr("", ".globl  main","","");
        f.genInstr("main", "", "", "");
        f.genInstr("", "call", label, "Start main");
        f.genInstr("", "movl", "$0,%eax", "");
        f.genInstr("", "ret", "", "#End main");

        progBlock.genFuncProcCode(f);

        //Prog
        f.genInstr(label, "", "", "");
        f.genInstr("", "enter", "$"+(32+progBlock.declearedBytes())+",$"+
                                progBlock.blockLevel() , "Start " + label);
        progBlock.genCode(f);
        f.genInstr("","leave","","");
        f.genInstr("","ret","", "End " + label);
    }

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {
        where.error("You cannot assign to a program.");
    }

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error("You cannot call a program as a function.");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error("You cannot call a program as a procedure.");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {
        where.error("You cannot expect a value from program decl."); //
    }

    public static Program parse(Scanner s) {
        enterParser("program");
        s.skip(programToken);
        s.test(nameToken);

        Program p = new Program(s.curToken.id, s.curLineNum());
        s.skip(nameToken);
        s.skip(semicolonToken);
        p.progBlock = Block.parse(s); p.progBlock.context = p;
        s.skip(dotToken);

        leaveParser("program");
        return p;
    }

}
