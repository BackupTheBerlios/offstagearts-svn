mvn install:install-file  -Dfile=TableLayout.jar \
                          -DgroupId=org.citibob.jar \
                          -DartifactId=TableLayout \
                          -Dversion=0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=iReport-2.0.0.jar \
                          -DgroupId=org.citibob.jar \
                          -DartifactId=iReport \
                          -Dversion=2.0.0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=secondstring/cls.jar \
                          -DgroupId=org.citibob.jar.secondstring \
                          -DartifactId=cls \
                          -Dversion=0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=secondstring/jwf.jar \
                          -DgroupId=org.citibob.jar.secondstring \
                          -DartifactId=jwf \
                          -Dversion=0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=secondstring/klingerIncludes.jar \
                          -DgroupId=org.citibob.jar.secondstring \
                          -DartifactId=klingerIncludes \
                          -Dversion=0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=secondstring/secondstring.jar \
                          -DgroupId=org.citibob.jar \
                          -DartifactId=secondstring \
                          -Dversion=0 \
                          -Dpackaging=jar \
                          -DgeneratePom=true





% ---------- Temporary...
mvn install:install-file  -Dfile=stringtemplate/stringtemplate.jar \
                          -DgroupId=org.citibob.jar \
                          -DartifactId=stringtemplate \
                          -Dversion=3.1 \
                          -Dpackaging=jar \
                          -DgeneratePom=true

mvn install:install-file  -Dfile=stringtemplate/antlr-2.7.7.jar \
                          -DgroupId=org.citibob.jar \
                          -DartifactId=antlr \
                          -Dversion=2.7.7 \
                          -Dpackaging=jar \
                          -DgeneratePom=true
