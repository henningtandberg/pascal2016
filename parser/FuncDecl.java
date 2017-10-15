package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

class FuncDecl extends ProcDecl {

    ParamDeclList   pDeclList;
    Type            parserType;
    Block           block;
    String          label;

    public FuncDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("funcdecl");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("\nfunction ");
        Main.log.prettyPrint(name);

        if(pDeclList != null)
            pDeclList.prettyPrint();

        Main.log.prettyPrint(" : ");
        parserType.prettyPrint();
        Main.log.prettyPrint(";");
        block.prettyPrint();
        Main.log.prettyPrintLn(";");
    }

    /* Generates func code */
    @Override
    public void genCode(CodeFile f) {
        label = "func$"+f.getLabel(name);

        block.genFuncProcCode(f);

        f.genInstr(label, "", "", "");
        f.genInstr("", "enter", "$"+(32+block.declearedBytes())+",$"+
                                        declLevel, label + " start");
        block.genCode(f);
        f.genInstr("", "movl", "-32(%ebp),%eax", "");
        f.genInstr("","leave","","");
        f.genInstr("","ret","", label + " end");
    }

    /*
    *   Saves decl to scope
    *   Saves type
    *   Checks if there are any params
    */
    @Override
    public void check(Block curScope, Library lib) {
        curScope.addDecl(name, this);

        parserType.check(curScope, lib);
        type = parserType.type;

        block.outerScope = curScope; // Rot
        //block.offset = curScope.offset;
        if( pDeclList != null )
            pDeclList.check(block, lib);

        block.check(curScope, lib);
        declLevel = block.blockLevel();
        block.offset = declOffset;
    }

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {}

    @Override
    public void checkWhetherFunction(PascalSyntax where) {}

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error(name + " is a function, not a procedure.");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {}

    public static FuncDecl parse(Scanner s) {
        enterParser("funcdecl");
        s.skip(functionToken);

        s.test(nameToken);
        FuncDecl fd = new FuncDecl(s.curToken.id, s.curLineNum());
        s.skip(nameToken);

        if( isParameters(s) ) {
            fd.pDeclList = ParamDeclList.parse(s);
        }

        s.skip(colonToken);
        fd.parserType   = Type.parse(s);   s.skip(semicolonToken);
        fd.block        = Block.parse(s);

        s.skip(semicolonToken);
        leaveParser("funcdecl");
        return fd;
    }

    /**
        Returns true if more leftParToken
        which means that there are parameters.
    */
    private static boolean isParameters(Scanner s) {
        return (s.curToken.kind == leftParToken);
    }

}
