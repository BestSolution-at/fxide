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

import java.util.Optional;

import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.IDocument;

@SuppressWarnings("restriction")
public class JDTCompletionProposal implements CompletionProposal {
	private org.eclipse.jdt.core.CompletionProposal jdtProposal;
	private IJavaProject javaProject;

	public JDTCompletionProposal(IJavaProject javaProject, org.eclipse.jdt.core.CompletionProposal jdtProposal) {
		this.jdtProposal = jdtProposal;
		this.javaProject = javaProject;
//		System.err.println(String.valueOf(jdtProposal.getName()) + " => " + Flags.isPublic(jdtProposal.getFlags()));
	}

	public org.eclipse.jdt.core.CompletionProposal getJdtProposal() {
		return jdtProposal;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	@Override
	public CharSequence getLabel() {
		// TODO Auto-generated method stub
		if( jdtProposal.getKind() == org.eclipse.jdt.core.CompletionProposal.TYPE_REF ) {
			return Signature.toString(String.valueOf(Signature.getTypeErasure(jdtProposal.getSignature())));
		}
		return jdtProposal.getName() != null ? String.valueOf(jdtProposal.getName()) : "<!unknown!>";
	}

	public Optional<IType> getType() {
		String erasedType = String.valueOf(Signature.getTypeErasure(jdtProposal.getSignature()));
		try {
			return Optional.ofNullable(getJavaProject().findType(Signature.toString(erasedType)));
		} catch (JavaModelException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public void apply(IDocument document) {
		// TODO Auto-generated method stub

	}

	@Override
	public TextSelection getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
