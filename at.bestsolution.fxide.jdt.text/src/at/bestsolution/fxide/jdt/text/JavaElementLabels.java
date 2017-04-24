/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package at.bestsolution.fxide.jdt.text;

import org.eclipse.jdt.core.IJavaElement;

import at.bestsolution.fxide.jdt.text.viewersupport.JavaElementLabelComposer;

public class JavaElementLabels {
	/**
	 * User-readable string for separating list items (e.g. ", ").
	 */
	public final static String COMMA_STRING= JavaUIMessages.JavaElementLabels_comma_string;

	/**
	 * User-readable string for separating post qualified names (e.g. " - ").
	 */
	public final static String CONCAT_STRING= JavaUIMessages.JavaElementLabels_concat_string;
	/**
	 * User-readable string for ellipsis ("...").
	 */
	public final static String ELLIPSIS_STRING= "..."; //$NON-NLS-1$
	/**
	 * User-readable string for separating the return type (e.g. " : ").
	 */
	public final static String DECL_STRING= JavaUIMessages.JavaElementLabels_declseparator_string;
	/**
	 * User-readable string for concatenating categories (e.g. " ").
	 * @since 3.5
	 */
	public final static String CATEGORY_SEPARATOR_STRING= JavaUIMessages.JavaElementLabels_category_separator_string;
	/**
	 * User-readable string for the default package name (e.g. "(default package)").
	 */
	public final static String DEFAULT_PACKAGE= JavaUIMessages.JavaElementLabels_default_package;

	/**
	 * Add root path to all elements except Package Fragment Roots and Java projects.
	 * e.g. <code>C:\java\lib\rt.jar - java.lang.Vector</code>
	 * Option only applies to getElementLabel
	 */
	public final static long PREPEND_ROOT_PATH= 1L << 44;

	/**
	 * Package Fragment Roots contain variable name if from a variable.
	 * e.g. <code>JRE_LIB - c:\java\lib\rt.jar</code>
	 */
	public final static long ROOT_VARIABLE= 1L << 40;

	/**
	 * Package Fragment Roots contain the project name if not an archive (prepended).
	 * e.g. <code>MyProject/src</code>
	 */
	public final static long ROOT_QUALIFIED= 1L << 41;

	/**
	 * Package Fragment Roots contain the project name if not an archive (appended).
	 * e.g. <code>src - MyProject</code>
	 */
	public final static long ROOT_POST_QUALIFIED= 1L << 42;

	/**
	 * Add root path to all elements except Package Fragment Roots and Java projects.
	 * e.g. <code>java.lang.Vector - C:\java\lib\rt.jar</code>
	 * Option only applies to getElementLabel
	 */
	public final static long APPEND_ROOT_PATH= 1L << 43;

	/**
	 * Post qualify referenced package fragment roots. For example
	 * <code>jdt.jar - org.eclipse.jdt.ui</code> if the jar is referenced
	 * from another project.
	 */
	public final static long REFERENCED_ROOT_POST_QUALIFIED= 1L << 45;

	/**
	 * Specifies to use the resolved information of a IType, IMethod or IField. See {@link IType#isResolved()}.
	 * If resolved information is available, types will be rendered with type parameters of the instantiated type.
	 * Resolved methods render with the parameter types of the method instance.
	 * <code>Vector&lt;String&gt;.get(String)</code>
	 */
	public final static long USE_RESOLVED= 1L << 48;

	/**
	 * Method labels contain parameter annotations.
	 * E.g. <code>foo(@NonNull int)</code>.
	 * This flag is only valid if {@link #M_PARAMETER_NAMES} or {@link #M_PARAMETER_TYPES} is also set.
	 * @since 3.8
	 */
	public final static long M_PARAMETER_ANNOTATIONS= 1L << 52;

	/**
	 * Method names contain type parameters prepended.
	 * e.g. <code>&lt;A&gt; foo(A index)</code>
	 */
	public final static long M_PRE_TYPE_PARAMETERS= 1L << 2;

	/**
	 * Method names contain return type (appended)
	 * e.g. <code>int foo</code>
	 */
	public final static long M_PRE_RETURNTYPE= 1L << 6;
	/**
	 * Method names are fully qualified.
	 * e.g. <code>java.util.Vector.size</code>
	 */
	public final static long M_FULLY_QUALIFIED= 1L << 7;

	/**
	 * Type names contain type parameters.
	 * e.g. <code>Map&lt;S, T&gt;</code>
	 */
	public final static long T_TYPE_PARAMETERS= 1L << 21;

	/**
	 * Type names are fully qualified.
	 * e.g. <code>java.util.Map.Entry</code>
	 */
	public final static long T_FULLY_QUALIFIED= 1L << 18;

	/**
	 * Method names contain parameter types.
	 * e.g. <code>foo(int)</code>
	 */
	public final static long M_PARAMETER_TYPES= 1L << 0;

	/**
	 * Method names contain parameter names.
	 * e.g. <code>foo(index)</code>
	 */
	public final static long M_PARAMETER_NAMES= 1L << 1;
	/**
	 * Method names contain thrown exceptions.
	 * e.g. <code>foo throws IOException</code>
	 */
	public final static long M_EXCEPTIONS= 1L << 4;
	/**
	 * Method names contain type parameters appended.
	 * e.g. <code>foo(A index) &lt;A&gt;</code>
	 */
	public final static long M_APP_TYPE_PARAMETERS= 1L << 3;
	/**
	 * Method names contain return type (appended)
	 * e.g. <code>foo : int</code>
	 */
	public final static long M_APP_RETURNTYPE= 1L << 5;
	/**
	 * Prepend first category (if any) to method.
	 * @since 3.2
	 */
	public final static long M_CATEGORY= 1L << 50;
	/**
	 * Method names are post qualified.
	 * e.g. <code>size - java.util.Vector</code>
	 */
	public final static long M_POST_QUALIFIED= 1L << 8;
	/**
	 * Package names are abbreviated if
	 * {@link PreferenceConstants#APPEARANCE_ABBREVIATE_PACKAGE_NAMES} is <code>true</code> and/or
	 * compressed if {@link PreferenceConstants#APPEARANCE_COMPRESS_PACKAGE_NAMES} is
	 * <code>true</code>.
	 */
	public final static long P_COMPRESSED= 1L << 37;
	/**
	 * Type names are type container qualified.
	 * e.g. <code>Map.Entry</code>
	 */
	public final static long T_CONTAINER_QUALIFIED= 1L << 19;
	/**
	 * Package names are qualified.
	 * e.g. <code>MyProject/src/java.util</code>
	 */
	public final static long P_QUALIFIED= 1L << 35;
	/**
	 * Package names are post qualified.
	 * e.g. <code>java.util - MyProject/src</code>
	 */
	public final static long P_POST_QUALIFIED= 1L << 36;
	/**
	 * Prepend first category (if any) to type.
	 * @since 3.2
	 */
	public final static long T_CATEGORY= 1L << 51;
	/**
	 * Type names are post qualified.
	 * e.g. <code>Entry - java.util.Map</code>
	 */
	public final static long T_POST_QUALIFIED= 1L << 20;
	/**
	 * Field names contain the declared type (prepended)
	 * e.g. <code>int fHello</code>
	 */
	public final static long F_PRE_TYPE_SIGNATURE= 1L << 15;
	/**
	 * Fields names are fully qualified.
	 * e.g. <code>java.lang.System.out</code>
	 */
	public final static long F_FULLY_QUALIFIED= 1L << 16;
	/**
	 * Field names contain the declared type (appended)
	 * e.g. <code>fHello : int</code>
	 */
	public final static long F_APP_TYPE_SIGNATURE= 1L << 14;
	/**
	 * Prepend first category (if any) to field.
	 * @since 3.2
	 */
	public final static long F_CATEGORY= 1L << 49;
	/**
	 * Fields names are post qualified.
	 * e.g. <code>out - java.lang.System</code>
	 */
	public final static long F_POST_QUALIFIED= 1L << 17;
	/**
	 * Type parameters are post qualified.
	 * e.g. <code>K - java.util.Map.Entry</code>
	 *
	 * @since 3.5
	 */
	public final static long TP_POST_QUALIFIED= 1L << 22;
	/**
	 * Initializer names are fully qualified.
	 * e.g. <code>java.util.Vector.{ ... }</code>
	 */
	public final static long I_FULLY_QUALIFIED= 1L << 10;
	/**
	 * Type names are post qualified.
	 * e.g. <code>{ ... } - java.util.Map</code>
	 */
	public final static long I_POST_QUALIFIED= 1L << 11;
	/**
	 * Class file names are fully qualified.
	 * e.g. <code>java.util.Vector.class</code>
	 */
	public final static long CF_QUALIFIED= 1L << 27;
	/**
	 * Class file names are post qualified.
	 * e.g. <code>Vector.class - java.util</code>
	 */
	public final static long CF_POST_QUALIFIED= 1L << 28;
	/**
	 * Compilation unit names are fully qualified.
	 * e.g. <code>java.util.Vector.java</code>
	 */
	public final static long CU_QUALIFIED= 1L << 31;
	/**
	 * Compilation unit names are post  qualified.
	 * e.g. <code>Vector.java - java.util</code>
	 */
	public final static long CU_POST_QUALIFIED= 1L << 32;
	/**
	 * Declarations (import container / declaration, package declaration) are qualified.
	 * e.g. <code>java.util.Vector.class/import container</code>
	 */
	public final static long D_QUALIFIED= 1L << 24;
	/**
	 * Declarations (import container / declaration, package declaration) are post qualified.
	 * e.g. <code>import container - java.util.Vector.class</code>
	 */
	public final static long D_POST_QUALIFIED= 1L << 25;

	/**
	 * Returns the label for a Java element with the flags as defined by this class.
	 *
	 * @param element the element to render
	 * @param flags the rendering flags
	 * @param buf the buffer to append the resulting label to
	 */
	public static void getElementLabel(IJavaElement element, long flags, StringBuffer buf) {
		new JavaElementLabelComposer(buf).appendElementLabel(element, flags);
	}
}
