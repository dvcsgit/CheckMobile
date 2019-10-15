package com.jfdimarzio.check.model.response;

public class ServerResponse {
    private boolean IsSuccess;
    private String Message;

    public ServerResponse(){}

    public boolean isSuccess(){return IsSuccess;}

    public String getMessage(){return Message;}

    public void setSuccess(boolean success){IsSuccess=success;}

    public void setMessage(String message){Message=message;}
}
