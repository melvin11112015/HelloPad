package com.gki.v107.net;

/**
 * Copyright 2018 bejson.com
 */

import com.google.gson.annotations.SerializedName;


/**
 * Auto-generated: 2018-07-19 14:31:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class GenericError {

    @SerializedName("odata.error")
    private Error odata_error;

    public Error getOdata_error() {
        return odata_error;
    }

    public void setOdata_error(Error odata_error) {
        this.odata_error = odata_error;
    }

    public static class Error {
        private String code;
        private Message message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public static class Message {
            private String lang, value;

            public String getLang() {
                return lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

}
