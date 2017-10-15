package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class TermOperator extends Operator {

    String img;
    TokenKind ref;

    public TermOperator(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("term operator");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(" " + img + " ");
    }

    @Override   //No need for check
    public void check(Block curScope, Library lib) {}

    /* Generates correct code for operator */
    @Override
    public void genCode(CodeFile f){
        switch( ref ) {
            case addToken:
                f.genInstr("", "addl", "%ecx,%eax", img);
                break;
            case subtractToken:
                f.genInstr("", "subl", "%ecx,%eax", img);
                break;
            case orToken:
                f.genInstr("", "orl", "%ecx,%eax", img);
                break;
            default:
                Main.panic("Panic! TermOperator refrence is incorrect!");
                break;
        }
    }

    public static TermOperator parse(Scanner s) {
        enterParser("term operator");

        TermOperator to = new TermOperator(s.curLineNum());
        to.img = s.curToken.kind.toString();

        if ( !s.curToken.kind.isTermOpr() )
            to.error(to.img + " is not a term operator");

        to.ref = s.curToken.kind;
        s.readNextToken();

        leaveParser("term operator");
        return to;
    }

}
