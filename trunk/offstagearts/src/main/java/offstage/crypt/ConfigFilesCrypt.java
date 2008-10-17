/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.crypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

/**
 * Encrypt and decrypt configuration files using PBE
 * @author citibob
 */
public abstract class ConfigFilesCrypt {

/** Programmer-defined way to get the password */
abstract char[] getPassword();

	
/** Encrypt a file, removing the original */
public void encryptFile(File fin)
throws IOException
{
	try {
		PBECrypt pbe = new PBECrypt();
		String contents = FileUtils.readFileToString(fin);
//		if (!contents.startsWith(PBECrypt.BEGIN_ENCRYPTED));
		File fout = new File(fin.getPath() + ".crypt");
		FileUtils.writeStringToFile(fout,
			pbe.encrypt(contents, getPassword()));
		fin.delete();
	} catch(IOException ioe) {
		throw ioe;
	} catch(Exception e) {
		IOException ioe = new IOException(e.getMessage());
		ioe.initCause(e);
		throw ioe;
	}
}

public void encryptDir(File dir) throws IOException
{
	File[] files = dir.listFiles();
	for (File f : files) {
		if (f.getName().endsWith(".properties")) encryptFile(f);
	}
}

static String readURLToString(URL url) throws IOException
{
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	byte[] bytes = new byte[8192];
	InputStream in = url.openStream();
	int n;
	while ((n = in.read(bytes)) > 0) out.write(bytes, 0, n);
	return out.toString("UTF8");
	
}

/** Encrypt a file, removing the original */
public String readCryptedURL(URL url)
throws IOException
{
	// First try to read the unencrypted version
	try {
		return readURLToString(url);
	} catch(IOException e) {
		try {
			// Unencrypted version does not exist; read encrypted version
			char[] password = getPassword();
			URL cipherURL = new URL(url.toExternalForm() + ".crypt");
			String cipherString = readURLToString(url);
			PBECrypt pbe = new PBECrypt();
			return pbe.decrypt(cipherString, password);
		} catch(IOException ioe) {
			throw ioe;
		} catch(Exception e2) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e2);
			throw ioe;
		}
	}
}

/**
     * Reads user password from given input stream.
     */
    public static char[] readPassword(InputStream in) throws IOException {
        char[] lineBuffer;
        char[] buf;
        int i;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

loop:   while (true) {
            switch (c = in.read()) {
              case -1: 
              case '\n':
                break loop;

              case '\r':
                int c2 = in.read();
                if ((c2 != '\n') && (c2 != -1)) {
                    if (!(in instanceof PushbackInputStream)) {
                        in = new PushbackInputStream(in);
                    }
                    ((PushbackInputStream)in).unread(c2);
                } else 
                    break loop;

              default:
                if (--room < 0) {
                    buf = new char[offset + 128];
                    room = buf.length - offset - 1;
                    System.arraycopy(lineBuffer, 0, buf, 0, offset);
                    Arrays.fill(lineBuffer, ' ');
                    lineBuffer = buf;
                }
                buf[offset++] = (char) c;
                break;
            }
        }

        if (offset == 0) {
            return null;
        }

        char[] ret = new char[offset];
        System.arraycopy(buf, 0, ret, 0, offset);
        Arrays.fill(buf, ' ');

        return ret;
    }
	
/** Encrypt a full directory */
public static void main(String[] args)
{
	ConfigFilesCrypt crypt = new ConfigFilesCrypt() {
	public char[] getPassword() {
		return readPassword(System.in);
	}};
	new ConfigFilesCrypt().encryptDir(new File(args[0]));
}

}
