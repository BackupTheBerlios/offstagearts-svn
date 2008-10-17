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
\rm -rf offstage/config/*

# Copy the configuration directory
(cd $CONFIGDIR; tar cf - .) | (cd offstage/config; tar xvf -)

# Remove spurious SVN stuff
\rm -rf `find offstage/config -name .svn`

# Set up the name
#echo $CONFIGNAME >offstage/configname.txt

# Set up the JNLP File
rm offstage/*.jnlp*
cp $OFFSTAGEARTS/jaws/jnlp/offstagearts_oalaunch-$VERSION.jnlp.template offstage/offstagearts.jnlp.template

# Jar it back up
jar cvfm ../tmp.jar META-INF/MANIFEST.MF .

# sign it
jarsigner -storepass keyst0re ../tmp.jar offstagearts

popd
rm -f $OUTJAR
mv $TMP/tmp.jar $OUTJAR
