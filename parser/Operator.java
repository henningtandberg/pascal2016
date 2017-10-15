package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public abstract class Operator extends PascalSyntax {

    public Operator(int lNum) {
        super(lNum);
    }

    /**
        Uses TokenKind's methos to check
        what kind of operator s.curToken is.
        Returns correct sub-class of Operator.
    */
    public static Operator parse(Scanner s) {
        enterParser("operator");

        Operator o = null;
        if( s.curToken.kind.isFactorOpr() )
            o = FactorOperator.parse(s);
        else if( s.curToken.kind.isPrefixOpr() )
            o = PrefixOperator.parse(s);
        else if( s.curToken.kind.isRelOpr() )
            o = RelOperator.parse(s);
        else if( s.curToken.kind.isTermOpr() )
            o = TermOperator.parse(s);
        else
            Main.error("Could not identify operator");

        leaveParser("operator");
        return o;
    }
}
