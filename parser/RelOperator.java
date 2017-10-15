package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class RelOperator extends Operator {

    String img;
    TokenKind ref;

    public RelOperator(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("rel operato");
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
            case equalToken:
                f.genInstr("", "sete", "%al", img);
                break;
            case notEqualToken:
                f.genInstr("", "setne", "%al", img);
                break;
            case lessToken:
                f.genInstr("", "setl", "%al", img);
                break;
            case lessEqualToken:
                f.genInstr("", "setle", "%al", img);
                break;
            case greaterToken:
                f.genInstr("", "setg", "%al", img);
                break;
            case greaterEqualToken:
                f.genInstr("", "setge", "%al", img);
                break;
            default:
                Main.panic("Panic! RelOperator reference is incorrect!");
                break;
        }
    }

    public static RelOperator parse(Scanner s) {
        enterParser("rel operator");

        RelOperator ro = new RelOperator(s.curLineNum());
        ro.img = s.curToken.kind.toString();

        if ( !s.curToken.kind.isRelOpr() )
            ro.error(ro.img + " is not a relation operator");

        ro.ref = s.curToken.kind;
        s.readNextToken();

        leaveParser("rel operator");
        return ro;
    }
}
