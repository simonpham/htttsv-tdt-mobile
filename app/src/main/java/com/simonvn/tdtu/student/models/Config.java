package com.simonvn.tdtu.student.models;

import io.realm.RealmObject;

/**
 * Created by Bichan on 7/21/2016.
 */
public class Config extends RealmObject{
    private String idHocKyMacDinh;
    private String hdptIdHocKyMacDinh;
    private String lichThiIdHocKyMacDinh;

    public String getIdHocKyMacDinh() {
        return idHocKyMacDinh;
    }

    public void setIdHocKyMacDinh(String idHocKyMacDinh) {
        this.idHocKyMacDinh = idHocKyMacDinh;
    }

    public String getHdptIdHocKyMacDinh() {
        return hdptIdHocKyMacDinh;
    }

    public void setHdptIdHocKyMacDinh(String hdptIdHocKyMacDinh) {
        this.hdptIdHocKyMacDinh = hdptIdHocKyMacDinh;
    }

    public String getLichThiIdHocKyMacDinh() {
        return lichThiIdHocKyMacDinh;
    }

    public void setLichThiIdHocKyMacDinh(String lichThiIdHocKyMacDinh) {
        this.lichThiIdHocKyMacDinh = lichThiIdHocKyMacDinh;
    }
}
