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
//	身份证号(必填)	姓名　(必填)	性别	年龄	家庭地址	联系电话	家庭人口	所在区县(必填)	所在镇(街道)(必填)	所在村(必填)	所属户编号(必填)	与户主的关系(必填)	文化程度	人员状态
//	320624193208035328	高红英	女	87	25组48号		5	通州区	刘桥镇	蒋一村	320624196411085332	之母亲		正常

	public String age;
}
