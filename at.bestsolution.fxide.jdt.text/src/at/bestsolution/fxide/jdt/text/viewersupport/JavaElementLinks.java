/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package at.bestsolution.fxide.jdt.text.viewersupport;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementLinks {
	/**
	 * The link is composed of a number of segments, separated by LINK_SEPARATOR:
	 * <p>
	 * segments[0]: ""<br>
	 * segments[1]: baseElementHandle<br>
	 * segments[2]: typeName<br>
	 * segments[3]: memberName<br>
	 * segments[4...]: parameterTypeName (optional)
	 */
	private static final char LINK_SEPARATOR= '\u2602';
	private static final char LINK_BRACKET_REPLACEMENT= '\u2603';

	public static final String JAVADOC_SCHEME= "eclipse-javadoc"; //$NON-NLS-1$

	/**
	 * Creates an {@link URI} with the given scheme for the given element.
	 *
	 * @param scheme the scheme
	 * @param element the element
	 * @return an {@link URI}, encoded as {@link URI#toASCIIString() ASCII} string, ready to be used
	 *         as <code>href</code> attribute in an <code>&lt;a&gt;</code> tag
	 * @throws URISyntaxException if the arguments were invalid
	 */
	public static String createURI(String scheme, IJavaElement element) throws URISyntaxException {
		return createURI(scheme, element, null, null, null);
	}

	/**
	 * Creates an {@link URI} with the given scheme based on the given element.
	 * The additional arguments specify a member referenced from the given element.
	 *
	 * @param scheme a scheme
	 * @param element the declaring element
	 * @param refTypeName a (possibly qualified) type or package name, can be <code>null</code>
	 * @param refMemberName a member name, can be <code>null</code>
	 * @param refParameterTypes a (possibly empty) array of (possibly qualified) parameter type
	 *            names, can be <code>null</code>
	 * @return an {@link URI}, encoded as {@link URI#toASCIIString() ASCII} string, ready to be used
	 *         as <code>href</code> attribute in an <code>&lt;a&gt;</code> tag
	 * @throws URISyntaxException if the arguments were invalid
	 */
	public static String createURI(String scheme, IJavaElement element, String refTypeName, String refMemberName, String[] refParameterTypes) throws URISyntaxException {
		/*
		 * We use an opaque URI, not ssp and fragments (to work around Safari bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=212527 (wrongly encodes #)).
		 */

		StringBuffer ssp= new StringBuffer(60);
		ssp.append(LINK_SEPARATOR); // make sure first character is not a / (would be hierarchical URI)

		// replace '[' manually, since URI confuses it for an IPv6 address as per RFC 2732:
		ssp.append(element.getHandleIdentifier().replace('[', LINK_BRACKET_REPLACEMENT)); // segments[1]

		if (refTypeName != null) {
			ssp.append(LINK_SEPARATOR);
			ssp.append(refTypeName); // segments[2]

			if (refMemberName != null) {
				ssp.append(LINK_SEPARATOR);
				ssp.append(refMemberName); // segments[3]

				if (refParameterTypes != null) {
					ssp.append(LINK_SEPARATOR);
					for (int i= 0; i < refParameterTypes.length; i++) {
						ssp.append(refParameterTypes[i]); // segments[4|5|..]
						if (i != refParameterTypes.length - 1) {
							ssp.append(LINK_SEPARATOR);
						}
					}
				}
			}
		}
		return new URI(scheme, ssp.toString(), null).toASCIIString();
	}

}
