'(C)2002 Quiksoft Corporation
'All Rights Reserved
'06/14/2002 
'This software is the proprietary information of Quiksoft Corporation  
'Use is subject to license terms.

'To do: Set the temp directory, licensekey, mail server, and pop3 account information.

dim strDirectory
strDirectory = "C:\data\"

Main


sub Main()

    Dim objPOP3, nCnt
    Dim nBounceType, nId, nPos1, nPos2
    Dim strBodyText, strToAddr, nOrdinal, strConnection, nRetVal
    
    Set objPOP3 = CreateObject("EasyMail.POP3")

    objPOP3.LicenseKey = ""
    objPOP3.MailServer = ""
    objPOP3.Account = ""
    objPOP3.Password = ""

    nRetVal = objPOP3.Connect()
    If Not nRetVal = 0 Then
        MsgBox "Error connecting to mail server."
        exit sub
    End If
    
    ' Preparse the database.
    Set cnnData = CreateObject("ADODB.Connection")
    
    strConnection = "DBQ=" & strDirectory & "email_database.mdb"
    cnnData.Open "DRIVER={Microsoft Access Driver (*.mdb)}; " & strConnection
    
    Set rs = CreateObject("ADODB.RecordSet")
    rs.Open "SELECT * FROM email_table", cnnData, 1, 3
   
    nCnt = objPOP3.GetDownloadableCount()
    
    For x = 1 To nCnt
        nOrdinal = objPOP3.DownloadSingleMessage(x)
        If nOrdinal < 0 Then
             MsgBox "There was an error downloading the message. " & x
             exit sub
        End If
        strBodyText = objPOP3.Messages(nOrdinal).BodyText
         
         'get id from To: address
	For Each Recipinet In objPOP3.Messages(nOrdinal).Recipients
            strToAddr = Recipinet.Address
            If LCase(Left(strToAddr, 6)) = "bounce" Then Exit For
        Next
	'If address is not found then try searching timestamps.
	If Not LCase(Left(strToAddr, 6)) = "bounce" Then
            For Each TimeS In objPOP3.Messages(nOrdinal).Timestamps
                strToAddr = TimeS.For
                If LCase(Left(strToAddr, 6)) = "bounce" Then Exit For
            Next
        End If

        'If it is a bounce message we will process it
        If Left(strToAddr, 6) = "bounce" And InStr(strToAddr, "_") Then
            nPos1 = InStr(strToAddr, "_") + 1
            nPos2 = InStr(strToAddr, "@")
            
            If nPos2 > nPos1 Then
                nId = Mid(strToAddr, nPos1, nPos2 - nPos1)
            End If
        
            nBounceType = CheckMessage(strBodyText)
              
            If nBounceType > 0 Then
               'update database
               rs.Find ("id=" & nId)
               If rs.EOF = False Then
                  If nBounceType = 1 Then
                     rs("soft_bounces") = rs("soft_bounces") + 1
                  Else
                     rs("hard_bounces") = rs("hard_bounces") + 1
                  End If
               End If
               
               rs.update
               'remove message from bounce box
               objPOP3.DeleteSingleMessage x
  	    elseif nBounceType = 0 then
	       'If nBounceType is 0 then it is a warning message or auto-response. 
                objPOP3.DeleteSingleMessage x
            End If
        End If
        objPOP3.Messages.DeleteAll
    Next
    
    objPOP3.Disconnect
    rs.Close
    msgbox "Operation Complete."
End sub

Function CheckMessage(strBodyText)

    Set st = CreateObject("ADODB.Stream")
    Set rs = CreateObject("ADODB.RecordSet")

    st.Open
    st.LoadFromFile (strDirectory & "bounce_signatures.xml")

    rs.Open st
    rs.Sort = "weight DESC"

    CheckMessage = 0

    Do While Not rs.EOF
        If InStr(1, strBodyText, rs("signature"), vbTextCompare) Then
            CheckMessage = rs("weight")
       End If
        rs.MoveNext
    Loop

    rs.Close

End Function