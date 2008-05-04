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
<tr><th align="right">To</th><td>$person.addressto$</td></tr>
<tr><th align="right">Mail Pref</th><td>$person.mailprefid$</td></tr>
<tr><th align="right">Last Modified</th><td>$person.lastupdated$</td></tr>
<tr><th align="right">ID</th><td>$person.entityid$</td></tr>
</table>

$if(_notes)$
<hr>
<h3>Notes</h3>
$notes:{it |
<p><b>$it.date$</b>: $it.note$</p>
}$
$endif$

$if(_donations)$
<hr>
<h3>Donations</h3>
<table>
<tr><th>Type</th><th>Date</th><th>Amount</th></tr>
$donations:{it |
<tr><td><b>$it.groupid$</b></td><td>$it.date$</td><td align="right">$it.amount$</td></tr>
}$
</table>
$endif$

$if(_events)$
<hr>
<h3>Events</h3>
<table>
$events:{it |
<tr><td><b>$it.groupid$</b></td></tr>
}$
</table>
$endif$

$if(_tickets)$
<hr>
<h3>Tickets</h3>
<table>
<tr><th>Show</th><th>Type</th><th>Venue</th><th>Perf</th><th>#Tix</th>
<th>Payment</th><th>Offer Code</th></tr>
$tickets:{it |
<tr><td><b>$it.groupid$</b></td><td>$it.tickettypeid$</td><td>$it.venueid$</td>
<td>$it.perftypeid$</td><td align="right">$it.numberoftickets$</td><td align="right">$it.payment$</td>
<td>$it.offercodeid$</td></tr>
}$
</table>
$endif$

$if(_interests)$
<hr>
<h3>Interests</h3>
<table>
$interests:{it |
<tr><td>$it.groupid$</td></tr>
}$
</table>
$endif$

$if(_terms)$
<hr>
<h3>Terms</h3>
<table>
$terms:{it |
<tr><td><b>$it.groupid$</b></td><td>$it.courserole$</td></tr>
}$
</table>
$endif$

$if(_flags)$
<hr>
<h3>Flags</h3>
<table>
$flags:{it |
<tr><td>$it.groupid$</td></tr>
}$
</table>
$endif$
