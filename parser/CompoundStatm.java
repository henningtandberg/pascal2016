package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class CompoundStatm extends Statement {

    StatmList statmList;

    public CompoundStatm(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("compound-statment");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrintLn("begin");

        Main.log.prettyIndent();
        statmList.prettyPrint();
        Main.log.prettyOutdent();

        Main.log.prettyPrint("end");
    }

    @Override
    public void check(Block curScope, Library lib) {
        statmList.check(curScope, lib);
    }

    @Override
    public void genCode(CodeFile f){
        statmList.genCode(f);
    }

    public static CompoundStatm parse(Scanner s) {
        enterParser("compound-statment");

        CompoundStatm cp = new CompoundStatm( s.curLineNum() );
        s.skip(beginToken);
        cp.statmList = StatmList.parse(s);
        s.skip(endToken);

        leaveParser("compound-statment");
        return cp;
    }

}
