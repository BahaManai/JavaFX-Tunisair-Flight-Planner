package com.beginsecure.tunisairaeroplan.Model;

import java.util.Date;

public class ArchivVol {
    private int idArchive;
    private vol vol;
    private Date dateArchivage;

    public ArchivVol() {}

    public ArchivVol(vol vol, Date dateArchivage) {
        this.vol = vol;
        this.dateArchivage = dateArchivage;
    }

    public int getIdArchive() {
        return idArchive;
    }

    public void setIdArchive(int idArchive) {
        this.idArchive = idArchive;
    }

    public vol getVol() {
        return vol;
    }

    public void setVol(vol vol) {
        this.vol = vol;
    }

    public Date getDateArchivage() {
        return dateArchivage;
    }

    public void setDateArchivage(Date dateArchivage) {
        this.dateArchivage = dateArchivage;
    }
}
