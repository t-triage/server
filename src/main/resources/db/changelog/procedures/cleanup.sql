CREATE PROCEDURE CLEANUP_TESTS(time_stamp bigint)
LANGUAGE 'sql'
AS $BODY$

delete from qa_test_execution q
WHERE not exists (SELECT test_id FROM qa_test_triage tt where tt.test_id = q.id);

delete from qa_error_detail q
WHERE q.timestamp<=time_stamp;

delete from qa_artifact q
WHERE q.timestamp<=time_stamp;

delete from qa_test_triage q
WHERE q.id not IN (SELECT distinct testtriage_id FROM qa_automated_test_issue) and q.timestamp<=time_stamp;

delete from qa_executor_stat q
WHERE q.timestamp<=time_stamp;

delete from qa_build_triage q
WHERE q.id not in (select buildtriage_id from qa_test_triage) and q.id not in (select buildtriage_id from qa_executor_stat) and q.timestamp<=time_stamp;

delete from qa_build q
WHERE q.id not in (select build_id from qa_build_triage) and q.timestamp<=time_stamp;

delete from qa_test_execution q
WHERE not exists (SELECT test_id FROM qa_test_triage tt where tt.test_id = q.id);

delete from qa_report q
WHERE q.id NOT IN (SELECT report_id FROM qa_test_execution) and q.id NOT IN (SELECT report_id FROM qa_build) and q.timestamp<=time_stamp;

$BODY$;