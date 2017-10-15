package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class CharLiteral extends UnsignedConstant {

    public CharLiteral(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("charliteral");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("'" + img + "'");
    }

    @Override // Set type to lib.characterType
    public void check(Block curScope, Library lib) {
        type = lib.characterType;
    }

    /* Moves dec val of characteter to %eax */
    @Override
    public void genCode(CodeFile f){
        f.genInstr("", "movl", "$"+(int)img.charAt(0)+",%eax", "");
    }

    @Override
    public int getImgAsInt() {
        return (int)img.charAt(0);
    }

    public static CharLiteral parse(Scanner s) {
        enterParser("char literal");

        CharLiteral cl = new CharLiteral(s.curLineNum());
        cl.img = ""+s.curToken.charVal;
        s.skip(charValToken);

        leaveParser("char literal");
        return cl;
    }

}
