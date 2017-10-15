package types;

public class IntType extends Type {
    @Override public String identify() {
    	return "type Integer";
    }

    @Override public int size() {
    	return 4;
    }
}
