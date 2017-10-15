package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class CharType extends Type {

    String      libId;

    public CharType(int lNum) {
        super(lNum);
        libId = "char";
    }

    @Override
    public String identify() {
    	return identify("type Char");
    }

	@Override
	public void prettyPrint() {
		Main.log.prettyPrint("character");
	}

    @Override //Saves refrence and type from lib
    public void check(Block curScope, Library lib) {
        ref = (TypeDecl)lib.findDecl(libId, this);
        type = ref.type;
    }

    @Override
    public void genCode(CodeFile f){}

	public static CharType parse(Scanner s) {
		enterParser("type char");

		CharType ct = new CharType( s.curLineNum() );
		s.skip(nameToken);

		leaveParser("type char");
		return ct;
	}
}
