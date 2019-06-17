package com.appr.framework.messages;

public class ResponseWrapper {

    //region Public members

    public static ResponseWrapper success(Response data) {
        return new Success(data);
    }

    public static ResponseWrapper progress(boolean isLoading) {
        return new Loading(isLoading);
    }

    public static ResponseWrapper error(Throwable error) {
        return new Error(error);
    }

    public static ResponseWrapper error(Throwable error, String message) {
        return new Error(error, message);
    }

    //endregion

    //region Success

    public static class Success<G> extends ResponseWrapper {
        //region Variables

        public Response mResponse;

        //endregion

        //region Constructor

        public Success(Response data) {
            this.mResponse = data;
        }

        //endregion
    }

    //endregion

    //region Loading

    public static class Loading<L> extends ResponseWrapper {
        //region Variables

        public boolean isLoading;

        //endregion

        //region Constructor

        public Loading(boolean isLoading) {
            this.isLoading = isLoading;
        }

        //endregion
    }

    //endregion

    //region Error

    public static class Error<E> extends ResponseWrapper {
        //region Variables

        public Throwable exception;
        public String message;

        //endregion

        //region Constructor

        public Error(Throwable exception, String message) {
            this.exception = exception;
            this.message = message;
        }

        public Error(Throwable exception) {
            this.exception = exception;
            this.message = exception.getMessage();
        }

        //endregion
    }

    //endregion
}
