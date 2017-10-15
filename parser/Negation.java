package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class Negation extends Factor {

    Factor      fact;

    public Negation(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("negation");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("not ");
        fact.prettyPrint();
    }

    @Override //Saves type of expr
    public void check(Block curScope, Library lib) {
        fact.check(curScope, lib);
        type = fact.type;
    }

    /* Negates eax */
    @Override
    public void genCode(CodeFile f){
        fact.genCode(f);
        f.genInstr("", "xorl", "$1,%eax", "");
    }

    public static Negation parse(Scanner s) {
        enterParser("negation");

        Negation n = new Negation( s.curLineNum() );
        s.skip(notToken); n.fact = Factor.parse(s);

        leaveParser("negation");
        return n;
    }
}
