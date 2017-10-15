package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class EmptyStatm extends Statement {

    public EmptyStatm(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("empty-statment");
    }

    @Override
    public void prettyPrint() {}

    @Override
    public void genCode(CodeFile f){}

    @Override // No need for check
    public void check(Block curScope, Library lib) {}

    public static EmptyStatm parse(Scanner s) {
        enterParser("empty-statment");

        EmptyStatm es = new EmptyStatm(s.curLineNum());

        leaveParser("empty-statment");
        return es;
    }
}
