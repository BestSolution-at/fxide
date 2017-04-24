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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.code.editor.services.EditingContext;
import org.eclipse.fx.code.editor.services.ProposalComputer;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

@SuppressWarnings("restriction")
public class JDTProposalComputer implements ProposalComputer {
	private JDTSourceFileInput input;
	private EditingContext editingContext;

	private final static String VISIBILITY= JavaCore.CODEASSIST_VISIBILITY_CHECK;
	private final static String ENABLED= "enabled"; //$NON-NLS-1$
	private final static String DISABLED= "disabled"; //$NON-NLS-1$

	@Inject
	public JDTProposalComputer(Input<String> input, EditingContext editingContext) {
		this.input = (JDTSourceFileInput) input;
		this.editingContext = editingContext;
		restrictVisibility(true);
	}

	private void restrictVisibility(boolean restrict) {
		Hashtable<String, String> options= JavaCore.getOptions();
		Object value= options.get(VISIBILITY);
		if (value instanceof String) {
			String newValue= restrict ? ENABLED : DISABLED;
			if ( !newValue.equals(value)) {
				options.put(VISIBILITY, newValue);
				JavaCore.setOptions(options);
			}
		}
	}

	@Override
	public CompletableFuture<List<CompletionProposal>> compute() {
		List<CompletionProposal> rv = new ArrayList<>();
		try {
			input.getCompilationUnit().codeComplete(editingContext.getCaretOffset(), new CompletionRequestor() {
				@Override
				public void acceptContext(CompletionContext context) {
					super.acceptContext(context);
				}

				@Override
				public void accept(org.eclipse.jdt.core.CompletionProposal proposal) {
					if( (proposal.getAccessibility() & IAccessRule.K_ACCESSIBLE) == IAccessRule.K_ACCESSIBLE ) {
						rv.add(new JDTCompletionProposal(input.getCompilationUnit().getJavaProject(),proposal));
					}
				}
			}, (IProgressMonitor)null);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return CompletableFuture.completedFuture(rv);
	}

}
