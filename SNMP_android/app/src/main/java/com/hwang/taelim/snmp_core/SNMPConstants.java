package com.hwang.taelim.snmp_core;


// snmp에서 사용하는 에러 상수 및 버전의 데이터를 참고하기 위해 snmp4j의 상수 class를 이용하였습니다. (version과 몇 가지 에러 메시지만 사용)
public class SNMPConstants {
    public static final int DEFAULT_COMMAND_RESPONDER_PORT = 161;
    public static final int DEFAULT_NOTIFICATION_RECEIVER_PORT = 162;

    public static final int MIN_PDU_LENGTH = 484;

    public static final int MILLISECOND_TO_NANOSECOND = 1000000;
    public static final int HUNDREDTHS_TO_NANOSECOND = 10000000;

    public static final int version1  = 0;
    public static final int version2c = 1;
    public static final int version3  = 3;

    // SNMP error conditions defined (indirectly) by the SNMP standards:
    /** Command responders did not respond within specified timeout interval. */
    public static final int SNMP_ERROR_TIMEOUT                 = -1;
    /** OIDs returned from a GETNEXT or GETBULK are less or equal than the requested one (which is not allowed by SNMP). */
    public static final int SNMP_ERROR_LEXICOGRAPHIC_ORDER     = -2;
    /** A unresolvable REPORT message was received while processing a request. */
    public static final int SNMP_ERROR_REPORT                  = -3;
    /** An IOException occurred during request processing. */
    public static final int SNMP_ERROR_IO                      = -4;

    // SNMP error codes defined by the protocol:
    public static final int SNMP_ERROR_SUCCESS                 = 0;
    public static final int SNMP_ERROR_TOO_BIG                 = 1;
    public static final int SNMP_ERROR_NO_SUCH_NAME            = 2;
    public static final int SNMP_ERROR_BAD_VALUE               = 3;
    public static final int SNMP_ERROR_READ_ONLY               = 4;
    public static final int SNMP_ERROR_GENERAL_ERROR           = 5;
    public static final int SNMP_ERROR_NO_ACCESS               = 6;
    public static final int SNMP_ERROR_WRONG_TYPE              = 7;
    public static final int SNMP_ERROR_WRONG_LENGTH            = 8;
    public static final int SNMP_ERROR_WRONG_ENCODING          = 9;
    public static final int SNMP_ERROR_WRONG_VALUE             =10;
    public static final int SNMP_ERROR_NO_CREATION             =11;
    public static final int SNMP_ERROR_INCONSISTENT_VALUE      =12;
    public static final int SNMP_ERROR_RESOURCE_UNAVAILABLE    =13;
    public static final int SNMP_ERROR_COMMIT_FAILED           =14;
    public static final int SNMP_ERROR_UNDO_FAILED             =15;
    public static final int SNMP_ERROR_AUTHORIZATION_ERROR     =16;
    public static final int SNMP_ERROR_NOT_WRITEABLE           =17;
    public static final int SNMP_ERROR_INCONSISTENT_NAME       =18;

    public static final int SNMP_MP_OK                          = 0;
    public static final int SNMP_MP_ERROR                       = -1400;
    public static final int SNMP_MP_UNSUPPORTED_SECURITY_MODEL  = -1402;
    public static final int SNMP_MP_NOT_IN_TIME_WINDOW          = -1403;
    public static final int SNMP_MP_DOUBLED_MESSAGE             = -1404;
    public static final int SNMP_MP_INVALID_MESSAGE             = -1405;
    public static final int SNMP_MP_INVALID_ENGINEID            = -1406;
    public static final int SNMP_MP_NOT_INITIALIZED             = -1407;
    public static final int SNMP_MP_PARSE_ERROR                 = -1408;
    public static final int SNMP_MP_UNKNOWN_MSGID               = -1409;
    public static final int SNMP_MP_MATCH_ERROR                 = -1410;
    public static final int SNMP_MP_COMMUNITY_ERROR             = -1411;
    public static final int SNMP_MP_WRONG_USER_NAME             = -1412;
    public static final int SNMP_MP_BUILD_ERROR                 = -1413;
    public static final int SNMP_MP_USM_ERROR                   = -1414;
    public static final int SNMP_MP_UNKNOWN_PDU_HANDLERS        = -1415;
    public static final int SNMP_MP_UNAVAILABLE_CONTEXT         = -1416;
    public static final int SNMP_MP_UNKNOWN_CONTEXT             = -1417;
    public static final int SNMP_MP_REPORT_SENT                 = -1418;

    public static final int SNMPv1v2c_CSM_OK                       = 0;
    public static final int SNMPv1v2c_CSM_BAD_COMMUNITY_NAME       = 1501;
    public static final int SNMPv1v2c_CSM_BAD_COMMUNITY_USE        = 1502;
}
