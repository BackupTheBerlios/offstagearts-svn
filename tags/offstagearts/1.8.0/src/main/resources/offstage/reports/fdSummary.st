$if(person._isorg)$
<h2>Org: $person.orgname$ ($person.firstname$ $person.middlename$ $person.lastname$)</h2>
$else$
<h2>$person.firstname$ $person.middlename$ $person.lastname$ $if(person._orgname)$($person.orgname$)$endif$</h2>
$endif$
<table>
<tr><th align="right">Name</th><td>$person.firstname$ $person.middlename$ $person.lastname$</td></tr>
<tr><th align="right">Address</th><td>$person.address1$</td></tr>
<tr><th align="right"></th><td>$person.address2$</td></tr>
<tr><th align="right"></th><td>$person.city$, $person.state$ $person.zip$</td></tr>
<tr><th align="right">DOB</th><td>$person.dob$</td></tr>
<tr><th align="right">Occup.</th><td>$person.occupation$</td></tr>
<tr><th align="right">Title</th><td>$person.title$</td></tr>
<tr><th align="right">Email</th><td>$person.email$</td></tr>
<tr><th align="right">URL</th><td>$person.url$</td></tr>
$phones:{<tr><th align="right">Phone ($it.groupid$)</td><td>$it.phone$</td></tr>}$
<tr><th align="right">Mail Pref</th><td>$person.mailprefid$</td></tr>
<tr><th align="right">Last Modified</th><td>$person.lastupdated$</td></tr>
<tr><th align="right">ID</th><td>$person.entityid$ ($person.dbid$)</td></tr>
</table>

$if(_ocdiscs)$
<hr>
<h3>Discount Codes</h3>
<table>
<tr><th>Name</th><th>From</th><th>To</th></tr>
$ocdiscs:{it |
<tr><td><b>$it.ocdiscid$</b></td><td>$it.dstart$</td><td>$it.dend$</td></tr>
}$
</table>
$endif$