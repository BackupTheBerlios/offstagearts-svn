/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.email;

import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;
import com.jangomail.api.JangoMailSoap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import offstage.FrontApp;
import offstage.Jango;

/**
 *
 * @author citibob
 */
public class VettEmail {
	
public static String getHeader(Part msg, String headerName) throws Exception
{
	String[] ct = msg.getHeader(headerName);
	if (ct == null || ct.length == 0) return null;
	
	return ct[0];
}

public static String getBoundary(String contentTypeHeader) throws Exception
{
	String lheader = contentTypeHeader.toLowerCase();
	
	int c = lheader.indexOf("boundary");
	if (c < 0) return null;
	c += "boundary".length();
	
	for (; Character.isWhitespace(lheader.charAt(c)); ++c) ;	// Skip whitespace
	if (lheader.charAt(c) != '=') throw new ParseException("Cannot parse header: " + contentTypeHeader, c);
	++c;	// skip the '='
	for (; Character.isWhitespace(lheader.charAt(c)); ++c) ;	// Skip whitespace

	int start,next;
	if (lheader.charAt(c) == '"') {
		start = c+1;
		next = lheader.indexOf('"', start);
	} else {
		start = c;
		next = lheader.indexOf(';', start);
		if (next < 0) next = lheader.length();
	}
	return contentTypeHeader.substring(start, next).trim();
}
public static String getBoundary(Part msg) throws Exception
{
	String header = getHeader(msg, "Content-type");
	if (header == null) return null;
//	if (header == null) {
//		header = getHeader(msg, "Content-Type");
//		if (header == null) return null;
//	}
	
	return getBoundary(header);
}

// --------------------------------------------------
static String currentCustomersIdSql =
	" select distinct te.entityid as id\n" +
	" from termenrolls te, termids t\n" +
	" where te.groupid = t.groupid\n" +
	" and t.iscurrent\n" +
	" 	union\n" +
	" select distinct tr.payerid as id\n" +
	" from termenrolls te, termids t, termregs tr\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = tr.entityid\n" +
	" and t.iscurrent\n" +
	" and tr.payerid is not null\n" +
	" 	union\n" +
	" select distinct e.parent1id as id\n" +
	" from termenrolls te, termids t, entities e\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = e.entityid\n" +
	" and t.iscurrent\n" +
	" and e.parent1id is not null\n" +
	" 	union\n" +
	" select distinct e.parent2id as id\n" +
	" from termenrolls te, termids t, entities e\n" +
	" where te.groupid = t.groupid\n" +
	" and te.entityid = e.entityid\n" +
	" and t.iscurrent\n" +
	" and e.parent2id is not null\n";

public static String checkSchoolEmailQuery(String idSql)
{
	return
		" create temporary table _mm\n" +
		" (id int primary key,\n" +
		" iscurrent bool not null default false,\n" +
		" hasemail bool not null default false);\n" +
		" \n" +
		" insert into _mm (id, iscurrent, hasemail)\n" +
		" select list.id,\n" +
		" case when current.id is not null then true else false end as iscurrent,\n" +
		" case when e.email is not null then true else false end as hasemail\n" +
		" from (" + idSql + ") list\n" +
		" inner join persons e on list.id = e.entityid\n" +
		" left outer join (" + currentCustomersIdSql + ") current on list.id = current.id;\n";

}
public static String checkBulkEmailQuery(String idSql)
{
	return
		" create temporary table _mm\n" +
		" (id int primary key,\n" +
		" iscurrent bool not null default false,\n" +
		" hasemail bool not null default false);\n" +
		" \n" +
		" insert into _mm (id, iscurrent, hasemail)\n" +
		" select list.id,\n" +
		" case when current.id is not null then true else false end as iscurrent,\n" +
		" case when e.email is not null then true else false end as hasemail\n" +
		" from (" + idSql + ") list\n" +
		" inner join persons e on list.id = e.entityid\n" +
		" left outer join (" + currentCustomersIdSql + ") current on list.id = current.id;\n";

}
// ---------------------------------------------------------------------------
/** JangoMail doesn't do Multipart/Alternative messages.  This bug is worked
 * around by wrapping such messages.
 * @param msg
 */
public static byte[] wrapMultipartAlternative(byte[] msgBytes)
throws MessagingException, IOException, Exception
{
	// Parse original msg
	//System.out.println(new String(msg.body));
	MimeMessage mm = new MimeMessage(null, new ByteArrayInputStream(msgBytes));
	MimeMultipart multipart = (MimeMultipart)mm.getContent();
	if (multipart.getContentType().contains("multipart/alternative")) {
		// Wrap it
		MimeMultipart newMultipart = new MimeMultipart();
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setContent(multipart);
		newMultipart.addBodyPart(mbp);

//	System.out.println("contentType = " + newMultipart.getContentType());
//		msg.boundary = getBoundary(newMultipart.getContentType());

		MimeMessage newMM = new MimeMessage((Session)null);
		newMM.setContent(newMultipart);
		newMM.setSubject(mm.getSubject());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		newMM.writeTo(baos);
		return baos.toByteArray();
	//	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//	StreamUtils.copy(newMultipart.getInputStream(), baos);
	//	msg.body = baos.toByteArray();
	//	msg.boundary = getBoundary(newMM);

//	System.out.println("=======================================================");
//	System.out.println(new String(msg.body));
	} else {
		return msgBytes;
	}

}
// ---------------------------------------------------------------------------

public static final int ET_BULK = 0;
public static final int ET_CUSTOMER = 1;

/** @param idSql People to send email to (we need to weed out bad addresses) */
public static void sendJangoMail(final FrontApp app, SqlRun str,
final byte[] msgBytes, String idSql0,
final String equeryXML, final Integer equeryid,
final int emailType)
{
	str.execSql(checkSchoolEmailQuery(idSql0));
	
	String sql =
		" select e.entityid, e.email as EmailAddress, e.firstname, e.lastname" +
		" from _mm, persons e" +
		" where _mm.iscurrent and _mm.hasemail\n" +
		" and _mm.id = e.entityid";

	
	
//ToOther=
//John,Smith,john@smith.com|David,Gary,david@gary.com|Sheila,
//Panther,sheila@panther.com
//
//Options=
//ToOtherRowDelimiter=|,ToOtherColDelimiter=c,
//ToOtherFieldNames=FirstName|LastName|EmailAddress
//
//Subject=
//Hello %%FirstName%%
//
//Message=
//Hello %%FirstName%% %%LastName%% -- your email address is %%EmailAddress%%
		

	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		// Keep things clean...
//		app.jango().deleteOldGroups();

		Jango jango = app.jango();
		JangoMailSoap soap = jango.soap;
		String usr = jango.usr;
		String pwd = jango.pwd;

		// GroupName
		String groupName = "Group-" + Jango.groupNameFmt.format(new java.util.Date());

		// FieldNames
		ResultSetMetaData md = rs.getMetaData();
		StringBuffer fieldNames = new StringBuffer();
		for (int i=0; i<md.getColumnCount(); ++i) {
			String fieldName = md.getColumnName(i+1);
			if ("emailaddress".equals(fieldName.toLowerCase())) fieldName = "EmailAddress";
			fieldNames.append(fieldName);
			if (i < md.getColumnCount() - 1) fieldNames.append(',');
		}

		// ImportData
		StringBuffer importData = new StringBuffer();
		while (rs.next()) {
			for (int i=0; i<md.getColumnCount(); ++i) {
				// Append the String from SQL.
				// Change whitespace to ' '.  The assumption is that if anything
				// is a large block of text, it will end up as part of an HTML
				// email, and thus whitespace is all equivalent anyway.  This prevents
				// whitespace from interfering with our tab and newline delimiters.
				String s = rs.getString(i+1);
				for (int j=0; j<s.length(); ++j) {
					if (Character.isWhitespace(s.charAt(j))) importData.append(' ');
					else importData.append(s.charAt(j));
				}
				
				// Delimit the column with TAB
				if (i < md.getColumnCount()-1) importData.append('\t');
			}
			
			// Delimit the row with a newline.
			importData.append('\n');
		}

		String columnDelimiter = "t";
		String rowDelimiter = "l";
		String textQualifier = "";
				
		// Create Group in JangoMail
		System.out.println("Setting up group...");
		soap.addGroup(usr, pwd, groupName);
		
		// Add fields to the group
		for (int i=0; i<md.getColumnCount(); ++i) {
			String fieldName = md.getColumnName(i+1);
			if ("emailaddress".equals(fieldName.toLowerCase())) continue;		// Added by default in JangoMail
			soap.addGroupField(usr, pwd, groupName, fieldName);
		}
		

		// Set up data in the group
		String ret = soap.importGroupMembersFromData(usr, pwd, groupName,
				fieldNames.toString(), importData.toString(),
				columnDelimiter, rowDelimiter, textQualifier);
System.out.println("importGroupMembersFromData return: " + ret);


		

		// Email to the group
		String fromEmail = usr + "@jangomail.com";
		String fromName = "Bob Fischer";
		String toGroups = groupName;
		String toGroupFilter = "";
		String toOther = "";
		String toWebDatabase = "";
//		String subject = msg.subject;
//		String rawMessage = new String(msg.body);
//		String boundary = msg.boundary;
		String options = "";
		
		
//difference between school and bulk email will be here.  For bulk email,
//we need to make sure that CAN SPAM provisions have been followed.

		if (true || emailType == ET_CUSTOMER) {
			// Work around JangoMail bug for some messages
			byte[] msgBytes2 = wrapMultipartAlternative(msgBytes);
			
			// Parse our final message
			MimeMessage msg = new MimeMessage(null, new ByteArrayInputStream(msgBytes2));
			String subject = msg.getSubject();
			String boundary = getBoundary(msg);
		
			ret = soap.sendMassEmailRaw(usr, pwd, fromEmail, fromName,
				toGroups, toGroupFilter, toOther, toWebDatabase,
				subject, new String(msgBytes2), boundary, options);
		} else {
			MimeMessage msg = new MimeMessage(null, new ByteArrayInputStream(msgBytes));
			String subject = msg.getSubject();
			
			MimeMultipart multipart = (MimeMultipart)msg.getContent();
			String contentType = multipart.getContentType();
			String messagePlain = null;
			String messageHtml = null;
			if (contentType.contains("multipart/alternative")) {
				// Assume two-part email: first text, then HTML
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				MimeMultipart htmlPart = (MimeMultipart)multipart.getBodyPart(1);
MimeMultipart htmlContent = (MimeMultipart)multipart.getBodyPart(1).getContent();
				htmlContent.getBodyPart(1).writeTo(baos);
				messageHtml = new String(baos.toByteArray());
				
				messagePlain = (String)multipart.getBodyPart(0).getContent();
//				baos = new ByteArrayOutputStream();
//				multipart.getBodyPart(0).writeTo(baos);
//				messagePlain = new String(baos.toByteArray());
messagePlain = "Hello World!";
messageHtml = "<html><h2>Hello World!</h2><p>Now it is.</p></html>";
			} else if (contentType.contains("multipart/mixed")
				|| contentType.contains("text/html")) {
				// Assume JangoMail can deal with the whole thing as HTML
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				multipart.writeTo(baos);
				messageHtml = new String(baos.toByteArray());
			} else {
				// Punt, figure it's all text.
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				multipart.writeTo(baos);
				messagePlain = new String(baos.toByteArray());
			}
			
			ret = soap.sendMassEmail(usr, pwd, fromEmail, fromName,
				toGroups, toGroupFilter, toOther, toWebDatabase,
				subject, messagePlain, messageHtml, options);
		}
System.out.println("sendMassEmail return: " + ret);
		String campaignName = ret.substring(ret.lastIndexOf('\n') + 1).trim();
		
		// Record this campaign in our database...
		String sql =
			" insert into emailingids (emailproviderid, emailingtype,\n" +
			" campaignname, groupname, equeryid, equery) values (\n" +
			" (select emailproviderid from emailproviderids where name='JangoMail'),\n" +
			" 'c', " + SqlString.sql(campaignName) + ", " +
			SqlString.sql(groupName) + "," +
			SqlInteger.sql(equeryid) + "," +
			SqlString.sql(equeryXML) + ");\n" +
			
			" insert into emailings (emailingid, entityid)\n" +
			" select currval('emailingids_emailingid_seq'), id\n" +
			" from _mm;\n";
		
		str.execSql(sql);

		str.execSql(" drop table _mm;");
	}});
	
	
	
}

}
