package com.jfdimarzio.check.provider;

public class APPColumn {
    public interface CheckResultSign{
        String TABLE_NAME="CheckResultSign";
        String UniqueID="UniqueID";
        String ArriveRecordUniqueID="ArriveRecordUniqueID";
        String JobUniqueID="JobUniqueID";
        String ControlPointUniqueid="ControlPointUniqueID";
        String ABNote="ABNote";
        String ABHand="ABHand";
        String SingPath="SingPath";
    }
    public interface SuggestUser{
        String TABLE_NAME="SuggestUser";
        String UserID="UserID";
        String QueryDateTime="QueryDateTime";
    }
    public interface Version{
        String TABLE_NAME="Version";
        String Id="Id";
        String AppName="AppName";
        String ApkName="ApkName";
        String VerName="VerName";
        String VerCode="VerCode";
        String ReleaseNote="ReleaseNote";
        String IsForceUpdate="IsForceUpdate";
        String DateReleased="DateReleased";
    }
}
