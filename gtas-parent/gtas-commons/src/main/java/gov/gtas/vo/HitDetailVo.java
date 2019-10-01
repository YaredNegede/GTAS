/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;

import java.util.*;

public class HitDetailVo {

	private HitsSummary parent;

	private Long paxId;

	private Long flightId;

	private String ruleConditions;

	private Date createDate;

	private Long ruleId;

	private String ruleTitle;

	private String ruleDesc;

	private String ruleType;

	private String category;

	private HashMap<Integer, List<HitDetail>> HitsRulesAndDetails;

	private List<HitDetail> hitsDetailsList = new ArrayList<HitDetail>();

	public static HitDetailVo from(HitDetail hitDetail) {
		HitDetailVo hitDetailVo = new HitDetailVo();
		hitDetailVo.setRuleId(hitDetail.getRuleId());
		hitDetailVo.setCreateDate(hitDetail.getCreatedDate());
		hitDetailVo.setCategory(hitDetail.getLookout().getHitCategory().getName());
		hitDetailVo.setFlightId(hitDetail.getPassenger().getFlight().getId());
		hitDetailVo.setPaxId(hitDetail.getPassenger().getId());
		hitDetailVo.setRuleConditions(hitDetail.getRuleConditions());
		hitDetailVo.setRuleDesc(hitDetail.getDescription());
		hitDetailVo.setRuleTitle(hitDetail.getTitle());
		return hitDetailVo;
	}

	public HitsSummary getParent() {
		return parent;
	}

	public void setParent(HitsSummary parent) {
		this.parent = parent;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getRuleTitle() {
		return ruleTitle;
	}

	public void setRuleTitle(String ruleTitle) {
		this.ruleTitle = ruleTitle;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRuleConditions() {
		return ruleConditions;
	}

	public void setRuleConditions(String ruleConditions) {
		this.ruleConditions = ruleConditions;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public List<HitDetail> getHitsDetailsList() {
		return hitsDetailsList;
	}

	public void setHitsDetailsList(List<HitDetail> hitsDetailsList) {
		this.hitsDetailsList = hitsDetailsList;
	}

	public HashMap<Integer, List<HitDetail>> getHitsRulesAndDetails() {
		return HitsRulesAndDetails;
	}

	public void setHitsRulesAndDetails(HashMap<Integer, List<HitDetail>> hitsRulesAndDetails) {
		HitsRulesAndDetails = hitsRulesAndDetails;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Long getPaxId() {
		return paxId;
	}

	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HitDetailVo that = (HitDetailVo) o;
		return Objects.equals(getPaxId(), that.getPaxId()) &&
				Objects.equals(getFlightId(), that.getFlightId()) &&
				Objects.equals(getRuleConditions(), that.getRuleConditions()) &&
				Objects.equals(getCreateDate(), that.getCreateDate()) &&
				Objects.equals(getRuleId(), that.getRuleId()) &&
				Objects.equals(getRuleTitle(), that.getRuleTitle()) &&
				Objects.equals(getRuleDesc(), that.getRuleDesc()) &&
				Objects.equals(getCategory(), that.getCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPaxId(), getFlightId(), getRuleConditions(), getCreateDate(), getRuleId(), getRuleTitle(), getRuleDesc(), getCategory());
	}
}
