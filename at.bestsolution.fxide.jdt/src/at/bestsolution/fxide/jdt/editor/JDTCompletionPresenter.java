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

import org.eclipse.fx.code.editor.fx.services.CompletionProposalPresenter;
import org.eclipse.fx.code.editor.services.CompletionProposal;
import org.eclipse.fx.text.hover.HtmlString;
import org.eclipse.fx.text.ui.contentassist.ICompletionProposal;
import org.eclipse.fx.text.ui.contentassist.IContextInformation;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.text.IDocument;

import at.bestsolution.fxide.jdt.editor.internal.SignatureUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

@SuppressWarnings("restriction")
public class JDTCompletionPresenter implements CompletionProposalPresenter {

	@Override
	public ICompletionProposal createProposal(CompletionProposal proposal) {
		return new CompletionProposalPresenterImpl((JDTCompletionProposal) proposal);
	}

	static class CompletionProposalPresenterImpl implements ICompletionProposal {
		private JDTCompletionProposal proposal;
		private CharSequence hoverInfo;

		public CompletionProposalPresenterImpl(JDTCompletionProposal proposal) {
			this.proposal = proposal;
//			System.err.println(proposal.getJdtProposal());
		}

		@Override
		public Node getContentNode() {
			if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.FIELD_REF ) {
				HBox box = new HBox();
				box.getStyleClass().add("proposal-content");

				{
					Label l = new Label(String.copyValueOf(proposal.getJdtProposal().getName()));
					l.setStyle("-fx-font-weight: bold;");
					box.getChildren().add(l);
				}

				{
					Region child = new Region();
					box.getChildren().add(child);
					HBox.setHgrow(child, Priority.ALWAYS);
				}

				{
					Label l = new Label(String.copyValueOf(Signature.getSignatureSimpleName(proposal.getJdtProposal().getSignature())));
					l.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
					box.getChildren().add(l);
				}
				return box;
			} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.METHOD_REF ) {
				HBox box = new HBox();
				box.getStyleClass().add("proposal-content");
				box.setMinWidth(0);
				box.setPrefWidth(0);

				{
					Label l = new Label(String.copyValueOf(proposal.getJdtProposal().getName()));
					l.setStyle("-fx-font-weight: bold;");
					l.setMinWidth(Region.USE_PREF_SIZE);
					box.getChildren().add(l);
				}

				{
					char[][] types = Signature.getParameterTypes(proposal.getJdtProposal().getSignature());
					char[][] parameterNames= proposal.getJdtProposal().findParameterNames(null);

					StringBuilder b = new StringBuilder();
					for( int i = 0; i < parameterNames.length; i++ ) {
						if( b.length() > 0 ) {
							b.append(", ");
						}
						b.append( Signature.getSignatureSimpleName(types[i]) );
						b.append(' ');
						b.append( parameterNames[i] );
					}

					Label l = new Label("(" + b.toString() + ")");
					l.setMinSize(0, 0);
					l.setTextOverrun(OverrunStyle.ELLIPSIS);
					box.getChildren().add(l);
				}

				{
					Region child = new Region();
					child.setMinWidth(20);
					box.getChildren().add(child);
					HBox.setHgrow(child, Priority.ALWAYS);
				}

				{
					Label l = new Label(String.copyValueOf(Signature.getSimpleName(Signature.getSignatureSimpleName(Signature.getReturnType(proposal.getJdtProposal().getSignature())))));
					l.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
					box.getChildren().add(l);
				}

				return box;
			} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.TYPE_REF ) {
				HBox box = new HBox();
				box.setMinWidth(0);
				box.setPrefWidth(0);
				box.getStyleClass().add("proposal-content");

				{
					Label l = new Label(String.valueOf(Signature.getSimpleName(Signature.toCharArray(proposal.getJdtProposal().getSignature()))));
					l.setMinWidth(Region.USE_PREF_SIZE);
					box.getChildren().add(l);
				}

				{
					Label l = new Label(" (" + String.valueOf(Signature.getSignatureQualifier(proposal.getJdtProposal().getSignature())) + ")");
					l.setStyle("-fx-font-weight: bold;");
					l.setMinSize(0, 0);
					l.setTextOverrun(OverrunStyle.ELLIPSIS);
					l.getStyleClass().add("package-name");
					box.getChildren().add(l);
				}

				return box;
			}
			Label label = new Label(proposal.getLabel().toString());
			label.getStyleClass().add("proposal-content");
			return label;
		}

		@Override
		public CharSequence getHoverInfo() {
			if( hoverInfo == null && proposal.getJavaProject() != null && proposal.getJdtProposal().getDeclarationSignature() != null ) {
				if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.FIELD_REF ) {
					try {
						hoverInfo = JDTJavaDocSupport.toHtml(proposal.getJdtProposal(), proposal.getJavaProject());
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.METHOD_REF ) {
					try {
						hoverInfo = JDTJavaDocSupport.toHtml(proposal.getJdtProposal(), proposal.getJavaProject());
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//					type = Signature.getElementType(type);
//					type = Signature.toCharArray(type);
//					try {
//						IType jType = proposal.getJavaProject().findType(String.valueOf(type));
//						String[] parameterTypes = Signature.getParameterTypes(String.valueOf(Signature.removeCapture(proposal.getJdtProposal().getSignature())));
//
//						jType.getMethods();
//
//						IMethod method = jType.getMethod(String.valueOf(proposal.getJdtProposal().getName()),parameterTypes);
//						hoverInfo = getMemberJavaDoc(method);
//						if( hoverInfo == null && method.getOpenable().getBuffer() == null ) {
//							return method.getAttachedJavadoc(null);
//						}
//					} catch (JavaModelException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
			return hoverInfo;
		}

		@Override
		public Node getGraphic() {
			ImageView v = new ImageView();
			if( (proposal.getJdtProposal().getFlags() & Flags.AccPrivate) == Flags.AccPrivate ) {
				v.getStyleClass().add("visibility_private");
			} else if( (proposal.getJdtProposal().getFlags() & Flags.AccPublic) == Flags.AccPublic ) {
				v.getStyleClass().add("visibility_public");
			} else if( (proposal.getJdtProposal().getFlags() & Flags.AccProtected) == Flags.AccProtected ) {
				v.getStyleClass().add("visibility_protected");
			} else {
				v.getStyleClass().add("visibility_default");
			}

			if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.FIELD_REF ) {
				v.getStyleClass().add("field-ref");
			} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.METHOD_REF ) {
				v.getStyleClass().add("method-ref");
			} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.LOCAL_VARIABLE_REF ) {
				v.getStyleClass().add("local-var-ref");
			} else if( proposal.getJdtProposal().getKind() == org.eclipse.jdt.core.CompletionProposal.TYPE_REF ) {
				proposal.getType().map( t -> {
					try {
						if( t.isAnnotation() ) {
							System.err.println("===============>");
							return "annotation-type-ref";
						} else if( t.isInterface() ) {
							return "interface-type-ref";
						} else if( t.isEnum() ) {
							return "enum-type-ref";
						} else {
							return "class-type-ref";
						}
					} catch( JavaModelException e ) {
						// TODO
						e.printStackTrace();
					}
					return null;
				}).ifPresent( s -> {
					v.getStyleClass().add(s);
				});
			} else {
				System.err.println("UNKNOWN: " + proposal.getJdtProposal());
			}

			//TODO Check more flags
			return v;
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
