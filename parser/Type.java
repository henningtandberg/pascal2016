package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public abstract class Type extends PascalSyntax {

	PascalDecl  ref;
	types.Type 	type;

	public Type(int lNum) {
		super(lNum);
	}

	/**
		Checks s.curToken and retuns correct
		sub-class of Type;
	*/
	public static Type parse(Scanner s) {
		enterParser("type");

		Type t = null;
		switch( s.curToken.kind ) {
			case arrayToken:
				t = ArrayType.parse(s);	break;
			case nameToken:
				switch( s.curToken.id.toLowerCase() ) {
					case "integer":
						t = IntType.parse(s);	break;
					case "char":
						t = CharType.parse(s);	break;
					case "boolean":
						t = BoolType.parse(s);	break;
					default:
						t = NameType.parse(s);	break;
				}
				break;
			default:
				Main.error("Could not parse type!");
				break;
		}

		leaveParser("type");
		return t;
	}

}
