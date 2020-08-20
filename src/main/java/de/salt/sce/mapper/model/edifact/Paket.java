package de.salt.sce.mapper.model.edifact;

import java.util.List;

public class Paket {

	private Cni cni;
	private Sts sts;
	private List<Rff> rffs;
	private List<Dtm> dtms;
	private Ftx ftx;
	private List<Nad> nads;
	private Gid gid;
	private Nad nad;
	private List<Gin> gins;
	private Dtm dtm;
	private Rff rff;
	private Pci pci;
	private Tdt tdt;
	private Gin gin;
	private List<Loc> locs;
	private Loc loc;
	
	public Pci getPci() { return pci; }
	public void setPci(Pci pci) {
		this.pci = pci;
	}
	public Dtm getDtm() {
		return dtm;
	}
	public void setDtm(Dtm dtm) {
		this.dtm = dtm;
	}
	public Rff getRff() { return rff; }
	public void setRff(Rff rff) {
		this.rff = rff;
	}
	public Nad getNad() {
		return nad;
	}
	public void setNad(Nad nad) {
		this.nad = nad;
	}
	public List<Gin> getGins() {
		return gins;
	}
	public void setGins(List<Gin> gins) {
		this.gins = gins;
	}
	public Cni getCni() {
		return cni;
	}
	public void setCni(Cni cni) {
		this.cni = cni;
	}
	public Sts getSts() { return sts; }
	public void setSts(Sts sts) {
		this.sts = sts;
	}
	public List<Rff> getRffs() {
		return rffs;
	}
	public void setRffs(List<Rff> rffs) {
		this.rffs = rffs;
	}
	public List<Dtm> getDtms() {
		return dtms;
	}
	public void setDtms(List<Dtm> dtms) {
		this.dtms = dtms;
	}
	public Ftx getFtx() {
		return ftx;
	}
	public void setFtx(Ftx ftx) {
		this.ftx = ftx;
	}
	public List<Nad> getNads() {
		return nads;
	}
	public void setNads(List<Nad> nads) {
		this.nads = nads;
	}
	public Gid getGid() {
		return gid;
	}
	public void setGid(Gid gid) {
		this.gid = gid;
	}
	public Tdt getTdt() {
		return tdt;
	}
	public void setTdt(Tdt tdt) {
		this.tdt = tdt;
	}
	public Gin getGin() { return gin; }
	public void setGin(Gin gin) { this.gin = gin; }
	public List<Loc> getLocs() { return locs; }
	public void setLocs(List<Loc> locs) { this.locs = locs; }
	public Loc getLoc() { return loc; }
	public void setLoc(Loc loc) { this.loc = loc; }

}
