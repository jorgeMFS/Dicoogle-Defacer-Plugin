package pt.ieeta.dicoogledefacerplugin.core.util;

import java.io.IOException;

/**
 * @author: Eduardo Pinho
 */
public class RuntimeIOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuntimeIOException(IOException e) {
		super(e);
	}

	public IOException getCause() {
		return (IOException)super.getCause();
	}
}