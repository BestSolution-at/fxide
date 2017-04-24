package at.bestsolution.fxide.jdt.corext.dom;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;

public class ASTNodes {
	/**
	 * Escapes a string value to a literal that can be used in Java source.
	 *
	 * @param stringValue the string value
	 * @return the escaped string
	 * @see StringLiteral#getEscapedValue()
	 */
	public static String getEscapedStringLiteral(String stringValue) {
		StringLiteral stringLiteral= AST.newAST(ASTProvider.SHARED_AST_LEVEL).newStringLiteral();
		stringLiteral.setLiteralValue(stringValue);
		return stringLiteral.getEscapedValue();
	}

	/**
	 * Escapes a character value to a literal that can be used in Java source.
	 *
	 * @param ch the character value
	 * @return the escaped string
	 * @see CharacterLiteral#getEscapedValue()
	 */
	public static String getEscapedCharacterLiteral(char ch) {
		CharacterLiteral characterLiteral= AST.newAST(ASTProvider.SHARED_AST_LEVEL).newCharacterLiteral();
		characterLiteral.setCharValue(ch);
		return characterLiteral.getEscapedValue();
	}

	public static String getSimpleNameIdentifier(Name name) {
		if (name.isQualifiedName()) {
			return ((QualifiedName) name).getName().getIdentifier();
		} else {
			return ((SimpleName) name).getIdentifier();
		}
	}

	public static String asString(ASTNode node) {
		ASTFlattener flattener= new ASTFlattener();
		node.accept(flattener);
		return flattener.getResult();
	}
}
