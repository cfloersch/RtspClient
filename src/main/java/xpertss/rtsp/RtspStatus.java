/**
 * Created by IntelliJ IDEA.
 * User: cfloersch
 * Date: 3/9/11 1:37 PM
 * Copyright Manheim online
 */
package xpertss.rtsp;

public enum RtspStatus {

   Continue(100, "Continue", false),

   Ok(200, "Ok", true),
   Created(201, "Created", true),
   LowOnStorageSpace(250, "Low on Storage Space", true),


   MultipleChoices(300, "Multiple Choices", true),
   MovedPermanently(301, "Moved Permanently", true),
   MovedTemporarily(302, "Moved Temporarily", true),              // HTTP/1.0
   SeeOther(303, "See Other", true),
   NotModified(304, "Not Modified", false),
   UseProxy(305, "Use Proxy", true),


   BadRequest(400, "Bad Request", true),
   Unauthorized(401, "Unauthorized", true),
   PaymentRequired(402, "Payment Required", true),
   Forbidden(403, "Forbidden", true),
   NotFound(404, "Not Found", true),
   MethodNotAllowed(405, "Method Not Allowed", true),
   NotAcceptable(406, "Not Acceptable", true),
   ProxyAuthenticationRequired(407, "Proxy Authentication Required", true),
   RequestTimeout(408, "Request Timeout", true),
   Gone(410, "Gone", true),

   LengthRequired(411, "Length Required", true),
   PreconditionFailed(412, "Precondition Failed", true),
   RequestEntityTooLarge(413, "Request Entity Too Large", true),
   RequestUriTooLong(414, "Request-URI Too Long", true),
   UnsupportedMediaType(415, "Unsupported Media Type", true),

   ParameterNotUnderstood(451, "Parameter Not Understood", true),
   ConferenceNotFound(452, "Conference Not Found", true),
   NotEnoughBandwidth(453, "Not Enough Bandwidth", true),
   SessionNotFound(454, "Session Not Found", true),
   MethodNotValidInThisState(455, "Method Not Valid in This State", true),
   HeaderFieldNotValid(456, "Header Field Not Valid for Resource", true),
   InvalidRange(457, "Invalid Range", true),
   ParameterIsReadOnly(458, "Parameter Is Read-Only", true),
   UnsupportedTransport(461, "Unsupported Transport", true),
   DestinationUnreachable(462, "Destination Unreachable", true),


   InternalServerError(500, "Internal Server Error", true),
   NotImplemented(501, "Not Implemented", true),
   BadGateway(502, "Bad Gateway", true),
   ServiceUnavailable(503, "Service Unavailable", true),
   GatewayTimeout(504, "Gateway Timeout", true),
   RTSPVersionNotSupported(505, "RTSP Version Not Supported", true),
   OptionNotSupported(551, "Option Not Supported", true);           // WebDav


   private int code;
   private String reason;
   private boolean allowsEntity;

   private RtspStatus(int code, String reason, boolean allowsEntity)
   {
      this.code = code;
      this.reason = reason;
      this.allowsEntity = allowsEntity;
   }

   public int getCode()
   {
      return code;
   }

   public String getReason()
   {
      return reason;
   }

   public boolean allowsEntity()
   {
      return allowsEntity;
   }

   @Override
   public String toString()
   {
      return String.format("%d %s", getCode(), getReason());
   }


   public boolean isInformational()
   {
      return code >= 100 && code < 200;
   }

   public boolean isSuccess()
   {
      return code >= 200 && code < 300;
   }

   public boolean isRedirection()
   {
      return code >= 300 && code < 400;
   }

   public boolean isClientError()
   {
      return code >= 400 && code < 500;
   }

   public boolean isServerError()
   {
      return code >= 500;
   }



   public static RtspStatus valueOf(int code)
   {
      for(RtspStatus status : RtspStatus.values()) {
         if(status.getCode() == code) return status;
      }
      throw new IllegalArgumentException("Non existent status code specified: " + code);
   }

}
