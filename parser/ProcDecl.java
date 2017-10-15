package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class ProcDecl extends PascalDecl {

    ParamDeclList pDeclList;
    Block         block;
    String        label;

    public ProcDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("procdecl");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("\nprocedure ");
        Main.log.prettyPrint(name);

        if(pDeclList != null)
            pDeclList.prettyPrint();

        Main.log.prettyPrint(";");
        block.prettyPrint();
        Main.log.prettyPrintLn(";");
    }

    /*
    *   Saves decl to scope
    */
    @Override
    public void check(Block curScope, Library lib) {
        curScope.addDecl(name, this);
        block.outerScope = curScope; // Rot
        if( pDeclList != null )
            pDeclList.check(block, lib);

        block.check(curScope, lib);
        declLevel = block.blockLevel();
        block.offset = declOffset;
    }

    /* Generates proc code */
    @Override
    public void genCode(CodeFile f) {
        label = "proc$"+f.getLabel(name);

        block.genFuncProcCode(f);

        f.genInstr(label, "", "", "");
        f.genInstr("", "enter", "$"+(32+block.declearedBytes())+",$"+
                                block.blockLevel(), label + " start");
        block.genCode(f);
        f.genInstr("","leave","","");
        f.genInstr("","ret","", label + " end");
    }

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {
        where.error("You cannot assign to a procedure.");
    }

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error(name + " is a procedure, not a function.");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {}

    @Override
    public void checkWhetherValue(PascalSyntax where) {
        where.error(name + " is a procedure, and has no return value.");
    }

    public static ProcDecl parse(Scanner s) {
        enterParser("procdecl");
        s.skip(procedureToken);

        s.test(nameToken);
        ProcDecl pd = new ProcDecl(s.curToken.id, s.curLineNum());
        s.skip(nameToken);

        if( isParameters(s) ) {
            pd.pDeclList = ParamDeclList.parse(s);
        }

        s.skip(semicolonToken);
        pd.block = Block.parse(s);

        s.skip(semicolonToken);
        leaveParser("procdecl");
        return pd;
    }

    /**
        Checks is there are parameters
    */
    private static boolean isParameters(Scanner s) {
        return (s.curToken.kind == leftParToken);
    }
}
