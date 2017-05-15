package at.bestsolution.fxide.jdt.text.javadoc;

public class CodeSanatizer {
	public static String sanatizeCodeContent(String content) {
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
				b.append(content.substring(fromIndex+1));
				break;
			}
		}

		return b.toString();
	}
}
