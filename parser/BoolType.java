package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class BoolType extends Type {

    String      libId;

    public BoolType(int lNum) {
        super(lNum);
        libId = "boolean";
    }

    @Override public String identify() {
    	return identify("type bool");
    }

	@Override
	public void prettyPrint() {
		Main.log.prettyPrint("boolean");
	}

    @Override // Saves refrence and type from lib
    public void check(Block curScope, Library lib) {
        ref = (TypeDecl)lib.findDecl(libId, this);
        type = ref.type;
    }

    @Override
    public void genCode(CodeFile f){}

	public static BoolType parse(Scanner s) {
		enterParser("type bool");

		BoolType bt = new BoolType( s.curLineNum() );
		s.skip(nameToken);

		leaveParser("type bool");
		return bt;
	}
}
