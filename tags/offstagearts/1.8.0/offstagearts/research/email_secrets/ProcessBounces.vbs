'(C)2002 Quiksoft Corporation
'All Rights Reserved
'06/14/2002 

'To do: Set the following variables:
strLicenseKey = ""
strMailServer=""
strAccount=""
strPassword=""
'End To Do

Main

sub Main()

	Dim objPOP3, nCnt
	Dim nBounceType, nId, nPos1, nPos2
	Dim strBodyText, strToAddr, nOrdinal
	Dim strConnection, nRetVal

	'create the EasyMail POP3 object and assign the basic properties
	Set objPOP3 = CreateObject("EasyMail.POP3")
	objPOP3.LicenseKey = strLicenseKey
	objPOP3.MailServer = strMailServer
	objPOP3.Account = strAccount
	objPOP3.Password = strPassword

	'connect to the mail server
	nRetVal = objPOP3.Connect()
	If Not nRetVal = 0 Then
		MsgBox "Error connecting to mail server."
		exit sub
	End If

	'prepare the database and select our e-mail table
	Set cnnData = CreateObject("ADODB.Connection")
	strConnection = "DBQ=email_database.mdb"
	cnnData.Open "DRIVER={Microsoft Access Driver (*.mdb)}; " & strConnection
	Set rs = CreateObject("ADODB.RecordSet")
	rs.Open "SELECT * FROM email_table", cnnData, 1, 3

	'get the count of messages waiting in the bounce box and
	'download and process each one
	nCnt = objPOP3.GetDownloadableCount()
	For x = 1 To nCnt
	
		nOrdinal = objPOP3.DownloadSingleMessage(x)
		If nOrdinal < 0 Then
			MsgBox "There was an error downloading the message. " & nOrdinal
			exit sub
		End If
		strBodyText = objPOP3.Messages(nOrdinal).BodyText
			
		'get id from To: address
		For Each Recipinet In objPOP3.Messages(nOrdinal).Recipients
			strToAddr = Recipinet.Address
			If LCase(Left(strToAddr, 6)) = "bounce" Then Exit For
		Next
		
		'if address is not found then try searching
		'timestamps (AKA received headers)
		If Not LCase(Left(strToAddr, 6)) = "bounce" Then
			For Each TimeS In objPOP3.Messages(nOrdinal).Timestamps
				strToAddr = TimeS.For
				If LCase(Left(strToAddr, 6)) = "bounce" Then Exit For
			Next
		End If

		'if it is a bounce message we will process it
		If Left(strToAddr, 6) = "bounce" And InStr(strToAddr, "_") Then
			nPos1 = InStr(strToAddr, "_") + 1
			nPos2 = InStr(strToAddr, "@")
		
			If nPos2 > nPos1 Then
				nId = Mid(strToAddr, nPos1, nPos2 - nPos1)
			End If

			'call the IdentifyBounce routing which scans the bodytext
			'for the phrases found in our xml file
			nBounceType = IdentifyBounce(strBodyText)
			
			If nBounceType > 0 Then
				
				'the message has been identified as a hard or soft bounce
				'so update the database
				rs.Find ("id=" & nId)
				If rs.EOF = False and rs.BOF=False Then
					If nBounceType = 1 Then
						rs("soft_bounces") = rs("soft_bounces") + 1
					Else
						rs("hard_bounces") = rs("hard_bounces") + 1
					End If
					'update changes
					rs.update
				End If

				'delete the message from the bounce box
				objPOP3.DeleteSingleMessage x
				
			elseif nBounceType = 0 then
			
				'If nBounceType is 0 then it is a warning message or auto-response. 
				'delete the message from the bounce box
				objPOP3.DeleteSingleMessage x
			End If
		End If

		'free the resources used by the parsed message.  
		'this call does not delete messages from the server.
		objPOP3.Messages.DeleteAll

	Next

	'disconnect from mail server and free remaining resources
	objPOP3.Disconnect
	rs.Close
	msgbox "Operation Complete."
	
End sub

Function IdentifyBounce(strBodyText)

	Set st = CreateObject("ADODB.Stream")
	Set rs = CreateObject("ADODB.RecordSet")

	st.Open
	st.LoadFromFile ("bounce_signatures.xml")

	rs.Open st
	rs.Sort = "weight DESC"

	IdentifyBounce = -1

	Do While Not rs.EOF
		If InStr(1, strBodyText, rs("signature"), vbTextCompare) Then
			IdentifyBounce = rs("weight")
		End If
		rs.MoveNext
	Loop

	rs.Close

End Function