'(C)2002 Quiksoft Corporation
'All Rights Reserved
'06/14/2002 

'To do: Set the following variables:
strLicenseKey = ""
strMailServer=""
strBounceBoxDomain=""
strFriendlyFromName=""
strFriendlyFromAddress=""
'End To Do

Dim objSMTP, Data, RS, nRetVal

'create EasyMail SMTP object and set basic properties
Set objSMTP = CreateObject("EasyMail.SMTP")
objSMTP.LicenseKey = strLicenseKey
objSMTP.MailServer = strMailServer
objSMTP.OptionFlags = 1
objSMTP.AddCustomHeader "From", _
           """" & strFriendlyFromName & """" & " <" & strFriendlyFromAddress & ">"
objSMTP.Subject = "Subject..."
objSMTP.BodyText = "Message text"

'setup database and select addresses.  This sample uses a access database.
Set cnnData = CreateObject("ADODB.Connection")
strConnection = "DBQ=email_database.mdb"
cnnData.Open "DRIVER={Microsoft Access Driver (*.mdb)}; " & strConnection
Set RS = CreateObject("ADODB.RecordSet")
RS.Open "SELECT hard_bounces,id, name, address FROM email_table where hard_bounces < 2 and soft_bounces < 8", cnnData, 1, 3

'send to each address selected
Do While RS.EOF = False
   
   'encode record id in from address
   objSMTP.FromAddr = "bounce_" & RS("id") & "@" & strBounceBoxDomain
   objSMTP.AddRecipient RS("name"), RS("address"), 1
   nRetVal = objSMTP.Send
   
   'if the recipients address fails right away then we mark it as
   'a hard bounce now.
   If nRetVal = 8 Then
        RS("hard_bounces") = RS("hard_bounces") + 1
   End If
   
   'remove the recipients
   objSMTP.Clear 1
   
   RS.MoveNext
   
Loop
	
'free remaining resources
RS.Close

msgbox "Operation Complete."
