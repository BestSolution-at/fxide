package at.bestsolution.fxide.jdt.editor;

import org.eclipse.fx.code.editor.fx.services.CompletionProposalPresenter;
import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.text.ui.contentassist.ICompletionProposal;
import org.eclipse.fx.text.ui.contentassist.IContextInformation;
import org.eclipse.fx.ui.controls.styledtext.StyledString;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;

import javafx.scene.Node;

@SuppressWarnings("restriction")
public class JDTCompletionPresenter implements CompletionProposalPresenter {

	@Override
	public ICompletionProposal createProposal(CompletionProposal proposal) {
		return new CompletionProposalImpl((JDTCompletionProposal) proposal);
	}

	static class CompletionProposalImpl implements ICompletionProposal {
		private JDTCompletionProposal proposal;

		public CompletionProposalImpl(JDTCompletionProposal proposal) {
			this.proposal = proposal;
			System.err.println(proposal.getJdtProposal());
		}

		@Override
		public CharSequence getLabel() {
//			StyledString s = new StyledString();
//			s.
			// TODO Auto-generated method stub
			return proposal.getLabel();
		}

		@Override
		public CharSequence getHoverInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Node getGraphic() {
			// TODO Auto-generated method stub
			return null;
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
		public IContextInformation getContextInformation() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
