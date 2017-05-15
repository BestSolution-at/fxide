/**
 * FX-IDE - JavaFX and Eclipse based IDE
 *
 * Copyright (C) 2017 - BestSoltion.at
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package at.bestsolution.fxide.jdt.editor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.fx.core.IOUtils;
import org.eclipse.fx.core.ServiceUtils;
import org.eclipse.fx.text.hover.HtmlString;
import org.eclipse.fx.ui.services.theme.ThemeManager;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

import com.sun.javafx.webkit.ThemeClientImpl;

import at.bestsolution.fxide.jdt.editor.internal.MethodUtil;
import at.bestsolution.fxide.jdt.editor.internal.SignatureUtil;
import at.bestsolution.fxide.jdt.text.javadoc.JavadocContentAccess2;

public class JDTJavaDocSupport {
	private static String defaultCSS = IOUtils.readToString(JDTJavaDocSupport.class.getResourceAsStream("internal/javadoc.css"), Charset.forName("UTF-8"));
	private static String darkCSS = IOUtils.readToString(JDTJavaDocSupport.class.getResourceAsStream("internal/javadoc-dark.css"), Charset.forName("UTF-8"));
	private static String prettifyJS = IOUtils.readToString(JDTJavaDocSupport.class.getResourceAsStream("internal/prettify.js"), Charset.forName("UTF-8"));
	private static ThemeManager themeMgr = ServiceUtils.getService(ThemeManager.class).get();

	public static HtmlString toHtml(CompletionProposal proposal, IJavaProject jProject) throws JavaModelException {
		String content = null;
		if (proposal.getKind() == org.eclipse.jdt.core.CompletionProposal.FIELD_REF) {
			IType jType = getOwnerType(proposal, jProject);
			if( jType != null ) {
				IField field = jType.getField(String.valueOf(proposal.getName()));
				if( field != null && field.exists() ) {
					try {
						content = JavadocContentAccess2.getHTMLContent(field, true);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if( proposal.getKind() == org.eclipse.jdt.core.CompletionProposal.METHOD_REF ) {
			IType jType = getOwnerType(proposal, jProject);
			if( jType != null ) {
				MethodUtil m = new MethodUtil(proposal, jType);
				IMethod method = m.resolve();
				if( method != null && method.exists() ) {
					try {
						content = JavadocContentAccess2.getHTMLContent(method, true);
						if( content != null ) {
							content = content.replace("<pre><code>","<pre class=\"prettyprint\"><code class=\"language-java\">");
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		if( content != null ) {
			return new HtmlString("<html><header><style>"+ ("theme.dark".equals(themeMgr.getCurrentTheme().getId()) ? darkCSS : defaultCSS) +"</style><script>"+prettifyJS+"\n</script></header><body onload='PR.prettyPrint();'>"+content+"</body></html>");
		}

		return null;
	}

	private static IType getOwnerType(CompletionProposal proposal, IJavaProject jProject) throws JavaModelException {
		char[] declSignature = proposal.getDeclarationSignature();
		String typeName = SignatureUtil.stripSignatureToFQN(String.valueOf(declSignature));
		return jProject.findType(String.valueOf(typeName));
	}

	private static HtmlString getMemberJavaDoc(IMember member) throws JavaModelException {
		IBuffer buf = member.getOpenable().getBuffer();
		if (buf != null) {
			ISourceRange javadocRange = member.getJavadocRange();
			if (javadocRange != null) {
				String rawJavadoc = buf.getText(javadocRange.getOffset(), javadocRange.getLength());
				return JDTJavaDocSupport.toHtml(member, rawJavadoc);
			}
		}
		return null;
	}

	public static HtmlString toHtml(IJavaElement element, String rawJavadoc) {
		String source = rawJavadoc + "class C{}"; //$NON-NLS-1$
		CompilationUnit root = createAST(element, source);
		if (root == null)
			return null;
		List<AbstractTypeDeclaration> types = root.types();
		if (types.size() != 1)
			return null;
		AbstractTypeDeclaration type = types.get(0);
		Javadoc javadoc = type.getJavadoc();
		String js = "";
		try( InputStream in = JDTJavaDocSupport.class.getResourceAsStream("internal/prettify.js") ) {
			js = IOUtils.readToString(in, Charset.forName("UTF-8"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StringBuilder b = new StringBuilder("<html><head></head><body>");
		List<TagElement> l = javadoc.tags();
		for (TagElement e : l) {
			if (e.getTagName() == null) {
				b.append(' ');
				handleContents(b, e);
			}
			// System.err.println(" ===> " + e.getTagName());
		}
		b.append("</body></html>");
//		System.err.println(b);
		return new HtmlString(b.toString());

		// BufferedReader r = new BufferedReader(new StringReader(javaDoc));
		// StringBuilder b = new StringBuilder();
		// return javaDoc.replaceAll("/\\*.*\r?\n", "").replaceAll(".*\\*", "");
	}

	private static void handleContents(StringBuilder b, TagElement e) {
		List<ASTNode> l = e.fragments();
		for (ASTNode child : l) {
			if (child instanceof TextElement) {
				b.append(((TextElement) child).getText()+"\n");
			} else {
				b.append(child.toString() +"\n");
			}

		}
	}

	private static CompilationUnit createAST(IJavaElement element, String cuSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		IJavaProject javaProject = element.getJavaProject();
		parser.setProject(javaProject);
		Map<String, String> options = javaProject.getOptions(true);
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED); // workaround
																				// for
																				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=212207
		parser.setCompilerOptions(options);

		parser.setSource(cuSource.toCharArray());
		return (CompilationUnit) parser.createAST(null);
	}
}
