drop view termenrolls;

CREATE OR REPLACE VIEW termenrolls AS
SELECT cc.termid AS groupid, ee.entityid, ee.courserole,
count(*) as ncourses,
tt.name, tt.firstdate
FROM enrollments ee, courseids cc, termids tt
WHERE ee.courseid = cc.courseid AND cc.termid = tt.groupid
group by cc.termid, ee.entityid, ee.courserole, tt.name, tt.firstdate
ORDER BY cc.termid, ee.entityid, ee.courserole, tt.name, tt.firstdate;

