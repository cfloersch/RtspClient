/*
 * Copyright XpertSoftware All rights reserved.
 *
 * Created on Mar 6, 2006
 */
package xpertss.mime;

import xpertss.lang.Objects;

/**
 * This class tokenizes RFC822 and MIME headers into the basic symbols specified
 * by RFC822 and MIME.
 * <p/>
 * This class handles folded headers (ie headers with embedded CRLF SPACE sequences).
 * The folds are removed in the returned tokens.
 *
 * @author cfloersch
 */
public class HeaderTokenizer {

   /**
    * The Token class represents tokens returned by the
    * HeaderTokenizer.
    */
   public static class Token {

      private int type;
      private CharSequence value;

      /**
       * Token type indicating an ATOM.
       */
      public static final int ATOM = -1;

      /**
       * Token type indicating a quoted string. The value
       * field contains the string without the quotes.
       */
      public static final int QUOTEDSTRING = -2;

      /**
       * Token type indicating a comment. The value field
       * contains the comment string without the comment
       * start and end symbols.
       */
      public static final int COMMENT = -3;

      /**
       * Token type indicating linear whitespace.
       */
      public static final int LWS = -4;

      /**
       * Token type indicating end of input.
       */
      public static final int EOF = -5;

      /**
       * Constructor.
       *
       * @param type  Token type
       * @param value Token value
       */
      public Token(int type, CharSequence value)
      {
         this.type = type;
         this.value = value;
      }

      /**
       * Return the type of the token. If the token represents a
       * delimiter or a control character, the type is that character
       * itself, converted to an integer. Otherwise, it's value is
       * one of the following:
       * <ul>
       * <li><code>ATOM</code> A sequence of ASCII characters
       * delimited by either SPACE, CTL, "(", <"> or the
       * specified SPECIALS
       * <li><code>QUOTEDSTRING</code> A sequence of ASCII characters
       * within quotes
       * <li><code>COMMENT</code> A sequence of ASCII characters
       * within "(" and ")".
       * <li><code>EOF</code> End of header
       * </ul>
       */
      public int getType()
      {
         return type;
      }

      /**
       * Returns the value of the token just read. When the current
       * token is a quoted string, this field contains the body of the
       * string, without the quotes. When the current token is a comment,
       * this field contains the body of the comment.
       *
       * @return token value
       */
      public String getValue()
      {
         return value.toString();
      }

      public boolean equals(Object obj)
      {
         if(obj instanceof Token) {
            Token other = (Token) obj;
            return type == other.type && eq(value, other.value);
         }
         return false;
      }

      public int hashCode()
      {
         int valHash = (value != null) ? value.hashCode() : 0;
         return type ^ valHash;
      }

      private boolean eq(Object one, Object two)
      {
         return (one == null) ? two == null : one.equals(two);
      }
   }

   private CharSequence string; // the string to be tokenized
   private String delimiters; // delimiter string
   private int currentPos; // current parse position
   private int maxPos; // string length
   private int nextPos; // track start of next Token for next()
   private int peekPos; // track start of next Token for peek()

   /**
    * RFC822 specials
    */
   public final static String RFC822 = "()<>@,;:\\\"\t .[]";

   /**
    * MIME specials
    */
   public final static String MIME = "()<>@,;:\\\"\t []/?=";

   // The EOF Token
   private final static Token EOFToken = new Token(Token.EOF, null);

   /**
    * Constructor that takes a rfc822 style header.
    *
    * @param header       The rfc822 header to be tokenized
    * @param delimiters   Set of delimiter characters
    *                     to be used to delimit ATOMS. These
    *                     are usually <code>RFC822</code> or
    *                     <code>MIME</code>
    */
   public HeaderTokenizer(CharSequence header, String delimiters)
   {
      string = Objects.ifNull(header, "");
      this.delimiters = Objects.ifNull(delimiters, RFC822);
      currentPos = nextPos = peekPos = 0;
      maxPos = string.length();
   }


   /**
    * Constructor. The RFC822 defined delimiters - RFC822 - are
    * used to delimit ATOMS. Also comments are skipped and not
    * returned as tokens
    */
   public HeaderTokenizer(String header)
   {
      this(header, RFC822);
   }




   public int position()
   {
      return currentPos;
   }


   /**
    * Parses the next token from this String. <p>
    * <p/>
    * Clients sit in a loop calling next() to parse successive
    * tokens until an EOF Token is returned.
    *
    * @return the next Token
    * @throws MalformedException if the parse fails
    */
   public Token next() throws MalformedException
   {
      currentPos = nextPos; // setup currentPos
      Token tk = getNext();
      nextPos = peekPos = currentPos; // update currentPos and peekPos
      return tk;
   }


   /**
    * Peek at the next token, without actually removing the token
    * from the parse stream. Invoking this method multiple times
    * will return successive tokens, until <code>next()</code> is
    * called. <p>
    *
    * @return the next Token
    * @throws MalformedException if the parse fails
    */
   public Token peek() throws MalformedException
   {
      currentPos = peekPos; // setup currentPos
      Token tk = getNext();
      peekPos = currentPos; // update peekPos
      return tk;
   }


   /**
    * Return the rest of the Header.
    *
    * @return String rest of header. null is returned if we are
    *         already at end of header
    */
   public String getRemainder()
   {
      return string.subSequence(nextPos, string.length()).toString();
   }


   /*
    * Return the next token starting from 'currentPos'. After the
    * parse, 'currentPos' is updated to point to the start of the
    * next token.
    */

   private Token getNext() throws MalformedException
   {
      // If we're already at end of string, return EOF
      if(currentPos >= maxPos)
         return EOFToken;

      char c;
      int start;
      boolean filter = false;
      c = string.charAt(currentPos);

      // Check for whitespace
      if(isWhiteSpace(c)) {
         for(start = currentPos; currentPos < maxPos; currentPos++) {
            c = string.charAt(currentPos);
            if(!isWhiteSpace(c)) break;
         }
         return new Token(Token.LWS, string.subSequence(start, currentPos));
      }

      // Check for comments and position currentPos
      // beyond the comment
      if(c == '(') {
         // Parsing comment ..
         int nesting;
         for(start = ++currentPos, nesting = 1; nesting > 0 && currentPos < maxPos; currentPos++) {
            c = string.charAt(currentPos);
            if(c == '\\') {  // Escape sequence
               currentPos++; // skip the escaped character
               filter = true;
            } else if(c == '\r') {
               filter = true;
            } else if(c == '(') {
               nesting++;
            } else if(c == ')') {
               nesting--;
            }
         }
         if(nesting != 0)
            throw new MalformedException("Unbalanced comments");
         // Return the comment, if we are asked to.
         // Note that the comment start & end markers are ignored.
         CharSequence s;
         if(filter)
            s = filterToken(string, start, currentPos - 1);
         else
            s = string.subSequence(start, currentPos - 1);
         return new Token(Token.COMMENT, s);
      }

      // Check for quoted-string and position currentPos
      //  beyond the terminating quote
      if(c == '"') {
         for(start = ++currentPos; currentPos < maxPos; currentPos++) {
            c = string.charAt(currentPos);
            if(c == '\\') { // Escape sequence
               currentPos++;
               filter = true;
            } else if(c == '\r') {
               filter = true;
            } else if(c == '"') {
               currentPos++;
               CharSequence s;
               if(filter)
                  s = filterToken(string, start, currentPos - 1);
               else
                  s = string.subSequence(start, currentPos - 1);
               return new Token(Token.QUOTEDSTRING, s);
            }
         }
         throw new MalformedException("Unbalanced quoted string");
      }

      // Check for SPECIAL or CTL
      if(c < 040 || c >= 0177 || delimiters.indexOf(c) >= 0) {
         currentPos++; // re-position currentPos
         char[] ch = new char[1];
         ch[0] = c;
         return new Token((int) c, new String(ch));
      }

      // Check for ATOM
      for(start = currentPos; currentPos < maxPos; currentPos++) {
         c = string.charAt(currentPos);
         // ATOM is delimited by either SPACE, CTL, "(", <">
         // or the specified SPECIALS
         if(c < 040 || c >= 0177 || c == '(' || c == ' ' || c == '"' || delimiters.indexOf(c) >= 0)
            break;
      }
      return new Token(Token.ATOM, string.subSequence(start, currentPos));
   }


   // Skip SPACE, HT, CR and NL

   private int skipWhiteSpace()
   {
      char c;
      for(; currentPos < maxPos; currentPos++) {
         if(((c = string.charAt(currentPos)) != '\t') && (c != ' ') && (c != '\r') && (c != '\n'))
            return currentPos;
      }
      return Token.EOF;
   }

   private boolean isWhiteSpace(char c)
   {
      return (c == '\t' || c == '\r' || c == '\n' || c == ' ');
   }


   /* Process escape sequences and embedded LWSPs from a comment or
    * quoted string.
    */

   private static CharSequence filterToken(CharSequence s, int start, int end)
   {
      StringBuffer sb = new StringBuffer();
      char c;
      boolean gotEscape = false;
      boolean gotCR = false;

      for(int i = start; i < end; i++) {
         c = s.charAt(i);
         if(c == '\n' && gotCR) {
            // This LF is part of an unescaped
            // CRLF sequence (i.e, LWSP). Skip it.
            gotCR = false;
            continue;
         }

         gotCR = false;
         if(!gotEscape) {
            // Previous character was NOT '\'
            if(c == '\\') // skip this character
               gotEscape = true;
            else if(c == '\r') // skip this character
               gotCR = true;
            else // append this character
               sb.append(c);
         } else {
            // Previous character was '\'. So no need to
            // bother with any special processing, just
            // append this character
            sb.append(c);
            gotEscape = false;
         }
      }
      return sb;
   }


}
