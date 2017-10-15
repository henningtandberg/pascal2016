package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

class ConstDecl extends PascalDecl {
    Constant    con;

    public ConstDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("constdecl");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);
        Main.log.prettyPrint(" = ");
        con.prettyPrint();
        Main.log.prettyPrintLn(";");
    }

    /*
    *   Saves decl to scope
    *   Saves type
    *   Saves declLevel
    */
    @Override
    public void check(Block curScope, Library lib) {
        curScope.addDecl(name, this);
        con.check(curScope, lib);
        type = con.type;

        declLevel = curScope.blockLevel();
    }

    @Override
    public void genCode(CodeFile f){
        con.genCode(f);
    }

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {
        where.error("You cannot assign to a constant");
    }

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error(name + " is a conant, not a function.");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error(name + " is a conant, not a procedure.");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {}

    public static ConstDecl parse(Scanner s) {
        enterParser("constdecl");

        ConstDecl cd = new ConstDecl(s.curToken.id, s.curLineNum());
        s.skip(nameToken); s.skip(equalToken);
        cd.con = Constant.parse(s);

        s.skip(semicolonToken);
        leaveParser("constdecl");
        return cd;
    }
}
