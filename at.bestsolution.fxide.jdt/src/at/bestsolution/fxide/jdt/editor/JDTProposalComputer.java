package at.bestsolution.fxide.jdt.editor;

import java.util.ArrayList;
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
import org.eclipse.jdt.core.JavaModelException;

@SuppressWarnings("restriction")
public class JDTProposalComputer implements ProposalComputer {
	private JDTSourceFileInput input;
	private EditingContext editingContext;

	@Inject
	public JDTProposalComputer(Input<String> input, EditingContext editingContext) {
		this.input = (JDTSourceFileInput) input;
		this.editingContext = editingContext;
	}

	@Override
	public CompletableFuture<List<CompletionProposal>> compute() {
		List<CompletionProposal> rv = new ArrayList<>();
		try {
			input.getCompilationUnit().codeComplete(editingContext.getCaretOffset(), new CompletionRequestor() {
				@Override
				public void acceptContext(CompletionContext context) {
					// TODO Auto-generated method stub
					super.acceptContext(context);
				}

				@Override
				public void accept(org.eclipse.jdt.core.CompletionProposal proposal) {
					rv.add(new JDTCompletionProposal(proposal));
				}
			}, (IProgressMonitor)null);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return CompletableFuture.completedFuture(rv);
	}

}
