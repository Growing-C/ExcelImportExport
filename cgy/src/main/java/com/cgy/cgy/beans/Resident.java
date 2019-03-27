package com.cgy.cgy.beans;

public class Resident {
//	魏美如	刘桥镇蒋一村八组10号  	6855273		农业家庭户口	320624197201155325	妻	女	320683018008285
	public String name;
	public String address;
	public String contactPhone;// 联系电话
	public String mobile;// 移动电话
	public String residenceType;// 户口性质
	public String idNum;// 身份证号
	public String relationship;// 与户主关系
	public String gender;// 性别
	public String residenceNum;// 户号

	public String toString() {
		return "<<<<" + name + "-" + address + "-" + contactPhone + "-" + mobile + "-" + residenceType + "-" + idNum
				+ "-" + relationship + "-" + gender + "-" + residenceNum + ">>>>";
	}
}
