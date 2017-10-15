package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class IntType extends Type {

    String      libId;

    public IntType(int lNum) {
        super(lNum);
        libId = "integer";
    }

    @Override
    public String identify() {
    	return identify("type integer");
    }

	@Override
	public void prettyPrint() {
		Main.log.prettyPrint("integer");
	}

    @Override //Saves refrence and type from lib
    public void check(Block curScope, Library lib) {
        ref = (TypeDecl)lib.findDecl(libId, this);
        type = ref.type;
    }

    @Override
    public void genCode(CodeFile f){}

	public static IntType parse(Scanner s) {
		enterParser("type int");

		IntType it = new IntType( s.curLineNum() );
		s.skip(nameToken);

		leaveParser("type int");
		return it;
	}
}
