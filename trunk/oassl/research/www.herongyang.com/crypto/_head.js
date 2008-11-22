function adsLeft() {
google_ad_client = "pub-8765173615603734";
google_ad_width = 160;
google_ad_height = 600;
google_ad_format = "160x600_as";
google_ad_type = "text";
google_ad_channel ="";
google_color_border = "fbfbfb";
google_color_bg = "fbfbfb";
google_color_link = "0000CC";
google_color_url = "008000";
google_color_text = "000000";
}

function adsTop() {
google_ad_client = "pub-8765173615603734";
google_ad_width = 468;
google_ad_height = 60;
google_ad_format = "468x60_as";
google_ad_type = "text";
google_ad_channel ="";
google_color_border = "fbfbfb";
google_color_bg = "fbfbfb";
google_color_link = "0000CC";
google_color_url = "008000";
google_color_text = "000000";
}

function adsBottom() {
google_ad_client = "pub-8765173615603734";
google_ad_width = 728;
google_ad_height = 15;
google_ad_format = "728x15_0ads_al_s";
google_ad_channel ="";
}

function showPageFooter0(curPath, curFile) {
   document.write('<p class="syndication">');
   document.write(' <a href="http://fusion.google.com/add?feedurl='+curPath+'/atom.xml"><img src="add_google.gif" border="0" alt="Add to Google"></a>');
   document.write(' <a href="http://add.my.yahoo.com/rss?url='+curPath+'/atom.xml"><img border="0" src="http://us.i1.yimg.com/us.yimg.com/i/us/my/addtomyyahoo4.gif" alt="Add to MyYahoo!"></a>');
   document.write(' <a href="http://www.bloglines.com/sub/'+curPath+'/atom.xml"><img src="http://www.bloglines.com/images/sub_modern11.gif" border="0" alt="Add to Bloglines"/></a>');
   document.write(' <a href="http://www.newsgator.com/ngs/subscriber/subext.aspx?url='+curPath+'/atom.xml"><img runat="server" src="http://www.newsgator.com/images/ngsub1.gif" alt="Add to NewsGator" border="0" /></a>');
   document.write(' <a href="http://www.netvibes.com/subscribe.php?url='+curPath+'/atom.xml"><img src="http://www.netvibes.com/img/add2netvibes.gif" border="0" alt="Add to Netvibes"/></a>');
   document.write(' <a href="atom.xml"><img src="rss_feed.gif" alt="RSS Feed (Atom 1.0)" border="0"></a>');
   document.write('</p>');
   document.write('<p class="syndication">');
   document.write(' <a href="http://digg.com/submit?phase=2&url='+curPath+'/'+curFile+'"><img src="http://www.digg.com/favicon.ico" alt="Add to Digg" border="0"></a>');
   document.write(' <a href="http://del.icio.us/post?url='+curPath+'/'+curFile+'&title=Cryptography Tutorials Notes by Herong"><img src="http://del.icio.us/favicon.ico" alt="Add to del.icio.us" border="0"></a>');
   document.write('</p>');

   document.write('<p class="hy_copyright">Copyright &#169; 2007 by Dr. Herong Yang. All rights reserved.</p>');
}

function showPageFooter(curPath, curFile) {
   showPageFooter0(curPath, curFile);

   document.write('<p class="section_title">Other Topics in This Book</p>');
   document.write('<p class="toc_item"><a href="about.html">About This Book</a></p>');
   document.write('<p class="toc_item"><a href="term.html">Terminology</a></p>');
   document.write('<p class="toc_item"><a href="concept.html">Basic Concepts</a></p>');
   document.write('<p class="toc_item"><a href="cipher_des.html">Cipher - DES Algorithm</a></p>');
   document.write('<p class="toc_item"><a href="des_implTest.html">DES Algorithm - Illustrated with Java Programs</a></p>');
   document.write('<p class="toc_item"><a href="des_implJava.html">DES Algorithm - Java Implementation</a></p>');
   document.write('<p class="toc_item"><a href="des_java_jce_sun_implementation.html">DES Algorithm - Java JCE SUN Implementation</a></p>');
   document.write('<p class="toc_item"><a href="des_encryption_operation_modes.html">DES Algorithm - Operation Modes and JCE SUN Implementation</a></p>');
   document.write('<p class="toc_item"><a href="des_in_stream_cipher_modes.html">DES Algorithm - Stream Cipher Modes and JCE SUN Implementation</a></p>');
   document.write('<p class="toc_item"><a href="des_php_implementation_mcrypt.html">DES Algorithm - PHP Implementation in mcrypt</a></p>');
   document.write('<p class="toc_item"><a href="jce_cipher.html">JDK/JCE - Cipher for Encryption and Decryption</a></p>');
   document.write('<p class="toc_item"><a href="cipher_blowfish.html">Cipher - Blowfish Algorithm</a></p>');
   document.write('<p class="toc_item"><a href="message_digest_md5.html">Message Digest - MD5 Algorithm</a></p>');
   document.write('<p class="toc_item"><a href="message_digest_sha1.html">Message Digest - SHA1 Algorithm</a></p>');
   document.write('<p class="toc_item"><a href="openssl.html">OpenSSL - Installation on Windows</a></p>');
   document.write('<p class="toc_item"><a href="openssl_rsa.html">OpenSSL - Generating RSA Private and Public Keys</a></p>');
   document.write('<p class="toc_item"><a href="openssl_crt.html">OpenSSL - Generating Self-Signed Certificates</a></p>');
   document.write('<p class="toc_item"><a href="openssl_csr.html">OpenSSL - Signing Certificates from Others</a></p>');
   document.write('<p class="toc_item"><a href="openssl_verify.html">OpenSSL - Certification Path and Validation</a></p>');
   document.write('<p class="toc_item"><a href="jca_keytool.html">keytool - JDK Tool to Manage Certificates Using keystore</a>');
   document.write('<p class="toc_item"><a href="web_browser.html">Using Certificates with Web Browsers</a></p>');
   document.write('<p class="toc_item"><a href="OpenSSL_Signing_keytool_CSR.html">OpenSSL Signing CSR Generated by keytool</a>');
   document.write('<p class="toc_item"><a href="Migrating_Keys_keytool_to_OpenSSL.html">Migrating Keys from keytool to OpenSSL</a>');
   document.write('<p class="toc_item"><a href="Certificate_Formats_X509_DER_PEM.html">Certificate Formats - X.509, DER and PEM</a>');
   document.write('<p class="toc_item"><a href="Key_Formats_PKCS8_PKCS12.html">Key Formats PKCS#8 and PKCS#12 and Migration</a>');
   document.write('<p class="toc_item"><a href="reference.html">References</a></p>');
}

function writeMenu(subject) {
   document.write('[ <a href="../index.html">Home</a> | ');
   document.write('<a href="../news.html">News</a> | ');
   document.write('<a href="../search.html">Search</a> | ');
   document.write('<a href="../comments/?TopicID=2">Comments</a> ]&nbsp; &nbsp; ');

   document.write('[ <a href="pdf.html">PDF</a> | ');
   document.write('<a href="index.html">Index</a> ]');
}

function writePageLeft(subject) {
   document.write('<img src=dot.gif width=160 height=1/>');
   document.write('<p>&nbsp;</p>');
   document.write('<p class="g_key"><a href="/hySite">Download: hySite</a></p>');
   document.write('<p class="g_des">Simple site directory in ASP</p>');
   document.write('<p class="g_key"><a href="/hyBook">Download: hyBook</a></p>');
   document.write('<p class="g_des">Simple guestbook in ASP</p>');

   document.write('<img src=dot.gif width=160 height=1/>');
   document.write('<p>&nbsp;</p>');
   document.write('<p class="g_key"><a href="/2008">2008 Chinese Calendar</a></p>');
   document.write('<p class="g_key"><a href="/chinese/wedding/wedding_dates_2008.html">2008 Wedding Calendar</a></p>');
   document.write('<p class="g_key"><a href="/chinese/index.html">More...</a></p>');

   document.write('<p class="g_key"></p>');
   document.write('<img src=dot.gif width=160 height=1/>');
   document.write('<p class="g_key">&nbsp;</p>');
   document.write('<p class="g_key"><a href="http://validator.w3.org/check?uri=referer"><img src="http://www.w3.org/Icons/valid-xhtml10" alt="Valid XHTML 1.0 Strict" height="31" width="88" border="0"/></a></p>');
}

function writeRight(subject) {
   document.write('<img src=dot.gif width=220 height=1/>');
   document.write('<p class="g_key">Free Tutorial Books by Herong</a></p>');
   document.write('<p class="g_des">-----------------------</p>');
   document.write('<img src=dot.gif width=200 height=1/>');
   document.write('<p class="g_key"><a href="/PHP-Chinese/">Chinese Web Sites using PHP</a></p>');
   document.write('<p class="g_des">PHP, UTF-8, GBK, GB18030, Big5...</p>');

   document.write('<p class="g_key"><a href="/jdbc/">Java JDBC Tutorials</a></p>');
   document.write('<p class="g_des">Connection, ResultSet, SQL Server...</p>');

   document.write('<p class="g_key"><a href="../vb_script/">VB Script Tutorials</a></p>');
   document.write('<p class="g_des">Variant, Array, Loop, Function...</p>');

   document.write('<p class="g_key"><a href="../jtool">Java Tools Tutorials</a></p>');
   document.write('<p class="g_des">javac, java, jar, jdb, keytool...</p>');

   document.write('<p class="g_key"><a href="../jsp">Tutorial Book: JSP</a></p>');
   document.write('<p class="g_des">Cookie, JavaBean, JSTL, Tags...</p>');

   document.write('<p class="g_key"><a href="../jdk">Tutorial Book: JDK</a></p>');
   document.write('<p class="g_des">XML, DOM, keytool & keystore...</p>');

   document.write('<p class="g_key"><a href="../php">Tutorial Book: PHP</a></p>');
   document.write('<p class="g_des">Session, Cookie, MySQL, SOAP...</p>');

   document.write('<p class="g_key"><a href="../perl">Tutorial Book: Perl - Part A</a></p>');
   document.write('<p class="g_des">References, Class, Binary, DBM...</p>');

   document.write('<p class="g_key"><a href="../crypto">Tutorial Book: Cryptography</a></p>');
   document.write('<p class="g_des">Blowfish, DES, SSL, OpenSSL...</p>');

   document.write('<p class="g_key"><a href="../asp">Tutorial Book: ASP</a></p>');
   document.write('<p class="g_des">Cookies, Sessions, Response...</p>');

   document.write('<p class="g_key"><a href="../swing">Tutorial Book: Swing</a></p>');
   document.write('<p class="g_des">JFrame, Layouts, Localization...</p>');

   document.write('<p class="g_key"><a href="../xml">Tutorial Book: XML Technologies</a></p>');
   document.write('<p class="g_des">DOM, SAX, XSD, XSL, XPath, DTD...</p>');

   document.write('<p class="g_key"><a href="../gb2312">Book: GB2312 Character Set</a></p>');
   document.write('<p class="g_des">Unicode, UTF8, Mapping table...</p>');

   document.write('<p class="g_key"><a href="../java">Tutorial Book: Java</a></p>');
   document.write('<p class="g_des">Threads, Synchronize, Deadlock...</p>');

   document.write('<p class="g_key"><a href="../sql">Tutorial Book: SQL</a></p>');
   document.write('<p class="g_des">MySQL, Locks, Stored procedure...</p>');

   document.write('<p class="g_key"><a href="../ws">Book: SOAP & Web Service</a></p>');
   document.write('<p class="g_des">SOAP 1.1, Protocol Binding, WSDL...</p>');

   document.write('<p class="g_key"><a href="../jvm">Tutorial Book: JVM</a></p>');
   document.write('<p class="g_des">HotSpot, JRockit, GC, Memory...</p>');

   document.write('<p class="g_key"><a href="../xhtml">Tutorial Book: XSLT & XHTML</a></p>');
   document.write('<p class="g_des">XSL-FO, FOP, Generating PDF...</p>');

   document.write('<p class="g_key"><a href="../perl_b">Tutorial Book: Perl - Part B</a></p>');
   document.write('<p class="g_des">Socket, XML, SOAP, RPC, CGI...</p>');

   document.write('<p class="g_key"><a href="../sort">Book: Sorting Algorithms</a></p>');
   document.write('<p class="g_des">Quicksort, Merge, Heap, Shell...</p>');

   document.write('<p class="g_key"><a href="../cs_a">Tutorial Book: C# - Part A </a></p>');
   document.write('<p class="g_des">Data type, Expression, .NET...</p>');

   document.write('<p class="g_key"><a href="../cs_b">Tutorial Book: C# - Part B</a></p>');
   document.write('<p class="g_des">"double", "decimal", Precision...</p>');

   document.write('<p class="g_key"><a href="../year">200 Years of Chinese Calendar</a></p>');
   document.write('<p class="g_des">Algorithm, Printable calendars...</p>');

   document.write('<p class="g_key"><a href="../data">Tutorial Book: Data Encoding</a></p>');
   document.write('<p class="g_des">Base64, UUEncode, Java code...</p>');

   document.write('<p class="g_key"><a href="../unicode">Tutorial Book: Unicode</a></p>');
   document.write('<p class="g_des">Character set, UTF8, Conversion...</p>');

   document.write('<p class="g_key"><a href="../win">Tutorial Book: Windows</a></p>');
   document.write('<p class="g_des">FTP, ZIP, RAR, Crossover cable...</p>');

   document.write('<img src=dot.gif width=200 height=1/>');
   document.write('<p class="g_key"><a href="../gb2312_gb">In Chinese: GB2312 Character Set</a></p>');
   document.write('<p class="g_des">Unicode, UTF8, Mapping table...</p>');

   document.write('<p class="g_key"><a href="../xhtml">In Chinese: XSLT & XHTML</a></p>');
   document.write('<p class="g_des">XSL-FO, FOP, Generating PDF...</p>');

   document.write('<p class="g_key"><a href="../year_gb">In Chinese: 200 Years Calendar</a></p>');
   document.write('<p class="g_des">Algorithm, Printable calendars...</p>');
   
   document.write('<img src=dot.gif width=200 height=1/>');
   document.write('<p class="g_key">Copyright &#169; Dr. Herong Yang</p>');
   document.write('<p class="g_des">1995 - 2007</p>');
   document.write('<img src=dot.gif width=200 height=1/>');
   document.write('<p class="g_des">WebCounter <img src="http://counter.digits.com/wc/-d/4/herong" alt="Hits" align="center" width="80" height="20" border="0"/></p>');
}

function writeBottom(subject) {
}

function writeSection(subject) {
}

function writeTop(subject) {
}

function writeLeft(subject) {
}
