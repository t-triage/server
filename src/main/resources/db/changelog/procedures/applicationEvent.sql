CREATE PROCEDURE DELETE_APPLICATION_EVENT_BY_TIMESTAMP(time_stamp bigint)
LANGUAGE 'sql'
AS $BODY$
delete from qa_event q
where q.timestamp>=time_stamp;
$BODY$;