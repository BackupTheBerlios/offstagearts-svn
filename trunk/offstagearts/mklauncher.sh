#/bin/bash
# Usage: mklauncher.sh <OA JNLP Version> <Config Dir> <Output Jar File>
# eg:  ./mklauncher.sh 1.7 'Ballet Theatre (Test)' ~/mvn/oalaunch/src/main/resources/offstage/config test_ballettheatre.jar

CMDDIR=$0
CMDDIR=$(readlink -f $CMDDIR)
CMDDIR=$(dirname $CMDDIR)

VERSION=$1
#CONFIGNAME=$2
CONFIGDIR=$(readlink -f "$2")
OUTJAR=$(readlink -f "$3")

OALAUNCH=$CMDDIR/../oalaunch
TMP=`pwd`/tmp

\rm -rf $TMP
mkdir -p $TMP

pushd $TMP
jar xvf $OALAUNCH/target/oalaunch*.jar
\rm -rf oalaunch/config/*

echo >oalaunch/oalaunch.properties

# Copy the configuration directory
(cd $CONFIGDIR; tar cf - .) | (cd oalaunch/config; tar xvf -)
#rm oalaunch/config/Linux.properties
#rm oalaunch/config/Mac.properties
#rm oalaunch/config/Windows.properties


# Remove spurious SVN stuff
\rm -rf `find oalaunch/config -name .svn`

# Set up the name
#echo $CONFIGNAME >oalaunch/configname.txt

# Set up the JNLP File
rm oalaunch/*.jnlp*
#cp $CMDDIR/jaws/jnlp/offstagearts_oalaunch-$VERSION.jnlp.template oalaunch/offstagearts.jnlp.template
echo 'jnlp.template.url = http://offstagearts.org/releases/offstagearts/offstagearts_oalaunch-'$VERSION'.jnlp.template' >>oalaunch/oalaunch.properties
echo "config.name = $OUTJAR" >>oalaunch/oalaunch.properties

# Jar it back up
jar cvfm "$OUTJAR" META-INF/MANIFEST.MF .

# sign it
jarsigner -storepass keyst0re "$OUTJAR" offstagearts

popd

\rm -rf $TMP
