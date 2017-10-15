package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public abstract class Statement extends PascalSyntax {

    public Statement(int lNum) {
        super(lNum);
    }

    /**
        Checks s.curToken and returns correct
        sub-class of Statment.
    */
    public static Statement parse(Scanner s) {
        enterParser("statement");

        Statement st = null;
        switch( s.curToken.kind ) {

            case beginToken:
                st = CompoundStatm.parse(s);    break;

            case ifToken:
                st = IfStatm.parse(s);          break;

            case nameToken:
                switch( s.nextToken.kind ) {
                    case assignToken:
                    case leftBracketToken:
                        st = AssignStatm.parse(s);      break;
                    default:
                        st = ProcCallStatm.parse(s);    break;
                } break;

            case whileToken:
                st = WhileStatm.parse(s);       break;

            case endToken:
                st = EmptyStatm.parse(s);       break;

            default:
                new EmptyStatm(s.curLineNum()).error("Expected a end token, but found "
                                                    + s.curToken.kind.toString() + "!");
                break;
        }

        leaveParser("statement");
        return st;
    }

}
