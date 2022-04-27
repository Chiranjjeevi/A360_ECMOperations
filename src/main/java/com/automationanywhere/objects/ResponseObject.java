package com.automationanywhere.objects;
/**
 * @author Fayaz Mohammed
 *
 */
public class ResponseObject {
        private final int code;
        private final String body;
        public ResponseObject(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
}
