package parser;

import main.*;

public class TypeDecl extends PascalDecl {

    public TypeDecl(String id, int lNum) {
        super(id, lNum);
    }

    @Override
    public String identify() {
        return identify("typdedecl");
    }

    @Override
    public void prettyPrint() {}

    @Override
    public void check(Block curScope, Library lib) {}

    @Override
    public void genCode(CodeFile f){}

    @Override
    public void checkWhetherAssignable(PascalSyntax where) {
        where.error("You cannot assign to a type!");
    }

    @Override
    public void checkWhetherFunction(PascalSyntax where) {
        where.error("A type is not a function!");
    }

    @Override
    public void checkWhetherProcedure(PascalSyntax where) {
        where.error("A type is not a procedure!");
    }

    @Override
    public void checkWhetherValue(PascalSyntax where) {
        where.error("A type has no specific value!");
    }
}
