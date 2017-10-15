package parser;

import main.*;
import types.*;
import scanner.*;
import types.*;
import static scanner.TokenKind.*;

class VarDecl extends PascalDecl {
    Type        parserType;

    public VarDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("vardecl");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);
        Main.log.prettyPrint(" : ");
        parserType.prettyPrint();
        Main.log.prettyPrintLn(";");
    }

    /*
    *   Saves decl to scope
    *   Saves type
    */
    @Override
    public void check(Block curScope, Library lib) {
        curScope.addDecl(name, this);
        parserType.check(curScope, lib);
        type = parserType.type;

        declLevel = curScope.blockLevel();
    }

    @Override
    public void genCode(CodeFile f){}

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {}

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error(name +  " is a variable, not a function.");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error(name +  " is a variable, not a procedure.");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {}

    public static VarDecl parse(Scanner s) {
        enterParser("vardecl");

        VarDecl vd = new VarDecl(s.curToken.id, s.curLineNum());
        s.skip(nameToken); s.skip(colonToken);
		vd.parserType = Type.parse(s);

        s.skip(semicolonToken);
        leaveParser("vardecl");
        return vd;
    }

}
