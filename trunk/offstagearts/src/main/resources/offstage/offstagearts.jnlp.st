<?xml version="1.0" encoding="utf-8"?>
<!-- JNLP File for Notepad -->

<jnlp spec="1.0+"
      codebase="http://offstagearts.org/releases/offstagearts"
      href="offstagearts-$releaseVersion$.jnlp">
   <information>
      <title>OffstageArts</title>
      <vendor>OffstageArts.org</vendor>
      <description>Arts Management Database Client</description>
      <homepage href="http://offstagearts.org"/>
      <icon href="http://offstagearts.org/releases/offstagearts/images/logo64.gif"/>
      <offline-allowed/>
   </information>
<security>
   <all-permissions/>
</security> 
   <resources>     
	<j2se version="1.5"
	      href="http://java.sun.com/products/autodl/j2se"/>

$jars:{<jar href="$jarbase$$it$"/>
}$

   </resources>
   <application-desc main-class="offstage.gui.OffstageLauncher"/>
</jnlp>
