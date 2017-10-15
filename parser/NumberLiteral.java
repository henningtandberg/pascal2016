package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class NumberLiteral extends UnsignedConstant {

    public NumberLiteral(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("numberliteral");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(img);
    }

    @Override // Save type to lib.integerType
    public void check(Block curScope, Library lib) {
        type = lib.integerType;
    }

    /* Moves img to %eax */
    @Override
    public void genCode(CodeFile f){
        f.genInstr("", "movl", "$"+img+",%eax", "");
    }

    @Override
    public int getImgAsInt() {
        return Integer.parseInt(img);
    }

    public static NumberLiteral parse(Scanner s) {
        enterParser("number literal");

        NumberLiteral nl = new NumberLiteral(s.curLineNum());
        nl.img = ""+s.curToken.intVal;
        s.skip(intValToken);

        leaveParser("number literal");
        return nl;
    }

}
