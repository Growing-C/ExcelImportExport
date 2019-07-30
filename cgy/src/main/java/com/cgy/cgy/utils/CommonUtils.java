package com.cgy.cgy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 根据身份证号获取年龄
	 * 
	 * @param id
	 * @return
	 */
	public static int getAgeFromIdNum(String id) {
		if (id == null || id.length() == 0) {
			return 0;
		}
		int leh = id.length();
		String dates = "";
		if (leh == 18) {
//			int se = Integer.valueOf(id.substring(leh - 1)) % 2;
			dates = id.substring(6, 10);
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			String year = df.format(new Date());
			int u = Integer.parseInt(year) - Integer.parseInt(dates);
			return u;
		} else {
			dates = id.substring(6, 8);
			return Integer.parseInt(dates);
		}

	}

	

	private static String[] bigNumStr = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
	private static int[] bigNum = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

	/**
	 * 刘桥镇蒋一村八组2号==>8组26号
	 * 
	 * @param address
	 * @return
	 */
	public static String getSimpleAddress(String address) {
		if (address == null || address.length() == 0) {
			return "";
		}
		int cunIndex = address.indexOf("村") + 1;
		String result = address;
		if (cunIndex > 1) {
			result = address.substring(cunIndex);
		}

		int groupOffset = 0;
		if (result.contains("庙")) {
			result = result.substring(result.indexOf("庙") + 1);
			groupOffset = 21;// 十五里庙一组= 22组
		}
		String group = result.substring(0, result.indexOf("组"));
		int groupNum = 0;
		int groupLen = group.length();
		if (groupLen > 1) {// 十几二十几
			String char0 = String.valueOf(group.charAt(0));
			for (int i = 0; i < 10; i++) {
				if (char0.equals(bigNumStr[i])) {
					groupNum = i == 9 ? bigNum[i] : bigNum[i] * 10;
				}
			}
			String charEnd = String.valueOf(group.charAt(group.length() - 1));
			if (!charEnd.equals("十")) {
				for (int i = 0; i < 10; i++) {
					if (charEnd.equals(bigNumStr[i])) {
						groupNum += bigNum[i];
					}
				}
			}

			if (groupLen > 3) {
				throw new NullPointerException("没这多组啊！");
			}
		} else {
			for (int i = 0; i < 10; i++) {
				if (group.equals(bigNumStr[i])) {
					groupNum = bigNum[i];
				}
			}
		}
		groupNum += groupOffset;
		result = groupNum + result.substring(result.indexOf("组"));
//		System.out.println("adress:" + address + ">>>" + result);
		return result;
	}
}
