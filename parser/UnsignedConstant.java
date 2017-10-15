package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public abstract class UnsignedConstant extends Factor {

    String img;

    public UnsignedConstant(int lNum) {
        super(lNum);
    }

    public abstract int getImgAsInt();

    /**
        Checks s.curToken.kind and Returns
        the apropriat sub-class of UnsignedConstant.
    */
    public static UnsignedConstant parse(Scanner s) {
        enterParser("unsigned constant");

        UnsignedConstant uc = null;
        switch(s.curToken.kind) {
            case nameToken:
                uc = NamedConst.parse(s);       break;
            case intValToken:
                uc = NumberLiteral.parse(s);    break;
            case charValToken:
                uc = CharLiteral.parse(s);      break;
        }

        leaveParser("unsigned constant");
        return uc;
    }

}
