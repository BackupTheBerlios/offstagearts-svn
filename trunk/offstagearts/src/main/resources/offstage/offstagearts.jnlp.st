<?xml version="1.0" encoding="utf-8"?>
<!-- JNLP File for Notepad -->

<jnlp spec="1.0+"
      codebase="http://offstagearts.org/releases/offstagearts"
      href="offstagearts-$releaseVersion$.jnlp">
   <information>
      <title>OffstageArts $releaseVersion$</title>
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
	<j2se version="1.5+"
	      href="http://java.sun.com/products/autodl/j2se"/>
$if(oalaunch)$<jar href="%oalaunch.jar%" />
$endif$
$jars:{<jar href="$jarBase$$it.jar$" download="$it.downloadType$"/>
}$

   </resources>
$if(oalaunch)$
   <application-desc main-class="oalaunch.Launch1">
$else$
   <application-desc main-class="offstage.gui.OffstageLauncher">
$endif$
$if(oalaunch)$<argument>--oalaunch</argument>
<argument>%configName%</argument>
$endif$
$args:{		<argument>$it$</argument>
}$
	</application-desc>
</jnlp>
