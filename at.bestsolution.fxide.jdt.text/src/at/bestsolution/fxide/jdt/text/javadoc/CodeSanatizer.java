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
package at.bestsolution.fxide.jdt.text.javadoc;

public class CodeSanatizer {
	public static String sanatizeCodeContent(String content) {
//		System.err.println("INPUT: " + content);
		if( ! content.contains("{@code") ) {
			return content;
		}

		int fromIndex = 0;
		StringBuffer b = new StringBuffer();
		while( true ) {
			int start = content.indexOf("{@code", fromIndex);
			if( start != -1 ) {
				b.append(content.substring(fromIndex,start)+"{@code");
				fromIndex = start + "{@code".length();
				int counter = 1;
				for( ; fromIndex < content.length(); fromIndex++ ) {
					if( content.charAt(fromIndex) == '}' ) {
						counter -= 1;
						if( counter == 0 ) {
							b.append("}");
							fromIndex += 1;
							break;
						} else {
							b.append("#__");
						}
					} else if( content.charAt(fromIndex) == '{' ) {
						counter += 1;
						b.append("__#");
					} else {
						b.append(content.charAt(fromIndex));
					}
				}

			} else {
				b.append(content.substring(fromIndex));
				break;
			}
		}
//		System.err.println("OUTPUT: " + b.toString());

		return b.toString();
	}
}
