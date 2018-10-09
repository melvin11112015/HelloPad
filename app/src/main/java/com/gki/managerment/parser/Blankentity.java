package com.gki.managerment.parser;

//�ɹ���ʵ����
public class Blankentity {

    private String Key;
    private String Document_no;
    private String Type;
    private String TypeSpecified;

    private String Code;
    private String Description;

    private String Sorting_Key;
    private String Sorting_KeySpecified;

    private String Quantity_Time;

    public String getKey() {
        return Key;
    }

    public void setKey(String strKey) {
        this.Key = strKey;
    }

    public String getDocument_No() {
        return Document_no;
    }

    public void setDocument_no(String strDocument_no) {
        this.Document_no = strDocument_no;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getTypeSpecified() {
        return TypeSpecified;
    }

    public void setTypeSpecified(String TypeSpecified) {
        this.TypeSpecified = TypeSpecified;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    //
    public String getSorting_Key() {
        return Sorting_Key;
    }

    public void setSorting_Key(String Sorting_Key) {
        this.Sorting_Key = Sorting_Key;
    }

    public String getSorting_KeySpecified() {
        return Sorting_KeySpecified;
    }

    public void setSorting_KeySpecified(String Sorting_KeySpecified) {
        this.Sorting_KeySpecified = Sorting_KeySpecified;
    }

    public String getQuantity_Time() {
        return Quantity_Time;
    }

    public void setQuantity_Time(String strQuantity_Time) {
        this.Quantity_Time = strQuantity_Time;
    }
}
