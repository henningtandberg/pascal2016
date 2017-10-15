package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class ParamDecl extends PascalDecl {
    Type        parserType;

    public ParamDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("paramdecl " + name);
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);
        Main.log.prettyPrint(" : ");;

        parserType.prettyPrint();
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
    public void checkWhetherAssignable(PascalSyntax where) {
        where.error("You cannot assign to a constant");
    }

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error(name + " is a parameter, not a function.");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error(name + " is a parameter, not a procedure.");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {}

    public static ParamDecl parse(Scanner s) {
        enterParser("paramdecl");

        ParamDecl pd = new ParamDecl(s.curToken.id, s.curLineNum());
        s.skip(nameToken); s.skip(colonToken);

        s.test(nameToken);
        pd.parserType = Type.parse(s);

        if(s.curToken.kind == semicolonToken)
            s.skip(semicolonToken);

        leaveParser("paramdecl");
        return pd;
    }

}
