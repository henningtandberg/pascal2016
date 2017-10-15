package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class FactorOperator extends Operator {

    String img;
    TokenKind ref;

    public FactorOperator(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("factor operator");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(" " + img + " ");
    }

    @Override   //No need for check
    public void check(Block curScope, Library lib) {}

    /* Geneerates correct code for operator */
    @Override
    public void genCode(CodeFile f){
        switch( ref ) {
            case multiplyToken:
                f.genInstr("", "imull", "%ecx", img);
                break;
            case divToken:
                f.genInstr("", "cdq", "", "");
                f.genInstr("", "idivl", "%ecx", img);
                break;
            case modToken:
                f.genInstr("", "cdq", "", "");
                f.genInstr("", "idivl", "%ecx", "");
                f.genInstr("", "movl", "%edx,%eax", img);
                break;
            case andToken:
                f.genInstr("", "andl", "%ecx,%eax", img);
                break;
            default:
                Main.panic("Panic! FactorOperator reference is incorrect!");
                break;
        }
    }

    public static FactorOperator parse(Scanner s) {
        enterParser("factor operator");

        FactorOperator fo = new FactorOperator(s.curLineNum());
        fo.img = s.curToken.kind.toString();

        if ( !s.curToken.kind.isFactorOpr() )
            fo.error(fo.img + " is not a factor operator");

        fo.ref = s.curToken.kind;
        s.readNextToken();

        leaveParser("factor operator");
        return fo;
    }

}
