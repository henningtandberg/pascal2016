package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public abstract class Factor extends PascalSyntax {

    public types.Type  type;

    public Factor(int lNum) {
        super(lNum);
    }

    /**
        Checks s.curToken.kind and returns an
        object of the apropriat sub-class.
    */
    public static Factor parse(Scanner s) {
        enterParser("factor");

        Factor f = null;
        switch( s.curToken.kind ) {
            case leftParToken:
                f = InnerExpr.parse(s);         break;

            case notToken:
                f = Negation.parse(s);          break;

            case nameToken:
                if( s.nextToken.kind == leftParToken )
                    f = FuncCall.parse(s);
                else
                    f = Variable.parse(s);
                                                break;

            case intValToken:
                f = UnsignedConstant.parse(s);  break;

            case charValToken:
                f = UnsignedConstant.parse(s);  break;

            default:
                new InnerExpr(s.curLineNum()).error("Exprected a factor but found: "
                                                        + s.curToken.kind.toString());
                break;
        }

        leaveParser("factor");
        return f;
    }

}
