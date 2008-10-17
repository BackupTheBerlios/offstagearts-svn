#/bin/bash
# Usage: mklauncher.sh <OA JNLP Version> <Config Dir> <Output Jar File>
# eg:  ./mklauncher.sh 1.7 'Ballet Theatre (Test)' ~/mvn/oalaunch/src/main/resources/offstage/config test_ballettheatre.jar

VERSION=$1
#CONFIGNAME=$2
CONFIGDIR=$2
OUTJAR=$3

OFFSTAGEARTS=`pwd`
OALAUNCH=`pwd`/../oalaunch
TMP=`pwd`/tmp

\rm -rf $TMP
mkdir -p $TMP/jar

pushd $TMP/jar
jar xvf $OALAUNCH/target/oalaunch*.jar
\rm -rf oalaunch/config/*

echo >oalaunch/oalaunch.properties

# Copy the configuration directory
(cd $CONFIGDIR; tar cf - .) | (cd oalaunch/config; tar xvf -)

# Remove spurious SVN stuff
\rm -rf `find oalaunch/config -name .svn`

# Set up the name
#echo $CONFIGNAME >oalaunch/configname.txt

# Set up the JNLP File
rm oalaunch/*.jnlp*
#cp $OFFSTAGEARTS/jaws/jnlp/offstagearts_oalaunch-$VERSION.jnlp.template oalaunch/offstagearts.jnlp.template
echo 'jnlp.template.url = http://offstagearts.org/releases/offstagearts/offstagearts_oalaunch-'$VERSION'.jnlp.template' >>oalaunch/oalaunch.properties

# Jar it back up
jar cvfm ../tmp.jar META-INF/MANIFEST.MF .

# sign it
jarsigner -storepass keyst0re ../tmp.jar offstagearts

popd
rm -f "$OUTJAR"
mv $TMP/tmp.jar "$OUTJAR"
