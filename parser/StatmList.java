package parser;

import main.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class StatmList extends PascalSyntax {

    ArrayList<Statement> stList;

    public StatmList(int lNum) {
        super(lNum);
        stList = new ArrayList<>();
    }

    @Override
    public String identify() {
        return identify("statement-list");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyIndent();

        for( int i = 0; i < stList.size(); i++ ) {
            Statement s = stList.get(i);
            s.prettyPrint();

            if( !(s instanceof EmptyStatm) )
                Main.log.prettyPrintLn(";");
        }

        Main.log.prettyOutdent();
    }

    @Override
    public void check(Block curScope, Library lib) {
        for( int i = 0; i < stList.size(); i++ )
            stList.get(i).check(curScope, lib);
    }

    /* Lets each stament generate its code */
    @Override
    public void genCode(CodeFile f){
        for( int i = 0; i < stList.size(); i++ )
            stList.get(i).genCode(f);
    }

    public static StatmList parse(Scanner s) {
        enterParser("statement-list");

        StatmList stl = new StatmList(s.curLineNum());
        //Statmentlist is allways between beginToken and endToken
        while ( s.curToken.kind != endToken ) {
            if( s.curToken.kind == semicolonToken)
                s.skip(semicolonToken);

            stl.stList.add( Statement.parse(s) );
        }

        leaveParser("statement-list");
        return stl;
    }

}
